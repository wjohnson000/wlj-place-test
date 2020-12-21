package std.wlj.dbdump;

import java.io.File;
import javax.sql.DataSource;
import org.familysearch.standards.loader.helper.DbHelper;

import std.wlj.util.DbConnectionManager;

public class DbDumpDisplayNames {

    static final String fileBase = "C:/temp/db-dump";
    static final String fileName = "display-name-all.txt";

    static final String query =
            "SELECT rep.rep_id, rep.delete_id, dsp.locale, dsp.text, dsp.tran_id, dsp.delete_flag " +
            "  FROM place_rep AS rep " +
            "  JOIN rep_display_name AS dsp ON dsp.rep_id = rep.rep_id " +
            " WHERE rep.tran_id = (SELECT MAX(tran_id) FROM place_rep AS repx WHERE rep.rep_id = repx.rep_id) " +
            "   AND dsp.tran_id = (SELECT MAX(tran_id) FROM rep_display_name AS dspx WHERE dsp.rep_id = dspx.rep_id AND dsp.locale = dspx.locale) " +
            " ORDER BY rep.rep_id, dsp.locale ";

    public static void main(String... args) {
        DataSource ds = DbConnectionManager.getDataSourceAwsDev();
        DbHelper dbHelper = new DbHelper(ds);

        long time0 = System.nanoTime();
        long recCount = dbHelper.execQueryAndSave(query, new File(fileBase, fileName), '|');
        long time1 = System.nanoTime();

        System.out.println("DISP-NAME.Row-count: " + recCount + " .. Time=" + (time1-time0) / 1_000_000.0);
        if (args.length == 0) {
            System.exit(0);
        }
    }
}
