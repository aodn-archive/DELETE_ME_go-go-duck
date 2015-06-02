package au.org.emii.gogoduck.worker;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.*;

public class GoGoDuck {
    private final String geoserver;
    private final String profile;
    private final String subset;
    private final Path outputFile;
    private final Integer limit;
    private Path baseTmpDir;

    public GoGoDuck(String geoserver, String profile, String subset, String outputFile, Integer limit) {
        this.geoserver = geoserver;
        this.profile = profile;
        this.subset = subset;
        this.outputFile = new File(outputFile).toPath();
        this.limit = limit;
        this.baseTmpDir = new File(System.getProperty("java.io.tmpdir")).toPath();
    }

    public void setTmpDir(String tmpDir) {
        this.baseTmpDir = new File(tmpDir).toPath();
    }

    public void run() {
        GoGoDuckModule module = getProfileModule(profile, geoserver, subset);

        Path tmpDir = null;

        try {
            tmpDir = Files.createTempDirectory(baseTmpDir, "gogoduck");

            URIList URIList = module.getUriList();

            enforceFileLimit(URIList, limit);
            downloadFiles(URIList, tmpDir);
            applySubset(tmpDir, module);
            postProcess(tmpDir, module);
            aggregate(tmpDir, outputFile);
            updateMetadata(module, outputFile);
        }
        catch (Exception e) {
            System.out.println(e);
            throw new GoGoDuckException(e.getMessage());
        }
        finally {
            cleanTmpDir(tmpDir);
        }
    }

    public Integer score() {
        return 0;
    }

    private static void enforceFileLimit(URIList URIList, Integer limit) throws GoGoDuckException {
        System.out.println("Enforcing file limit...");
        if (URIList.size() > limit) {
            System.out.println(String.format("Aggregation asked for %d, we allow only %d", URIList.size(), limit));
            throw new GoGoDuckException("Too many files");
        }
        else if (URIList.size() == 0) {
            System.out.println("No URLs returned for aggregation");
            throw new GoGoDuckException("No files returned from geoserver");
        }

        // All good - keep going :)
    }

    private static void downloadFiles(URIList uriList, Path tmpDir) throws GoGoDuckException {
        System.out.println(String.format("Downloading %d files", uriList.size()));

        try {
            for (URI uri : uriList) {
                File srcFile = new File(uriList.get(0).toString());
                String basename = new File(uri.toString()).getName();
                Path dst = new File(tmpDir + File.separator + basename).toPath();

                if(srcFile.exists() && !srcFile.isDirectory()) {
                    Path src = new File(uri.toString()).toPath();

                    try {
                        System.out.println(String.format("Linking '%s' -> '%s'", src, dst));
                        Files.createSymbolicLink(src, dst);
                    } catch (IOException e) {
                        throw new GoGoDuckException(e.getMessage());
                    }
                }
                else {
                    // TODO HARDCODED!!!
                    URL url = new URL(uri.toString().replace("/mnt/imos-t3/", "http://data.aodn.org.au/"));
                    System.out.println(String.format("Downloading '%s' -> '%s'", url.toString(), dst));

                    try {
                        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                        FileOutputStream fos = new FileOutputStream(dst.toFile());
                        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    }
                    catch (Exception e) {
                        throw new GoGoDuckException(e.getMessage());
                    }
                }

                if (dst.getFileName().endsWith(".gz")) {
                    // TODO handle .gz files!!
                    //gunzip(dst);
                }
            }
        }
        catch(MalformedURLException e) {
            throw new GoGoDuckException(String.format("Could not download NetCDF file: '%s'", e.getMessage()));
        }
    }

    private static void applySubset(Path tmpDir, GoGoDuckModule module) throws GoGoDuckException {
        System.out.println(String.format("Applying subset on directory '%s'", tmpDir));

        // TODO parallelize

        File[] directoryListing = tmpDir.toFile().listFiles();
        List<String> ncksSubsetParameters = module.getSubsetParameters().getNcksParameters();
        List<String> ncksExtraParameters = module.ncksExtraParameters();

        System.out.println(String.format("Subset for operation is '%s'", ncksSubsetParameters));
        for (File file : directoryListing) {
            try {
                File tmpFile = File.createTempFile("tmp", ".nc");

                List<String> command = new ArrayList<String>();
                command.add("/usr/bin/ncks"); // TODO hardcoded
                command.add("-a");
                command.add("-4");
                command.add("-O");
                command.addAll(ncksSubsetParameters);
                command.addAll(ncksExtraParameters);

                command.add(file.getPath());
                command.add(tmpFile.getPath());

                System.out.println(String.format("Applying subset '%s' to '%s'", ncksSubsetParameters, file.toPath()));
                execute(command);

                Files.delete(file.toPath());
                Files.move(tmpFile.toPath(), file.toPath());
            }
            catch (Exception e) {
                throw new GoGoDuckException(String.format("Could not apply subset to file '%s': '%s'", file.getPath(), e.getMessage()));
            }
        }
    }

    private static void postProcess(Path tmpDir, GoGoDuckModule module) throws GoGoDuckException {
        File[] directoryListing = tmpDir.toFile().listFiles();
        for (File file : directoryListing) {
            module.postProcess(file);
        }
    }

    private static void aggregate(Path tmpDir, Path outputFile) throws GoGoDuckException {
        List<String> command = new ArrayList<String>();
        command.add("/usr/bin/ncrcat"); // TODO hardcoded
        command.add("-D2");
        command.add("-4");
        command.add("-h");
        command.add("-O");

        File[] directoryListing = tmpDir.toFile().listFiles();
        if (directoryListing.length == 1) {
            // Special case where we have only 1 file
            File file = directoryListing[0];
            try {
                System.out.println(String.format("Renaming '%s' -> '%s'", file, outputFile));
                Files.move(file.toPath(), outputFile);
            }
            catch (IOException e) {
                throw new GoGoDuckException(String.format("Could not concatenate files into a single file: '%s'", e.getMessage()));
            }
        }
        else {
            System.out.println(String.format("Concatenating %d files into '%s'", directoryListing.length, outputFile));
            for (File file : directoryListing) {
                command.add(file.getAbsolutePath());
            }
            command.add(outputFile.toFile().getAbsolutePath());

            // Running ncrcat
            try {
                execute(command);
            }
            catch (Exception e) {
                throw new GoGoDuckException(String.format("Could not concatenate files into a single file: '%s'", e.getMessage()));
            }
        }
    }

    private static void updateMetadata(GoGoDuckModule module, Path outputFile) {
        module.updateMetadata(outputFile);
    }

    private static void cleanTmpDir(Path tmpDir) {
        System.out.println(String.format("Removing temporary directory '%s'", tmpDir));
        try {
            FileUtils.deleteDirectory(tmpDir.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static GoGoDuckModule getProfileModule(String profile, String geoserver, String subset) {
        String thisPackage = GoGoDuckModule.class.getPackage().getName();
        String classToInstantiate = String.format("GoGoDuckModule_%s", profile);

        GoGoDuckModule module = null;
        while (null == module && "" != classToInstantiate) {
            System.out.println(String.format("Trying class '%s.%s'", thisPackage, classToInstantiate));
            try {
                Class classz = Class.forName(String.format("%s.%s", thisPackage, classToInstantiate));
                module = (GoGoDuckModule) classz.newInstance();
                module.init(profile, geoserver, subset);
                System.out.println(String.format("Using class '%s.%s'", thisPackage, classToInstantiate));
                return module;
            }
            catch (Exception e) {
                System.out.println(String.format("Could not find class for '%s.%s'", thisPackage, classToInstantiate));
            }
            classToInstantiate = nextProfile(classToInstantiate);
        }

        throw new GoGoDuckException(String.format("Error initializing class for profile '%s'", profile));
    }

    /* Finds the correct profile to run for the given layer, starts with:
       GoGoDuckModule_acorn_hourly_avg_sag_nonqc_timeseries_url
       GoGoDuckModule_acorn_hourly_avg_sag_nonqc_timeseries
       GoGoDuckModule_acorn_hourly_avg_sag_nonqc
       GoGoDuckModule_acorn_hourly_avg_sag
       GoGoDuckModule_acorn_hourly_avg
       GoGoDuckModule_acorn_hourly
       GoGoDuckModule_acorn
       GoGoDuckModule
    */
    private static String nextProfile(String profile) {
        return profile.substring(0, profile.lastIndexOf("_"));
    }

    public static int execute(List<String> command) throws Exception {
        System.out.println(command);
        System.out.println(command.size());

        ProcessBuilder builder = new ProcessBuilder(command);
        Map<String, String> environ = builder.environment();

        final Process process = builder.start();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

        try {
            process.waitFor();
        }
        catch (InterruptedException e) {
            System.out.println("Interrupted: '%s'");
            throw e;
        }

        return process.exitValue();
    }
}