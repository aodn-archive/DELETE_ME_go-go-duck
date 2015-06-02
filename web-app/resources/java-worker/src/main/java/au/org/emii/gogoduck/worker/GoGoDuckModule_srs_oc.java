package au.org.emii.gogoduck.worker;

public class GoGoDuckModule_srs_oc extends GoGoDuckModule_srs {
    @Override
    public SubsetParameters getSubsetParameters() {
        SubsetParameters subsetParametersNew = new SubsetParameters(subset);
        subsetParametersNew.remove("TIME");

        // Rename LATITUDE -> latitude
        // Rename LONGITUDE -> longitude
        subsetParametersNew.parameters.put("latitude", subset.parameters.get("LATITUDE"));
        subsetParametersNew.parameters.put("longitude", subset.parameters.get("LONGITUDE"));

        return subsetParametersNew;
    }
}
