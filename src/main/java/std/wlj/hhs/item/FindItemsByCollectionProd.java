/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.item;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.familysearch.homelands.lib.common.util.JsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import std.wlj.ws.rawhttp.HttpClientX;

/**
 * @author wjohnson000
 *
 */
public class FindItemsByCollectionProd {

    private static final String BASE_URL = "http://core.homelands.service.prod.us-east-1.prod.fslocal.org";
    private static final String authToken = "6535249a-94ef-43a1-b895-ecabe61b0f93-prod";

    private static final List<String> languages = Arrays.asList("en", "es", "pt", "fr", "it", "ru", "de", "zh-hans", "ja", "ko", "zh");

    private static Map<String, String> headers = new HashMap<>();
    static {
        headers.put("Authorization", "Bearer " + authToken);
    }

    public static void main(String...args) throws Exception {
        processCollection("MMMM-9BZ");
    }

    public static void processCollection(String collectionId) throws Exception {
        List<String> cql = new ArrayList<>(10_000);

        System.out.println();
        for (String language : languages) {
            List<String[]> idsAndType = getAllItems(collectionId, language);
            System.out.println(collectionId + ".Language: " + language + ";  Item-Count: " + idsAndType.size());
            
            for (String[] it : idsAndType) {
                cql.add("DELETE FROM hhs.item_search WHERE itemid = '" + it[0] + "';");
                cql.add("DELETE FROM hhs.item WHERE id = '" + it[0] + "' AND type = '" + it[1] + "';");
            }
        }

        Files.write(Paths.get("C:/temp/prod-delete-" + collectionId + ".cql"), cql, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static List<String[]> getAllItems(String collId, String language) throws Exception {
        List<String[]> results = new ArrayList<>(5_000);

        headers.put("Accept-Language", language);

        int start = 0;
        String   json  = HttpClientX.doGetJSON(BASE_URL + "/item?collectionId=" + collId + "&start=" + start + "&count=100", headers);
        JsonNode node  = JsonUtility.parseJson(json);
        int      count = JsonUtility.getIntValue(node, "count");

        while (count > 0) {
            List<JsonNode> items = JsonUtility.getArrayValueAsNodes(node, "items");
            for (JsonNode iNode : items) {
                String id   = JsonUtility.getStringValue(iNode, "id");
                String type = JsonUtility.getStringValue(iNode, "type");
                results.add(new String[] { id, type });
            }

            start += 99;
            json  = HttpClientX.doGetJSON(BASE_URL + "/item?collectionId=" + collId + "&start=" + start + "&count=100", headers);
            node  = JsonUtility.parseJson(json);
            count = JsonUtility.getIntValue(node, "count");
        }

        return results;
    }
}
