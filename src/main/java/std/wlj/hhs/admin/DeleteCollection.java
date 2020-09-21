/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.familysearch.homelands.admin.parser.helper.JsonUtility;

import com.fasterxml.jackson.databind.JsonNode;

import std.wlj.hhs.model.CollectionResource;
import std.wlj.ws.rawhttp.HttpClientX;

/**
 * @author wjohnson000
 *
 */
public class DeleteCollection {

    private static final String BASE_URL = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org";
    private static final String authToken = "15a20bae-1d90-4651-95cf-4ca100acbf75-integ";

    private static Map<String, String> headers = new HashMap<>();
    static {
        headers.put("Authorization", "Bearer " + authToken);
        headers.put("Accept-Language", "en");
    }

    public static void main(String...args) throws Exception {
        String collectionId = "MMMM-RRH";
        CollectionResource collection = retrieveCollection(collectionId);
        List<String> nameIds = retrieveNameIds(collectionId);

        System.out.println("ID: " + collection.getId());
        System.out.println("   Name: " + collection.getName());
        System.out.println("  Descr: " + collection.getDescription());
        System.out.println("  Creat: " + collection.getCreateDate());
        System.out.println("  Modif: " + collection.getModifyDate());
        nameIds.forEach(id -> System.out.println(" NameId: " + id));

        for (String nameId : nameIds) {
            deleteName(nameId, "en");
            deleteName(nameId, "es");
            deleteName(nameId, "de");
            deleteName(nameId, "da");
            deleteName(nameId, "ko");
        }

        deleteCollection(collectionId, "en");
        deleteCollection(collectionId, "es");
        deleteCollection(collectionId, "de");
        deleteCollection(collectionId, "da");
        deleteCollection(collectionId, "ko");
    }

    static CollectionResource retrieveCollection(String id) throws Exception {
        String json = HttpClientX.doGetJSON(BASE_URL + "/collection/" + id, headers);
        JsonNode node = JsonUtility.parseJson(json);
        return JsonUtility.createObject(node, CollectionResource.class);
    }

    static List<String> retrieveNameIds(String id) throws Exception {
        String idsRaw = HttpClientX.doGetJSON(BASE_URL + "/name/id?collection=" + id, headers);
        JsonNode idsJson = JsonUtility.parseJson(idsRaw);
        return Arrays.asList(JsonUtility.getArrayValue(idsJson, "nameIds"));
    }

    static void deleteName(String nameId, String language) throws Exception {
        Map<String, String> headersX = new HashMap<>();
        headersX.put("Authorization", "Bearer " + authToken);
        headersX.put("Accept-Language", language);

        HttpClientX.doDelete(BASE_URL + "/name/" + nameId, headersX);
    }

    static void deleteCollection(String collId, String language) throws Exception {
        Map<String, String> headersX = new HashMap<>();
        headersX.put("Authorization", "Bearer " + authToken);
        headersX.put("Accept-Language", language);

        HttpClientX.doDelete(BASE_URL + "/collection/" + collId, headersX);
    }
}
