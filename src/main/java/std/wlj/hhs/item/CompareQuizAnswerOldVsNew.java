/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.item;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class CompareQuizAnswerOldVsNew {

    static final String BASE_DIR = "C:/temp";
    static final String OLD_FILE = "quiz-answer-item-old.csv";
    static final String NEW_FILE = "quiz-answer-item-new.csv";

    public static void main(String...args) throws Exception {
        Map<Integer, String[]> oldSample = getSample(OLD_FILE);
        Map<Integer, String[]> newSample = getSample(NEW_FILE);

        for (int id : oldSample.keySet()) {
            String[] oldData = oldSample.get(id);
            String[] newData = newSample.get(id);
            System.out.println("\n======================================================================");
            System.out.println("QuizID: " + id);
            System.out.println("4: " + oldData[4]);
            System.out.println(" : " + newData[4]);
            System.out.println("8: " + oldData[8]);
            System.out.println(" : " + newData[8]);
        }
    }

    static Map<Integer, String[]> getSample(String filename) throws Exception {
        Map<Integer, String[]> sampleData = new TreeMap<>();

        List<String> rows = Files.readAllLines(Paths.get(BASE_DIR, filename), StandardCharsets.UTF_8);
        for (String row : rows) {
            String[] fields = PlaceHelper.split(row, '|');
            if (fields.length > 5) {
                int ndx = fields[1].indexOf('-');
                if (ndx > 0) {
                    int quizid = Integer.parseInt(fields[1].substring(0, ndx));
                    if (! sampleData.containsKey(quizid)) {
                        sampleData.put(quizid, fields);
                    }
                }
            }
        }

        return sampleData;
    }
}
