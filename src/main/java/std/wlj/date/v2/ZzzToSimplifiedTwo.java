/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.familysearch.standards.core.lang.util.ChineseVariants;
import org.familysearch.standards.core.lang.util.TraditionalToSimplifiedChineseMapper;

/**
 * @author wjohnson000
 *
 */
public class ZzzToSimplifiedTwo {

    static class What {
        String text;
        String script;
        String sText;
        String sScript;
        String lang;
        String meta;
        String type;

        public String toString() {
            return text + "::" + script + "::" + sText + "::" + sScript + "::" + lang + "::" + type + "::" + meta;
        }
    }

    private static String[] ZH_FILES = {
        "/org/familysearch/standards/date/shared/imperial_zh.xml",
//        "/org/familysearch/standards/date/shared/modifier-dict.xml",
//        "/org/familysearch/standards/date/shared/month-dict.xml",
//        "/org/familysearch/standards/date/shared/numbers.xml",
    };

    private static TraditionalToSimplifiedChineseMapper mapper;

    public static void main(String... args) throws Exception {
        mapper = new TraditionalToSimplifiedChineseMapper();
        ZzzToSimplifiedTwo engine = new ZzzToSimplifiedTwo();
        engine.doIt();
        System.exit(0);
    }

    void doIt() throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser;

        String lang = "";
        String meta = "";
        String type = "";

        for (String zhFile : ZH_FILES) {
            Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(zhFile), Charset.forName("UTF-8"));
            parser = factory.createXMLStreamReader(reader);

            List<What> hani2hani = new ArrayList<>(1000);
            List<What> hant2hant = new ArrayList<>(1000);
            List<What> hans2hans = new ArrayList<>(1000);
            List<What> hant2hans = new ArrayList<>(1000);
            List<What> other = new ArrayList<>(1000);

            System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(zhFile);

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
                    String text = parser.getText();
                    if (! text.trim().isEmpty()  &&  (lang.startsWith("zh") || lang.startsWith("ja") || lang.startsWith("ko"))) {
                        What what = new What();
                        what.text = text;
                        what.script = String.valueOf(ChineseVariants.isTraditionalOrSimplified(what.text));
                        what.sText = mapper.mapTraditionalToSimplified(what.text);
                        what.sScript = String.valueOf(ChineseVariants.isTraditionalOrSimplified(what.sText));
                        what.lang = lang;
                        what.meta = meta;
                        what.type = type;

                        if ("Hani".equals(what.script) && "Hani".equals(what.sScript)) {
                            hani2hani.add(what);
                        } else if ("Hant".equals(what.script) && "Hant".equals(what.sScript)) {
                            hant2hant.add(what);
                        } else if ("Hans".equals(what.script) && "Hans".equals(what.sScript)) {
                            hans2hans.add(what);
                        } else if ("Hant".equals(what.script) && "Hans".equals(what.sScript)) {
                            hant2hans.add(what);
                        } else {
                            other.add(what);
                        }
                    }
                    break;
                }
            }

            System.out.println();
            System.out.println();
            System.out.println("---------------------------------------------------------------");
            System.out.println("HANI to HANI ...");
            System.out.println("---------------------------------------------------------------");
            hani2hani.forEach(System.out::println);

            System.out.println();
            System.out.println();
            System.out.println("---------------------------------------------------------------");
            System.out.println("HANT to HANT ...");
            System.out.println("---------------------------------------------------------------");
            hant2hant.forEach(System.out::println);

            System.out.println();
            System.out.println();
            System.out.println("---------------------------------------------------------------");
            System.out.println("HANS to HANS ...");
            System.out.println("---------------------------------------------------------------");
            hans2hans.forEach(System.out::println);

            System.out.println();
            System.out.println();
            System.out.println("---------------------------------------------------------------");
            System.out.println("HANT to HANS ...");
            System.out.println("---------------------------------------------------------------");
            hant2hans.forEach(System.out::println);

            System.out.println();
            System.out.println();
            System.out.println("---------------------------------------------------------------");
            System.out.println("HAN? to HAN? ...");
            System.out.println("---------------------------------------------------------------");
            other.forEach(System.out::println);
        }
    }
}
