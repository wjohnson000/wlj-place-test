/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wjohnson000
 *
 */
public class TextCleaner {

    // Order the transformations just to be safe
    private static Map<String, String> singlePass = new LinkedHashMap<>();
    private static Map<String, String> multiPass  = new LinkedHashMap<>();

    static {
        singlePass.put("\n\r", "<br/>");
        singlePass.put("\r\n", "<br/>");
        singlePass.put("\n", "<br/>");
        singlePass.put("\r", "<br/>");
        singlePass.put("\"", "&quot;");

        multiPass.put("<br/><br/>", "<p/>");
        multiPass.put("<br/><p/>", "<p/>");
        multiPass.put("<p/><br/>", "<p/>");
        multiPass.put("<p/><p/>", "<p/>");
    }

    /**
     * Replace certain formatting (new lines, etc) with HTML tags.
     * 
     * @param text unclean text
     * @return cleansed text
     */
    public String cleanse(String text) {
        String temp = text;

        // Replaces all occurrences of one String with another
        for (Map.Entry<String, String> entry : singlePass.entrySet()) {
            temp = temp.replace(entry.getKey(), entry.getValue());
        }

        // Iteratively replaces all occurrences of one String with another, since
        for (Map.Entry<String, String> entry : multiPass.entrySet()) {
            boolean again = true;
            int len = temp.length();
            while (again) {
                temp = temp.replace(entry.getKey(), entry.getValue());
                again = (len != temp.length());
                len = temp.length();
            }
        }
        
        return temp;
    }
}
