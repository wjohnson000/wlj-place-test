package std.wlj.dbdump;

import java.io.File;
import javax.sql.DataSource;
import org.familysearch.standards.loader.helper.DbHelper;
import std.wlj.datasource.DbConnectionManager;

public class DbDumpExtXrefs {

    static final String fileBase = "C:/temp/db-dump";
    static final String fileName = "ext-xref-all.txt";

    static final String query =
        "SELECT xref_id, rep_id, type_id, external_key, pub_flag " +
        "  FROM external_xref " +
        " ORDER BY xref_id";

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
