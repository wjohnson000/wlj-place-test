package std.wlj.dbdump;

import java.io.File;
import javax.sql.DataSource;
import org.familysearch.standards.loader.helper.DbHelper;

import std.wlj.util.DbConnectionManager;

public class DbDumpCitations {

    static final String fileBase = "C:/temp/db-dump";
    static final String fileName = "citation-all.txt";

    static final String query =
        "SELECT rep_id, citation_id, tran_id, source_id, type_id, citation_date, " +
        "       regexp_replace(description, E'[\\n\\r\\|]+', ' ', 'g') AS description," +
        "       regexp_replace(source_ref, E'[\\n\\r\\|]+', ' ', 'g') AS source_ref," +
        "       delete_flag " +
        "  FROM citation " +
        " ORDER BY rep_id, citation_id, tran_id";

    public static void main(String... args) {
        DataSource ds = DbConnectionManager.getDataSourceAwsDev();
        DbHelper dbHelper = new DbHelper(ds);

        long time0 = System.nanoTime();
        long recCount = dbHelper.execQueryAndSave(query, new File(fileBase, fileName), '|');
        long time1 = System.nanoTime();

        System.out.println("CITATIONS.Row-count: " + recCount + " .. Time=" + (time1-time0) / 1_000_000.0);
        if (args.length == 0) {
            System.exit(0);
        }
    }
}
