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
import org.apache.commons.io.FileUtils;


public class GoGoDuck {
    private final String geoserver;
    private final String profile;
    private final String subset;
    private final Path outputFile;
    private final Integer limit;

    public GoGoDuck(String geoserver, String profile, String subset, String outputFile, Integer limit) {
        this.geoserver = geoserver;
        this.profile = profile;
        this.subset = subset;
        this.outputFile = new File(outputFile).toPath();
        this.limit = limit;
    }

    public void run() {
        GoGoDuckModule module = getProfileModule(profile, geoserver, subset);

        Path tmpDir = null;

        try {
            Path baseTmpDir = new File(System.getProperty("java.io.tmpdir")).toPath();
            tmpDir = Files.createTempDirectory(baseTmpDir, "gogoduck");

            URIList URIList = module.getUriList();

            enforceFileLimit(URIList, limit);
            downloadFiles(URIList, tmpDir);
            applySubset(tmpDir, module.getSubsetParameters());
            aggregate(module, tmpDir, outputFile);
            updateMetadata(module, outputFile);
        }
        catch (GoGoDuckException e) {
            System.out.println(String.format("Aggregation failed because of '%s'", e.getCause()));
            throw e;
        }
        catch (Exception e) {
            System.out.println(String.format("Aggregation failed because of '%s'", e.getCause()));
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

    private static void downloadFiles(URIList uriList, Path tmpDir) throws Exception {
        System.out.println(String.format("Downloading %d files", uriList.size()));

        // TODO handle .gz files!!

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
                        System.err.println(e);
                        throw e;
                    }
                }
                else {
                    // TODO HARDCODED!!!
                    URL url = new URL(uri.toString().replace("/mnt/imos-t3/", "http://data.aodn.org.au/"));
                    System.out.println(String.format("Downloading '%s' -> '%s'", url.toString(), dst));

                    ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                    FileOutputStream fos = new FileOutputStream(dst.toFile());
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                }
            }
        }
        catch(MalformedURLException e) {
            System.out.println(e.getStackTrace());
            throw e;
        }
    }

    private static void applySubset(Path tmpDir, SubsetParameters subsetParameters) throws GoGoDuckException {
        System.out.println(String.format("Applying subset on directory '%s'", tmpDir));

        File[] directoryListing = tmpDir.toFile().listFiles();
        String ncksSubsetParameters = subsetParameters.getNcksParameters();
        System.out.println(String.format("Subset for operation is '%s'", ncksSubsetParameters));
        for (int i = 0; i < directoryListing.length; i++) {
            File file = directoryListing[i];
            try {
                File tmpFile = File.createTempFile("tmp", ".nc");

                String command = String.format(
                        "ncks -a -4 -O %s %s %s",
                        ncksSubsetParameters,
                        file.getAbsolutePath(),
                        tmpFile.getAbsolutePath()
                );
                System.out.println(String.format("Applying subset '%s' to '%s'", ncksSubsetParameters, file.toPath()));
                System.out.println(command);
                // TODO execute command

                Files.move(tmpFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            catch (Exception e) {
                throw new GoGoDuckException("Could not apply subset to file '" + file.getAbsolutePath() + "'");
            }
        }
    }

    private static void aggregate(GoGoDuckModule module, Path tmpDir, Path outputFile) throws GoGoDuckException {
        //String command = "ncrcat -D2 -4 -h -O " $dir/* $output_file
        String command = "ncrcat -D2 -4 -h -O ";

        File[] directoryListing = tmpDir.toFile().listFiles();
        if (directoryListing.length == 1) {
            // Special case where we have only 1 file
            File file = directoryListing[0];
            try {
                System.out.println(String.format("Renaming '%s' -> '%s'", file, outputFile));
                Files.move(file.toPath(), outputFile);
            }
            catch (IOException e) {
                System.out.println(e);
                throw new GoGoDuckException("Could not concatenate file");
            }
        }
        else {
            System.out.println(String.format("Concatenating %d files into '%s'", directoryListing.length, outputFile));
            for (int i = 0; i < directoryListing.length; i++) {
                File file = directoryListing[i];
                command += " " + file.getAbsolutePath();
            }
            command += " " + outputFile;
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
        return new GoGoDuckModule(profile, geoserver, subset);
    }
}