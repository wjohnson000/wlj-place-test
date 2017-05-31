package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Process a file that contains the fixed place (variant) name.  The file is pipe-delimited; the
 * fields we care about are:
 * <ul>
 *   <li><strong>[0]</strong> -- the name-id, DB primary key</li>
 *   <li><strong>[3]</strong> -- the revision number</li>
 *   <li><strong>[5]</strong> -- flag indicating the name is invalid</li>
 *   <li><strong>[7]</strong> -- new text for the name</li>
 * </ul>
 * 
 * @author wjohnson000
 *
 */
public class Story91594GenVariantSQL {

    static final String baseDir = "D:/important/fixed";

    static final String updateNameSql = "UPDATE place_name SET text = '%s' WHERE name_id = %s AND tran_id = %s;";
    static final String deleteNameSql = "UPDATE place_name SET delete_flag = TRUE WHERE name_id = %s AND tran_id = %s;";

    public static void main(String...args) throws IOException {
        List<String> nameFixData = Files.readAllLines(Paths.get(baseDir, "place-name-fixes-03.txt"), Charset.forName("UTF-8"));
        List<String> sqlCommands = new ArrayList<>();

        for (String fixData : nameFixData) {
            String[] chunks  = fixData.split("\\|");
            if (chunks.length > 8) {
                String nameId   =  chunks[0];
                String revision = chunks[3];
                String invalid  = chunks[5];
                String name     = chunks[7];
                String escName  = name.replace("'", "''");
                if ("true".equalsIgnoreCase(invalid)) {
                    sqlCommands.add(String.format(deleteNameSql, nameId, revision));
                } else {
                    sqlCommands.add(String.format(updateNameSql, escName, nameId, revision));
                }
            }
        }

        Files.write(Paths.get(baseDir, "place-name-fixes-03.sql"), sqlCommands, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    
}
