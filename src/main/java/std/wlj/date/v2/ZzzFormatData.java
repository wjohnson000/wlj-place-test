/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
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
public class ZzzFormatData {

    private static class MiniDyn {
        String name;
        String enName;
        int    year;
        List<MiniEmp> emperors;

        public String toString() {
            return name + " [" + enName + "]: " + year;
        }
    }

    private static class MiniEmp {
        String name;
        int    year;
        List<MiniRgn> reigns;
    }

    private static class MiniRgn {
        String name;
        int    year;
    }

//    private static final String CJK_CALENDAR_FILENAME = "/std/wlj/date/v2/imperial_zh.xml";
    private static final String CJK_CALENDAR_FILENAME = "/org/familysearch/standards/date/shared/imperial_zh.xml";

    public static void main(String... args) {
        ZzzFormatData engine = new ZzzFormatData();

        Map<Integer, MiniDyn> dynasties = engine.getDynasties();
//        dynasties.entrySet().forEach(System.out::println);
        engine.addEmperors(dynasties);

        System.exit(0);
    }

    Map<Integer, MiniDyn> getDynasties() {
        Map<Integer, MiniDyn> dynasties = new TreeMap<>();

        try(Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(CJK_CALENDAR_FILENAME), Charset.forName("UTF-8"))) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader parser = factory.createXMLStreamReader(reader);

            String metadata = "";
            boolean inDynasty = false;
            boolean stillReading = true;

            while (stillReading) {
                switch (parser.next()) {
                case XMLStreamConstants.END_DOCUMENT:
                    parser.close();
                    stillReading = false;
                    break;

                case XMLStreamConstants.START_ELEMENT:
                    metadata = "";
                    if (parser.getLocalName().equals("word-group")) {
                        String type = parser.getAttributeValue(null, "type");
                        inDynasty = ("dynasty".equals(type));
                    } else if (inDynasty  &&  parser.getLocalName().equals("word")) {
                        metadata = parser.getAttributeValue(null, "meta");
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (parser.getLocalName().equals("word-group")) {
                        inDynasty = false;
                    }
                    break;

                case XMLStreamConstants.CHARACTERS:
                    String chars = parser.getText();
                    if (inDynasty  &&  metadata != null  &&  ! chars.trim().isEmpty()) {
                        String[] chunks = PlaceHelper.split(metadata, '|');
                        MiniDyn mdyn = new MiniDyn();
                        mdyn.name = chars;
                        mdyn.enName = chunks[0];
                        mdyn.year = Integer.parseInt(chunks[1]);
                        dynasties.put(mdyn.year, mdyn);
                        
                    }
                    break;
                }
            }
        } catch (XMLStreamException | IOException e) {
            System.out.println("Oops ... " + e.getClass().getName() + " --> " + e.getMessage());
        }

        return dynasties;
    }

    /**
     * @param dynasties
     */
    void addEmperors(Map<Integer, MiniDyn> dynasties) {
        try(Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(CJK_CALENDAR_FILENAME), Charset.forName("UTF-8"))) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader parser = factory.createXMLStreamReader(reader);

            int     noDynCount = 0;
            String  dynName = "";
            String  metadata = "";
            boolean inEmperor = false;
            boolean stillReading = true;

            while (stillReading) {
                switch (parser.next()) {
                case XMLStreamConstants.END_DOCUMENT:
                    parser.close();
                    stillReading = false;
                    break;

                case XMLStreamConstants.START_ELEMENT:
                    metadata = "";
                    if (parser.getLocalName().equals("word-group")) {
                        String type = parser.getAttributeValue(null, "type");
                        dynName = parser.getAttributeValue(null, "meta");
                        inEmperor = ("emperor".equals(type));
                    } else if (inEmperor  &&  parser.getLocalName().equals("word")) {
                        String type = parser.getAttributeValue(null, "type");
                        metadata = parser.getAttributeValue(null, "meta");
                        if (! type.equals(dynName)) {
                            System.out.println("Mismatch ... " + dynName + " vs. " + type);
                        }
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (parser.getLocalName().equals("word-group")) {
                        inEmperor = false;
                    }
                    break;

                case XMLStreamConstants.CHARACTERS:
                    String chars = parser.getText();
                    if (inEmperor  &&  metadata != null  &&  ! chars.trim().isEmpty()) {
                        String[] chunks = PlaceHelper.split(metadata, '|');
                        MiniDyn mdyn = new MiniDyn();
                        mdyn.name = chars;
                        mdyn.enName = chunks[0];
                        mdyn.year = Integer.parseInt(chunks[1]);
                        dynasties.put(mdyn.year, mdyn);
                        
                    }
                    break;
                }
            }
        } catch (XMLStreamException | IOException e) {
            System.out.println("Oops ... " + e.getClass().getName() + " --> " + e.getMessage());
        }
    }

}
