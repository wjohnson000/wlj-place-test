/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.item;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.familysearch.homelands.admin.parser.model.QuizModel;
import org.familysearch.homelands.admin.parser.quiz.DiscoveryQuizParser;

/**
 * @author wjohnson000
 *
 */
public class TestQuizParser {

    private static final String filePathQuestion = "C:/D-drive/homelands/AAM/quizQuestions.csv";

    public static void main(String...args) throws Exception {
        byte[] rawDataQuestion = Files.readAllBytes(Paths.get(filePathQuestion));
        DiscoveryQuizParser qParser = new DiscoveryQuizParser();
        List<QuizModel> quizzes = qParser.parse(rawDataQuestion);
        quizzes.forEach(quiz -> System.out.println(
                  quiz.getId() + "|" + quiz.getExternalId() + "|" + quiz.getLanguage() + "|" +
                  quiz.getQuestion()+ "|" + quiz.getAnswerType() + "|" +
                  quiz.getCategory() + "|" + quiz.getSubcategory())); 

        System.exit(0);
    }
}
