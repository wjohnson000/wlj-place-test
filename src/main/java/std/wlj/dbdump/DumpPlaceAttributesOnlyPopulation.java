package std.wlj.dbdump;

import java.io.File;
import javax.sql.DataSource;
import org.familysearch.standards.loader.helper.DbHelper;

import std.wlj.util.DbConnectionManager;

public class DumpPlaceAttributesOnlyPopulation {

    static final String fileBase = "C:/temp/db-dump";
    static final String fileName = "place-attr-hhs-population.txt";

    static final String query =
           "SELECT atr.rep_id, " +
           "       nam.text AS place_name, " +
           "       atr.tran_id, " +
           "       atr.attr_id, " +
           "       atr.attr_type_id, " +
           "       typ.code," +
           "       atr.year, " +
           "       atr.to_year, " +
           "       regexp_replace(atr.title, E'[\\n\\r\\|]+', ' ', 'g') AS title, " +
           "       regexp_replace(atr.attr_value, E'[\\n\\r\\|]+', ' ', 'g') AS attr_value " +
           "  FROM rep_attr AS atr " +
           "  JOIN type AS typ ON typ.type_id = atr.attr_type_id" +
           " LEFT OUTER JOIN rep_display_name AS nam ON nam.rep_id = atr.rep_id AND nam.locale = 'en' " +
           " WHERE atr.tran_id = (SELECT MAX(tran_id) FROM rep_attr AS atrx WHERE atr.attr_id = atrx.attr_id)" +
           "   AND typ.code = 'POP' " +
           "   AND atr.delete_flag = FALSE" +
           "   AND copyright_notice IS NULL " +
           "   AND copyright_url IS NULL " +
//           "   AND atr.rep_id BETWEEN 1000 AND 2000 " +
           " ORDER BY atr.rep_id ASC, atr.attr_id ASC";

    public static void main(String... args) {
        DataSource ds = DbConnectionManager.getDataSourceAwsDev();
        DbHelper dbHelper = new DbHelper(ds);

        long time0 = System.nanoTime();
        long recCount = dbHelper.execQueryAndSave(query, new File(fileBase, fileName), '|');
        long time1 = System.nanoTime();

        System.out.println("Attribute.Row-count: " + recCount + " .. Time=" + (time1-time0) / 1_000_000.0);
        if (args.length == 0) {
            System.exit(0);
        }
    }
}
