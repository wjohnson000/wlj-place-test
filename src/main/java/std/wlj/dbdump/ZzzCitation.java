package std.wlj.dbdump;

import java.io.File;
import org.familysearch.standards.loader.helper.DbHelper;
import std.wlj.datasource.DbConnectionManager;

public class ZzzCitation {

    private static String repCitationQuery =
        "SELECT cit.rep_id, " +
        "       cit.citation_id, " +
        "       cit.source_id, " +
        "       cit.tran_id, " +
        "       cit.type_id, " +
        "       TO_CHAR(cit.citation_date, 'YYYY-MM-DD') AS citation_date, " +
        "       regexp_replace(cit.description, E'[\\n\\r]+', ' ', 'g') AS description, " +
        "       cit.source_ref, " +
        "       cit.delete_flag " +
        "  FROM citation AS cit " +
//        " WHERE cit.rep_id BETWEEN 1 AND 1000000 " +
        " ORDER BY cit.rep_id ASC, cit.tran_id ASC, cit.citation_id ASC ";

    public static void main(String... args) {
//        DbHelper helper = new DbHelper(DbConnectionManager.getDataSourceAwsDev());
        DbHelper helper = new DbHelper(DbConnectionManager.getDataSourceSams());
        long time0 = System.nanoTime();
        helper.execQueryAndSave(repCitationQuery, new File("C:/temp/zzz-citation-all-SAMS.dat"), '|');
        long time1 = System.nanoTime();
        System.out.println("TIme: " + (time1 - time0) / 1_000_000.0);
    }
}
