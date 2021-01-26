/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.quiz;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import org.familysearch.homelands.admin.parser.helper.CSVUtility;
import org.familysearch.homelands.lib.common.util.JsonUtility;
import org.familysearch.standards.place.util.PlaceHelper;
import std.wlj.ws.rawhttp.HttpClientX;

/**
 * Create all quizzes, all quiz items ...
 * 
 * @author wjohnson000
 *
 */
public class FindAnswerMismatches {

    static final String BASE_URL    = "http://localhost:5000";
    static final String QUIZ_PATH    = "C:/D-drive/homelands/AAM/quizQuestions.csv";
    static final String ANSWER_PATH  = "C:/D-drive/homelands/AAM/quizAnswers.csv";

    static Set<String> ONE = new HashSet<>();
    static {
        ONE.add("ein");
        ONE.add("one");
        ONE.add("un");
        ONE.add("une");
    }

    static final Map<Integer, String[]> typeCatSubcat = new HashMap<>();
    static {
        typeCatSubcat.put( 1, new String[] { "PEOPLE", "RELIGIOUS_LEADER", null });
        typeCatSubcat.put( 2, new String[] { "PEOPLE", "WORLD_POPULATION", null });
        typeCatSubcat.put( 3, new String[] { "PEOPLE", "POLITICS", null });
        typeCatSubcat.put( 4, new String[] { "PEOPLE", "COUNTRY_POPULATION", null });
        typeCatSubcat.put( 5, new String[] { "EVENT", "TEMPLE", null });
//        typeCatSubcat.put( 1, new String[] { "PEOPLE", "RELIGIOUS_LEADER", null });
//        typeCatSubcat.put( 2, new String[] { "PEOPLE", "WORLD_POPULATION", null });
//        typeCatSubcat.put( 3, new String[] { "PEOPLE", "POLITICS", null });
//        typeCatSubcat.put( 4, new String[] { "PEOPLE", "COUNTRY_POPULATION", null });
//        typeCatSubcat.put( 5, new String[] { "EVENT", "TEMPLE", null });
        typeCatSubcat.put( 6, new String[] { "ACTIVITY", "SPORTS", "BASEBALL" });
        typeCatSubcat.put( 7, new String[] { "ACTIVITY", "SPORTS", "BASKETBALL" });
        typeCatSubcat.put( 8, new String[] { "ACTIVITY", "SPORTS", "BASEBALL" });
        typeCatSubcat.put( 9, new String[] { "ACTIVITY", "SPORTS", "BASKETBALL" });
        typeCatSubcat.put(10, new String[] { "ACTIVITY", "SPORTS", "AMERICAN_FOOTBALL" });
        typeCatSubcat.put(11, new String[] { "ACTIVITY", "SPORTS", "AMERICAN_FOOTBALL", });
        typeCatSubcat.put(12, new String[] { "ACTIVITY", "SPORTS", "SOCCER" });
        typeCatSubcat.put(13, new String[] { "ACTIVITY", "SPORTS", "WINTER_SPORTS" });
        typeCatSubcat.put(14, new String[] { "ACTIVITY", "SPORTS", "AMERICAN_FOOTBALL" });
        typeCatSubcat.put(15, new String[] { "ACTIVITY", "SPORTS", "OTHER_SPORTS" });
    }

    static final Map<Integer, List<QuizModel>> quizDetails = new TreeMap<>();
    static final Map<Integer, Map<String, List<QuizAnswerModel>>> answerDetails = new TreeMap<>();

    public static void main(String...args) throws Exception {
        loadQuizDetails();
        loadAnswerDetails();

        String collId  = createCollection();
        System.out.println("Coll-ID:" + collId);

        for (Integer id : quizDetails.keySet()) {
            String quizId = createQuiz(id, collId);
            System.out.println("\nQuiz-ID: " + quizId);
            Set<String> itemIds = createItems(id, collId);
            System.out.println("  items: " + itemIds.size());
            addItemsToQuiz(quizId, itemIds);
            System.out.println("  items added to quiz ...");
        }

//        dumpQuizDetails();
        quizDetails.values().stream()
                            .map(ll -> ll.get(0))
                            .forEach(System.out::println);
        System.exit(0);
    }

    static void loadQuizDetails() throws Exception {
        byte[] rawBytes = Files.readAllBytes(Paths.get(QUIZ_PATH));
        List<List<String>> rows = CSVUtility.loadCsvFile(rawBytes);
        rows.remove(0);

        for (List<String> row : rows) {
            if (row.size() > 5) {
                QuizModel quizM = new QuizModel();
                quizM.id       = Integer.parseInt(row.get(0));
                quizM.question = row.get(1);
                quizM.language = row.get(3);
                quizM.region   = row.get(4);

                List<QuizModel> quizMs = quizDetails.computeIfAbsent(quizM.id, kk -> new ArrayList<>());
                if (quizMs.isEmpty()) {
                    quizMs.add(quizM);
                } else if (quizM.language.equals("en")) {
                    quizMs.add(0, quizM);
                } else {
                    quizMs.add(quizM);
                }
            }
        }
    }
static String blah = "";
    static void loadAnswerDetails() throws Exception {
        byte[] rawBytes = Files.readAllBytes(Paths.get(ANSWER_PATH));
        List<List<String>> rows = CSVUtility.loadCsvFile(rawBytes);
        rows.remove(0);

        for (List<String> row : rows) {
            if (row.size() > 11) {
                blah = row.stream().collect(Collectors.joining(","));
                QuizAnswerModel answerM = new QuizAnswerModel();
                answerM.id       = Integer.parseInt(row.get(0));
                answerM.year     = Integer.parseInt(row.get(1));
                answerM.options  = PlaceHelper.split(row.get(2), '|');
                answerM.summary  = row.get(3);
                answerM.text1    = row.get(4);
                answerM.text2    = row.get(5);
                answerM.text3    = row.get(6);
                answerM.text4    = row.get(7);
                answerM.image    = row.get(8);
                answerM.language = row.get(9);
                answerM.region   = row.get(10);
                answerM.answer   = determineAnswer(answerM.options, answerM.summary);

                Map<String, List<QuizAnswerModel>> answerMss = answerDetails.computeIfAbsent(answerM.id, kk -> new TreeMap<>());
                List<QuizAnswerModel> answerMs = answerMss.computeIfAbsent(answerM.language, kk -> new ArrayList<>());
                answerMs.add(answerM);
            }
        }
    }

    static String determineAnswer(String[] options, String summary) {
        String summaryL = summary.toLowerCase();

        // Check to see if the summary contains one of the options as an exact match for numeric values
        for (String option : options) {
            if (summaryL.contains(" " + option.toLowerCase() + " ")) {
                return option;
            }
        }

        // Check to see if the summary contains the word "one"
        if (Arrays.stream(options).anyMatch(opt -> opt.equals("1"))) {
            for (String one : ONE) {
                if (summaryL.contains(" " + one + " ")) {
                    return "1";
                }
            }
        }

        // Check to see if the summary contains one of the options as an exact match
        for (String option : options) {
            if (summaryL.contains(option.toLowerCase())) {
                return option;
            }
        }

        // Check to see if the summary contains one of the options if the option ends with "s"
        for (String option : options) {
            if (option.endsWith("s")  &&  summaryL.contains(option.substring(0, option.length()-1).toLowerCase())) {
                return option;
            }
        }
System.out.println("==============================================================");
System.out.println(blah);
System.out.println("Options: " + Arrays.toString(options));
System.out.println("Summary: " + summary);
        // For every question, the first option is the correct answer!!
        return options[0];
    }

    static void dumpQuizDetails() {
        for (Map.Entry<Integer, List<QuizModel>> entryQ : quizDetails.entrySet()) {
            Map<String, List<QuizAnswerModel>> answerMss = answerDetails.getOrDefault(entryQ.getKey(), Collections.emptyMap());

            System.out.println("\n=================================================================");
            System.out.println("Quiz.id: " + entryQ.getKey());
            for (QuizModel quizM : entryQ.getValue()) {
                System.out.println(quizM);
                List<QuizAnswerModel> answerMs = answerMss.getOrDefault(quizM.language, Collections.emptyList());
                Collections.sort(answerMs);
                for (QuizAnswerModel answerM : answerMs) {
                    System.out.println("   " + answerM);
                }
            }
        }
    }

    static String createCollection() {
        Map<String, String> headers = Collections.singletonMap("Accept-Language", "en");

        Set<String> langs = answerDetails.values().stream()
                              .map(vv -> vv.keySet())
                              .flatMap(ee -> ee.stream())
                              .collect(Collectors.toCollection(TreeSet::new));

        JsonNode attribution = JsonUtility.emptyNode();
        JsonUtility.addField(attribution, "en", "Actually stuff from AAM ...");

        JsonNode collection = JsonUtility.emptyNode();
        JsonUtility.addField(collection, "name", "Wayne's Test ...");
        JsonUtility.addField(collection, "description", "");
        JsonUtility.addField(collection, "originLanguage", "en");
        JsonUtility.addArray(collection, "availableLanguages", langs.toArray(new String[langs.size()]));
        JsonUtility.addField(collection, "visibility", "PUBLIC");
        JsonUtility.addArray(collection, "types", "QUIZ", "ACTIVITY", "EVENT", "FACT", "PEOPLE");
        JsonUtility.addField(collection, "category", "SPORTS");
        JsonUtility.addField(collection, "source", "The mind of Wayne");
        JsonUtility.addField(collection, "partner", "The mind of Wayne");
        JsonUtility.addField(collection, "attribution", attribution);
        JsonUtility.addField(collection, "contractType", "LEASE");
        JsonUtility.addField(collection, "createUserId", "cis.user.MMMM-ABCD");
        JsonUtility.addField(collection, "modifyUserId", "cis.user.MMMM-ABCD");

//        String result = HttpClientX.doPostJson(BASE_URL + "/collection", collection.toPrettyString(), headers);
//        if (result != null) {
//            int ndx = result.lastIndexOf('/');
//            return result.substring(ndx+1);
//        }
        
        return null;
    }

    static String createQuiz(Integer aamId, String collId) {
        String quizId = null;

        for (QuizModel quizM : quizDetails.getOrDefault(aamId, Collections.emptyList())) {
            String newQuizId = createQuiz(aamId, quizId, collId, quizM.language, quizM.region, quizM.question);
            if (quizId == null) {
                quizId = newQuizId;
            }
            quizM.quizId = quizId;
        }

        return quizId;
    }

    static String createQuiz(int aamId, String id, String collId, String lang, String region, String question) {
        Map<String, String> headers = Collections.singletonMap("Accept-Language", lang);

        JsonNode quiz = JsonUtility.emptyNode();
        JsonUtility.addField(quiz, "id", id);
        JsonUtility.addField(quiz, "externalId", "dummy");
        JsonUtility.addField(quiz, "name", question);
        JsonUtility.addField(quiz, "description", question);
        JsonUtility.addField(quiz, "language", lang);
        JsonUtility.addField(quiz, "region", region);
        JsonUtility.addField(quiz, "placeRepId", 1);
        JsonUtility.addField(quiz, "answerType", typeCatSubcat.get(aamId)[0]);
        JsonUtility.addField(quiz, "category", typeCatSubcat.get(aamId)[1]);
        JsonUtility.addField(quiz, "subcategory", typeCatSubcat.get(aamId)[2]);
        JsonUtility.addField(quiz, "question", question);
        JsonUtility.addField(quiz, "collectionId", collId);
        JsonUtility.addField(quiz, "quizVisibility", "PUBLIC");
        JsonUtility.addField(quiz, "attribution", "Some old codger");
        JsonUtility.addField(quiz, "createUserId", "cis.user.MMMM-ABCD");
        JsonUtility.addField(quiz, "modifyUserId", "cis.user.MMMM-ABCD");

//        if (id == null) {
//            String result = HttpClientX.doPostJson(BASE_URL + "/quiz", quiz.toPrettyString(), headers);
//            if (result != null) {
//                System.out.println("POST.quiz: " + id + " --> " + result);
//                int ndx = result.lastIndexOf('/');
//                return result.substring(ndx+1);
//            }
//        } else {
//            String result = HttpClientX.doPutJson(BASE_URL + "/quiz/" + id, quiz.toPrettyString(), headers);
//            System.out.println("PUT.quiz: " + id + " --> " + result);
//        }

        return null;
    }

    /**
     * @param aamId "All About Me" quiz identifier
     * @param collId collection identifier
     * @return
     */
    static Set<String> createItems(Integer aamId, String collId) {
        Map<Integer, String> idByYear = new HashMap<>();

        for (Map.Entry<String, List<QuizAnswerModel>> entry : answerDetails.get(aamId).entrySet()) {
            Map<String, String> headers = Collections.singletonMap("Accept-Language", entry.getKey());

            for (QuizAnswerModel quizM : entry.getValue()) {
                String id = idByYear.getOrDefault(quizM.year, null);

                JsonNode item = JsonUtility.emptyNode();
                JsonUtility.addField(item, "id", id);
                JsonUtility.addField(item, "type", typeCatSubcat.get(aamId)[0]);
                JsonUtility.addField(item, "name", quizM.answer);
                JsonUtility.addField(item, "language", quizM.language);
                JsonUtility.addField(item, "externalId", "dummy");
                JsonUtility.addField(item, "locale", quizM.region);
                JsonUtility.addField(item, "placeRepId", 1);
                JsonUtility.addField(item, "category", typeCatSubcat.get(aamId)[1]);
                JsonUtility.addField(item, "subcategory", typeCatSubcat.get(aamId)[2]);
                JsonUtility.addField(item, "startYear", quizM.year);
                JsonUtility.addField(item, "summary", quizM.summary);
                JsonUtility.addField(item, "description", makeFormattedData(quizM.getDescription()));
                JsonUtility.addField(item, "collectionId", collId);
                JsonUtility.addField(item, "image", quizM.image);
                JsonUtility.addField(item, "visibility", "PUBLIC");
                JsonUtility.addField(item, "attribution", "Some old codger");
                JsonUtility.addField(item, "createUserId", "cis.user.MMMM-ABCD");
                JsonUtility.addField(item, "modifyUserId", "cis.user.MMMM-ABCD");

                String newId = saveItem(id, item, headers);
                if (id == null  &&  newId != null) {
                    id = newId;
                    idByYear.put(quizM.year, newId);
                }
                quizM.itemId = id;
            }
        }

        return new HashSet<>(idByYear.values());
    }

    static void addItemsToQuiz(String quizId, Set<String> itemIds) throws Exception {
        Map<String, String> headers = Collections.singletonMap("Accept-Language", "en");

        String itemIdsJson = itemIds.stream().map(id -> '"' + id + '"').collect(Collectors.joining(", ", "[ ", " ]"));
        JsonNode idNode = JsonUtility.parseJson(itemIdsJson);

        JsonNode itemIdNode = JsonUtility.emptyNode();
        JsonUtility.addField(itemIdNode, "size", itemIds.size());
        JsonUtility.addField(itemIdNode, "itemIds", idNode);
        System.out.println(itemIdNode.toPrettyString());

//        HttpClientX.doPostJson(BASE_URL + "/quiz/" + quizId + "/item", itemIdNode.toPrettyString(), headers);
    }

    static String saveItem(String id, JsonNode item, Map<String, String> headers) {
//        if (id == null) {
//            String result = HttpClientX.doPostJson(BASE_URL + "/item", item.toPrettyString(), headers);
//            if (result != null) {
//                int ndx = result.lastIndexOf('/');
//                return result.substring(ndx+1);
//            }
//        } else {
//            String result = HttpClientX.doPutJson(BASE_URL + "/item/EVENT/" + id, item.toPrettyString(), headers);
//        }
//
        return null;
    }

    static JsonNode makeFormattedData(List<String> rawText) {
        List<JsonNode> content = rawText.stream()
                                        .map(txt -> {
                                            JsonNode node = JsonUtility.emptyNode();
                                            JsonUtility.addField(node, "style", "HTML");
                                            JsonUtility.addField(node, "text", txt);
                                            return node;
                                        })
                                        .collect(Collectors.toList());

        JsonNode fmtStr = JsonUtility.emptyNode();
        JsonUtility.addArray(fmtStr, "content", content);

        JsonNode descr = JsonUtility.emptyNode();
        JsonUtility.addArray(descr, "formattedString", Arrays.asList(fmtStr));

        return descr;
    }
}
