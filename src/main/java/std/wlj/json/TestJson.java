/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.json;

import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author wjohnson000
 *
 */
public class TestJson {

    public static void main(String...args) throws Exception {
        JsonNode node = JsonUtility.parseJson(makeJson("id", "123-abc", "type", "EVENT"));
        System.out.println("JS: " + node);

        JsonUtility.addField(node, "iii", 123);
        System.out.println("JS: " + node);

        JsonUtility.addField(node, "ddd", 45.678);
        System.out.println("JS: " + node);

        JsonUtility.addField(node, "bbb", true);
        System.out.println("JS: " + node);

        JsonUtility.addArray(node, "list", "one", "two", "three", "four", "five");
        System.out.println("JS: " + node);

        System.out.println();
        System.out.println(JsonUtility.getStringValue(node, "id"));
        System.out.println(JsonUtility.getStringValue(node, "type"));
        System.out.println(JsonUtility.getIntValue(node, "iii"));
        System.out.println(JsonUtility.getDoubleValue(node, "ddd"));
        System.out.println(JsonUtility.getBooleanValue(node, "bbb"));
        System.out.println(Arrays.toString(JsonUtility.getArrayValue(node, "list")));
    }

    private static String makeJson(String... kvPairs) {
        StringBuilder buff = new StringBuilder();
        buff.append("{ ");

        for (int i=0;  i<kvPairs.length;  i+=2) {
            if (buff.length() > 2) {
                buff.append(", ");
            }
            buff.append('"').append(kvPairs[i]).append('"').append(": ");
            buff.append('"').append(kvPairs[i+1]).append('"');
        }

        buff.append(" }");

        return buff.toString();
    }
}
