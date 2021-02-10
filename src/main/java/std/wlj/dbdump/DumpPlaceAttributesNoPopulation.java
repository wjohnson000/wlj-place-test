package std.wlj.dbdump;

import java.io.File;
import javax.sql.DataSource;
import org.familysearch.standards.loader.helper.DbHelper;

import std.wlj.util.DbConnectionManager;

public class DumpPlaceAttributesNoPopulation {

    static final String fileBase = "C:/temp/db-dump";
    static final String fileName = "place-attr-hhs.txt";

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
           "       regexp_replace(atr.attr_value, E'[\\n\\r\\|]+', ' ', 'g') AS attr_value, " +
           "       atr.locale, " +
           "       regexp_replace(atr.attr_url, E'[\\n\\r\\|]+', ' ', 'g') AS attr_url, " +
           "       regexp_replace(atr.url_title, E'[\\n\\r\\|]+', ' ', 'g') AS url_title, " +
           "       regexp_replace(atr.copyright_notice, E'[\\n\\r\\|]+', ' ', 'g') AS copyright_notice, " +
           "       regexp_replace(atr.copyright_url, E'[\\n\\r\\|]+', ' ', 'g') AS copyright_url " +
           "  FROM rep_attr AS atr " +
           "  JOIN type AS typ ON typ.type_id = atr.attr_type_id " +
           " LEFT OUTER JOIN rep_display_name AS nam ON nam.rep_id = atr.rep_id AND nam.locale = 'en' " +
           " WHERE atr.tran_id = (SELECT MAX(tran_id) FROM rep_attr AS atrx WHERE atr.attr_id = atrx.attr_id) " +
           "   AND typ.code in ('ART',  'CLIMATE',  'CLOTHING_FASHION',  'COMMUNICATION',  'CUSTOMS',  'DENOM',  'DISASTERS'," +
           "                    'EDUCATION',  'ENTERTAINMENT',  'ETYMOLOGY',  'FOOD_RECIPES',  'GEOGRAPHIC_INFO',  'HISTORIC_INFO'," +
           "                    'HISTORICAL_MAPS',  'HISTORY',  'HOLIDAYS_CELEBRATIONS',  'INCORP_DATE',  'INVENTIONS_TECH'," +
           "                    'LDS_TMPLST',  'MEDICAL ',  'MIGRATIONS',  'MUSIC',  'NEWSPAPERS',  'NOTE',  'OCCUPATIONS'," +
           "                    'PARISH_REGISTER',  'POLITIC_INFO',  'POP_CULTURE',  'PROMINENT_PEOPLE',  'RELIG_HISTORY'," +
           "                    'SPORT',  'STORY',  'TRANSPORT',  'TRIVIA',  'WORK_CHORES') " +
           "   AND atr.delete_flag = FALSE" +
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
