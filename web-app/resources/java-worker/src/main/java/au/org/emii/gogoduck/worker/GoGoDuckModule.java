package au.org.emii.gogoduck.worker;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class GoGoDuckModule {
    protected String profile = null;
    protected String geoserver = null;
    protected SubsetParameters subset = null;

    public GoGoDuckModule() {
    }

    public void init(String profile, String geoserver, String subset) {
        this.profile = profile;
        this.geoserver = geoserver;
        this.subset = new SubsetParameters(subset);
    }

    public URIList getUriList() throws GoGoDuckException {
        String timeCoverageStart = subset.get("TIME").start;
        String timeCoverageEnd = subset.get("TIME").end;

        URIList uriList = null;

        try {
            uriList = new URIList();

            String downloadUrl = String.format("%s/wfs", geoserver);

            String cqlFilter = String.format("time >= %s and time <= %s", timeCoverageStart, timeCoverageEnd);
            //downloadUrl += "&CQL_FILTER=" + URLEncoder.encode(cqlFilter, "UTF-8");

            Map<String, String> params = new HashMap<String, String>();
            params.put("typeName", profile);
            params.put("SERVICE", "WFS");
            params.put("outputFormat", "csv");
            params.put("REQUEST", "GetFeature");
            params.put("VERSION", "1.0.0");
            params.put("CQL_FILTER", cqlFilter);

            System.out.println(downloadUrl);

            byte[] postDataBytes = encodeMapForPostRequest(params);

            URL url = new URL(downloadUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);


            InputStream inputStream = conn.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));

            System.out.println(String.format("Getting list of files from '%s'", downloadUrl));
            System.out.println(String.format("Parameters: '%s'", new String(postDataBytes)));
            String line = null;
            Integer i = 0;
            while ((line = dataInputStream.readLine()) != null) {
                if (i > 0) { // Skip first line - it's the headers
                    String[] lineParts = line.split(",");
                    uriList.add(new URI(lineParts[2]));
                }
                i++;
            }
        }
        catch (Exception e) {
            throw new GoGoDuckException(String.format("Error getting list of URLs: '%s'", e.getMessage()));
        }

        return uriList;
    }

    private byte[] encodeMapForPostRequest(Map<String, String> params) {
        byte[] postDataBytes = null;
        try {
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            postDataBytes = postData.toString().getBytes("UTF-8");
        }
        catch (Exception e) {
            System.out.println("Error encoding parameters");
        }

        return postDataBytes;
    }

    public void postProcess(File file) {
        return;
    }

    public String ncksExtraParameters() {
        return "";
    }

    public void updateMetadata(Path outputFile) {
        // TODO IMPLEMENT WITH netcdf utils!!
    }

    public SubsetParameters getSubsetParameters() {
        // Remove time parameter as we don't need to subset on it, we already
        // have only files that are in the correct time range
        SubsetParameters subsetParametersNoTime = new SubsetParameters(subset);
        subsetParametersNoTime.remove("TIME");
        return subsetParametersNoTime;
    }
}
