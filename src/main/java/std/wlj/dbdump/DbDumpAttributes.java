package std.wlj.dbdump;

import java.io.File;
import javax.sql.DataSource;
import org.familysearch.standards.loader.helper.DbHelper;
import std.wlj.datasource.DbConnectionManager;

public class DbDumpAttributes {

    static final String fileBase = "C:/temp/db-dump";
    static final String fileName = "attribute-all.txt";

    static final String query =
        "SELECT rep_id, " +
        "       attr_id, " +
        "       tran_id, " +
        "       attr_type_id, " +
        "       year, " +
        "       to_year, " +
        "       locale, " + 
        "       regexp_replace(attr_value, E'[\\n\\r\\|]+', ' ', 'g') AS attr_value, " + 
        "       regexp_replace(title, E'[\\n\\r\\|]+', ' ', 'g') AS title, " +
        "       regexp_replace(attr_url, E'[\\n\\r\\|]+', ' ', 'g') AS attr_url, " +
        "       regexp_replace(url_title, E'[\\n\\r\\|]+', ' ', 'g') AS url_title, " +
        "       regexp_replace(copyright_notice, E'[\\n\\r\\|]+', ' ', 'g') AS copyright_notice, " +
        "       regexp_replace(copyright_url, E'[\\n\\r\\|]+', ' ', 'g') AS copyright_url, " +
        "       delete_flag " +
        "  FROM rep_attr " +
        " ORDER BY rep_id, attr_id, tran_id";

    public static void main(String... args) {
        DataSource ds = DbConnectionManager.getDataSourceAwsDev();
        DbHelper dbHelper = new DbHelper(ds);

        long time0 = System.nanoTime();
        long recCount = dbHelper.execQueryAndSave(query, new File(fileBase, fileName), '|');
        long time1 = System.nanoTime();

        System.out.println("ATTRIBUTE.Row-count: " + recCount + " .. Time=" + (time1-time0) / 1_000_000.0);
        if (args.length == 0) {
            System.exit(0);
        }
    }
}
