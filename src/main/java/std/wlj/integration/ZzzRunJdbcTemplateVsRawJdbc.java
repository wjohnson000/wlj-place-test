package std.wlj.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.familysearch.standards.place.dao.model.DbPlace;
import org.familysearch.standards.place.dao.model.DbPlaceName;

import std.wlj.datasource.DbConnectionManager;

public class ZzzRunJdbcTemplateVsRawJdbc {

    private static final Random random = new Random();
    private static ZzzJdbcTemplateVsRawJdbc engine;

    /**
     * Start this silly thing ...
     * @param args
     */
    public static void main(String[] args) {
        DataSource ds = DbConnectionManager.getDataSourceAwsDev();
        engine = new ZzzJdbcTemplateVsRawJdbc(ds);

        testPlace(500);
    }

    /**
     * Read a bunch of places + names
     */
    private static void testPlace(int count) {
        List<Integer> placeIds = new ArrayList<Integer>();
        placeIds.add(1);
        placeIds.add(1337578);
        for (int i=0;  i<count;  i++) {
            placeIds.add(random.nextInt(6500000));
        }

        System.out.println(" ... Seed database stuff ...");
        // Run through all of the places and place names, to "seed" the database
        for (Integer placeId : placeIds) {
            engine.readPlaceTemplate(placeId);
            engine.readNamesTemplate(placeId);
        }
        System.out.println(" ... Done seed database ...\n");

        // Run through all of the places and place names, timing the PLACE and NAME stuff separately
        int  place02Cnt  = 0;
        int  name02Cnt   = 0;
        long place02Time = 0;
        long name02Time  = 0;
        for (Integer placeId : placeIds) {
            long nnow = System.currentTimeMillis();
            DbPlace dbPlace = engine.readPlaceJDBC(placeId);
            place02Cnt  += (dbPlace == null) ? 0 : 1;
            place02Time += (System.currentTimeMillis() - nnow);

            nnow = System.currentTimeMillis();
            List<DbPlaceName> dbNames = engine.readNamesJDBC(placeId);
            name02Cnt += dbNames.size();
            name02Time += (System.currentTimeMillis() - nnow);
        }

        // Run through all of the places and place names, timing the PLACE and NAME stuff separately
        int  place01Cnt  = 0;
        int  name01Cnt   = 0;
        long place01Time = 0;
        long name01Time  = 0;
        for (Integer placeId : placeIds) {
            long nnow = System.currentTimeMillis();
            DbPlace dbPlace = engine.readPlaceTemplate(placeId);
            place01Cnt  += (dbPlace == null) ? 0 : 1;
            place01Time += (System.currentTimeMillis() - nnow);

            nnow = System.currentTimeMillis();
            List<DbPlaceName> dbNames = engine.readNamesTemplate(placeId);
            name01Cnt += dbNames.size();
            name01Time += (System.currentTimeMillis() - nnow);
        }

        System.out.println("------- Read PLACE and PLACE_NAME ---------------------------------------------------------------");
        System.out.println("Place[Template]: " + place01Cnt + " --> " + place01Time);
        System.out.println("Place[Raw JDBC]: " + place02Cnt + " --> " + place02Time);
        System.out.println();
        System.out.println("Name[Template]: " + name01Cnt + " --> " + name01Time);
        System.out.println("Name[Raw JDBC]: " + name02Cnt + " --> " + name02Time);
    }
}
