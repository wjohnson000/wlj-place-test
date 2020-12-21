/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

/**
 * @author wjohnson000
 *
 */
public class ParseRandomHtmlDOM {

    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

    static String[] tests = {
        "<h2>The Head</h2><p>Paragraph one.</p><p>Paragraph two</p>",
        "<h2>The Head<p>Paragraph one.<p>Paragraph two</p><ul><li>one</li><li>two</ul>",
    };

    public static void main(String...args) {
        dbFactory.setValidating(false);
        dbFactory.setIgnoringComments(true);
 
        for (String test : tests) {
            try {
                System.out.println("===========================================================");
                System.out.println("HTML: " + test);

                ByteArrayInputStream bais = new ByteArrayInputStream(test.getBytes());
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(bais);
            } catch(Exception ex) {
                System.out.println("Oops: " + ex.getMessage());
            }
        }
    }
}
