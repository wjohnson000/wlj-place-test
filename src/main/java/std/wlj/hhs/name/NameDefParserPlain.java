/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wjohnson000
 *
 */
public class NameDefParserPlain implements NameDefParser {

    private static final Set<String> TAGS_TO_REMOVE = new HashSet<>();
    static {
        TAGS_TO_REMOVE.add("date");
        TAGS_TO_REMOVE.add("div1");
        TAGS_TO_REMOVE.add("nameGrp");
        TAGS_TO_REMOVE.add("note");
        TAGS_TO_REMOVE.add("sc");
        TAGS_TO_REMOVE.add("xref");
        TAGS_TO_REMOVE.add("xrefGrp");
    }


    @Override
    public NameDef parseXml(String xml) {
        NameDef nameDef = new NameDef();

        nameDef.id = getAttrValue(xml, "e", "id");
        nameDef.text = getTagValue(xml, "headword");
        nameDef.language = getTagValue(xml, "span");
        nameDef.language = "en";
        nameDef.refId = getAttrValue(xml, "xref", "ref");
        nameDef.definition = cleanup(getTagValue(xml, "textMatter"));
        nameDef.type = this.extractTypeFromDefinition(getTagValue(xml, "div1"));
        nameDef.isMale = xml.indexOf(MALE_CHAR) > 0;
        nameDef.isFemale = xml.indexOf(FEMALE_CHAR) > 0;
        nameDef.variants = getVariants(xml);

        return (nameDef.id == null) ? null : nameDef;
    }

    static String getAttrValue(String row, String tag, String key) {
        int ndx0 = row.indexOf("<" + tag);
        int ndx1 = row.indexOf(" " + key + "=", ndx0 + 1);

        if (ndx0 >=0  &&  ndx1 > 0) {
            int ndx2 = row.indexOf('"', ndx1 + key.length() + 3);
            return row.substring(ndx1 + key.length() + 3, ndx2);
        } else {
            return null;
        }
    }

    String getTagValue(String row, String tag) {
        List<String> values = getTagValueMulti(row, tag);
        return (values.isEmpty()) ? null : values.get(0);
    }

    List<String> getTagValueMulti(String row, String tag) {
        List<String> results = new ArrayList<>();

        String tRow = (row == null) ? "" : row;
        while (! tRow.isEmpty()) {
            int ndx0 = tRow.indexOf("<" + tag + ">");
            if (ndx0 < 0) {
                ndx0 = tRow.indexOf("<" + tag + " ");
            }
            int ndx1 = tRow.indexOf(">", ndx0 + 1);
            
            if (ndx0 >= 0  &&  ndx1 > 0) {
                int ndx2 = tRow.indexOf("</" + tag, ndx1 + 1);
                if (ndx2 > 0) {
                    results.add(tRow.substring(ndx1 + 1, ndx2));
                    tRow = tRow.substring(ndx2 + tag.length());
                } else {
                    tRow = "";
                }
            } else {
                tRow = "";
            }
        }

        return results;
    }

    List<NameDef> getVariants(String row) {
        List<NameDef> variants = new ArrayList<>();

        List<String> notes = getTagValueMulti(row, "note");
        for (String note : notes) {
            String sc = getTagValue(note, "sc");
            List<String> varNames = getTagValueMulti(note, "nameGrp");
            for (String varName : varNames) {
                NameDef varDef = new NameDef();
                varDef.id = "";
                varDef.text = varName.replaceAll("<b>", "").replaceAll("</b>", "").trim();
                varDef.language = "";
                varDef.type = this.extractTypeFromDefinitionVariant(sc);
                variants.add(varDef);
            }
        }

        return variants;
    }

    String cleanup(String text) {
        if (text == null) {
            return null;
        }

//        String tText = removeComments(text);
        String tText = text;
        for (String tag : TAGS_TO_REMOVE) {
            tText = removeTag(tText, tag);
        }

        return tText;
    }

    String removeComments(String text) {
        int ndx0 = text.indexOf("<!--");
        int ndx1 = text.indexOf("-->", ndx0);
        if (ndx0 >= 0  &&  ndx1 > ndx0) {
            String chunk0 = (ndx0 == 0) ? "" : text.substring(0, ndx0);
            String chunk1 = (ndx1+3 > text.length()) ? "" : text.substring(ndx1+3);
            return (removeComments(chunk0 + chunk1));
        } else {
            return text;
        }
    }

    String removeTag(String text, String tag) {
        String content = getTagValue(text, tag);

        int ndx0 = text.indexOf("<" + tag + ">");
        if (ndx0 == -1) {
            ndx0 = text.indexOf("<" + tag + " ");
        }
        int ndx1 = text.indexOf("</" + tag +">", ndx0);
        if (ndx0 >= 0  &&  ndx1 > ndx0) {
            String chunk0 = (ndx0 == 0) ? "" : text.substring(0, ndx0);
            String chunk1 = (ndx1+tag.length()+3 > text.length()) ? "" : text.substring(ndx1+tag.length()+3);
            return (removeTag(chunk0 + content + chunk1, tag));
        } else {
            return text;
        }
    }
}
