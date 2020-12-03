/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs;

import java.util.Collections;
import java.util.List;

import org.familysearch.homelands.admin.parser.helper.JsonUtility;

import com.fasterxml.jackson.databind.JsonNode;

import std.wlj.ws.rawhttp.HttpClientX;

/**
 * @author wjohnson000
 *
 */
public class ReadAllCollections {

    protected static final String PROD_URL = "http://core.homelands.service.prod.us-east-1.prod.fslocal.org";
    protected static final String BETA_URL = "http://core.homelands.service.beta.us-east-1.test.fslocal.org";
    protected static final String INT_URL  = "http://core.homelands.service.integ.us-east-1.dev.fslocal.org";
    protected static final String DEV_URL  = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org";

    public static void main(String...args) throws Exception {
        String json = HttpClientX.doGetJSON(PROD_URL + "/collection", Collections.singletonMap("Accept-Language", "en"));
        JsonNode collNode = JsonUtility.parseJson(json);
        List<JsonNode> colls = JsonUtility.getArrayValueAsNodes(collNode, "collections");
        colls.forEach(ReadAllCollections::printCollData);
    }

    static void printCollData(JsonNode node) {
        System.out.println("ID: " + JsonUtility.getStringValue(node, "id"));
        System.out.println("  name: " + JsonUtility.getStringValue(node, "name"));
        System.out.println("  lang: " + JsonUtility.getStringValue(node, "originLanguage"));
        System.out.println("  prio: " + JsonUtility.getJsonNode(node, "priority"));
    }
}
