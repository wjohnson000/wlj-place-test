/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.maven.surefire.shade.org.apache.commons.io.IOUtils;
import org.familysearch.homelands.core.persistence.model.NameType;
import org.familysearch.homelands.core.persistence.util.JsonUtility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import std.wlj.ws.rawhttp.HttpClientX;

/**
 * Update variant names for OXFORD data
 * 
 * @author wjohnson000
 *
 */
public class LoadOxfordNamesUpdate {

    private static final String baseUrl = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org/";
    
    public static void main(String... args) throws Exception {
        Map<String, String> nameToIdMap = loadSavedIds();

        for (String id : nameToIdMap.values()) {
            JsonNode nameNode = readName(id);
            if (nameNode == null) {
                System.out.println("Unable to read: " + id);
            } else {
                JsonNode variantNode = JsonUtility.getJsonNode(nameNode, "variants");
                if (variantNode instanceof ArrayNode) {
                    boolean variantChanged = false;

                    ArrayNode variants = (ArrayNode)variantNode;
                    for (int i=0;  i<variants.size();  i++) {
                        JsonNode variant = variants.get(i);
                        String name = JsonUtility.getStringValue(variant, "name");
                        String varNameId = nameToIdMap.get(name);
                        if (varNameId != null) {
                            variantChanged = true;
                            JsonUtility.addField(variant, "nameId", varNameId);
                        }
                    }

                    if (variantChanged) {
                        System.out.println("PUT " + baseUrl + "/name/" + id);
                        HttpClientX.doPutJson(baseUrl + "/name/" + id, JsonUtility.prettyPrint(nameNode));
                    }
                }
            }
        }
    }

    static Map<String, String> loadSavedIds() throws Exception {
        List<String> rawData = IOUtils.readLines(LoadOxfordNamesUpdate.class.getResourceAsStream("oxford-refid-to-nameid.txt"), StandardCharsets.UTF_8);
        return rawData.stream()
                      .map(line -> line.split("\\|"))
                      .filter(row -> row.length == 3)
                      .collect(Collectors.toMap(val -> val[1], val -> val[2], (e, r) -> e));
    }

    static JsonNode readName(String id) throws Exception {
        String json = HttpClientX.doGetJSON(baseUrl + "name/" +  id);
        return JsonUtility.parseJson(json);
    }
}