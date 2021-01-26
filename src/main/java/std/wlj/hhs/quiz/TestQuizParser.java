/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.quiz;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.familysearch.homelands.admin.parser.model.ItemModel;
import org.familysearch.homelands.admin.parser.model.QuizModel;
import org.familysearch.homelands.admin.parser.quiz.DiscoveryQuizItemParser;
import org.familysearch.homelands.admin.parser.quiz.DiscoveryQuizParser;

/**
 * @author wjohnson000
 *
 */
public class TestQuizParser {

    private static final String filePathQuestion = "C:/D-drive/homelands/AAM/quizQuestions.csv";
    private static final String filePathAnswer   = "C:/D-drive/homelands/AAM/quizAnswers.csv";

    public static void main(String...args) throws Exception {
        byte[] rawDataQuestion = Files.readAllBytes(Paths.get(filePathQuestion));
        DiscoveryQuizParser qParser = new DiscoveryQuizParser();
        List<QuizModel> quizzes = qParser.parse(rawDataQuestion);
        quizzes.forEach(quiz -> System.out.println(
                  quiz.getId() + "|" + quiz.getExternalId() + "|" + quiz.getLanguage() + "|" +
                  quiz.getTitle() + "|" + quiz.getAnswerType() + "|" +
                  quiz.getCategory() + "|" + quiz.getSubcategory())); 

        byte[] rawDataAnswer = Files.readAllBytes(Paths.get(filePathAnswer));
        DiscoveryQuizItemParser iParser = new DiscoveryQuizItemParser();
        List<ItemModel> items = iParser.parse(rawDataAnswer);
        items.forEach(item -> System.out.println(
                  item.getId() + "|" + item.getExternalId() + "|" + item.getLanguage() + "|" +
                  item.getStartYear() + "|" + item.getTitle() + "|" + item.getType() + "|" +
                  item.getCategory() + "|" + item.getSubcategory() + "|" + item.getBody())); 
    }
}
