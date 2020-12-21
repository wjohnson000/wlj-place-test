package std.wlj.dbdump;

import java.io.File;
import javax.sql.DataSource;
import org.familysearch.standards.loader.helper.DbHelper;

import std.wlj.util.DbConnectionManager;

public class DbDumpPlaceReps {

    static final String fileBase = "C:/temp/db-dump";
    static final String fileName = "place-rep-all.txt";

    static final String query =
        "SELECT rep_id, tran_id, parent_id, owner_id, centroid_long, centroid_lattd, place_type_id,  " +
        "       parent_from_year, parent_to_year, delete_id, pref_locale, pub_flag, validated_flag, " +
        "       uuid, group_id, pref_boundary_id " +
        "  FROM place_rep AS rep " +
        " WHERE tran_id = (SELECT MAX(tran_id) FROM place_rep AS repx WHERE rep.rep_id = repx.rep_id) " +
        " ORDER BY rep_id";

    public static void main(String... args) {
        DataSource ds = DbConnectionManager.getDataSourceAwsDev();
        DbHelper dbHelper = new DbHelper(ds);

        long time0 = System.nanoTime();
        long recCount = dbHelper.execQueryAndSave(query, new File(fileBase, fileName), '|');
        long time1 = System.nanoTime();

        System.out.println("PLACE-REP.Row-count: " + recCount + " .. Time=" + (time1-time0) / 1_000_000.0);
        if (args.length == 0) {
            System.exit(0);
        }
    }
}
