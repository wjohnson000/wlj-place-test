/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.quiz;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import org.familysearch.homelands.admin.parser.helper.CSVUtility;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class ParseQuizAnswersDoNotUse {

    static final String ANSWER_PARAM = "[answer]";
    static final String YEAR_PARAM   = "[year]";

    static String INPUT_PATH  = "C:/D-drive/homelands/AAM/quizAnswers.csv";
    static String OUTPUT_PATH = "C:/D-drive/homelands/AAM/quizAnswers-new.csv";

    static Set<String> ONE = new HashSet<>();
    static {
        ONE.add("ein");
        ONE.add("un");
        ONE.add("une");
    }

    public static void main(String...args) throws Exception {
//        System.out.println(">>" + replace("AABB", "AA", "CC") + "<<");
//        System.out.println(">>" + replace("AABB", "AB", "CC") + "<<");
//        System.out.println(">>" + replace("AABB", "BB", "CC") + "<<");
//        System.out.println(">>" + replace("AABB", "DD", "CC") + "<<");

        List<String> results = new ArrayList<>(10_000);
        byte[] rawBytes = Files.readAllBytes(Paths.get(INPUT_PATH));
        List<List<String>> rows = CSVUtility.loadCsvFile(rawBytes);

        for (List<String> row : rows) {
            if (row.size() > 4) {
                String   quizYr   = row.get(1);
                String[] options  = PlaceHelper.split(row.get(2), '|');
                String   answer   = null;
                String   ansText  = row.get(3);
                
                ansText = replace(ansText, quizYr, YEAR_PARAM);
                for (String option : options) {
                    ansText = replace(ansText, option, ANSWER_PARAM);
                    if (answer == null  &&  ansText.contains(ANSWER_PARAM)) {
                        answer = option;
                    }
                    if (option.endsWith("s")) {
                        ansText = replace(ansText, option.substring(0, option.length()-1), ANSWER_PARAM);
                        if (answer == null  &&  ansText.contains(ANSWER_PARAM)) {
                            answer = option;
                        }
                    }
                }
                if (answer == null) {
                    for (String one : ONE) {
                        ansText = replace(ansText, " " + one + " ", ANSWER_PARAM);
                        if (answer == null  &&  ansText.contains(ANSWER_PARAM)) {
                            answer = "1";
                        }
                    }
                }

                if (answer == null) {
                    answer = "???";
                }

                StringBuilder buff = new StringBuilder(256);
                buff.append(row.get(0));
                buff.append(",").append(quizYr);
                buff.append(",").append(quoteIt(row.get(2)));
                buff.append(",").append(quoteIt(answer));
                buff.append(",").append(quoteIt(ansText));
                for (int i=3;  i<row.size();  i++) {
                    buff.append(",").append(quoteIt(row.get(i)));
                }

                results.add(buff.toString());
                if (answer.equals("???")) {
                    System.out.println(buff.toString());
                }
            }
        }

        Files.write(Paths.get(OUTPUT_PATH), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static String replace(String input, String from, String to) {
        int ndx = input.toLowerCase().indexOf(from.toLowerCase());
        if (ndx == -1) {
            return input;
        } else if (ndx == 0) {
            return to + input.substring(from.length());
        } else if (ndx > 0  &&  ndx < input.length()+from.length()) {
            return input.substring(0, ndx) + to + input.substring(ndx + from.length());
        } else {
            return input.substring(0, ndx) + to;
        }
    }

    static String quoteIt(String input) {
        if (input.contains(",")) {
            return '"' + input + '"';
        } else {
            return input;
        }
    }
}
