package std.wlj.dbdump;

import java.io.File;
import javax.sql.DataSource;
import org.familysearch.standards.loader.helper.DbHelper;
import std.wlj.datasource.DbConnectionManager;

public class DbDumpAltJurisdictions {

    static final String fileBase = "C:/temp/db-dump";
    static final String fileName = "alt-jurisdiction-all.txt";

    static final String query =
        "SELECT rep_id, alt_jurisdiction_id, tran_id, related_rep_id, rel_type_id, delete_flag " +
        "  FROM alt_jurisdiction AS altj " +
        " WHERE altj.tran_id = (SELECT MAX(tran_id) FROM alt_jurisdiction AS altjx WHERE altj.rep_id = altjx.rep_id) " +
        " ORDER BY rep_id, alt_jurisdiction_id, tran_id";

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
