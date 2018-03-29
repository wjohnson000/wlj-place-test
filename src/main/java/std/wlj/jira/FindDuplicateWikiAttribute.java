package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.familysearch.standards.loader.sql.FileResultSet;

public class FindDuplicateWikiAttribute {

    public static void main(String... args) throws IOException {
        int prevRepId = 0;
        Map<Integer, String> attrMap = new TreeMap<>();
        List<String> allStuff = new ArrayList<>();

        try(FileResultSet rset = new FileResultSet()) {
            rset.setSeparator("\\|");
            rset.openFile("C:/temp/db-dump/attribute-all.txt");

            while (rset.next()) {
                int repId       = rset.getInt("rep_id");
                int attrId      = rset.getInt("attr_id");
                int typeId      = rset.getInt("attr_type_id");
                String year     = rset.getString("year");
                String locale   = rset.getString("locale");
                String value    = rset.getString("attr_value");
                boolean delFlag = rset.getBoolean("delete_flag");

                StringBuilder buff = new StringBuilder();
                buff.append(repId);
                buff.append("|").append(attrId);
                buff.append("|").append(typeId);
                buff.append("|").append(year);
                buff.append("|").append(locale);
                buff.append("|").append(value);

                if (repId != prevRepId) {
                    prevRepId = repId;
                    if (attrMap.size() > 1) {
                        attrMap.values().forEach(data -> allStuff.add(data));
                        allStuff.add("");
                    }
                    attrMap.clear();
                }

                if (delFlag) {
                    attrMap.remove(attrId);
                } else if (474 == typeId) {
                    attrMap.put(attrId, buff.toString());
                }
            }
        } catch(SQLException ex) {
            System.out.println("EX: " + ex.getMessage());
        };

        Files.write(Paths.get("C:/temp/rep-dup-474-attribute.txt"), allStuff, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        System.exit(0);
    }
}
