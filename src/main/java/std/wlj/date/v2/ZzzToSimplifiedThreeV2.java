/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.familysearch.standards.core.lang.util.ChineseVariants;
import org.familysearch.standards.core.lang.util.TraditionalToSimplifiedChineseMapper;

/**
 * @author wjohnson000
 *
 */
public class ZzzToSimplifiedThreeV2 {

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

    private static String IMPERIAL_ZH_FILE = "C:/temp/imperial_zh.xml";

    private static TraditionalToSimplifiedChineseMapper mapper;

    public static void main(String... args) throws Exception {
        mapper = new TraditionalToSimplifiedChineseMapper();
        ZzzToSimplifiedThreeV2 engine = new ZzzToSimplifiedThreeV2();
        engine.doIt();
        System.exit(0);
    }

    void doIt() throws Exception {
        List<String> data = Files.readAllLines(Paths.get(IMPERIAL_ZH_FILE), StandardCharsets.UTF_8);
        for (String datum : data) {
            Word word = createWordFromLine(datum);
            if (word == null) {
                System.out.println(datum);
            } else {
                String script  = String.valueOf(ChineseVariants.isTraditionalOrSimplified(word.value));
                String nValue  = mapper.mapTraditionalToSimplified(word.value);
                String nScript = String.valueOf(ChineseVariants.isTraditionalOrSimplified(nValue));
                if ("Hani".equals(script)  &&  "Hani".equals(nScript)) {
                    word.lang = "\"zh\"";
                    System.out.println(word);
                } else if ("Hant".equals(script)  &&  "Hant".equals(nScript)) {
                    word.lang = "\"zh-Hant\"";
                    System.out.println(word);
                } else if ("Hans".equals(script)  &&  "Hans".equals(nScript)) {
                    word.lang = "\"zh-Hans\"";
                    System.out.println(word);
                } else if ("Hant".equals(script)  &&  "Hans".equals(nScript)) {
                    word.lang = "\"zh-Hant\"";
                    System.out.println(word);
                    word.lang = "\"zh-Hans\"";
                    word.value = nValue;
                    System.out.println(word);
                } else if ("Hani".equals(script)  &&  "Hans".equals(nScript)) {
                    word.lang = "\"zh\"";
                    System.out.println(word);
                    word.lang = "\"zh-Hans\"";
                    word.value = nValue;
                    System.out.println(word);
                } else if ("Hant".equals(script)  &&  "Hani".equals(nScript)) {
                    word.lang = "\"zh-Hant\"";
                    System.out.println(word);
                    word.lang = "\"zh\"";
                    word.value = nValue;
                    System.out.println(word);
                }
            }
        }
    }

    static Word createWordFromLine(String line) {
        if (! line.contains("<word ")) {
            return null;
        }

        Word word = new Word();
        word.lang = getAttr(line, "lang");
        word.type = getAttr(line, "type");
        word.meta = getAttr(line, "meta");
        word.value = getValue(line);
        word.comment = getComment(line);
        return word;
    }

    static String getAttr(String line, String attr) {
        int ndx01 = line.indexOf(attr);
        int ndx02 = line.indexOf('"', ndx01+attr.length()+2);
        if (ndx01 >= 0  &&  ndx02 > ndx01) {
            return line.substring(ndx01+attr.length()+1, ndx02+1).trim();
        } else {
            return null;
        }
    }

    static String getValue(String line) {
        int ndx01 = line.indexOf('>');
        int ndx02 = line.indexOf('<', ndx01);
        if (ndx01 >= 0  &&  ndx02 > ndx01) {
            return line.substring(ndx01+1, ndx02).trim();
        } else {
            return null;
        }
    }

    static String getComment(String line) {
        int ndx = line.indexOf("<!--");
        return (ndx < 0) ? "" : " " + line.substring(ndx).trim();
    }
}
