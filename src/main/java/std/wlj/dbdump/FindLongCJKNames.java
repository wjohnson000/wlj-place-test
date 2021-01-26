/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import org.familysearch.standards.core.MultiScriptString;
import org.familysearch.standards.core.lang.TextUtil;
import org.familysearch.standards.loader.sql.FileResultSet;

/**
 * @author wjohnson000
 *
 */
public class FindLongCJKNames {

    private static final String dataDir    = "C:/temp/db-dump";
    private static final String vNameFile  = "variant-name-all.txt";
    private static final String outputFile = "long-cjk-names.xml";

    private static final Map<String, Set<String>> langNames = new TreeMap<>();

    private static final Map<Integer, String>     indentPre = new HashMap<>();
    static {
        indentPre.put(0, "");
        indentPre.put(1, "    ");
        indentPre.put(2, "        ");
        indentPre.put(3, "            ");
    }


    public static void main(String...args) throws Exception {
        File nFile = new File(dataDir, vNameFile);
        FileResultSet nameRS = new FileResultSet();
        nameRS.setSeparator("\\|");
        nameRS.openFile(nFile);

        while (nameRS.next()) {
            String lang = nameRS.getString("locale");
            String text = nameRS.getString("text");

            MultiScriptString mss = TextUtil.interpretText(text);
            if (mss.size() == 1  &&  mss.get(0).getLocale().getScript().isCharacterOriented()) {
                if (text.length() > 4) {
                    Set<String> names = langNames.computeIfAbsent(lang, kk -> new TreeSet<>());
                    names.add(text);
                }
            }
        }

        List<String> results = new ArrayList<>(60_000);
        results.add("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        results.add(openTag(0, "words"));
        for (Map.Entry<String, Set<String>> entry : langNames.entrySet()) {
            results.add(openTag(1, "word-group", "type", "cjk-name", "meta", entry.getKey()));
            for (String name : entry.getValue()) {
                results.add(fullTag(2, "word", name, "lang", entry.getKey()));
            }
            results.add(closeTag(1, "word-group"));
            results.add("");
        }
        results.add(closeTag(0, "words"));

        Files.write(Paths.get(dataDir, outputFile), results, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
 
        nameRS.close();
    }

    static String openTag(int level, String tag, String... attrs) {
        StringBuilder buff = new StringBuilder();

        buff.append(indentPre.getOrDefault(level, "    "));
        buff.append("<").append(tag);
        for (int i=0;  i<attrs.length;  i+=2) {
            buff.append(" ").append(attrs[i]).append("=");
            buff.append('"').append(attrs[i+1]).append('"');
        }
        buff.append(">");

        return buff.toString();
    }

    static String closeTag(int level, String tag) {
        StringBuilder buff = new StringBuilder();

        buff.append(indentPre.getOrDefault(level, "    "));
        buff.append("</").append(tag).append(">");

        return buff.toString();
    }

    static String fullTag(int level, String tag, String value, String... attrs) {
        StringBuilder buff = new StringBuilder();

        buff.append(openTag(level, tag, attrs));
        buff.append(value);
        buff.append(closeTag(0, tag));

        return buff.toString();
    }
}
