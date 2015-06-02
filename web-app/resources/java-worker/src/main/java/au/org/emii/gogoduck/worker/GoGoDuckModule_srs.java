package au.org.emii.gogoduck.worker;

import java.io.File;
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
        subsetParametersNew.parameters.put("lat", subset.parameters.get("LATITUDE"));
        subsetParametersNew.parameters.put("lon", subset.parameters.get("LONGITUDE"));

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
        // TODO implement!
    }

}
