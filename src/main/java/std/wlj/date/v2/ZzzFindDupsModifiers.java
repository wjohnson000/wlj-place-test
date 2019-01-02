/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Find duplicate entries in the "modifier-dict.xml" file ... duplicate meaning the same "lang"
 * and "type" attributes and the same value.
 * @author wjohnson000
 *
 */
public class ZzzFindDupsModifiers {

    private static final String CJK_MODIFIER_FILENAME = "/org/familysearch/standards/date/shared/modifier-dict.xml";

    public static void main(String... args) {
        ZzzFindDupsModifiers engine = new ZzzFindDupsModifiers();
        engine.init();
        System.exit(0);
    }

    void init() {
        Set<String> entries = new TreeSet<>();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser;

        try {
            Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(CJK_MODIFIER_FILENAME), StandardCharsets.UTF_8);
            parser = factory.createXMLStreamReader(reader);

            String lang = "";
            String type = "";
            String meta = "";
            
            boolean stillReading = true;
            while (stillReading) {
                switch (parser.next()) {
                case XMLStreamConstants.END_DOCUMENT:
                    parser.close();
                    reader.close();
                    stillReading = false;
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    lang = "";
                    meta = "";
                    type = "";
                    if (parser.getLocalName().equals("word")) {
                        lang = parser.getAttributeValue(null, "lang");
                        meta = parser.getAttributeValue(null, "meta");
                        type = parser.getAttributeValue(null, "type");
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    String chars = parser.getText();
                    if (! chars.trim().isEmpty()) {
                        String key = lang + "." + type + "." + chars;
                        if (entries.contains(key)) {
                            System.out.println("DUP: " + key + " -> " + meta);
                        } else {
                            entries.add(key);
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
