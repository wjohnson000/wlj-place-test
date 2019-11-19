/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.json;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;


/**
 * Utility class (hence "abstract") with methods for managing json data using "fasterxml" jackson
 * classes.
 * 
 * @author wjohnson000
 *
 */
public abstract class JsonUtility {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode parseJson(String jsonString) throws Exception {
        if (jsonString == null  ||  jsonString.trim().isEmpty()) {
            throw new Exception("Malformed input -- unable to process!!");
        }

        try {
            return mapper.readTree(jsonString);
        } catch (IOException e) {
            throw new Exception("Malformed input -- unable to process!!");
        }
    }

    public static void addField(JsonNode jsonNode, String key, String value) {
        ((ObjectNode)jsonNode).put(key, value);
    }

    public static void addField(JsonNode jsonNode, String key, int value) {
        ((ObjectNode)jsonNode).put(key, value);
    }

    public static void addField(JsonNode jsonNode, String key, double value) {
        ((ObjectNode)jsonNode).put(key, value);
    }

    public static void addField(JsonNode jsonNode, String key, boolean value) {
        ((ObjectNode)jsonNode).put(key, value);
    }

    public static void addArray(JsonNode jsonNode, String key, String... value) {
        ArrayNode arrayNode = mapper.createArrayNode();
        Arrays.stream(value).forEach(val -> arrayNode.add(val));
        ((ObjectNode)jsonNode).set(key, arrayNode);
    }

    public static String getStringValue(JsonNode jsonNode, String key) {
        JsonNode node = jsonNode.get(key);
        if (node == null) {
            return null;
        } else if (node instanceof TextNode) {
            return ((TextNode)node).asText();
        } else {
            return node.asText();
        }
    }

    public static Integer getIntValue(JsonNode jsonNode, String key) {
        JsonNode node = jsonNode.get(key);
        if (node != null  &&  node instanceof IntNode) {
            return ((IntNode)node).intValue();
        } else {
            return null;
        }
    }

    public static Double getDoubleValue(JsonNode jsonNode, String key) {
        JsonNode node = jsonNode.get(key);
        if (node != null  &&  node instanceof DoubleNode) {
            return ((DoubleNode)node).doubleValue();
        } else {
            return null;
        }
    }

    public static Boolean getBooleanValue(JsonNode jsonNode, String key) {
        JsonNode node = jsonNode.get(key);
        if (node != null  &&  node instanceof BooleanNode) {
            return ((BooleanNode)node).booleanValue();
        } else {
            return null;
        }
    }

    public static String[] getArrayValue(JsonNode jsonNode, String key) {
        JsonNode node = jsonNode.get(key);
        if (node != null  &&  node instanceof ArrayNode) {
            ArrayNode aNode = (ArrayNode)node;
            String[] results = new String[aNode.size()];
            for (int i=0;  i<results.length;  i++) {
                results[i] = aNode.get(i).toString();
            }
            return results;
        } else {
            return null;
        }
    }
}
