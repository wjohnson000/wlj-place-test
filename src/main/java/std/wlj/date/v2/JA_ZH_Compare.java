/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author wjohnson000
 *
 */
public class JA_ZH_Compare {

    static class Word {
        static final String WORD_FMT_WITH_TYPE = "    <word lang=%s type=%s meta=%s>%s</word>%s";
        static final String WORD_FMT_NO_TYPE   = "    <word lang=%s meta=%s>%s</word>%s";

        String lang;
        String type;
        String meta;
        String value;
        String comment = "";

        public String toString() {
            if (type == null) {
                return String.format(WORD_FMT_NO_TYPE, lang, meta, value, comment);
            } else {
                return String.format(WORD_FMT_WITH_TYPE, lang, type, meta, value, comment);
            }
        }
    }

    static enum TTT {
        DYNASTY,
        EMPEROR,
        REIGN
    };

    private static String IMPERIAL_ZH_FILE = "/org/familysearch/standards/date/shared/imperial_zh.xml";
    private static String IMPERIAL_JA_FILE = "/org/familysearch/standards/date/shared/imperial_ja.xml";

    static Map<String, Set<TTT>> jaData = new TreeMap<>();
    static Map<String, Set<TTT>> zhData = new TreeMap<>();

    public static void main(String... args) throws Exception {
        JA_ZH_Compare engine = new JA_ZH_Compare();
        zhData = engine.doIt(true);
        jaData = engine.doIt(false);

        Set<String> dups = new TreeSet<>(zhData.keySet());
        dups.retainAll(jaData.keySet());
        dups.forEach(dup -> System.out.println(dup + "\t" + zhData.get(dup) + "\t" + jaData.get(dup)));
        
        System.exit(0);
    }

    Map<String, Set<TTT>> doIt(boolean isChinese) throws Exception {
        Map<String, Set<TTT>> results = new TreeMap<>();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser;

        try {
            Reader reader;
            if (isChinese ) {
                reader = new InputStreamReader(this.getClass().getResourceAsStream(IMPERIAL_ZH_FILE), StandardCharsets.UTF_8);
            } else {
                reader = new InputStreamReader(this.getClass().getResourceAsStream(IMPERIAL_JA_FILE), StandardCharsets.UTF_8);
            }
            parser = factory.createXMLStreamReader(reader);

            TTT ttt = null;
            boolean stillReading = true;

            while (stillReading) {
                switch (parser.next()) {
                case XMLStreamConstants.END_DOCUMENT:
                    parser.close();
                    reader.close();
                    stillReading = false;
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    if (parser.getLocalName().equals("word-group")) {
                        String type = parser.getAttributeValue(null, "type");
                        if ("dynasty".equals(type)) {
                            ttt = TTT.DYNASTY;
                        } else if ("emperor".equals(type)) {
                            ttt = TTT.EMPEROR;
                        } else if ("reign".equals(type)) {
                            ttt = TTT.REIGN;
                        } else {
                            System.out.println("Unknown 'type': " + type);
                        }
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    String chars = parser.getText().trim();
                    if (! chars.isEmpty()  &&  ttt != null) {
                        Set<TTT> ttts = results.get(chars);
                        if (ttts == null) {
                            ttts = new HashSet<>();
                            results.put(chars, ttts);
                        }
                        ttts.add(ttt);
                    }
                    break;
                }
            }
        } catch (XMLStreamException | IOException e) {
            System.out.println("Oops ... " + e.getClass().getName() + " --> " + e.getMessage());
        }

        return results;
    }
}
