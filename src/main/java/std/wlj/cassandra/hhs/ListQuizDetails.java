/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import std.wlj.cassandra.hhs.model.CollectionData;
import std.wlj.cassandra.hhs.model.QuizData;

/**
 * @author wjohnson000
 *
 */
public class ListQuizDetails {

    public static void main(String... args) {
        CassandraHelper cHelper = new CassandraHelper();
        List<CollectionData> collData = cHelper.getCollections(false);
        Map<String, CollectionData> collMap = collData.stream().collect(Collectors.toMap(cc -> cc.id, cc -> cc));

        List<QuizData> quizData = cHelper.getQuizzes(true);

        for (QuizData qData : quizData) {
            System.out.println("\n====================================================================");
            if (! collMap.containsKey(qData.collectionId)) {
                System.out.println("DELETE " + qData.id + " --> " + cHelper.deleteItem(qData.id, "QUIZ"));
            } else {
                System.out.println("  ID: " + qData.id);
                System.out.println("coll: " + qData.collectionId + " --> " + collMap.containsKey(qData.collectionId));
                System.out.println("titl: " + qData.title);
                System.out.println("ques: " + qData.question);
                System.out.println("itag: " + qData.quizItemTag);
                System.out.println("catg: " + qData.category);
                System.out.println("subc: " + qData.subcategory);
                System.out.println("lang: " + qData.languages);
                System.out.println("user: " + qData.modifyUser);
                System.out.println("date: " + qData.modifyDate);
            }
        }

        System.exit(0);
    }
}
