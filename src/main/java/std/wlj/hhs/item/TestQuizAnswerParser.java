/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.item;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.familysearch.homelands.admin.parser.model.ItemModel;
import org.familysearch.homelands.admin.parser.quiz.DiscoveryQuizItemParser;

/**
 * @author wjohnson000
 *
 */
public class TestQuizAnswerParser {

    private static final String filePathAnswer   = "C:/D-drive/homelands/AAM/quizAnswers.csv";

    public static void main(String...args) throws Exception {
        byte[] rawDataAnswer = Files.readAllBytes(Paths.get(filePathAnswer));
        DiscoveryQuizItemParser iParser = new DiscoveryQuizItemParser();
        List<ItemModel> items = iParser.parse(rawDataAnswer);
        items.forEach(item -> System.out.println(
                  item.getId() + "|" + item.getExternalId() + "|" + item.getLanguage() + "|" +
                  item.getStartYear() + "|" + item.getTitle() + "|" + item.getType() + "|" +
                  item.getCategory() + "|" + item.getSubcategory() + "|" + item.getBody())); 

        System.exit(0);
    }
}
