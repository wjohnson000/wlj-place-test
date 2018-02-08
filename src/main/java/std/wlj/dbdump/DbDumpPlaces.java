package std.wlj.dbdump;

import java.io.File;
import javax.sql.DataSource;
import org.familysearch.standards.loader.helper.DbHelper;
import std.wlj.datasource.DbConnectionManager;

public class DbDumpPlaces {

    static final String fileBase = "C:/temp/db-dump";
    static final String fileName = "place-all.txt";

    static final String query =
        "SELECT place_id, tran_id, from_year, to_year, delete_id " +
        "  FROM place AS plc " +
        " WHERE tran_id = (SELECT MAX(tran_id) FROM place AS plcx WHERE plc.place_id = plcx.place_id) " +
        " ORDER BY place_id";

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
