/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.item;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.familysearch.homelands.admin.parser.helper.CSVUtility;
import org.familysearch.homelands.admin.parser.model.ItemModel;
import org.familysearch.homelands.admin.parser.transform.CanonicalCsvToItemTransformer;
import org.familysearch.homelands.admin.parser.transform.ItemToJsonTransformer;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author wjohnson000
 *
 */
public class TestItalyImport {

    static final String ITALY_PATH = "C:/temp/italy-timeline.csv";

    public static void main(String...args) throws Exception {
        byte[] rawBytes = FileUtils.readFileToByteArray(new File(ITALY_PATH));
        List<Map<String, String>> rowData = CSVUtility.loadCsvFileAsMaps(rawBytes);

        CanonicalCsvToItemTransformer itemTransformer = new CanonicalCsvToItemTransformer();
        List<ItemModel> items = itemTransformer.parse(rowData);

        ItemToJsonTransformer jsonTransformer = new ItemToJsonTransformer();
        List<JsonNode> jItems = jsonTransformer.transform(items, "MMMQ-YZJ");

        
    }
}
