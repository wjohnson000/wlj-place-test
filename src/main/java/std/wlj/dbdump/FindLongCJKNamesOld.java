/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

import org.familysearch.standards.core.MultiScriptString;
import org.familysearch.standards.core.lang.TextUtil;
import org.familysearch.standards.loader.sql.FileResultSet;

/**
 * @author wjohnson000
 *
 */
public class FindLongCJKNamesOld {

    private static final String dataDir   = "C:/temp/db-dump";
    private static final String vNameFile = "variant-name-all.txt";

    private static final Map<Integer, Integer>    lengCount = new TreeMap<>();
    private static final Map<String, Set<String>> langNames = new TreeMap<>();

    public static void main(String...args) throws SQLException {
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

                    Integer cnt = lengCount.getOrDefault(text.length(), new Integer(0));
                    lengCount.put(text.length(), cnt+1);
                }
            }
        }

        for (String lang : langNames.keySet()) {
            System.out.println(">>>>>>> " + lang);
            System.out.println("   cnt: " + langNames.get(lang).size());
            langNames.get(lang).forEach(System.out::println);
        }

        System.out.println();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        lengCount.entrySet().forEach(System.out::println);

        System.out.println();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        langNames.entrySet().forEach(ee -> System.out.println(ee.getKey() + " --> " + ee.getValue().size()));

        nameRS.close();
    }
}
