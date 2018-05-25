/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class ZzzFindDups {

    private static final String CJK_CALENDAR_FILENAME = "/std/wlj/date/v2/imperial_zh.xml";

    public static void main(String... args) {
        ZzzFindDups engine = new ZzzFindDups();
        engine.init();
        System.exit(0);
    }

    void init() {
        Map<String, Integer> reignData = new TreeMap<>();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser;

        try {
            Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(CJK_CALENDAR_FILENAME), Charset.forName("UTF-8"));
            parser = factory.createXMLStreamReader(reader);

            int lineno = 0;
            String metadata = "";
            boolean stillReading = true;

            while (stillReading) {
                lineno++;
                switch (parser.next()) {
                case XMLStreamConstants.END_DOCUMENT:
                    parser.close();
                    reader.close();
                    stillReading = false;
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    metadata = "";
                    if (parser.getLocalName().equals("word")) {
                        metadata = parser.getAttributeValue(null, "meta");
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    String chars = parser.getText();
                    if (! chars.trim().isEmpty()  &&  metadata != null) {
                        String[] chunks = PlaceHelper.split(metadata, '|');
//                        System.out.println("  CHARS::" + chars + " --> " + lineno + " . " + chunks[0] + " . " + chunks[1]);
                        String key = chars + "." + chunks[1];
                        if (reignData.containsKey(key)) {
                            System.out.println("Duplicate ... " + key + " -> " + reignData.get(key) + " vs. " + lineno);
                        } else {
                            reignData.put(key, lineno);
                        }
                    }
                    break;
                }
            }
        } catch (XMLStreamException | IOException e) {
            System.out.println("Oops ... " + e.getClass().getName() + " --> " + e.getMessage());
        }
    }

}
