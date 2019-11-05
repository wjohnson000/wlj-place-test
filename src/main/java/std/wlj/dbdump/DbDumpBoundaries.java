package std.wlj.dbdump;

import java.io.File;
import javax.sql.DataSource;
import org.familysearch.standards.loader.helper.DbHelper;

import std.wlj.util.DbConnectionManager;

public class DbDumpBoundaries {

    static final String fileBase = "C:/temp/db-dump";
    static final String fileName = "boundary-all.txt";

    static final String query =
        "SELECT rep_id, " +
        "       boundary_id, " +
        "       tran_id, " +
        "       point_count, " +
        "       from_year, " +
        "       to_year, " +
        "       delete_flag " +
        "  FROM rep_boundary " +
        " ORDER BY rep_id, boundary_id, tran_id";

    public static void main(String... args) {
        DataSource ds = DbConnectionManager.getDataSourceAwsDev();
        DbHelper dbHelper = new DbHelper(ds);

        long time0 = System.nanoTime();
        long recCount = dbHelper.execQueryAndSave(query, new File(fileBase, fileName), '|');
        long time1 = System.nanoTime();

        System.out.println("BOUNDARY.Row-count: " + recCount + " .. Time=" + (time1-time0) / 1_000_000.0);
        if (args.length == 0) {
            System.exit(0);
        }
    }
}
