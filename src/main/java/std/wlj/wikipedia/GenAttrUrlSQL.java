/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.wikipedia;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.familysearch.standards.loader.sql.FileResultSet;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class GenAttrUrlSQL {

    private static final String ATTR_FILE   = "C:/temp/db-dump/attribute-all.txt";
    private static final String TITLE_FILE  = "C:/temp/attr-url-title-easy.txt";
    private static final String SQL_FILE    = "C:/temp/update-attr-%04d.sql";
    private static final Charset UTF_8      = Charset.forName("UTF-8");

    static final int    stmtLimit = 50_000;
    
    private static final String[] beginSQL = {
        "DO $$",
        "BEGIN",
    };

    private static final String[] endSQL = {
        "END $$;"    
    };

    private static final String updateSQL = "  UPDATE rep_attr SET attr_title ='%s' WHERE attr_id = %d AND tran_id = %d;";

    private static Set<String> badTitles = new HashSet<>();
    static {
        badTitles.add("302 found");
        badTitles.add(" moved");
        badTitles.add("search results");
        badTitles.add("error");
    }
    
    public static void main(String... args) {
        Map<String,String> urlTitle = loadTitles();

        List<String> sqlUpdate = new ArrayList<>(stmtLimit);
        Arrays.stream(beginSQL).forEach(sql -> sqlUpdate.add(sql));

        int fileCount = 1;
        try (FileResultSet attrRS = new FileResultSet()) {
            attrRS.setSeparator("\\|");
            attrRS.openFile(ATTR_FILE);
            while (attrRS.next()) {
                int attrId   = attrRS.getInt("attr_id");
                int tranId   = attrRS.getInt("tran_id");
                String value = attrRS.getString("attr_value");

                if (value != null  &&  value.startsWith("http")  &&  urlTitle.containsKey(value)) {
                    String title = urlTitle.get(value).replaceAll("'", "''");
                    boolean isGood = badTitles.stream()
                        .noneMatch(chunk -> title.toLowerCase().contains(chunk));
                    if (isGood) {
                        sqlUpdate.add(String.format(updateSQL, title, attrId, tranId));
                    }
                }

                if (sqlUpdate.size() > stmtLimit) {
                    Arrays.stream(endSQL).forEach(sql -> sqlUpdate.add(sql));
                    saveSQL(sqlUpdate, fileCount);

                    fileCount++;
                    sqlUpdate.clear();
                    Arrays.stream(beginSQL).forEach(sql -> sqlUpdate.add(sql));
                }
            }

            Arrays.stream(endSQL).forEach(sql -> sqlUpdate.add(sql));
            saveSQL(sqlUpdate, fileCount);
        } catch (Exception ex) {
            System.out.println("OOPS: " + ex.getMessage());
        }

    }

    protected static void saveSQL(List<String> sql, int fileCount) throws IOException {
        String fileName = String.format(SQL_FILE, fileCount);
        Files.write(Paths.get(fileName), sql, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    protected static Map<String, String> loadTitles() {
        List<String> titleData;
        try {
            titleData = Files.readAllLines(Paths.get(TITLE_FILE), UTF_8);
            return titleData.stream()
                .map(line -> PlaceHelper.split(line, '|'))
                .filter(arr -> arr.length > 1)
                .collect(Collectors.toMap(
                    arr -> arr[0],
                    arr -> arr[1],
                    (val1, val2) -> val1,
                    TreeMap::new));
        } catch (IOException e) {
            return new TreeMap<>();
        }
    }
}
