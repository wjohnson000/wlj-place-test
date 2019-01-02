/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.familysearch.standards.core.lang.util.TraditionalToSimplifiedChineseMapper;

/**
 * @author wjohnson000
 *
 */
public class ZzzToSimplified {

    private static String[] ZH_FILES = {
//        "/org/familysearch/standards/date/shared/imperial_zh.xml",
        "/org/familysearch/standards/date/shared/modifier-dict.xml",
//        "/org/familysearch/standards/date/shared/month-dict.xml",
//        "/org/familysearch/standards/date/shared/numbers.xml",
    };

    private static TraditionalToSimplifiedChineseMapper mapper;

    public static void main(String... args) {
        mapper = new TraditionalToSimplifiedChineseMapper();
        ZzzToSimplified engine = new ZzzToSimplified();
        engine.doIt();
        System.exit(0);
    }

    void doIt() {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser;

        for (String zhFile : ZH_FILES) {
            try {
                Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(zhFile), StandardCharsets.UTF_8);
                parser = factory.createXMLStreamReader(reader);

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
                    case XMLStreamConstants.CHARACTERS:
                        String charsOld = parser.getText();
                        String charsNew = mapper.mapTraditionalToSimplified(charsOld);
                        if (! charsOld.equals(charsNew)) {
                            System.out.println(charsOld + "|" + charsNew);
                        }
                        break;
                    }
                }
            } catch (XMLStreamException | IOException e) {
                System.out.println("Oops ... " + e.getClass().getName() + " --> " + e.getMessage());
            }
        }
    }

}
