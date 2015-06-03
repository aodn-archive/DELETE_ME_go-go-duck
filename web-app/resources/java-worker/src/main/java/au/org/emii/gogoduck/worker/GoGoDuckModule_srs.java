package au.org.emii.gogoduck.worker;

import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFileWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class GoGoDuckModule_srs extends GoGoDuckModule {
    @Override
    public SubsetParameters getSubsetParameters() {
        SubsetParameters subsetParametersNew = new SubsetParameters(subset);
        subsetParametersNew.remove("TIME");

        // Rename LATITUDE -> lat
        // Rename LONGITUDE -> lon
        subsetParametersNew.put("lat", subset.parameters.get("LATITUDE"));
        subsetParametersNew.put("lon", subset.parameters.get("LONGITUDE"));

        return subsetParametersNew;
    }

    @Override
    public List<String> ncksExtraParameters() {
        List<String> ncksExtraParameters = new ArrayList<String>();
        ncksExtraParameters.add("--mk_rec_dmn");
        ncksExtraParameters.add("time");
        return ncksExtraParameters;
    }

    @Override
    public void postProcess(File file) {
        // TODO run command!!
        try {
            File tmpFile = File.createTempFile("ncpdq", ".nc");
            String command = String.format("ncpdq -O -U %s %s", file, tmpFile);

            System.out.println(String.format("Unpacking file (ncpdq) '%s' to '%s'", file.toPath(), tmpFile.toPath()));
            System.out.println(command);

            if(0 != Runtime.getRuntime().exec(command).exitValue()) {
                throw new GoGoDuckException("ncpdq exited with non-zero exit value");
            }

            Files.delete(file.toPath());
            Files.move(tmpFile.toPath(), file.toPath());
        }
        catch (Exception e) {
            throw new GoGoDuckException(String.format("Could not run ncpdq on file '%s'", file.toPath()));
        }
    }

    @Override
    public void updateMetadata(Path outputFile) {
        try {
            NetcdfFileWriter nc = NetcdfFileWriter.openExisting(outputFile.toAbsolutePath().toString());

            List<Attribute> newAttributeList = new ArrayList<Attribute>();

            String title = "";
            try {
                title = nc.getNetcdfFile().findGlobalAttribute("title").toString();

                // Remove time slice from title ('something_a, something_b, 2013-11-20T03:30:00Z' -> 'something_a, something_b')
                title = title.substring(0, title.lastIndexOf(","));
            }
            catch (NullPointerException e) {
                // Don't fail because of this bullshit :)
                System.out.println("Could not find 'title' attribute in result file");
            }

            newAttributeList.add(new Attribute("title",
                    String.format("%s, %s, %s",
                            title,
                            subset.get("TIME").start,
                            subset.get("TIME").end)));

            newAttributeList.add(new Attribute("southernmost_latitude", subset.get("LATITUDE").start));
            newAttributeList.add(new Attribute("northernmost_latitude", subset.get("LATITUDE").end));

            newAttributeList.add(new Attribute("westernmost_longitude", subset.get("LONGITUDE").start));
            newAttributeList.add(new Attribute("easternmost_longitude", subset.get("LONGITUDE").end));

            newAttributeList.add(new Attribute("start_time", subset.get("TIME").start));
            newAttributeList.add(new Attribute("stop_time", subset.get("TIME").end));

            nc.setRedefineMode(true);
            for (Attribute newAttr : newAttributeList) {
                nc.addGroupAttribute(null, newAttr);
            }
            nc.setRedefineMode(false);
            nc.close();
        }
        catch (IOException e) {
            throw new GoGoDuckException(String.format("Failed updating metadata for file '%s': '%s'", outputFile, e.getMessage()));
        }
    }
}
