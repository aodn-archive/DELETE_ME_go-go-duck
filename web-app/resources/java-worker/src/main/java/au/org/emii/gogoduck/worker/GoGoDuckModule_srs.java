package au.org.emii.gogoduck.worker;

import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFileWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoGoDuckModule_srs extends GoGoDuckModule {
    private static final String srsVariables = "time,lat,lon,dt_analysis,l2p_flags,quality_level,satellite_zenith_angle,sea_surface_temperature,sses_bias,sses_count,sses_standard_deviation,sst_dtime,wind_speed,wind_speed_dtime_from_sst";

    @Override
    public SubsetParameters getSubsetParameters() {
        SubsetParameters subsetParametersNew = new SubsetParameters(subset);
        subsetParametersNew.remove("TIME");

        // Rename LATITUDE -> lat
        // Rename LONGITUDE -> lon
        subsetParametersNew.put("lat", subset.get("LATITUDE"));
        subsetParametersNew.put("lon", subset.get("LONGITUDE"));

        return subsetParametersNew;
    }

    @Override
    public List<String> ncksExtraParameters() {
        List<String> ncksExtraParameters = new ArrayList<String>();
        ncksExtraParameters.add("--mk_rec_dmn");
        ncksExtraParameters.add("time");

        // For SRS, use only defined variables rather than all
        ncksExtraParameters.add("-v");
        ncksExtraParameters.add(srsVariables);
        return ncksExtraParameters;
    }

    @Override
    public void postProcess(File file) {
        try {
            File tmpFile = File.createTempFile("ncpdq", ".nc");

            List<String> command = new ArrayList<String>();
            command.add(GoGoDuck.ncdpqPath);
            command.add("-O");
            command.add("-U");
            command.add(file.getAbsolutePath());
            command.add(tmpFile.getAbsolutePath());

            System.out.println(String.format("Unpacking file (ncpdq) '%s' to '%s'", file.toPath(), tmpFile.toPath()));
            GoGoDuck.execute(command);

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

            String title = profile;
            try {
                title = nc.getNetcdfFile().findGlobalAttribute("title").toString();
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
