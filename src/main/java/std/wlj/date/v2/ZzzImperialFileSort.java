/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class ZzzImperialFileSort {

    private static class WordGroupX {
        static final String WORD_FMT_WITH_TYPE = "  <word-group lang=%s type=%s meta=%s>";
        static final String WORD_FMT_NO_TYPE   = "  <word-group lang=%s meta=%s>";

        String lang;
        String type;
        String meta;
        List<WordX> words = new ArrayList<>(16);

        public String toString() {
            StringBuilder buff = new StringBuilder();
            if (type == null) {
                buff.append(String.format(WORD_FMT_NO_TYPE, lang, meta));
            } else {
                buff.append(String.format(WORD_FMT_WITH_TYPE, lang, type, meta));
            }
            words.forEach(word -> buff.append("\n" + word));
            buff.append("\n  </word-group>");
            return buff.toString();
        }
    }

    private static class WordX implements Comparable<WordX> {
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
        @Override
        public int compareTo(WordX other) {
            String[] mm = PlaceHelper.split(meta, '|');
            int year = (mm.length < 2) ? 0 : Integer.parseInt(mm[1].replace('"', ' ').trim());

            mm = PlaceHelper.split(other.meta, '|');
            int otherYear = (mm.length < 2) ? 0 : Integer.parseInt(mm[1].replace('"', ' ').trim());

            return Integer.compare(year, otherYear);
        }
    }

    private static String IMPERIAL_ZH_FILE = "/org/familysearch/standards/date/shared/imperial_zh.xml";

    public static void main(String... args) throws Exception {
        ZzzImperialFileSort engine = new ZzzImperialFileSort();
        engine.doIt();
        System.exit(0);
    }

    void doIt() throws Exception {
        URL url = this.getClass().getResource(IMPERIAL_ZH_FILE);
        List<String> data = Files.readAllLines(Paths.get(url.toURI()), Charset.forName("UTF-8"));
        WordGroupX currGroup = null;
        for (String datum : data) {
            WordGroupX group = createGroupFromLine(datum);
            if (group != null) {
                currGroup = group;
            }

            WordX word = createWordFromLine(datum);
            if (word != null) {
                if (currGroup == null) {
                    System.out.println(word);
                } else {
                    currGroup.words.add(word);
                }
            }

            if ("</word-group>".equals(datum.trim())) {
                Collections.sort(currGroup.words);
                System.out.println(currGroup);
                if (currGroup.type.equals("reign")) {
                    System.out.println();
                }
                currGroup = null;
            }
        }
    }

    static WordGroupX createGroupFromLine(String line) {
        if (! line.contains("<word-group ")) {
            return null;
        }

        WordGroupX group = new WordGroupX();
        group.lang = getAttr(line, "lang");
        group.type = getAttr(line, "type");
        group.meta = getAttr(line, "meta");
        return group;
    }

    static WordX createWordFromLine(String line) {
        if (! line.contains("<word ")) {
            return null;
        }

        WordX word = new WordX();
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
