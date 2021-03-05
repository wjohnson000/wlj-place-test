/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.internal.core.cql.PagingIterableSpliterator;
import com.fasterxml.jackson.databind.JsonNode;

import std.wlj.cassandra.hhs.model.CollectionData;
import std.wlj.cassandra.hhs.model.QuizData;

import org.familysearch.homelands.lib.common.util.JsonUtility;

/**
 * @author wjohnson000
 *
 */
public class CassandraHelper {

    final static String  selectCollections = "SELECT * FROM hhs.collectiondata";
    final static String  selectItems       = "SELECT * FROM hhs.item";
    final static String  selectNames       = "SELECT * FROM hhs.name";

    final static String  deleteItem        = "DELETE FROM hhs.item WHERE id = '%s' AND type= '%s'";
    final static String  deleteItemSearch  = "DELETE FROM hhs.item_search WHERE itemId = '%s'";

    private CqlSession cqlSession;

    public CassandraHelper() {
        CqlSession cqlSession = SessionUtilityAWS.connect();
        System.out.println("SESS: " + cqlSession);
        this.cqlSession = cqlSession;
    }

    public CassandraHelper(CqlSession cqlSession) {
        System.out.println("SESS: " + cqlSession);
        this.cqlSession = cqlSession;
    }

    public List<CollectionData> getCollections(boolean includeItemDetail) {
        List<CollectionData> results = new ArrayList<>();

        ResultSet rset = cqlSession.execute("SELECT * FROM hhs.collectiondata LIMIT 1000");
        for (Row row : rset) {
            CollectionData collData = new CollectionData();
            results.add(collData);

            collData.id    = row.getString("id");
            collData.name  = row.getString("name");
            collData.types.addAll(row.getSet("type", String.class));

            String details = row.getString("details");
            try {
                JsonNode node = JsonUtility.parseJson(details);
                collData.description = JsonUtility.getStringValue(node, "description");
                collData.languages.addAll(Arrays.asList(JsonUtility.getArrayValue(node, "availableLanguages")));
            } catch(Exception ex) {
                System.out.println("Unable to parse 'details': " + ex.getMessage());
            }
        }

        if (includeItemDetail) {
            addItemDetails(results);
        }

        return results;
    }

    public List<QuizData> getQuizzes(boolean includeDetails) {
        List<QuizData> results = new ArrayList<>();

        ResultSet rset = cqlSession.execute(" SELECT * FROM hhs.item_search WHERE solr_query = 'tags: quiz' LIMIT 2000");
        for (Row row : rset) {
            QuizData quizData = new QuizData();
            results.add(quizData);

            quizData.id           = row.getString("itemid");
            quizData.collectionId = row.getString("collectionid");
            quizData.category     = row.getString("category");
            quizData.subcategory  = row.getString("subcategory");
            quizData.languages.addAll(row.getList("languages", String.class));
        }

        if (includeDetails) {
            for (QuizData qData : results) {
                boolean done = false;
                rset = cqlSession.execute("SELECT * FROM hhs.item WHERE id = '" + qData.id + "' AND type = 'QUIZ'");
                for (Row row : rset) {
                    if (! done) {
                        done = true;
                        qData.title      = row.getString("title");
                        qData.modifyUser = row.getString("modifyuserid");
                        Instant modDate  = row.getInstant("modifydate");
                        if (modDate != null) {
                            qData.modifyDate = LocalDateTime.ofInstant(modDate, ZoneOffset.UTC);
                        }
                        try {
                            JsonNode dNode = JsonUtility.parseJson(row.getString("details"));
                            qData.quizItemTag = JsonUtility.getStringValue(dNode, "quizItemTag");
                            qData.externalId  = JsonUtility.getStringValue(dNode, "externalId");

                            Map<String, String> lNodes = row.getMap("langdetails", String.class, String.class);
                            for (Map.Entry<String, String> entry : lNodes.entrySet()) {
                                if (qData.question == null  ||  qData.question.trim().isEmpty()  ||  "en".equals(entry.getKey())) {
                                    JsonNode lNode = JsonUtility.parseJson(entry.getValue());
                                    qData.question = JsonUtility.getStringValue(lNode, "question");
                                }
                            }
                        } catch(Exception ex) {
                            System.out.println("EX: " + ex.getMessage());
                        }
                    }
                }
            }
        }

        return results;
    }

    public boolean deleteItem(String id, String type) {
        boolean delOK = true;

        try {
            String query = String.format(deleteItem, id, type);
            ResultSet rset = cqlSession.execute(query);
            delOK = rset.wasApplied();

            if (delOK) {
                query = String.format(deleteItemSearch, id);
                rset = cqlSession.execute(query);
                delOK = rset.wasApplied();
            }
        } catch(Exception ex) {
            System.out.println("Unable to delete id=" + id + "; type=" + type + " --> " + ex.getMessage());
        }

        return delOK;
    }

    /**
     * Go through the "hhs.item" and "hhs.name" tables and count how many rows are in each collection.
     * @param results
     */
    void addItemDetails(List<CollectionData> results) {
        Map<String, CollectionData> collMap = results.stream()
                                                     .collect(Collectors.toMap(cc -> cc.id, cc -> cc));

        ResultSet rset1 = cqlSession.execute(selectNames);
        StreamSupport.stream(PagingIterableSpliterator.builder(rset1).withChunkSize(1024).build(), true)
                            .forEach(row -> addNameCount(collMap, row));

        ResultSet rset2 = cqlSession.execute(selectItems);
        StreamSupport.stream(PagingIterableSpliterator.builder(rset2).withChunkSize(1024).build(), true)
                            .forEach(row -> addItemCount(collMap, row));
    }

    void addNameCount(Map<String, CollectionData> collMap, Row row) {
        String collId = row.getString("collectionid");
        CollectionData collData = collMap.get(collId);
        if (collData != null) {
            collData.memberCount++;
        }
    }

    void addItemCount(Map<String, CollectionData> collMap, Row row) {
        String details = row.getString("details");
        if (details != null) {
            try {
                JsonNode node = JsonUtility.parseJson(details);
                String collId = JsonUtility.getStringValue(node, "collectionId");
                CollectionData collData = collMap.get(collId);
                if (collData != null) {
                    collData.memberCount++;
                }
            } catch(Exception ex) {
                System.out.println("Unable to parse 'details': " + ex.getMessage());
            }
        } else {
            System.out.println("NO collectionId for Item: " + row.getString("id"));
        }
    }
}
