package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.place.util.PlaceHelper;

public class S89688_04_GeneratePlaceRepSQL {

    static final int    stmtLimit   = 50_000;
    static final String fileBase    = "C:/temp/delete-by-type";
    static final String inFileName  = "s89688-rep-data-all.txt";
    static final String sqlFileName = "s89688-rep-%04d.sql";

    static final String[] beginSQL = {
        "DO $$",
        "DECLARE",
        "  tranx_id INTEGER := NEXTVAL('transaction_tran_id_seq');",
        "BEGIN",
        "  INSERT INTO transaction(tran_id, create_ts, create_id) VALUES(tranx_id, now(), 'system-delete-place-reps');"
    };

    static final String[] endSQL = {
        "END $$;"    
    };

    public static void main(String...args) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(fileBase, inFileName), Charset.forName("UTF-8"));
        System.out.println("Reps: " + allLines.size());

        int fileCount = 1;
        List<String> sqlStuff = new ArrayList<>(stmtLimit);
        Arrays.stream(beginSQL).forEach(sql -> sqlStuff.add(sql));

        for (String line : allLines) {
            String[] data = PlaceHelper.split(line, '|');
            sqlStuff.add(updateRepSQL(data));
            if (sqlStuff.size() == stmtLimit) {
                Arrays.stream(endSQL).forEach(sql -> sqlStuff.add(sql));
                generateSqlFile(fileCount, sqlStuff);
                fileCount++;
                sqlStuff.clear();
                Arrays.stream(beginSQL).forEach(sql -> sqlStuff.add(sql));
            }
        }

        Arrays.stream(endSQL).forEach(sql -> sqlStuff.add(sql));
        generateSqlFile(fileCount, sqlStuff);

        System.exit(0);
    }

    /**
     * Generate the SQL that will update the place-rep to version it.
     * 
     * @param data Array with all of the existing data fields ... change nothing
     *        but the "delete_id" and the "tran_id"
     */
    static String updateRepSQL(String[] data) {
        if (data.length < 16) {
            System.out.println("Too few fields!! " + Arrays.toString(data));
            return "";
        } else if (! "null".equals(data[9])) {
            System.out.println("Already deleted!! " + Arrays.toString(data));
            return "";
        }

        StringBuilder buff = new StringBuilder();

        buff.append("  INSERT INTO place_rep(rep_id, tran_id, parent_id, owner_id, centroid_long, centroid_lattd, ");
        buff.append("place_type_id, parent_from_year, parent_to_year, delete_id, pref_locale, pub_flag, validated_flag, ");
        buff.append("uuid, group_id, pref_boundary_id) ");
        buff.append("VALUES (").append(numericData(data[0]));
        buff.append(", ").append("tranx_id");
        buff.append(", ").append(numericData(data[2]));
        buff.append(", ").append(numericData(data[3]));
        buff.append(", ").append(numericData(data[4]));
        buff.append(", ").append(numericData(data[5]));
        buff.append(", ").append(numericData(data[6]));
        buff.append(", ").append(numericData(data[7]));
        buff.append(", ").append(numericData(data[8]));
        buff.append(", ").append(numericData(data[2]));   // Use the parent as the new "delete_id"
        buff.append(", ").append(characterData(data[10]));
        buff.append(", ").append(booleanData(data[11]));
        buff.append(", ").append(booleanData(data[12]));
        buff.append(", ").append(characterData(data[13]));
        buff.append(", ").append(numericData(data[14]));
        buff.append(", ").append(numericData(data[15]));
        buff.append(");");

        return buff.toString();
    }

    static String characterData(String datum) {
        if ("null".equals(datum)) {
            return datum;
        } else {
            return "'" + datum + "'";
        }
    }

    static String numericData(String datum) {
        return datum;
    }

    static String booleanData(String datum) {
        return (datum.toLowerCase().startsWith("t")) ? "TRUE" : "FALSE";
    }

    static void generateSqlFile(int fileCount, List<String> sqlStuff) throws IOException {
        String fileName = String.format(sqlFileName, fileCount);
        System.out.println("Saving file: " + fileName);
        Files.write(Paths.get(fileBase, fileName), sqlStuff, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
