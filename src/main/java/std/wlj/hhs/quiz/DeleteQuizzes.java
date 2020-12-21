/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.quiz;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.familysearch.homelands.lib.common.util.JsonUtility;
import org.familysearch.standards.place.util.PlaceHelper;
import com.fasterxml.jackson.databind.JsonNode;
import std.wlj.ws.rawhttp.HttpClientX;

/**
 * @author wjohnson000
 *
 */
public class DeleteQuizzes {

    private static final String BASE_URL = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org";
    private static final String authToken = "ff06e18e-af3e-4b32-adf1-6a8ecdd92311-integ";

    private static Map<String, String> headers = new HashMap<>();
    static {
        headers.put("Authorization", "Bearer " + authToken);
        headers.put("Accept-Language", "en");
    }

    public static void main(String...args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("C:/temp/items-to-delete.txt"), StandardCharsets.UTF_8);
        lines.remove(0);
        lines.remove(0);

        for (String line : lines) {
            String itemId = PlaceHelper.split(line, '|')[0].trim();
            JsonNode node = retrieveItem(itemId);
            if (node == null) {
                continue;
            }

            JsonNode details = JsonUtility.getJsonNode(node, "details");
            String id     = JsonUtility.getStringValue(node, "id");
            String type   = JsonUtility.getStringValue(node, "type");
            String collId = JsonUtility.getStringValue(node, "collectionId");
            String userId = JsonUtility.getStringValue(node, "createUserId");

            if (details != null) {
                if (collId == null) {
                    collId = JsonUtility.getStringValue(details, "collectionId");
                }
                if (userId == null) {
                    userId = JsonUtility.getStringValue(details, "createUserId");
                }
            }

            System.out.println("ID=" + id + ";  type=" + type + ";  user=" + userId + ";  collection=" + collId);
            if ("cis.user.MMMM-XL6K".equals(userId)) {
                System.out.println("  ... delete ...");
                deleteItem(id, "en");
            }
        }
    }

    static JsonNode retrieveItem(String id) throws Exception {
        String json = HttpClientX.doGetJSON(BASE_URL + "/quiz/" + id, headers);
        return JsonUtility.parseJson(json);
    }

    static void deleteItem(String quizId, String language) throws Exception {
        Map<String, String> headersX = new HashMap<>();
        headersX.put("Authorization", "Bearer " + authToken);
        headersX.put("Accept-Language", language);

        HttpClientX.doDelete(BASE_URL + "/quiz/" + quizId, headersX);
    }
}
