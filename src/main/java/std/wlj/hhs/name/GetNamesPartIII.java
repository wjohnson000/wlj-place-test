/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.familysearch.homelands.importer.common.JsonUtility;
import org.familysearch.homelands.importer.names.process.NameServiceHelper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * @author wjohnson000
 *
 */
public class GetNamesPartIII {

    static String BETA_URL = "http://core.homelands.service.beta.us-east-1.test.fslocal.org";

    public static void main(String... args) throws Exception {
        List<String> names = Files.readAllLines(Paths.get("C:/temp/all-names-hhs.csv"), StandardCharsets.UTF_8);
        List<String> nameData = new ArrayList<>();

        ScheduledExecutorService service = NameServiceHelper.getExecutor();
        for (String name : names) {
            service.execute(() -> {
                String[] chunks = name.split(",");
                JsonNode nameNode = NameServiceHelper.readName(BETA_URL, chunks[2], "");
                if (nameNode == null) {
                    System.out.println(">>>> Missing: " + chunks[2]);
                } else {
                    String type = JsonUtility.getStringValue(nameNode, "type");
                    String collId = JsonUtility.getStringValue(nameNode, "collectionId");
                    JsonNode variantsNode = JsonUtility.getJsonNode(nameNode, "variants");
                    if (variantsNode != null) {
                        Map<String, JsonNode> variantsMap = JsonUtility.getAllNodes(variantsNode);
                        for (Map.Entry<String, JsonNode> entry : variantsMap.entrySet()) {
                            for (JsonNode varNode : entry.getValue()) {
                                String varName = JsonUtility.getStringValue(varNode, "name");
                                String varNameId = JsonUtility.getStringValue(varNode, "nameId");
                                if (varNameId == null  ||  varNameId.trim().isEmpty()) {
                                    nameData.add(varName + "," + type + ",," + collId + "," + chunks[2]);
                                }
                            }
                        }
                    }
                }
            });
        }

        service.shutdown();
        service.awaitTermination(60, TimeUnit.MINUTES);

        Files.write(Paths.get("C:/temp/variant-names-data.csv"), nameData, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.exit(0);
    }
}
