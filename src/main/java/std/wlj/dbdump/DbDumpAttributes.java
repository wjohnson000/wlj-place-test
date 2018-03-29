package std.wlj.dbdump;

import java.io.File;
import javax.sql.DataSource;
import org.familysearch.standards.loader.helper.DbHelper;
import std.wlj.datasource.DbConnectionManager;

public class DbDumpAttributes {

    static final String fileBase = "C:/temp/db-dump";
    static final String fileName = "attribute-all.txt";

    static final String query =
        "SELECT rep_id, attr_id, tran_id, attr_type_id, year, to_year, locale, attr_value, attr_url, url_title, copyright_notice, copyright_url, delete_flag " +
        "  FROM rep_attr " +
        " ORDER BY rep_id, attr_id, tran_id";

    public static void main(String... args) {
        DataSource ds = DbConnectionManager.getDataSourceAwsDev();
        DbHelper dbHelper = new DbHelper(ds);

        long time0 = System.nanoTime();
        long recCount = dbHelper.execQueryAndSave(query, new File(fileBase, fileName), '|');
        long time1 = System.nanoTime();

        System.out.println("Row-count: " + recCount + " .. Time=" + (time1-time0) / 1_000_000.0);
        System.exit(0);
    }
}
