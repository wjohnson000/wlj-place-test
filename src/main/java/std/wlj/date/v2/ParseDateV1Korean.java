/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.familysearch.standards.core.LocalizedData;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.lang.xlit.TransliterationRequest;
import org.familysearch.standards.core.lang.xlit.xlits.HangulToRomanTransliterator;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author wjohnson000
 *
 */
public class ParseDateV1Korean {

    static class KoEra implements Comparable<KoEra> {
        String  dynasty = "";
        String  dynastyKo = "";
        String  dynastyLatin = "";
        String  name = "";
        String  nameKo = "";
        String  nameLatin = "";
        int     startYr = 0;
        int     endYr = 3000;
        
        @Override public String toString() {
            return dynasty + " (" + dynastyLatin + ") .. " + name + " (" + nameLatin + ") .. " + startYr + " .. " + endYr;
        }

        @Override public int compareTo(KoEra that) {
            return this.startYr - that.startYr;
        }
    }

    static final String INPUT_FILE  = "C:/temp/koNames-7-26-2018.xml";
    static final String OUTPUT_FILE = "C:/temp/imperial_ko.xml";

    static Map<KoEra, List<KoEra>> dynastyDetails = new HashMap<>();
    static HangulToRomanTransliterator hangulXlit = new HangulToRomanTransliterator();

    static String mainComment =
        "\n" +
        "    https://en.wikipedia.org/wiki/List_of_monarchs_of_Korea\n" + 
        "    https://www.metmuseum.org/toah/hd/koru/hd_koru.htm" +
        "\n    ";

    static Document doc  = null;
    static Element  root = null;
    static Element  dynasty = null;
    static Element  noDynasty = null;
    static Element  currEmperor = null;

    static int empCount = 1;

    public static void main(String...args) throws FileNotFoundException, IOException {
        parseInputFile();
        fixDynastyStartYears();
        fixEmperorEndYears();
        createOutputFile();
    }

    static void parseInputFile() throws FileNotFoundException, IOException {
        boolean keepReading = true;
        try(InputStream inStr = new FileInputStream(new File(INPUT_FILE))) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader parser = factory.createXMLStreamReader(inStr, "UTF-8");
            while (keepReading) {
                int event = parser.next();
                switch (event) {
                    case XMLStreamConstants.END_DOCUMENT:
                        keepReading = false;
                        break;
                    case XMLStreamConstants.START_ELEMENT:
                        KoEra era = getEra(parser);
                        if (era != null) {
                            KoEra parent = dynastyDetails.keySet().stream()
                                    .filter(par -> par.dynasty.equals(era.dynasty))
                                    .findFirst().orElse(null);
                            if (parent == null) {
                                parent = era;
                                dynastyDetails.put(parent, new ArrayList<>());
                            }
                            if (! era.name.isEmpty()) {
                                dynastyDetails.get(parent).add(era);
                            }
                        }
                        break;
                }
            }
        } catch (XMLStreamException ex) {
            System.out.println("OOPS ... " + ex.getMessage());
        }
    }

    static void fixDynastyStartYears() {
        for (KoEra era : dynastyDetails.keySet()) {
            Integer minStart = dynastyDetails.get(era).stream()
                .map(emp -> emp.startYr)
                .min(Integer::compare)
                .orElse(null);
            if (minStart != null) {
                era.startYr = minStart.intValue();
            }
        }
    }

    static void fixEmperorEndYears() {
        for (KoEra era : dynastyDetails.keySet()) {
            int endYr = era.endYr;
            for (KoEra emp : dynastyDetails.get(era)) {
                emp.endYr = endYr;
                endYr = emp.startYr;
            }
        }
    }

    static void createOutputFile() {
        List<KoEra> dynasties = dynastyDetails.keySet().stream()
            .sorted()
            .collect(Collectors.toList());
        createXML(dynasties);
    }

    static KoEra getEra(XMLStreamReader parser) {
        if ("emperor".equals(parser.getLocalName())) {
            KoEra era = new KoEra();
            
            for (int ndx=0;  ndx<parser.getAttributeCount();  ndx++) {
                String name = parser.getAttributeLocalName(ndx);
                String valu = parser.getAttributeValue(ndx);
                if ("dynasty".equals(name)) {
                    era.dynasty = valu;
                } else if ("ko-dynasty".equals(name)) {
                    era.dynastyKo = valu;
                    era.dynastyLatin = getLatin(valu);
                } else if ("name".equals(name)) {
                    era.name = valu;
                } else if ("ko-name".equals(name)) {
                    era.nameKo = valu;
                    era.nameLatin = getLatin(valu);
                } else if ("year".equals(name)) {
                    era.startYr = Integer.parseInt(valu);
                } else if ("end".equals(name)) {
                    era.endYr = Integer.parseInt(valu);
                }
            }
            return era;
        } else {
            return null;
        }
    }

    static void createXML(List<KoEra> dynasties) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.newDocument();
            
            // root element
            root = doc.createElement("words");
            doc.appendChild(root);
            addComment(root, mainComment);

            createNoDynasty();
            createDynasty();

            for (KoEra dynasty : dynasties) {
                handleDynasty(dynasty);
                List<KoEra> emperors = dynastyDetails.get(dynasty);
                Collections.sort(emperors);
                for (KoEra emperor : emperors) {
                    handleEmperor(emperor);
                }
            }
        } catch(Exception ex) {
            System.out.println("XML-EX: " + ex.getMessage());
        } finally {
            saveDoc();
        }
    }

    static void handleDynasty(KoEra dynEra) {
        String dynLatin = dynEra.dynastyLatin.toLowerCase();

        Element dynWord = doc.createElement("word");
        dynWord.setAttribute("lang", "ko");
        dynWord.setAttribute("meta", dynLatin + "|" + dynEra.startYr + "|" + dynEra.endYr);
        dynWord.setTextContent(dynEra.dynasty);
        dynasty.appendChild(dynWord);

        dynWord = doc.createElement("word");
        dynWord.setAttribute("lang", "ko-Hang");
        dynWord.setAttribute("meta", dynLatin + "|" + dynEra.startYr + "|" + dynEra.endYr);
        dynWord.setTextContent(dynEra.dynastyKo);
        dynasty.appendChild(dynWord);

        currEmperor = doc.createElement("word-group");
        currEmperor.setAttribute("lang", "ko");
        currEmperor.setAttribute("type", "emperor");
        currEmperor.setAttribute("meta", dynLatin);
        root.appendChild(doc.createTextNode("\n\n  "));
        addComment(root, dynLatin + " [" + dynEra.dynasty + "] - [" + dynEra.dynastyKo + "] dynasty's emperors");
        root.appendChild(doc.createTextNode("\n  "));
        root.appendChild(currEmperor);

        Element currReign = doc.createElement("word-group");
        currReign.setAttribute("lang", "ko");
        currReign.setAttribute("type", "reign");
        currReign.setAttribute("meta", dynLatin);
        root.appendChild(doc.createTextNode("\n  "));
        root.appendChild(currReign);
    }

    static void handleEmperor(KoEra empEra) {
        String dynLatin = empEra.dynastyLatin.toLowerCase();
        String empLatin = empEra.nameLatin.toLowerCase();

        int count = empCount++;

        Element empWord = doc.createElement("word");
        empWord.setAttribute("lang", "ko");
        empWord.setAttribute("type", dynLatin);
        empWord.setAttribute("meta", empLatin + "-" + count + "|" + empEra.startYr + "|" + empEra.endYr);
        empWord.setTextContent(empEra.name);
        currEmperor.appendChild(empWord);

        empWord = doc.createElement("word");
        empWord.setAttribute("lang", "ko-Hang");
        empWord.setAttribute("type", dynLatin);
        empWord.setAttribute("meta", empLatin + "-" + count + "|" + empEra.startYr + "|" + empEra.endYr);
        empWord.setTextContent(empEra.nameKo);
        currEmperor.appendChild(empWord);
    }

    @SuppressWarnings("deprecation")
    static String getLatin(String hangul) {
        TransliterationRequest req = new TransliterationRequest(new LocalizedData<String>(hangul, StdLocale.KOREAN_HANGUL), StdLocale.ENGLISH);
        LocalizedData<String> latin = hangulXlit.transliterate(req);
        return (latin == null) ? hangul : latin.get();
    }

    static void addComment(Element element, String commentTxt) {
        Comment comment = doc.createComment(commentTxt);
        element.appendChild(comment);
    }

    static void saveDoc() {
        // write the content into xml file
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(OUTPUT_FILE));
            transformer.transform(source, result);
        } catch (Exception ex) {
            System.out.println("SAVE-EX: " + ex.getMessage());
        }
    }
    
    static void createNoDynasty() {
        noDynasty = doc.createElement("word-group");
        root.appendChild(doc.createTextNode("\n\n  "));
        root.appendChild(noDynasty);

        noDynasty.setAttribute("type", "emperor");
        noDynasty.setAttribute("meta", "no-dynasty");
        noDynasty.setAttribute("lang", "zh");
    }

    static void createDynasty() {
        dynasty = doc.createElement("word-group");
        root.appendChild(doc.createTextNode("\n\n  "));
        root.appendChild(dynasty);

        dynasty.setAttribute("type", "dynasty");
        dynasty.setAttribute("lang", "zh");
    }
}
