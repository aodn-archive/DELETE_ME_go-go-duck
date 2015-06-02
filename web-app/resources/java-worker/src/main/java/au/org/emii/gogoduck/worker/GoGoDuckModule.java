package au.org.emii.gogoduck.worker;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class GoGoDuckModule {
    private final String name;
    private final String profile;
    private final String geoserver;
    private final SubsetParameters subset;

    public GoGoDuckModule(String profile, String geoserver, String subset) {
        this.name = "default";
        this.profile = profile;
        this.geoserver = geoserver;
        this.subset = new SubsetParameters(subset);
    }

    public URIList getUriList() {
        String timeCoverageStart = subset.get("TIME").start;
        String timeCoverageEnd = subset.get("TIME").end;

        URIList URIList = null;

        try {
            URIList = new URIList();

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
            System.out.println(new String(postDataBytes));

            InputStream inputStream = conn.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));

            System.out.println("preparing url");
            String line = null;
            Integer i = 0;
            while ((line = dataInputStream.readLine()) != null) {
                if (i > 0) { // Skip first line - it's the headers
                    String[] lineParts = line.split(",");
                    URIList.add(new URI(lineParts[2]));
                }
                i++;
            }
        }
        catch (Exception e) {
            System.out.println(e.getCause());
        }

        return URIList;
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

    public void updateMetadata(Path outputFile) {
    }

    public SubsetParameters getSubsetParameters() {
        SubsetParameters subsetParametersNoTime = new SubsetParameters(subset);
        subsetParametersNoTime.remove("TIME");
        return subsetParametersNoTime;
    }

    public String getName() {
        return name;
    }

}
