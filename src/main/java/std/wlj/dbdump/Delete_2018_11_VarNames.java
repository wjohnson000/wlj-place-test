/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class Delete_2018_11_VarNames {

    static final String baseDir  = "C:/temp/db-dump";
    static final String vNamFile = "variant-name-all.txt";
    static final String sqlFileName = "upd-var-name-%04d.sql";

    static Map<String, Integer> nameCount = new TreeMap<>();
    static Map<String, Integer> deleteCount = new HashMap<>();

    static String INSERT_SQL =
        "  INSERT INTO place_name(name_id, tran_id, text, locale, type_id, place_id, delete_flag)" +
        " VALUES(%s, tranx_id, %s, %s, %s, %s, TRUE);";

    static final String[] beginSQL = {
        "DO $$",
        "DECLARE",
        "  tranx_id INTEGER := NEXTVAL('transaction_tran_id_seq');",
        "BEGIN",
        "  INSERT INTO transaction(tran_id, create_ts, create_id) VALUES(tranx_id, now(), 'system-delete-names');",
        ""
    };

    static final String[] endSQL = {
        "END $$;"    
    };

    public static void main(String... args) throws Exception {
        doPlaceName();
    }

    static void doPlaceName() throws IOException {
        List<String> sqlStuff = new ArrayList<>(50_000);
        Arrays.stream(beginSQL).forEach(sql -> sqlStuff.add(sql));

        int fileCount = 1;
        int lineCnt = 0;
        int sort436Cnt = 0;
        int nondc438Cnt = 0;

        try(FileInputStream fis = new FileInputStream(new File(baseDir, vNamFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("PLC.NAME.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 7) {
                    String placeId  = chunks[0];
                    String deleteId = chunks[1];
                    String locale   = chunks[2];
                    String text     = chunks[3];
                    String nameId   = chunks[4];
                    String typeId   = chunks[5];
                    String tranId   = chunks[6];
                    String isDelete = chunks[7];

                    if (deleteId.isEmpty()) {
                        if (! nameCount.containsKey(placeId)) {
                            nameCount.put(placeId, Integer.valueOf(0));
                        }

                        if (! isDelete.toLowerCase().startsWith("t")) {
                            if ("436".equals(typeId)) {
                                sort436Cnt++;
                                String sql = processName(placeId, nameId, tranId, typeId, locale, text);
                                sqlStuff.add(sql);
                            } else if ("438".equals(typeId)) {
                                nondc438Cnt++;
                                String sql = processName(placeId, nameId, tranId, typeId, locale, text);
                                sqlStuff.add(sql);
                            } else {
                                int cnt = nameCount.get(placeId);
                                nameCount.put(placeId, cnt+1);
                            }
                        }
                    }

                    if (sqlStuff.size() == 50_000) {
                        Arrays.stream(endSQL).forEach(sql -> sqlStuff.add(sql));
                        generateSqlFile(fileCount, sqlStuff);
                        fileCount++;
                        sqlStuff.clear();
                        Arrays.stream(beginSQL).forEach(sql -> sqlStuff.add(sql));
                    }
                }
            }
        }

        Arrays.stream(endSQL).forEach(sql -> sqlStuff.add(sql));
        generateSqlFile(fileCount, sqlStuff);

//        System.out.println();
//        for (Map.Entry<String, Integer> entry : nameCount.entrySet()) {
//            if (entry.getValue() == 0) {
//                System.out.println("No names: " + entry.getKey());
//            }
//        }
//
//        System.out.println();
//        for (Map.Entry<String, Integer> entry : deleteCount.entrySet()) {
//            if (entry.getValue() > 1) {
//                System.out.println("Multiple deletes: " + entry.getKey() + " = " + entry.getValue());
//            }
//        }
        
        System.out.println();
        System.out.println("Total: " + lineCnt);
        System.out.println("  436: " + sort436Cnt);
        System.out.println("  438: " + nondc438Cnt);
    }

    static String processName(String placeId, String nameId, String tranId, String typeId, String locale, String text) {
        int cnt = deleteCount.getOrDefault(placeId, new Integer(0));
        deleteCount.put(placeId, cnt+1);

        if (locale == null  ||  locale.equalsIgnoreCase("null")) {
            System.out.println("NULL locale: " + placeId + " . " + nameId + " . " + tranId);
        }
        if (text == null  ||  text.equalsIgnoreCase("null")) {
            System.out.println("NULL text: " + placeId + " . " + nameId + " . " + tranId);
        }

        return String.format(INSERT_SQL,
            numericData(nameId),
            characterData(text),
            characterData(locale),
            numericData(typeId),
            numericData(placeId));
    }

    static String characterData(String datum) {
        if ("null".equals(datum)) {
            return datum;
        } else {
            return "'" + datum.replace("'", "''") + "'";
        }
    }

    static String numericData(String datum) {
        return datum;
    }

    static void generateSqlFile(int fileCount, List<String> sqlStuff) throws IOException {
        String fileName = String.format(sqlFileName, fileCount);
        System.out.println("Saving file: " + fileName);
        Files.write(Paths.get(baseDir, fileName), sqlStuff, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

}
