package std.wlj.dbdump;

import java.io.File;
import javax.sql.DataSource;
import org.familysearch.standards.loader.helper.DbHelper;
import std.wlj.datasource.DbConnectionManager;

public class DbDumpTransactions {

    static final String fileBase = "C:/temp/db-dump";
    static final String fileName = "transaction-all.txt";

    static final String query =
            "SELECT tran_id, create_ts, create_id, create_date " +
            "  FROM transaction " +
            " ORDER BY tran_id";

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
