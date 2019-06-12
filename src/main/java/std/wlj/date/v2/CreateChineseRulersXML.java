/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.familysearch.standards.core.lang.util.TraditionalToSimplifiedChineseMapper;
import org.familysearch.standards.place.util.PlaceHelper;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Create XML file with details of Chinese rules, based on data of the following format.  Note: the
 * data fields are pipe-delimited:
 * <ul>
 *   <li> 0 - Dynasty name (zh)</li>
 *   <li> 1 - Dynasty description (en)</li>
 *   <li> 2 - Dynasty start year</li>
 *   <li> 3 - Dynasty range</li>
 *   <li> 4 - Alternate name (zh)</li>
 *   <li> 5 - Alternate description (en)</li>
 *   <li> 6 - Alternate range</li>
 *   <li> 7 - Emperor name (en)</li>
 *   <li> 8 - Posthumous name (zh)</li>
 *   <li> 9 - Regnal name (zh)</li>
 *   <li>10 - Personal name (zh)</li>
 *   <li>11 - Temple name (zh)</li>
 *   <li>12 - Courtesy name (zh)</li>
 *   <li>13 - Other name (zh)</li>
 *   <li>14 - Reign range</li>
 *   <li>15 - Era description (en)</li>
 *   <li>16 - Era name (zh)</li>
 *   <li>17 - Era range</li>
 *   <li>18 - Real era range (if present)</li>
 * </ul>
 * 
 * @author wjohnson000
 *
 */
public class CreateChineseRulersXML {

    static final String EMPEROR_JI = "朝";
    static final String ARCHAIC_JI = "三皇五帝";

    private static TraditionalToSimplifiedChineseMapper mapper = new TraditionalToSimplifiedChineseMapper();

    static String mainComment =
        "\n" +
        "    https://eastasiastudent.net/china/classical/date-format/\n" + 
        "    https://en.wikipedia.org/wiki/List_of_Chinese_monarchs\n" + 
        "    http://pages.ucsd.edu/~dkjordan/chin/chinahistory/dyn10-u.html \n" + 
        "    https://en.wikipedia.org/wiki/Dynasties_in_Chinese_history\n" + 
        "\n    ";

    static Set<String> dynastyToIgnore = new HashSet<>();
    static {
        dynastyToIgnore.add("tiefu-tribe-A");
        dynastyToIgnore.add("yuwen-tribe-A");
        dynastyToIgnore.add("tuoba-tribe-A");
        dynastyToIgnore.add("wuping/hunan-A");
        dynastyToIgnore.add("quanzhang-A");
        dynastyToIgnore.add("taiping-heavenly-kingdom-A");
        dynastyToIgnore.add("xia-A");
        dynastyToIgnore.add("shang-A");
        dynastyToIgnore.add("zhou-A");
        dynastyToIgnore.add("qin-A");
        dynastyToIgnore.add("han-A");
        dynastyToIgnore.add("shu-han-A");
        dynastyToIgnore.add("eastern-wu-A");
        dynastyToIgnore.add("jin-A");
        dynastyToIgnore.add("liu-song-A");
        dynastyToIgnore.add("nán-qi-A");
        dynastyToIgnore.add("nán-liang-A");
        dynastyToIgnore.add("cao-wei-A");
        dynastyToIgnore.add("chen-A");
        dynastyToIgnore.add("sui-A");
        dynastyToIgnore.add("tang-A");
        dynastyToIgnore.add("hou-liang-A");
        dynastyToIgnore.add("hou-tang-A");
        dynastyToIgnore.add("hou-jin-A");
        dynastyToIgnore.add("hou-han-A");
        dynastyToIgnore.add("hou-zhou-A|");
        dynastyToIgnore.add("liao-A");
        dynastyToIgnore.add("western-liao-A");
        dynastyToIgnore.add("jin-A");
        dynastyToIgnore.add("song-A");
        dynastyToIgnore.add("yuan-A");
        dynastyToIgnore.add("ming-A");
        dynastyToIgnore.add("qing-A");
        dynastyToIgnore.add("china-A");
        dynastyToIgnore.add("");
    }

    private boolean addPosthumous = true;
    private boolean addRegnal = true;
//    private boolean addTemple = true;
//    private boolean addCourtesy = true;
//    private boolean addOther = true;
    private boolean addPersonal = true;

    private Document doc  = null;
    private Element  root = null;
    private Element  currDynasty = null;
    private Element  noDynasty = null;
    private Element  currEmperor = null;
    private Element  currReign = null;
    private Element  altEmperor = null;
    private Element  altReign = null;

    private int currEmpCount = 0;
    private String currEmpKey = "";
    private String prevEmpKey = "";
    private String currEmpMeta = "";
    private String prevReignKey = "";

    private Map<String, Integer> dynastyCount = new HashMap<>();

    public CreateChineseRulersXML() {
        this(true, true, true, true, true, true);
    }

    public CreateChineseRulersXML(boolean addPosthumous, boolean addRegnal, boolean addTemple, boolean addCourtesy, boolean addOther,  boolean addPersonal) {
        this.addPersonal = addPersonal;
        this.addPosthumous = addPosthumous;
        this.addRegnal = addRegnal;
//        this.addTemple = addTemple;
//        this.addCourtesy = addCourtesy;
//        this.addOther = addOther;
    }

    public void createXML(List<String> details) {
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
                if (! detail.trim().isEmpty()) {
                    addDetail(detail);
                }
            }
        } catch(Exception ex) {
            System.out.println("XML-EX: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            saveDoc();
        }
    }

    protected void addDetail(String detail) {
        String[] chunks = PlaceHelper.split(detail, '|');
        if (chunks.length >= 18) {
            processDynasty(chunks);
            processAltDynasty(chunks);
            processEmperor(chunks);
        }
    }

    protected void processDynasty(String[] data) {
        if (! data[0].trim().isEmpty()) {
            currEmperor = null;
            currReign = null;
            currEmpCount = 1;
//            currReignCount = 1;
            altEmperor = null;
            altReign = null;

            if (! data[0].equalsIgnoreCase(ARCHAIC_JI)) {
                String dynasty = data[0];
                if (dynasty.endsWith(EMPEROR_JI)) {
                    dynasty = dynasty.substring(0, dynasty.length()-1);
                }

                Integer count = dynastyCount.getOrDefault(dynasty, Integer.valueOf(0));
                char suffix = (char)('A' + count);
                String meta = (data[1].toLowerCase() + "-" + suffix).replaceAll("  ", " ").replace(' ', '-');
                dynastyCount.put(dynasty, count+1);

                if (! dynastyToIgnore.contains(meta)) {
                    String dynastyHans = mapper.mapTraditionalToSimplified(dynasty);
                    boolean sameName = (dynastyHans.equals(dynasty));

                    Element dynWord = doc.createElement("word");
                    dynWord.setAttribute("lang", (sameName ? "zh" : "zh-Hant"));
                    dynWord.setAttribute("meta", meta + "|" + getStartYear(data[3]) + "|" + getEndYear(data[3]));
                    dynWord.appendChild(doc.createTextNode(dynasty));
                    currDynasty.appendChild(dynWord);

                    if (! sameName) {
                        Element dynWordS = doc.createElement("word");
                        dynWordS.setAttribute("lang", "zh-Hans");
                        dynWordS.setAttribute("meta", meta + "|" + getStartYear(data[3]) + "|" + getEndYear(data[3]));
                        dynWordS.appendChild(doc.createTextNode(dynastyHans));
                        currDynasty.appendChild(dynWordS);
                    }

                    currEmperor = doc.createElement("word-group");
                    currEmperor.setAttribute("type", "emperor");
                    currEmperor.setAttribute("meta", meta);
                    currEmperor.setAttribute("lang", "zh");
                    root.appendChild(doc.createTextNode("\n\n  "));
                    addComment(root, "Emperor and Reign for " + dynasty + " (" + data[1] + ")");
                    root.appendChild(doc.createTextNode("\n  "));
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
    }

    protected void processAltDynasty(String[] data) {
        if (! data[4].trim().isEmpty()) {
            altEmperor = null;
            altReign = null;

            if (! data[4].equalsIgnoreCase(ARCHAIC_JI)) {
                String dynasty = data[4];
                if (dynasty.endsWith(EMPEROR_JI)) {
                    dynasty = dynasty.substring(0, dynasty.length()-1);
                }

                Integer count = dynastyCount.getOrDefault(dynasty, Integer.valueOf(0));
                char suffix = (char)('A' + count);
                String meta = (data[5].toLowerCase() + "-" + suffix).replaceAll("  ", " ").replace(' ', '-');
                dynastyCount.put(dynasty, count+1);
                count++;

                if (! dynastyToIgnore.contains(meta)) {
                    String dynastyHans = mapper.mapTraditionalToSimplified(dynasty);
                    boolean sameName = (dynastyHans.equals(dynasty));

                    Element dynWord = doc.createElement("word");
                    dynWord.setAttribute("lang", (sameName ? "zh" : "zh-Hant"));
                    dynWord.setAttribute("meta", meta + "|" + getStartYear(data[6]) + "|" + getEndYear(data[6]));
                    dynWord.appendChild(doc.createTextNode(dynasty));
                    currDynasty.appendChild(dynWord);

                    if (! sameName) {
                        Element dynWordS = doc.createElement("word");
                        dynWordS.setAttribute("lang", "zh-Hans");
                        dynWordS.setAttribute("meta", meta + "|" + getStartYear(data[6]) + "|" + getEndYear(data[6]));
                        dynWordS.appendChild(doc.createTextNode(dynastyHans));
                        currDynasty.appendChild(dynWordS);
                    }
                    
                    altEmperor = doc.createElement("word-group");
                    altEmperor.setAttribute("type", "emperor");
                    altEmperor.setAttribute("meta", meta);
                    altEmperor.setAttribute("lang", "zh");
                    root.appendChild(doc.createTextNode("\n\n  "));
                    addComment(root, "Emperor and Reign for " + dynasty + " (" + data[5] + ")");
                    root.appendChild(doc.createTextNode("\n  "));
                    root.appendChild(altEmperor);
                    
                    altReign = doc.createElement("word-group");
                    altReign.setAttribute("type", "reign");
                    altReign.setAttribute("meta", meta);
                    altReign.setAttribute("lang", "zh");
                    root.appendChild(doc.createTextNode("\n  "));
                    root.appendChild(altReign);
                }
            }
        }
    }

    protected void processEmperor(String[] data) {
        if (currEmperor != null) {
            String empKey = "";
            for (int i=7;  i<=13;  i++) {
                empKey += data[i];
            }

            if (! empKey.equalsIgnoreCase(currEmpKey)) {
                boolean added = false;
                
                if (addPosthumous  &&  ! added  &&  ! data[8].trim().isEmpty()) {
                    String tMeta = currEmperor.getAttribute("meta");
                    String eMeta = tMeta + "-emp-" + currEmpCount++;

                    String emperor = data[8];
                    String emperorHans = mapper.mapTraditionalToSimplified(emperor);
                    boolean sameName = (emperorHans.equals(emperor));

                    Element empWord = doc.createElement("word");
                    empWord.setAttribute("lang", (sameName ? "zh" : "zh-Hant"));
                    empWord.setAttribute("type", tMeta);
                    empWord.setAttribute("meta", eMeta + "|" + getStartYear(data[14]) + "|" + getEndYear(data[14]));
                    empWord.appendChild(doc.createTextNode(emperor));
                    currEmperor.appendChild(empWord);

                    if (! sameName) {
                        Element empWordS = doc.createElement("word");
                        empWordS.setAttribute("lang", "zh-Hans");
                        empWordS.setAttribute("type", tMeta);
                        empWordS.setAttribute("meta", eMeta + "|" + getStartYear(data[14]) + "|" + getEndYear(data[14]));
                        empWordS.appendChild(doc.createTextNode(emperorHans));
                        currEmperor.appendChild(empWordS);
                    }

                    currEmpMeta = eMeta;
                    added = true;
                }
                
                if (addRegnal  &&  ! added  &&  ! data[9].trim().isEmpty()) {
                    String tMeta = currEmperor.getAttribute("meta");
                    String eMeta = tMeta + "-emp-" + currEmpCount++;

                    String emperor = data[9];
                    String emperorHans = mapper.mapTraditionalToSimplified(emperor);
                    boolean sameName = (emperorHans.equals(emperor));
                    
                    Element empWord = doc.createElement("word");
                    empWord.setAttribute("lang", (sameName ? "zh" : "zh-Hant"));
                    empWord.setAttribute("type", tMeta);
                    empWord.setAttribute("meta", eMeta + "|" + getStartYear(data[14]) + "|" + getEndYear(data[14]));
                    empWord.appendChild(doc.createTextNode(emperor));
                    currEmperor.appendChild(empWord);

                    if (! sameName) {
                        Element empWordS = doc.createElement("word");
                        empWordS.setAttribute("lang", "zh-Hans");
                        empWordS.setAttribute("type", tMeta);
                        empWordS.setAttribute("meta", eMeta + "|" + getStartYear(data[14]) + "|" + getEndYear(data[14]));
                        empWordS.appendChild(doc.createTextNode(emperorHans));
                        currEmperor.appendChild(empWordS);
                    }
                    currEmpMeta = eMeta;
                    added = true;
                }

                if (addPersonal  &&  ! added  &&  ! data[10].trim().isEmpty()) {
                    String tMeta = currEmperor.getAttribute("meta");
                    String xxxEmpKey = tMeta + data[10];
                    if (! xxxEmpKey.equals(prevEmpKey)) {
                        prevEmpKey = xxxEmpKey;

                        String emperor = data[10];
                        String emperorHans = mapper.mapTraditionalToSimplified(emperor);
                        boolean sameName = (emperorHans.equals(emperor));

                        String eMeta = tMeta + "-emp-" + currEmpCount++;
                        Element empWord = doc.createElement("word");
                        empWord.setAttribute("lang", (sameName ? "zh" : "zh-Hant"));
                        empWord.setAttribute("type", tMeta);
                        empWord.setAttribute("meta", eMeta + "|" + getStartYear(data[14]) + "|" + getEndYear(data[14]));
                        empWord.appendChild(doc.createTextNode(emperor));
                        currEmperor.appendChild(empWord);

                        if (! sameName) {
                            Element empWordS = doc.createElement("word");
                            empWordS.setAttribute("lang", "zh-Hans");
                            empWordS.setAttribute("type", tMeta);
                            empWordS.setAttribute("meta", eMeta + "|" + getStartYear(data[14]) + "|" + getEndYear(data[14]));
                            empWordS.appendChild(doc.createTextNode(emperorHans));
                            currEmperor.appendChild(empWordS);
                        }

                        currEmpMeta = eMeta;
                    }
                    added = true;
                }

//                if (addTemple  &&  ! added  &&  ! data[11].trim().isEmpty()) {
//                    String tMeta = currEmperor.getAttribute("meta");
//                    String eMeta = tMeta + "-emp-" + currEmpCount++;
//
//                    Element empWord = doc.createElement("word");
//                    empWord.setAttribute("lang", "zh");
//                    empWord.setAttribute("type", tMeta);
//                    empWord.setAttribute("meta", eMeta + "|" + getStartYear(data[14]) + "|" + getEndYear(data[14]));
//                    empWord.appendChild(doc.createTextNode(data[11]));
//                    currEmperor.appendChild(empWord);
//                    currEmpMeta = eMeta;
//                    added = true;
//                }
//
//                if (addCourtesy  &&  ! added  &&  ! data[12].trim().isEmpty()) {
//                    String tMeta = currEmperor.getAttribute("meta");
//                    String eMeta = tMeta + "-emp-" + currEmpCount++;
//
//                    Element empWord = doc.createElement("word");
//                    empWord.setAttribute("lang", "zh");
//                    empWord.setAttribute("type", tMeta);
//                    empWord.setAttribute("meta", eMeta + "|" + getStartYear(data[14]) + "|" + getEndYear(data[14]));
//                    empWord.appendChild(doc.createTextNode(data[12]));
//                    currEmperor.appendChild(empWord);
//                    currEmpMeta = eMeta;
//                    added = true;
//                }
//
//                if (addOther  &&  ! added  &&  ! data[13].trim().isEmpty()) {
//                    String tMeta = currEmperor.getAttribute("meta");
//                    String eMeta = tMeta + "-emp-" + currEmpCount++;
//
//                    Element empWord = doc.createElement("word");
//                    empWord.setAttribute("lang", "zh");
//                    empWord.setAttribute("type", tMeta);
//                    empWord.setAttribute("meta", eMeta + "|" + getStartYear(data[14]) + "|" + getEndYear(data[14]));
//                    empWord.appendChild(doc.createTextNode(data[13]));
//                    currEmperor.appendChild(empWord);
//                    currEmpMeta = eMeta;
//                }

                if (added) {
                    currEmpKey = empKey;
                }

                if (altEmperor != null) {
                    boolean altAdded = false;
                    if (addPosthumous  &&  ! altAdded  &&  ! data[8].trim().isEmpty()) {
                        altAdded = true;
                        String tMeta = altEmperor.getAttribute("meta");
                        String eMeta = tMeta + "-emp-" + currEmpCount++;

                        String emperor = data[8];
                        String emperorHans = mapper.mapTraditionalToSimplified(emperor);
                        boolean sameName = (emperorHans.equals(emperor));

                        Element empWord = doc.createElement("word");
                        empWord.setAttribute("lang", (sameName ? "zh" : "zh-Hant"));
                        empWord.setAttribute("type", tMeta);
                        empWord.setAttribute("meta", eMeta + "|" + getStartYear(data[14]) + "|" + getEndYear(data[14]));
                        empWord.appendChild(doc.createTextNode(emperor));
                        altEmperor.appendChild(empWord);

                        if (! sameName) {
                            Element empWordS = doc.createElement("word");
                            empWordS.setAttribute("lang", "zh-Hans");
                            empWordS.setAttribute("type", tMeta);
                            empWordS.setAttribute("meta", eMeta + "|" + getStartYear(data[14]) + "|" + getEndYear(data[14]));
                            empWordS.appendChild(doc.createTextNode(emperorHans));
                            altEmperor.appendChild(empWordS);
                        }
                    }

                    if (addRegnal  &&  ! altAdded  &&  ! data[9].trim().isEmpty()) {
                        altAdded = true;
                        String tMeta = altEmperor.getAttribute("meta");
                        String eMeta = tMeta + "-emp-" + currEmpCount++;

                        String emperor = data[9];
                        String emperorHans = mapper.mapTraditionalToSimplified(emperor);
                        boolean sameName = (emperorHans.equals(emperor));

                        Element empWord = doc.createElement("word");
                        empWord.setAttribute("lang", (sameName ? "zh" : "zh-Hant"));
                        empWord.setAttribute("type", tMeta);
                        empWord.setAttribute("meta", eMeta + "|" + getStartYear(data[14]) + "|" + getEndYear(data[14]));
                        empWord.appendChild(doc.createTextNode(emperor));
                        altEmperor.appendChild(empWord);

                        if (! sameName) {
                            Element empWordS = doc.createElement("word");
                            empWordS.setAttribute("lang", "zh-Hans");
                            empWordS.setAttribute("type", tMeta);
                            empWordS.setAttribute("meta", eMeta + "|" + getStartYear(data[14]) + "|" + getEndYear(data[14]));
                            empWordS.appendChild(doc.createTextNode(emperorHans));
                            altEmperor.appendChild(empWordS);
                        }
                    }

                    if (addPersonal  &&  ! altAdded  &&  ! data[10].trim().isEmpty()) {
                        altAdded = true;
                        String tMeta = altEmperor.getAttribute("meta");
                        String xxxEmpKey = tMeta + data[10];
                        if (! xxxEmpKey.equals(prevEmpKey)) {
                            prevEmpKey = xxxEmpKey;

                            String emperor = data[10];
                            String emperorHans = mapper.mapTraditionalToSimplified(emperor);
                            boolean sameName = (emperorHans.equals(emperor));

                            String eMeta = tMeta + "-emp-" + currEmpCount++;
                            Element empWord = doc.createElement("word");
                            empWord.setAttribute("lang", (sameName ? "zh" : "zh-Hant"));
                            empWord.setAttribute("type", tMeta);
                            empWord.setAttribute("meta", eMeta + "|" + getStartYear(data[14]) + "|" + getEndYear(data[14]));
                            empWord.appendChild(doc.createTextNode(emperor));
                            altEmperor.appendChild(empWord);

                            if (! sameName) {
                                Element empWordS = doc.createElement("word");
                                empWordS.setAttribute("lang", "zh-Hans");
                                empWordS.setAttribute("type", tMeta);
                                empWordS.setAttribute("meta", eMeta + "|" + getStartYear(data[14]) + "|" + getEndYear(data[14]));
                                empWordS.appendChild(doc.createTextNode(emperorHans));
                                altEmperor.appendChild(empWordS);
                            }
                        }
                    }
                }
            }

            processReign(data);
            processAltReign(data);
        }
    }

    protected void processReign(String[] data) {
        if (currReign != null  &&  ! data[15].trim().isEmpty()  &&  ! data[16].trim().isEmpty()  &&  ! currEmpMeta.trim().isEmpty()) {

            String range = data[18];
            if (range.trim().isEmpty()) {
                range = data[17];
            }

            String reignKey = currEmpMeta + "|" + getStartYear(range);
            if (! reignKey.equalsIgnoreCase(prevReignKey)) {
                Integer count = dynastyCount.getOrDefault(currEmpMeta, Integer.valueOf(0));
                char suffix = (char)('A' + count);
                String meta = currEmpMeta + "-" + suffix;
                dynastyCount.put(currEmpMeta, count+1);

                String eraName = data[16];
                String eraNameHans = mapper.mapTraditionalToSimplified(eraName);
                boolean sameName = (eraNameHans.equals(eraName));

                Element reignWord = doc.createElement("word");
                reignWord.setAttribute("lang", (sameName ? "zh" : "zh-Hant"));
                reignWord.setAttribute("type", currEmpMeta);
                reignWord.setAttribute("meta", meta + "|" + getStartYear(range) + "|" + getEndYear(range));
                reignWord.appendChild(doc.createTextNode(eraName));
                currReign.appendChild(reignWord);

                if (! sameName) {
                    Element reignWordS = doc.createElement("word");
                    reignWordS.setAttribute("lang", "zh-Hans");
                    reignWordS.setAttribute("type", currEmpMeta);
                    reignWordS.setAttribute("meta", meta + "|" + getStartYear(range) + "|" + getEndYear(range));
                    reignWordS.appendChild(doc.createTextNode(eraNameHans));
                    currReign.appendChild(reignWordS);
                }
            }

            prevReignKey = reignKey;
        }
    }

    protected void processAltReign(String[] data) {
        if (altReign != null  &&  ! data[15].trim().isEmpty()  &&  ! data[16].trim().isEmpty()  &&  ! currEmpMeta.trim().isEmpty()) {

            String range = data[18];
            if (range.trim().isEmpty()) {
                range = data[17];
            }

            String reignKey = currEmpMeta + "|" + getStartYear(range);
            if (! reignKey.equalsIgnoreCase(prevReignKey)) {
                Integer count = dynastyCount.getOrDefault(currEmpMeta, Integer.valueOf(0));
                char suffix = (char)('A' + count);
                String meta = currEmpMeta + "-" + suffix;
                dynastyCount.put(currEmpMeta, count+1);

                String eraName = data[16];
                String eraNameHans = mapper.mapTraditionalToSimplified(eraName);
                boolean sameName = (eraNameHans.equals(eraName));

                Element reignWord = doc.createElement("word");
                reignWord.setAttribute("lang", (sameName ? "zh" : "zh-Hant"));
                reignWord.setAttribute("type", currEmpMeta);
                reignWord.setAttribute("meta", meta + "|" + getStartYear(range) + "|" + getEndYear(range));
                reignWord.appendChild(doc.createTextNode(eraName));
                altReign.appendChild(reignWord);

                if (! sameName) {
                    Element reignWordS = doc.createElement("word");
                    reignWordS.setAttribute("lang", "zh-Hans");
                    reignWordS.setAttribute("type", currEmpMeta);
                    reignWordS.setAttribute("meta", meta + "|" + getStartYear(range) + "|" + getEndYear(range));
                    reignWordS.appendChild(doc.createTextNode(eraNameHans));
                    altReign.appendChild(reignWordS);
                }
            }

            prevReignKey = reignKey;
        }
    }

    protected void addComment(Element element, String commentTxt) {
        Comment comment = doc.createComment(commentTxt);
        element.appendChild(comment);
    }

    protected void saveDoc() {
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
    
    protected void createNoDynasty() {
        noDynasty = doc.createElement("word-group");
        root.appendChild(doc.createTextNode("\n\n  "));
        root.appendChild(noDynasty);

        noDynasty.setAttribute("type", "emperor");
        noDynasty.setAttribute("meta", "no-dynasty");
        noDynasty.setAttribute("lang", "zh");
    }

    protected void createDynasty() {
        currDynasty = doc.createElement("word-group");
        root.appendChild(doc.createTextNode("\n\n  "));
        root.appendChild(currDynasty);

        currDynasty.setAttribute("type", "dynasty");
        currDynasty.setAttribute("lang", "zh");
    }

    protected String getStartYear(String range) {
        String fromYr = "";

        range = range.replace('?', ' ');

        int ndx00 = range.indexOf('(');
        if (ndx00 > 0) {
            range = range.substring(0, ndx00);
        }

        int ndx01 = range.lastIndexOf('–');
        if (ndx01 == -1) {
            fromYr = range.trim();
        } else {
            fromYr = range.substring(0, ndx01).trim();
        }

        int ndx02 = fromYr.lastIndexOf('-');
        if (ndx02 == -1) {
            fromYr = fromYr.trim();
        } else {
            fromYr = fromYr.substring(0, ndx02).trim();
        }

        if (fromYr.contains("BC")) {
            fromYr = "-" + fromYr.replaceAll("BC", "").trim();
        } else if (range.endsWith("BC")) {
            fromYr = "-" + fromYr.replaceAll("BC", "").trim();
        }

        if (fromYr.contains("CE")) {
            fromYr = fromYr.replaceAll("CE", "").trim();
        } else if (range.endsWith("CE")) {
            fromYr = fromYr.replaceAll("CE", "").trim();
        }

        try {
            Integer.parseInt(fromYr);
        } catch(NumberFormatException ex) {
            int ndx = fromYr.lastIndexOf(' ');
            if (ndx > 0) {
                fromYr = fromYr.substring(ndx).trim();
            }
        }

        return fromYr.trim();
    }

    protected String getEndYear(String range) {
        String endYr = "";

        range = range.replace('?', ' ');

        int ndx00 = range.indexOf('(');
        if (ndx00 > 0) {
            range = range.substring(0, ndx00);
        }

        int ndx01 = range.lastIndexOf('–');
        if (ndx01 == -1) {
            endYr = range.trim();
        } else {
            endYr = range.substring(ndx01+1).trim();
        }

        int ndx02 = endYr.lastIndexOf('-');
        if (ndx02 == -1) {
            endYr = endYr.trim();
        } else {
            endYr = endYr.substring(0, ndx02).trim();
        }

        if (endYr.contains("BC")) {
            endYr = "-" + endYr.replaceAll("BC", "").trim();
        }

        if (endYr.contains("CE")) {
            endYr = endYr.replaceAll("CE", "").trim();
        } else if (range.endsWith("CE")) {
            endYr = endYr.replaceAll("CE", "").trim();
        }

        try {
            Integer.parseInt(endYr);
        } catch(NumberFormatException ex) {
            int ndx = endYr.lastIndexOf(' ');
            if (ndx > 0) {
                endYr = endYr.substring(ndx).trim();
            }
        }

        return endYr.trim();
    }
}
