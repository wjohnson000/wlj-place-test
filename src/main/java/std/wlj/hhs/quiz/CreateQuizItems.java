/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.quiz;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.homelands.admin.parser.helper.CSVUtility;

import org.familysearch.homelands.core.persistence.util.JsonUtility;

import com.fasterxml.jackson.databind.JsonNode;

import std.wlj.ws.rawhttp.HttpClientX;

/**
 * @author wjohnson000
 *
 */
public class CreateQuizItems {

    static final String BASE_URL    = "http://localhost:5000";
    static final String INPUT_PATH  = "C:/D-drive/homelands/AAM/quizAnswers.csv";

    public static void main(String...args) throws Exception {
        String collId  = createCollection();
        System.out.println("Coll-ID:" + collId);

        String quizId = createQuiz(null, collId, "en", "us", "Who was the NFL champion?");
        createQuiz(quizId, collId, "es", "us", "¿Quién fue el campeón de la NFL?");
        createQuiz(quizId, collId, "pt", "br", "Quem foi o campeão da NFL?");

        Set<String> itemIds = createItems();
        addItemsToQuiz(quizId, itemIds);
        System.out.println("Quiz-ID: " + quizId);

        System.exit(0);
    }

    static String createCollection() {
        Map<String, String> headers = Collections.singletonMap("Accept-Language", "en");

        JsonNode attribution = JsonUtility.emptyNode();
        JsonUtility.addField(attribution, "en", "Actually stuff from AAM ...");

        JsonNode collection = JsonUtility.emptyNode();
        JsonUtility.addField(collection, "name", "Wayne's Test ...");
        JsonUtility.addField(collection, "description", "");
        JsonUtility.addField(collection, "originLanguage", "en");
        JsonUtility.addArray(collection, "availableLanguages", "en", "es", "pt");
        JsonUtility.addField(collection, "visibility", "PUBLIC");
        JsonUtility.addArray(collection, "types", "QUIZ", "EVENT");
        JsonUtility.addField(collection, "category", "SPORTS");
        JsonUtility.addField(collection, "source", "The mind of Wayne");
        JsonUtility.addField(collection, "partner", "The mind of Wayne");
        JsonUtility.addField(collection, "attribution", attribution);
        JsonUtility.addField(collection, "contractType", "LEASE");
        JsonUtility.addField(collection, "createUserId", "cis.user.MMMM-ABCD");
        JsonUtility.addField(collection, "modifyUserId", "cis.user.MMMM-ABCD");

        String result = HttpClientX.doPostJson(BASE_URL + "/collection", collection.toPrettyString(), headers);
        if (result != null) {
            int ndx = result.lastIndexOf('/');
            return result.substring(ndx+1);
        }
        
        return null;
    }

    static String createQuiz(String id, String collId, String lang, String region, String question) {
        Map<String, String> headers = Collections.singletonMap("Accept-Language", lang);

        JsonNode quiz = JsonUtility.emptyNode();
        JsonUtility.addField(quiz, "id", id);
        JsonUtility.addField(quiz, "name", "The Great American Football Quiz!");
        JsonUtility.addField(quiz, "description", question);
        JsonUtility.addField(quiz, "language", lang);
        JsonUtility.addField(quiz, "region", region);
        JsonUtility.addField(quiz, "placeRepId", 1);
        JsonUtility.addField(quiz, "category", "SPORTS");
        JsonUtility.addField(quiz, "subcategory", "AMERICAN_FOOTBALL");
        JsonUtility.addField(quiz, "answerType", "EVENT");
        JsonUtility.addField(quiz, "question", question);
        JsonUtility.addField(quiz, "collectionId", collId);
        JsonUtility.addField(quiz, "quizVisibility", "PUBLIC");
        JsonUtility.addField(quiz, "attribution", "Some old codger");
        JsonUtility.addField(quiz, "createUserId", "cis.user.MMMM-ABCD");
        JsonUtility.addField(quiz, "modifyUserId", "cis.user.MMMM-ABCD");

        if (id == null) {
            String result = HttpClientX.doPostJson(BASE_URL + "/quiz", quiz.toPrettyString(), headers);
            if (result != null) {
                int ndx = result.lastIndexOf('/');
                return result.substring(ndx+1);
            }
        } else {
            String result = HttpClientX.doPutJson(BASE_URL + "/quiz/" + id, quiz.toPrettyString(), headers);
            System.out.println("PUT: " + id + " --> " + result);
        }

        return null;

    }

    static Set<String> createItems() throws Exception {
        Map<String, String> idByYear = new HashMap<>();
        
        byte[] rawBytes = Files.readAllBytes(Paths.get(INPUT_PATH));
        List<List<String>> rows = CSVUtility.loadCsvFile(rawBytes);
        for (List<String> row : rows) {
            if (row.size() > 10  &&  row.get(0).equals("11")) {
                String year = row.get(1);
                String ans  = row.get(3);
                String desc = row.get(5);
                String lang = row.get(9);
                String regn = row.get(10);
                String id = idByYear.getOrDefault(year, null);
                Map<String, String> headers = Collections.singletonMap("Accept-Language", lang);

                JsonNode item = JsonUtility.emptyNode();
                JsonUtility.addField(item, "id", id);
                JsonUtility.addField(item, "type", "EVENT");
                JsonUtility.addField(item, "name", ans);
                JsonUtility.addField(item, "language", lang);
                JsonUtility.addField(item, "locale", regn);
                JsonUtility.addField(item, "placeRepId", 1);
                JsonUtility.addField(item, "category", "SPORTS");
                JsonUtility.addField(item, "subcategory", "AMERICAN_FOOTBALL");
                JsonUtility.addField(item, "startYear", Integer.parseInt(year));
                JsonUtility.addField(item, "summary", desc);
                JsonUtility.addField(item, "htmlDescription", desc);
                JsonUtility.addField(item, "description", makeFormattedData(desc));
                JsonUtility.addField(item, "collectionId", "MMMM-ABC");
                JsonUtility.addField(item, "visibility", "PUBLIC");
                JsonUtility.addField(item, "attribution", "Some old codger");
                JsonUtility.addField(item, "createUserId", "cis.user.MMMM-ABCD");
                JsonUtility.addField(item, "modifyUserId", "cis.user.MMMM-ABCD");

                String newId = saveItem(id, item, headers);
                System.out.println("YR=" + year + "." + lang + " --> " + newId);
                if (id == null  &&  newId != null) {
                    idByYear.put(year, newId);
                }
            }
        }

        return new HashSet<>(idByYear.values());
    }

    static void addItemsToQuiz(String quizId, Set<String> itemIds) throws Exception {
        Map<String, String> headers = Collections.singletonMap("Accept-Language", "en");

        String itemIdsJson = itemIds.stream().map(id -> '"' + id + '"').collect(Collectors.joining(", ", "[ ", " ]"));
        System.out.println(itemIdsJson);
        JsonNode idNode = JsonUtility.parseJson(itemIdsJson);

        JsonNode itemIdNode = JsonUtility.emptyNode();
        JsonUtility.addField(itemIdNode, "size", itemIds.size());
        JsonUtility.addField(itemIdNode, "itemIds", idNode);
        System.out.println(itemIdNode.toPrettyString());

        HttpClientX.doPostJson(BASE_URL + "/quiz/" + quizId + "/item", itemIdNode.toPrettyString(), headers);
    }

    static String saveItem(String id, JsonNode item, Map<String, String> headers) {
        if (id == null) {
            String result = HttpClientX.doPostJson(BASE_URL + "/item", item.toPrettyString(), headers);
            if (result != null) {
                int ndx = result.lastIndexOf('/');
                return result.substring(ndx+1);
            }
        } else {
            String result = HttpClientX.doPutJson(BASE_URL + "/item/EVENT/" + id, item.toPrettyString(), headers);
            System.out.println("PUT: " + id + " --> " + result);
        }

        return null;
    }

    static JsonNode makeFormattedData(String rawText) {
        JsonNode content = JsonUtility.emptyNode();
        JsonUtility.addField(content, "style", "HTML");
        JsonUtility.addField(content, "text", rawText);

        JsonNode fmtStr = JsonUtility.emptyNode();
        JsonUtility.addArray(fmtStr, "content", Arrays.asList(content));

        JsonNode descr = JsonUtility.emptyNode();
        JsonUtility.addArray(descr, "formattedString", Arrays.asList(fmtStr));

        return descr;
    }
}
