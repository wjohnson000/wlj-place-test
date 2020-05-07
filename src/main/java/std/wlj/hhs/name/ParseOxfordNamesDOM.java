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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Look at the name files from "Oxford", list names, variants, etc ...  The data is formated as XML with HTML tags
 * such used for links, text highlighting, etc.  This code uses DOM to parse the document.
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
public class ParseOxfordNamesDOM {


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
    private static final String OUTPUT_FILE_NAME = "C:/temp/oxford-fn-dom.csv";

    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

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
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(bais);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            nameDef.id = getId(doc);
            nameDef.text = getNameText(doc);
            nameDef.language = getLanguage(doc);
            nameDef.refId = getXref(doc);
            nameDef.type = getType(doc);
            nameDef.definition = getDefinition(doc);
            nameDef.isMale = row.contains(MALE_CHAR);
            nameDef.isFemale = row.contains(FEMALE_CHAR);
        } catch(Exception ex) {
            System.out.println("OOPS ... " + ex.getMessage());
            return null; 
        }
        return (nameDef.id == null) ? null : nameDef;
    }

    static String getId(Document doc) {
        NodeList nodes = doc.getElementsByTagName("e");
        if (nodes.getLength() > 0) {
            return ((Element)nodes.item(0)).getAttribute("id");
        } else {
            return null;
        }
    }

    static String getNameText(Document doc) {
        NodeList nodes = doc.getElementsByTagName("headword");
        if (nodes.getLength() > 0) {
            return ((Element)nodes.item(0)).getTextContent();
        } else {
            return null;
        }
    }

    static String getLanguage(Document doc) {
        NodeList nodes = doc.getElementsByTagName("span");

        for (int ndx=0;  ndx<nodes.getLength();  ndx++) {
            Node node = nodes.item(ndx);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                if (element.getAttribute("ency") != null) {
                    return element.getTextContent();
                }
            }
        }

        return null;
    }

    static String getXref(Document doc) {
        NodeList nodes = doc.getElementsByTagName("xrefGrp");

        for (int ndx=0;  ndx<nodes.getLength();  ndx++) {
            Node node = nodes.item(ndx);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                NodeList children = element.getElementsByTagName("xref");
                if (children.getLength() > 0) {
                    return ((Element)children.item(0)).getAttribute("ref");
                }
            }
        }

        return null;
    }

    static String getType(Document doc) {
        NodeList nodes = doc.getElementsByTagName("xrefGrp");

        for (int ndx=0;  ndx<nodes.getLength();  ndx++) {
            Node node = nodes.item(ndx);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                NodeList children = element.getElementsByTagName("xref");
                if (children.getLength() > 0) {
                    return ((Element)children.item(0)).getAttribute("type");
                }
            }
        }

        return null;
    }

    static String getDefinition(Document doc) {
        NodeList nodes = doc.getElementsByTagName("textMatter");
        if (nodes.getLength() == 0) {
            return "";
        } else {
            return getTextWithTags((Element)nodes.item(0));
        }
    }

    static String getTextWithTags(Element head) {
        if (! head.hasChildNodes()) {
            return head.getTextContent(); 
        } else  {
            StringBuilder buff = new StringBuilder();
            NodeList children = head.getChildNodes();
            for (int ndx=0;  ndx<children.getLength();  ndx++) {
                Node childNode = children.item(ndx);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element child = (Element)children.item(ndx);
                    if (OK_TEXT_TAGS.contains(child.getTagName().toLowerCase())) {
                        buff.append("<").append(child.getNodeName());
                        NamedNodeMap attrs = child.getAttributes();
                        if (attrs != null  &&  attrs.getLength() > 0) {
                            for (int cdx=0;  cdx<attrs.getLength();  cdx++) {
                                buff.append(" ").append(attrs.item(cdx).getNodeName());
                                buff.append("=\"").append(attrs.item(cdx).getNodeValue()).append("\"");
                            }
                        }
                        buff.append(">");
                    }
                    
                    buff.append(getTextWithTags(child));
                    
                    if (OK_TEXT_TAGS.contains(child.getTagName().toLowerCase())) {
                        buff.append("</").append(child.getTagName()).append(">");
                    }
                } else {
                    buff.append(childNode.getNodeValue());
                }
            }

            return buff.toString();
        }
    }
}
