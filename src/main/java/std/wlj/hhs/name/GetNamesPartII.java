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
public class GetNamesPartII {

    static String BETA_URL = "http://core.homelands.service.beta.us-east-1.test.fslocal.org";

    public static void main(String... args) throws Exception {
        List<String> names = Files.readAllLines(Paths.get("C:/temp/all-names.txt"), StandardCharsets.UTF_8);
        List<String> nameData = new ArrayList<>();

        ScheduledExecutorService service = NameServiceHelper.getExecutor();
        for (String name : names) {
            service.execute(() -> {
                ArrayNode namesNode = NameServiceHelper.searchName(BETA_URL, name);
                if (namesNode == null  ||  namesNode.size() == 0) {
                    nameData.add(name + ",,,");
                } else {
                    for (JsonNode nameNode : namesNode) {
                        String nameId = JsonUtility.getStringValue(nameNode, "id");
                        String collId = JsonUtility.getStringValue(nameNode, "collectionId");
                        String nType  = JsonUtility.getStringValue(nameNode, "type");
                        StringBuilder buff = new StringBuilder();
                        buff.append(name);
                        buff.append(",").append(nType);
                        buff.append(",").append(nameId);
                        buff.append(",").append(collId);
                        nameData.add(buff.toString());
                        System.out.println(buff.toString());
                    }
                }
            });
        }

        service.shutdown();
        service.awaitTermination(60, TimeUnit.MINUTES);

        Files.write(Paths.get("C:/temp/all-names-data.csv"), nameData, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.exit(0);
    }
}
