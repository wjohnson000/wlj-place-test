package std.wlj.flatfile;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import org.familysearch.standards.loader.sql.FileResultSet;

public class FindRepWithAsianNames {

    private static final String DELIMITER = "\\|";

    private File parentDir = new File("C:/temp/db-dump");
    private FileResultSet fileRS = null;

    public static void main(String...args) throws Exception {
        FindRepWithAsianNames engine = new FindRepWithAsianNames();
        engine.openReader();
        engine.findTargetReps();
        engine.closeReader();
    }

    /**
     * Open the "FileResultSet" for place-rep display names;
     */
    protected void openReader() {
        File aFile = new File(parentDir, "display-name-all.txt");

        fileRS = new FileResultSet();
        fileRS.setSeparator(DELIMITER);
        fileRS.openFile(aFile);
    }

    protected void findTargetReps() throws SQLException {
        int    prevRepId = 0;
        Map<String, String> dispNames = new TreeMap<>();

        System.out.println("repId|en|zh|ja|ko");
        while (fileRS.next()) {
            int     repId   = fileRS.getInt("rep_id");
            int     delId   = fileRS.getInt("delete_id");
            String  locale  = fileRS.getString("locale");
            String  text    = fileRS.getString("text");
            boolean delFlag = fileRS.getBoolean("delete_flag");

            String lang     = locale.substring(0, 2);

            if (delId <= 0  &&  ! delFlag) {
                if (repId != prevRepId) {
                    if (dispNames.size() > 1  &&  dispNames.containsKey("en")) {
                        StringBuilder buff = new StringBuilder();
                        buff.append(repId);
                        buff.append("|").append(dispNames.getOrDefault("en", ""));
                        buff.append("|").append(dispNames.getOrDefault("zh", ""));
                        buff.append("|").append(dispNames.getOrDefault("ja", ""));
                        buff.append("|").append(dispNames.getOrDefault("ko", ""));
                        System.out.println(buff.toString());
                    }
                    dispNames = new TreeMap<>();
                }

                prevRepId = repId;
                if (lang.equals("en")  ||  lang.equals("zh")  ||  lang.equals("ja")  ||  lang.equals("ko")) {
                    dispNames.put(lang, text);
                }
            }
        }
    }

    /**
     * Close all of the files and set the readers back to "null".
     */
    protected void closeReader() {
        fileRS.close();
    }
}
