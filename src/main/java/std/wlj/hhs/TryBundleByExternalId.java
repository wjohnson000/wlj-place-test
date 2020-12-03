/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.familysearch.homelands.admin.parser.helper.JsonUtility;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author wjohnson000
 *
 */
public class TryBundleByExternalId {

    static final String EXT_ID = "extId";
    static final Random random = new Random();

    public static void main(String...args) throws Exception {
        int extId = 1;

        List<JsonNode> nodes = new ArrayList<>();
        for (int i=0;  i<100;  i++) {
            JsonNode node = JsonUtility.emptyNode();
            JsonUtility.addField(node, "val", i);
            JsonUtility.addField(node, EXT_ID, "x." + extId);
            nodes.add(node);
            if (random.nextInt(9) < 2) {
                extId++;
            }
        }

        oldStyle(nodes);
        newStyle(nodes);
    }

    static void oldStyle(List<JsonNode> nodes) {
        AtomicInteger counter = new AtomicInteger();
        Map<Integer, List<JsonNode>> bundles =
                     nodes.stream()
                          .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / 10));

        bundles.entrySet().forEach(bb -> {
            System.out.println("\n=======================================================");
            System.out.println("KK: " + bb.getKey());
            bb.getValue().forEach(vv -> System.out.println("   O." + vv));
        });
    }

    static void newStyle(List<JsonNode> nodes) {
        List<JsonNode> currList = new ArrayList<>();
        List<List<JsonNode>> bundles = new ArrayList<>();
        bundles.add(currList);

        String prevExtId = String.valueOf(System.nanoTime());
        for (JsonNode node : nodes) {
            String extId = getExtId(node);
            if (currList.size() > 10  &&  ! prevExtId.equals(extId)) {
                currList = new ArrayList<>();
                bundles.add(currList);
            }
            currList.add(node);
            prevExtId = extId;
        }

        bundles.forEach(bb -> {
            System.out.println("\n=======================================================");
            bb.forEach(vv -> System.out.println("   N." + vv));
        });
    }

    static String getExtId(JsonNode item) {
        String extId = JsonUtility.getStringValue(item, EXT_ID);
        return (extId == null) ? String.valueOf(System.nanoTime()) : extId;
    }
}
