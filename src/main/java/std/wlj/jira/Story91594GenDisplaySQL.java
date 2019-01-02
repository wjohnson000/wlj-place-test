package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Process a file that contains the fixed rep (display) names.  The file is pipe-delimited; the
 * fields we care about are:
 * <ul>
 *   <li><string>[1]</strong> -- the place-rep identifier</li>
 *   <li><string>[2]</strong> -- the locale</li>
 *   <li><strong>[3]</strong> -- the revision number</li>
 *   <li><strong>[5]</strong> -- flag indicating the name is invalid</li>
 *   <li><strong>[7]</strong> -- new text for the name</li>
 * </ul>
 * 
 * @author wjohnson000
 *
 */
public class Story91594GenDisplaySQL {

    static final String baseDir = "D:/important/fixed";

    static final String updateNameSql = "UPDATE rep_display_name SET text = '%s' WHERE rep_id = %s AND locale = '%s' AND tran_id = %s;";
    static final String deleteNameSql = "UPDATE rep_display_name SET delete_flag = TRUE WHERE rep_id = %s AND locale = '%s' AND tran_id = %s;";

    public static void main(String...args) throws IOException {
        List<String> nameFixData = Files.readAllLines(Paths.get(baseDir, "display-name-fixes-03.txt"), StandardCharsets.UTF_8);
        List<String> sqlCommands = new ArrayList<>();

        for (String fixData : nameFixData) {
            String[] chunks  = fixData.split("\\|");
            if (chunks.length > 8) {
                String repId    = chunks[1];
                String locale   = chunks[2];
                String revision = chunks[3];
                String invalid  = chunks[5];
                String name     = chunks[7];
                String escName  = name.replace("'", "''");

                if ("true".equalsIgnoreCase(invalid)) {
                    sqlCommands.add(String.format(deleteNameSql, repId, locale, revision));
                } else {
                    sqlCommands.add(String.format(updateNameSql, escName, repId, locale, revision));
                }
            }
        }

        Files.write(Paths.get(baseDir, "display-name-fixes-03.sql"), sqlCommands, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    
}
