/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.familysearch.standards.place.util.PlaceHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author wjohnson000
 *
 */
public class ParseWikiChineseRulersXML {

    static final String EMPEROR_JI = "朝";
    static final String ARCHAIC_JI = "三皇五帝";

    static final boolean ADD_POSTHUMOUS = true;
    static final boolean ADD_REGNAL = true;
    static final boolean ADD_TEMPLE = true;
    static final boolean ADD_COURTESY = true;
    static final boolean ADD_OTHER = true;
    static final boolean ADD_PERSONAL = true;

    static String mainComment =
        "\n" +
        "    https://eastasiastudent.net/china/classical/date-format/\n" + 
        "    https://en.wikipedia.org/wiki/List_of_Chinese_monarchs\n" + 
        "    http://pages.ucsd.edu/~dkjordan/chin/chinahistory/dyn10-u.html \n" + 
        "    https://en.wikipedia.org/wiki/Dynasties_in_Chinese_history\n" + 
        "    http://afe.easia.columbia.edu/timelines/china_timeline.htm ... not particularly useful!!\n" + 
        "    http://www.historyforkids.net/dynasties-of-ancient-china.html ... not particularly useful!!" +
        "\n    ";

    static Document doc  = null;
    static Element  root = null;
    static Element  dynasty = null;
    static Element  noDynasty = null;
    static Element  currEmperor = null;
    static Element  currReign = null;
    static Element  altEmperor = null;
    static Element  altReign = null;

    static int currEmpCount = 0;
    static int currReignCount = 0;
    static String currEmpKey = "";
    static List<String> currEmpMeta = new ArrayList<>();

    static Map<String, Integer> dynastyCount = new HashMap<>();

    static void createXML(List<String> details) {
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

            for (String detail : details) {
                addDetail(detail);
            }
        } catch(Exception ex) {
            System.out.println("XML-EX: " + ex.getMessage());
        } finally {
            saveDoc();
        }
    }

    static void addDetail(String detail) {
        String[] chunks = PlaceHelper.split(detail, '|');
        if (chunks.length == 17) {
            processDynasty(chunks);
            processAltDynasty(chunks);
            processEmperor(chunks);
        }
    }

    private static void processDynasty(String[] data) {
        if (! data[0].trim().isEmpty()) {
            currEmperor = null;
            currReign = null;
            currEmpCount = 1;
            currReignCount = 1;

            if (! data[0].equalsIgnoreCase(ARCHAIC_JI)) {
                String emperor = data[0];
                if (emperor.endsWith(EMPEROR_JI)) {
                    emperor = emperor.substring(0, emperor.length()-1);
                }

                Integer count = dynastyCount.getOrDefault(emperor, Integer.valueOf(0));
                char suffix = (char)('A' + count);
                String meta = (data[1].toLowerCase() + "-" + suffix).replaceAll("  ", " ").replace(' ', '-');
                dynastyCount.put(emperor, count);
                count++;

                Element dynWord = doc.createElement("word");
                dynWord.setAttribute("lang", "zh-hant");
                dynWord.setAttribute("meta", meta + "/" + getStartYear(data[4]));
                dynWord.appendChild(doc.createTextNode(emperor));
                dynasty.appendChild(dynWord);

                currEmperor = doc.createElement("word-group");
                currEmperor.setAttribute("type", "emperor");
                currEmperor.setAttribute("meta", meta);
                currEmperor.setAttribute("lang", "zh");
                root.appendChild(doc.createTextNode("\n\n  "));
                root.appendChild(currEmperor);

                currReign = doc.createElement("word-group");
                currReign.setAttribute("type", "reign");
                currReign.setAttribute("meta", meta);
                currReign.setAttribute("lang", "zh");
                root.appendChild(doc.createTextNode("\n  "));
                root.appendChild(currReign);
            }
        }
    }

    private static void processAltDynasty(String[] data) {
        if (! data[2].trim().isEmpty()) {
            altEmperor = null;
            altReign = null;

            if (! data[2].equalsIgnoreCase(ARCHAIC_JI)) {
                String emperor = data[2];
                if (emperor.endsWith(EMPEROR_JI)) {
                    emperor = emperor.substring(0, emperor.length()-1);
                }

                Integer count = dynastyCount.getOrDefault(emperor, Integer.valueOf(0));
                char suffix = (char)('A' + count);
                String meta = (data[3].toLowerCase() + "-" + suffix).replaceAll("  ", " ").replace(' ', '-');
                dynastyCount.put(emperor, count);
                count++;

                Element dynWord = doc.createElement("word");
                dynWord.setAttribute("lang", "zh-hant");
                dynWord.setAttribute("meta", meta + "/" + getStartYear(data[4]));
                dynWord.appendChild(doc.createTextNode(emperor));
                dynasty.appendChild(dynWord);

                altEmperor = doc.createElement("word-group");
                altEmperor.setAttribute("type", "emperor");
                altEmperor.setAttribute("meta", meta);
                altEmperor.setAttribute("lang", "zh");
                root.appendChild(doc.createTextNode("\n\n  "));
                root.appendChild(currEmperor);

                altReign = doc.createElement("word-group");
                altReign.setAttribute("type", "reign");
                altReign.setAttribute("meta", meta);
                altReign.setAttribute("lang", "zh");
                root.appendChild(doc.createTextNode("\n  "));
                root.appendChild(altReign);
            }
        }
    }

    private static void processEmperor(String[] data) {
        if (currEmperor != null) {
            String empKey = "";
            for (int i=6;  i<=11;  i++) {
                empKey += data[i];
            }

            if (! empKey.equalsIgnoreCase(currEmpKey)) {
                currEmpMeta = new ArrayList<>();

                if (ADD_POSTHUMOUS  &&  ! data[6].trim().isEmpty()) {
                    String tMeta = currEmperor.getAttribute("meta");
                    String eMeta = tMeta + "-emp-" + currEmpCount++;

                    Element empWord = doc.createElement("word");
                    empWord.setAttribute("lang", "zh-Hant");
                    empWord.setAttribute("type", tMeta);
                    empWord.setAttribute("meta", eMeta + "/" + getStartYear(data[12]));
                    empWord.appendChild(doc.createTextNode(data[6]));
                    currEmperor.appendChild(empWord);
                    currEmpMeta.add(eMeta);
                }

                if (ADD_REGNAL  &&  ! data[7].trim().isEmpty()) {
                    String tMeta = currEmperor.getAttribute("meta");
                    String eMeta = tMeta + "-emp-" + currEmpCount++;

                    Element empWord = doc.createElement("word");
                    empWord.setAttribute("lang", "zh-Hant");
                    empWord.setAttribute("type", tMeta);
                    empWord.setAttribute("meta", eMeta + "/" + getStartYear(data[12]));
                    empWord.appendChild(doc.createTextNode(data[7]));
                    currEmperor.appendChild(empWord);
                    currEmpMeta.add(eMeta);
                }

                if (ADD_TEMPLE  &&  ! data[8].trim().isEmpty()) {
                    String tMeta = currEmperor.getAttribute("meta");
                    String eMeta = tMeta + "-emp-" + currEmpCount++;

                    Element empWord = doc.createElement("word");
                    empWord.setAttribute("lang", "zh-Hant");
                    empWord.setAttribute("type", tMeta);
                    empWord.setAttribute("meta", eMeta + "/" + getStartYear(data[12]));
                    empWord.appendChild(doc.createTextNode(data[8]));
                    currEmperor.appendChild(empWord);
                    currEmpMeta.add(eMeta);
                }

                if (ADD_COURTESY  &&  ! data[9].trim().isEmpty()) {
                    String tMeta = currEmperor.getAttribute("meta");
                    String eMeta = tMeta + "-emp-" + currEmpCount++;

                    Element empWord = doc.createElement("word");
                    empWord.setAttribute("lang", "zh-Hant");
                    empWord.setAttribute("type", tMeta);
                    empWord.setAttribute("meta", eMeta + "/" + getStartYear(data[12]));
                    empWord.appendChild(doc.createTextNode(data[9]));
                    currEmperor.appendChild(empWord);
                    currEmpMeta.add(eMeta);
                }

                if (ADD_OTHER  &&  ! data[10].trim().isEmpty()) {
                    String tMeta = currEmperor.getAttribute("meta");
                    String eMeta = tMeta + "-emp-" + currEmpCount++;

                    Element empWord = doc.createElement("word");
                    empWord.setAttribute("lang", "zh-Hant");
                    empWord.setAttribute("type", tMeta);
                    empWord.setAttribute("meta", eMeta + "/" + getStartYear(data[12]));
                    empWord.appendChild(doc.createTextNode(data[10]));
                    currEmperor.appendChild(empWord);
                    currEmpMeta.add(eMeta);
                }

                if (ADD_PERSONAL  &&  ! data[11].trim().isEmpty()) {
                    String tMeta = currEmperor.getAttribute("meta");
                    String eMeta = tMeta + "-emp-" + currEmpCount++;

                    Element empWord = doc.createElement("word");
                    empWord.setAttribute("lang", "zh-Hant");
                    empWord.setAttribute("type", tMeta);
                    empWord.setAttribute("meta", eMeta + "/" + getStartYear(data[12]));
                    empWord.appendChild(doc.createTextNode(data[11]));
                    currEmperor.appendChild(empWord);
                    currEmpMeta.add(eMeta);
                }
            }

            processReign(data);
        }
    }

    private static void processReign(String[] data) {
        // TODO Auto-generated method stub
        
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
            StreamResult result = new StreamResult(new File("C:/temp/imperial_zh.xml"));
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

    static String getStartYear(String range) {
        String fromYr = "";

        int ndx01 = range.lastIndexOf('–');
        if (ndx01 == -1) {
            fromYr = range;
        } else {
            fromYr = range.substring(0, ndx01);
        }

        if (fromYr.contains("BC")) {
            fromYr = "-" + fromYr.replaceAll("BC", "").trim();
        }

        return fromYr;
    }
}
