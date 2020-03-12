/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.json;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.familysearch.homelands.svc.exception.ItemServiceBadRequestException;
import org.familysearch.homelands.svc.exception.ItemServiceException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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

    /** Formatter for create-date, modify-date */
    private static final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

    private JsonUtility() {
        // Private constructor hides the implicit public one
    }

    public static JsonNode emptyNode() throws ItemServiceException {
        return mapper.createObjectNode();
    }

    public static JsonNode parseJson(String jsonString) throws ItemServiceException {
        if (jsonString == null  ||  jsonString.trim().isEmpty()) {
            return null;
        }

        try {
            return mapper.readTree(jsonString);
        } catch (IOException e) {
            throw new ItemServiceBadRequestException("Malformed input -- unable to process!!");
        }
    }

    public static String prettyPrint(JsonNode jsonNode) {
        try {
            return writer.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            return jsonNode.toString();
        }
    }

    public static Map<String, String> getAllFields(String jsonString) {
        try {
            JsonNode jsonNode = JsonUtility.parseJson(jsonString);
            return getAllFields(jsonNode);
        } catch(ItemServiceException ex) {
            return Collections.emptyMap();
        }
    }

    public static Map<String, String> getAllFields(JsonNode jsonNode) {
        Map<String, String> results = new HashMap<>();
        jsonNode.fields().forEachRemaining(child ->
            results.put(child.getKey(), child.getValue().asText()));

        return results;
    }

    public static Map<String, JsonNode> getAllNodes(JsonNode jsonNode) {
        Map<String, JsonNode> results = new HashMap<>();
        jsonNode.fields().forEachRemaining(child ->
            results.put(child.getKey(), child.getValue()));

        return results;
    }

    public static void addField(JsonNode jsonNode, String key, String value) {
        if (value != null  &&  ! value.isEmpty()) {
            ((ObjectNode)jsonNode).put(key, value);
        }
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
    
    public static void addField(JsonNode jsonNode, String key, LocalDate date) {
        ((ObjectNode)jsonNode).put(key, formatDate(date));
    }
    
    public static void addField(JsonNode jsonNode, String key, JsonNode value) {
        ((ObjectNode)jsonNode).set(key, value);
    }

    public static void addArray(JsonNode jsonNode, String key, String... value) {
        ArrayNode arrayNode = mapper.createArrayNode();
        Arrays.stream(value).forEach(arrayNode::add);
        ((ObjectNode)jsonNode).set(key, arrayNode);
    }

    public static void mergeNodes(JsonNode toNode, JsonNode fromNode) {
        fromNode.fields().forEachRemaining(entry ->
            ((ObjectNode)toNode).set(entry.getKey(), entry.getValue()));
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
        if (node instanceof IntNode) {
            return ((IntNode)node).intValue();
        } else {
            return null;
        }
    }

    public static Double getDoubleValue(JsonNode jsonNode, String key) {
        JsonNode node = jsonNode.get(key);
        if (node instanceof DoubleNode) {
            return ((DoubleNode)node).doubleValue();
        } else {
            return null;
        }
    }

    public static Boolean getBooleanValue(JsonNode jsonNode, String key) {
        JsonNode node = jsonNode.get(key);
        if (node instanceof BooleanNode) {
            return ((BooleanNode)node).booleanValue();
        } else {
            return null;  // NOSONAR -- "null" indicates that the JsonNode doesn't contain the given key
        }
    }

    public static LocalDate getDateValue(JsonNode jsonNode, String key) {
        JsonNode node = jsonNode.get(key);
        if (node instanceof TextNode) {
            String dateStr = ((TextNode)node).asText();
            return getLocalDate(dateStr);
        } else {
            return null;
        }
    }

    public static JsonNode getJsonNode(JsonNode jsonNode, String key) {
        return jsonNode.get(key);
    }

    public static String[] getArrayValue(JsonNode jsonNode, String key) {
        JsonNode node = jsonNode.get(key);
        if (node instanceof ArrayNode) {
            ArrayNode aNode = (ArrayNode)node;
            String[] results = new String[aNode.size()];
            for (int i=0;  i<results.length;  i++) {
                results[i] = aNode.get(i).asText();
            }
            return results;
        } else {
            return null;  // NOSONAR -- "null" indicates that the JsonNode doesn't contain the given key
        }
    }

    /**
     * Create a {@link LocalDate} instance of the format "01 January 1999" from a text string.
     * 
     * @param dateString date string
     * @return associated LocalDate
     */
    public static LocalDate getLocalDate(String dateString) {
        if (dateString == null  ||  dateString.trim().isEmpty()) {
            return null;
        } else {
            try {
                return LocalDate.from(dateFmt.parse(dateString));
            } catch(DateTimeException ex) {
                return null;
            }
        }
    }

    /**
     * Format a {@link LocalDate} to a normalized date string (in English, sorry kids ...)
     * 
     * @param date date, or null
     * @return formatted date string
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        } else {
            return dateFmt.format(date);
        }
    }
}
