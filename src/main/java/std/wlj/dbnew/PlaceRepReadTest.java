package std.wlj.dbnew;

import java.util.*;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.*;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Read one or more Place-Reps ...
 * 
 * @author wjohnson000
 *
 */
public class PlaceRepReadTest {

    static BasicDataSource ds = null;
    static DbReadableService dbRService = null;
    static DbWritableService dbWService = null;

    public static void main(String[] args) throws Exception {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("postgres-context-aws-prod.xml");
        ds = (BasicDataSource)appContext.getBean("dataSource");
        dbRService = new DbReadableService(ds);
        dbWService = new DbWritableService(ds);

//        readRep(8911772);
//        readRep(10334653);
//        readRep(1442484);
        readRep(8186295);

        ((ClassPathXmlApplicationContext)appContext).close();
        System.exit(0);
    }

    private static void readRep(int repId) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        PlaceRepBridge prBridge = dbRService.getRep(repId, null);
        output(prBridge);

        Set<Integer> ids = new HashSet<>();
        PlaceRepBridge[] jurisB = prBridge.getJurisdictions();
        for (PlaceRepBridge prb : jurisB) {
            if (ids.contains(prb.getRepId())) {
                System.out.println("CYCLE: " + prb.getRepId());
            } else {
                ids.add(prb.getRepId());
                output(prb);
            }
        }
    }

    /**
     * Dump out the details of a Place-Rep in a sorta' nice format
     * @param placeRepB
     */
    private static void output(PlaceRepBridge placeRepB) {
        if (placeRepB == null) {
            System.out.println("REP: " + null);
        } else {
            System.out.println("Has Cycle? " + dbRService.hasChainCycle(placeRepB.getRepId()));
            System.out.println("REP: " + placeRepB.getRepId() + "|" + Arrays.toString(placeRepB.getJurisdictionIdentifiers()) + "|" + 
                placeRepB.getRevision() + "|" + placeRepB.getPlaceId() + "|" + placeRepB.getDefaultLocale() + "|" +
                placeRepB.getPlaceType().getCode() + "|" + placeRepB.getLatitude() + "|" + placeRepB.getLongitude() + "|" +
                placeRepB.getJurisdictionFromYear() + "|" + placeRepB.getJurisdictionToYear() + "|" + placeRepB.isPublished() + "|" +
                placeRepB.isValidated() + "|" + placeRepB.getUUID() + "|" + placeRepB.isDeleted());
            int count = 0;
            for (Map.Entry<String,String> entry : placeRepB.getAllDisplayNames().entrySet()) {
                if (count < 5  ||  entry.getKey().toLowerCase().contains("en")) {
                    count++;
                    System.out.println("     " + entry.getKey() + "|" + entry.getValue());
                }
            }
        }
    }
}
