package std.wlj.dbdump;

import java.io.File;
import javax.sql.DataSource;
import org.familysearch.standards.loader.helper.DbHelper;
import std.wlj.datasource.DbConnectionManager;

public class DbDumpVariantNames {

    static final String fileBase = "C:/temp/db-dump";
    static final String fileName = "variant-name-all.txt";

    static final String query =
            "SELECT plc.place_id, plc.delete_id, var.locale, var.text, var.name_id, var.type_id, var.tran_id, var.delete_flag " +
            "  FROM place AS plc " +
            "  JOIN place_name AS var ON var.place_id = plc.place_id " +
            " WHERE plc.tran_id = (SELECT MAX(tran_id) FROM place AS plcx WHERE plcx.place_id = plc.place_id) " +
            "   AND var.tran_id = (SELECT MAX(tran_id) FROM place_name AS varx WHERE varx.name_id = var.name_id  AND varx.place_id = plc.place_id) " +
            " ORDER BY place_id, name_id, tran_id";

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
