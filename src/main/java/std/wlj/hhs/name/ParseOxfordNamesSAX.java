/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 * Look at the name files from "Oxford", list names, variants, etc ...  The data is formated as XML with HTML tags
 * such used for links, text highlighting, etc.  This code uses SAX to parse the document.
 * 
 * The tags of interest include:
 * <ul>
 *   <li><strong>e</strong> - presumably for "entry".  It has an "id" attribute that uniquely identifies a name.</li>
 *   <li><strong>headwordGroup</strong> - contains the name and gender.  A "♂" symbol is used to designate a male
 *               name, a "♀" is used to designate a female name.  Names may be associated with both.</li>
 *   <li><strong>headword</strong> - can appear in a few places, but mainly inside a "headwordGroup" tag.  It's
 *               value is the name.</li>
 *   <li><strong>span</strong> - inside the name definition a "span" tag with "ency" attribute contains the language
 *               of origin.</li>
 *   <li><strong>textMatter</strong> - this contains the name definition with a number of HTML tags allowed.  For
 *               now the code allows "i", "b", "p" and "span" tags to remain in the code.  All other HTML tags are
 *               removed from the definition.  However we do look for a "xref" tag with a "ref" attribute to determin
 *               if the name is associated with another name.</li>
 *   <li><strong>xrefGroup + xref</strong> - this pair signals that the current name is tied to another name.  The
 *               related name's unique identifier is defined in the "ref" attribute of the "xref" tag, which is in
 *               turn inside the "xrefGroup" tag.</li>
 * </ul>
 * 
 * The basic operation of this class is:
 * <ol>
 * <li>Read all lines from the input file, process them a line-at-a-time</li>
 * <li>For each line, create an 'XmlStreamReader' for that name definition</li>
 * <li>Extract all information about that name (ignore lines that are NOT name definitions)</li>
 * <li>Associate each "derived" or "variant" name with its parent</li>
 * <li>Sort the parent names by name text</li>
 * <li>Dump out all names, keeping the derived names with their parent name.  Fields are pipe-delimited as:</li>
 *   <ul>
 *     <li>name identifier</li>
 *     <li>text (of name)</li>
 *     <li>language of origin (currently not being set)</li>
 *     <li>type: "1" refers to a short form or pet name, "2" to a variant or cognate</li>
 *     <li>is this name male, "true" or "false"</li>
 *     <li>is this name female, "true" or "false"</li>
 *     <li>name meaning, with "i", "b", "p" and "span" HTML tags embedded</li>
 *   </ul>
 * </ol>
 * 
 * @author wjohnson000
 *
 */
public class ParseOxfordNamesSAX {

    private static final String MALE_CHAR = "♂";
    private static final String FEMALE_CHAR = "♀";

    private static final Set<String> OK_TEXT_TAGS = new HashSet<>();
    static {
        OK_TEXT_TAGS.add("i");
        OK_TEXT_TAGS.add("b");
        OK_TEXT_TAGS.add("p");
        OK_TEXT_TAGS.add("span");
    }

    private static String BASE_DIR = "C:/D-drive/homelands/names";
    private static String FIRST_FILE = "first_acref_9780198610601.xml";
    private static String LAST_FILE  = "last_acref_9780195081374.xml";
    private static final String OUTPUT_FILE_NAME = "C:/temp/oxford-fn-sax.csv";

    private static XMLInputFactory XML_FACTORY = XMLInputFactory.newInstance();

    private static Map<String, NameDef> allNames = new HashMap<>();
    private static List<String> results = new ArrayList<>(50_000);
    
    public static void main(String... args) throws Exception {
        process(FIRST_FILE);
//        process(LAST_FILE);
    }

    static void process(String file) throws Exception {
        List<String> rows = Files.readAllLines(Paths.get(BASE_DIR, file), StandardCharsets.UTF_8);

        for (String row : rows) {
            NameDef nameDef = parseRow(row);
            if (nameDef != null) {
                if (allNames.containsKey(nameDef.id)) {
                    System.out.println("Duplicate key: " + nameDef.id);
                } else {
                    allNames.put(nameDef.id, nameDef);
                }
            }
        }

        // Tie variants to their "master" name, and save the "Master" ones
        List<NameDef> masterNames = new ArrayList<>();
        List<NameDef> badMasterNames = new ArrayList<>();

        for (NameDef nameDef : allNames.values()) {
            if (nameDef.refId == null) {
                if (nameDef.text == null) {
                    badMasterNames.add(nameDef);
                } else {
                    masterNames.add(nameDef);
                }
            } else {
                NameDef parent = allNames.get(nameDef.refId);
                if (parent == null) {
                    System.out.println("Missing parent: " + nameDef.text + " --> " + nameDef.refId);
                } else {
                    parent.variants.add(nameDef);
                }
            }
        }

        // Sort-Sort and then Dump-dump
        Collections.sort(masterNames, (nd1, nd2) -> nd1.text.compareToIgnoreCase(nd2.text));
        masterNames.addAll(badMasterNames);

        int     maxVar  = 0;
        NameDef maxNDef = null;
        for (NameDef nameDef : masterNames) {
            results.add("");
            results.add("");
            results.add(format(nameDef));
            nameDef.variants.forEach(nd -> results.add(format(nd)));

            if (nameDef.variants.size() > maxVar) {
                maxVar = nameDef.variants.size();
                maxNDef = nameDef;
            }
        }
        System.out.println("Max=" + maxVar + " for " + maxNDef.text + " [" + maxNDef.id + "]");
        
        Files.write(Paths.get(OUTPUT_FILE_NAME), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static String format(NameDef nameDef) {
        StringBuilder buff = new StringBuilder(1024);

        buff.append(nameDef.id);
        buff.append("|").append(nameDef.text);
        buff.append("|").append(nameDef.language);
        buff.append("|").append(nameDef.type);
        buff.append("|").append(nameDef.isMale);
        buff.append("|").append(nameDef.isFemale);
        buff.append("|").append(nameDef.definition);

        return buff.toString();
    }

    static NameDef parseRow(String row) {
        NameDef nameDef = new NameDef();

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(row.getBytes());
            XMLStreamReader parser = XML_FACTORY.createXMLStreamReader(bais, "UTF-8");

            boolean inHWGroup = false;
            boolean htmlOK = false;
            boolean captureText = false;
            StringBuilder buff = new StringBuilder();

            boolean more = true;
            while (more) {
                int event = parser.next();
                switch(event) {
                case XMLStreamConstants.END_DOCUMENT:
                    more = false;
                    break;

                case XMLStreamConstants.START_ELEMENT:
                    if (parser.getLocalName().equals("e")) {
                        nameDef.id = getAttrValue(parser, "id");
                    } else if (parser.getLocalName().equals("xref")) {
                        nameDef.type = getAttrValue(parser, "type");
                        if (nameDef.refId == null) {
                            nameDef.refId = getAttrValue(parser, "ref");
                        }
                    } else if (parser.getLocalName().equals("headwordGroup")) {
                        inHWGroup = true;
                    } else if (parser.getLocalName().equals("headword")  &&  inHWGroup) {
                        htmlOK = false;
                        captureText = true;
                    } else if (parser.getLocalName().equals("textMatter")) {
                        htmlOK = true;
                        captureText = true;
                    } else if (captureText  &&  htmlOK  &&  OK_TEXT_TAGS.contains(parser.getLocalName())) {
                        buff.append("<").append(parser.getLocalName());
                        for (int ndx=0;  ndx<parser.getAttributeCount();  ndx++) {
                            buff.append(" ").append(parser.getAttributeLocalName(ndx));
                            buff.append("=\"").append(parser.getAttributeValue(ndx)).append("\"");
                        }
                        buff.append(">");
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (parser.getLocalName().equals("headwordGroup")  &&  inHWGroup) {
                        inHWGroup = false;
                    } else if (parser.getLocalName().equals("headword")  &&  inHWGroup) {
                        captureText = false;
                        nameDef.text = buff.toString();
                        buff = new StringBuilder();
                    } else if (parser.getLocalName().equals("textMatter")) {
                        htmlOK = false;
                        captureText = false;
                        nameDef.definition = buff.toString();
                        buff = new StringBuilder();
                    } else if (captureText  &&  htmlOK  &&  OK_TEXT_TAGS.contains(parser.getLocalName())) {
                        buff.append("</").append(parser.getLocalName()).append(">");
                    }
                    break;

                case XMLStreamConstants.CHARACTERS:
                    String text = parser.getText();
                    nameDef.isMale |= text.contains(MALE_CHAR);
                    nameDef.isFemale |= text.contains(FEMALE_CHAR);
 
                    if (captureText) {
                        buff.append(text);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("OOPS ... " + ex.getMessage());
            return null;
        }

        return (nameDef.id == null) ? null : nameDef;
    }

    static String getAttrValue(XMLStreamReader parser, String key) {
        for (int ndx=0;  ndx<parser.getAttributeCount();  ndx++) {
            if (parser.getAttributeLocalName(ndx).equalsIgnoreCase(key)) {
                return parser.getAttributeValue(ndx);
            }
        }
        return null;
    }
}
