/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin;

import java.util.HashMap;
import java.util.Map;

import org.familysearch.homelands.lib.common.util.JsonUtility;

import com.fasterxml.jackson.databind.JsonNode;
import std.wlj.hhs.model.NameResource;
import std.wlj.ws.rawhttp.HttpClientX;

/**
 * @author wjohnson000
 *
 */
public class CompareCollections {

    private static final String BASE_URL = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org";
    private static final String authToken = "7537d60c-b257-4260-a179-833b725af69a-integ";

    private static Map<String, String> headers = new HashMap<>();
    static {
        headers.put("Authorization", "Bearer " + authToken);
        headers.put("Accept-Language", "en");
    }

    static String[][] names = {
        {"Adaline",   "First"},
        {"Adamina",   "First"},
        {"Bridget",   "First"},
        {"Denis",     "First"},
        {"Elizabeth", "First"},
        {"Harriatt",  "First"},
        {"Joshua",    "First"},
        {"Matilda",   "First"},
        {"Phillis",   "First"},
        {"Randolph",  "First"},
        {"Roger",     "First"},
        {"Terry",     "First"},
        {"Zenobia",   "First"},
 
        {"Adamina",   "Last"},
        {"Bosek",     "Last"},
        {"Coke",      "Last"},
        {"Fell",      "Last"},
        {"Jamieson",  "last"},
        {"Kynett",    "Last"},
        {"Muir",      "Last"},
        {"Randolph",  "Last"},
        {"Storck",    "Last"},
        {"Warnig",    "Last"},
        {"Woodruff",  "Last"},
    };

    public static void main(String...args) throws Exception {
        String collId01 = "MMM9-X7D";
        String collId02 = "MMMM-L3N";

        for (String[] name : names) {
            NameResource one = retrieveName(name[0], name[1], collId01);
            NameResource two = retrieveName(name[0], name[1], collId02);
            compareNames(name[0], one, two);
        }
    }

    static NameResource retrieveName(String text, String type, String collId) throws Exception {
        String json = HttpClientX.doGetJSON(BASE_URL + "/name?text=" + text + "&type=" + type + "&collection=" + collId, headers);
        if (json == null) {
            return null;
        }

        JsonNode node = JsonUtility.parseJson(json);
        return JsonUtility.createObject(node, NameResource.class);
    }

    static void compareNames(String text, NameResource name01, NameResource name02) {
        System.out.println("\n=== " + text + " ====================================================================");
        if (name01 == null  &&  name02 == null) {
            System.out.println("Both are null ...");
        } else if (name01 == null) {
            System.out.println("Name01 is null");
            System.out.println("ID.02=" + name02.getId());
            System.out.println("nm.02=" + name02.getName());
            System.out.println("tt.02=" + name02.getType());
            System.out.println("df.02=" + name02.getHtmlDefinition());
            name02.getVariants().entrySet().forEach(vv -> System.out.println("vv.02=" + vv));
        } else if (name02 == null) {
            System.out.println("Name02 is null");
            System.out.println("ID.01=" + name01.getId());
            System.out.println("nm.01=" + name01.getName());
            System.out.println("tt.01=" + name01.getType());
            System.out.println("df.01=" + name01.getHtmlDefinition());
            name01.getVariants().entrySet().forEach(vv -> System.out.println("vv.01=" + vv));
        } else {
            System.out.println("ID.01=" + name01.getId());
            System.out.println("ID.02=" + name02.getId());
            System.out.println("nm.01=" + name01.getName());
            System.out.println("nm.02=" + name02.getName());
            System.out.println("tt.01=" + name01.getType());
            System.out.println("tt.02=" + name02.getType());
            System.out.println("df.01=" + name01.getHtmlDefinition());
            System.out.println("df.02=" + name02.getHtmlDefinition());
            name01.getVariants().entrySet().forEach(vv -> System.out.println("vv.01=" + vv));
            name02.getVariants().entrySet().forEach(vv -> System.out.println("vv.02=" + vv));
        }
    }
}
