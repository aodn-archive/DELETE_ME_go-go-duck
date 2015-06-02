package au.org.emii.gogoduck.worker;

import java.util.HashMap;
import java.util.Map;

public class SubsetParameters {
    public class Pair {
        public String start;
        public String end;

        public Pair(String start, String end) {
            this.start = start;
            this.end = end;
        }
    }

    protected Map<String, Pair> parameters = null;

    public SubsetParameters(String subset) {
        parameters = new HashMap<String, Pair>();

        for (String part : subset.split(";")) {
            String[] subsetParts = part.split(",");
            parameters.put(subsetParts[0], new Pair(subsetParts[1], subsetParts[2]));
        }
    }

    public SubsetParameters(SubsetParameters subset) {
        this.parameters = subset.parameters;
    }

    public Pair get(String key) {
        return parameters.get(key);
    }

    public void remove(String key) {
        parameters.remove(key);
    }

    public String getNcksParameters() {
        String ncksParameters = "";
        for (String key : parameters.keySet()) {
            ncksParameters += String.format(" -d %s,%s,%s", key, parameters.get(key).start, parameters.get(key).end);
        }

        return ncksParameters;
    }
}
