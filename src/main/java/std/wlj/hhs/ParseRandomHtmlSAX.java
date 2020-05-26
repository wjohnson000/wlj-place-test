/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs;

import java.io.ByteArrayInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 * @author wjohnson000
 *
 */
public class ParseRandomHtmlSAX {

    private static XMLInputFactory XML_FACTORY = XMLInputFactory.newInstance();

    static String[] tests = {
        "<h2>The Head</h2><p>Paragraph one.</p><p>Paragraph <i>two</i></p>",
        "<h2>The Head<p>Paragraph one.<p>Paragraph two</p><ul><li>one</li><li>two</ul>",
    };

    public static void main(String...args) {
        XML_FACTORY.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);

        for (String test : tests) {
            System.out.println("===========================================================");
            System.out.println("HTML: " + test);

            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(test.getBytes());
                XMLStreamReader parser = XML_FACTORY.createXMLStreamReader(bais, "UTF-8");

                boolean more = true;
                while (more) {
                    int event = parser.next();
                    switch(event) {
                    case XMLStreamConstants.END_DOCUMENT:
                        more = false;
                        break;

                    case XMLStreamConstants.START_ELEMENT:
                        System.out.println("Start: " + parser.getLocalName());
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        System.out.println("End: " + parser.getLocalName());
                        break;

                    case XMLStreamConstants.CHARACTERS:
                    }

                }
            } catch(Exception ex) {
                System.out.println("Oops: " + ex.getMessage());
            }
        }
    }
}
