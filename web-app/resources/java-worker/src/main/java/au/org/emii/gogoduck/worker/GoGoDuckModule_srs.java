package au.org.emii.gogoduck.worker;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
    public String ncksExtraParameters() {
        return "--mk_rec_dmn time";
    }

    @Override
    public void postProcess(File file) {
        // TODO run command!!
        try {
            File tmpFile = File.createTempFile("ncpdq", ".nc");
            String command = String.format("ncpdq -O -U %s %s", file, tmpFile);

            System.out.println(String.format("Unpacking file (ncpdq) '%s' to '%s'", file.toPath(), tmpFile.toPath()));
            System.out.println(command);
            // TODO execute command

            Files.move(tmpFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
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
