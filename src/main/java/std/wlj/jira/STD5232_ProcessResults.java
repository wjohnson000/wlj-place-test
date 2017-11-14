package std.wlj.jira;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The query for "names:canada" was run four times, as follows:
 *   -- Regular, i.e., no re-rank query  [canada-no-rq.txt]
 *   -- Re-rank query, multiplier=3      [canada-rq-03.txt]
 *   -- Re-rank query, multiplier=12     [canada-rq-12.txt]
 *   -- Re-rank query, multiplier=20     [canada-rq-20.txt]
 * 
 * For each file, pull out the JSON, create a "JSONObject", find the rep-id and score
 * 
 * @author wjohnson000
 *
 */
public class STD5232_ProcessResults {

    static final String   BASE_DIR  = "C:/temp/solr-score";
    static final String[] DATA_FILE = { "canada-no-rq.txt", "canada-rq-03.txt", "canada-rq-12.txt", "canada-rq-20.txt" };

    static class RepInfo {
        int      repId;
        int      typeId;
        double[] rqScore = new double[4];
        String   repIdChain = null;
    }

    public static void main(String...args) throws IOException {
        Map<Integer, RepInfo> scores = new TreeMap<>();

        int ndx = 0;
        for (String dataFile : DATA_FILE) {
            JSONObject jsonO = getJSON(dataFile);
            addData(scores, ndx++, jsonO);
        }

        scores.values().forEach(ri -> System.out.println(ri.repId + "|" + ri.typeId + "|" + ri.rqScore[0] + "|" + ri.rqScore[1] + "|" + ri.rqScore[2] + "|" + ri.rqScore[3] + "|" + ri.repIdChain));
    }

    static JSONObject getJSON(String fileName) throws IOException {
        String allBytes = new String(Files.readAllBytes(Paths.get(BASE_DIR, fileName)));
        int ndx = allBytes.indexOf("JSON: {");
        if (ndx > 0) {
            return new JSONObject(allBytes.substring(ndx+6));
        }
        return null;
    }

    static void addData(Map<Integer, RepInfo> scores, int ndx, JSONObject jsonO) {
        JSONArray docs = jsonO.getJSONObject("response").getJSONArray("docs");
        docs.forEach(doc -> addSingleData(scores, (JSONObject)doc, ndx));
    }

    static void addSingleData(Map<Integer, RepInfo> scores, JSONObject doc, int ndx) {
        int repId  = doc.getInt("repId");
        int typeId = doc.getInt("type");
        double score = doc.getDouble("score");
        String jurisIds = doc.getJSONArray("repIdChain").toString();

        RepInfo ri = scores.get(repId);
        if (ri == null) {
            ri = new RepInfo();
            ri.repId = repId;
            ri.typeId = typeId;
            ri.repIdChain = jurisIds;
            scores.put(repId, ri);
        }
        ri.rqScore[ndx] = score;
    }
}
