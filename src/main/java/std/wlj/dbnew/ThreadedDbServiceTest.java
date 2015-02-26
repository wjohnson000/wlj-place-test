package std.wlj.dbnew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.*;
import org.familysearch.standards.place.data.WritableDataService.VariantNameDef;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Kick of several threads to create stuff, make sure that nothing gets the same
 * transaction identifier ...
 * 
 * @author wjohnson000
 *
 */
public class ThreadedDbServiceTest {

    private static String username = "wjohnson000";
    private static Random random   = new Random();

    private static ApplicationContext appContext = null;
    private static DbWritableService  dbWService = null;

    public static void main(String... args) {

        try {
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost-wlj.xml");
            dbWService = new DbWritableService((BasicDataSource)appContext.getBean("dataSource"));

            ExecutorService exSvc = Executors.newFixedThreadPool(10);
            for (int i=0;  i<100;  i++) {
                exSvc.submit(new Runnable() {
                    @Override public void run() {
                        try { Thread.sleep(200L); } catch(Exception ex) { }
                        doStuff();
                    }
                });
            }

            exSvc.shutdown();
            System.out.println("SHUT? " + exSvc.isShutdown());
            System.out.println("TERM? " + exSvc.isTerminated());

            exSvc.awaitTermination(10, TimeUnit.MINUTES);
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            ((ClassPathXmlApplicationContext)appContext).close();
        }

        System.exit(0);
    }

    /**
     * Create and update lots of things ...
     */
    private static void doStuff() {
        String tName = Thread.currentThread().getName();

        PlaceBridge pBridge = createPlace();
        System.out.println(tName + " -- create place: [" + (pBridge==null ? "" : pBridge.getPlaceRevision()) + "]");
        if (pBridge != null) {
            pBridge = updatePlace(pBridge);
            System.out.println(tName + " -- update place: [" + (pBridge==null ? "" : pBridge.getPlaceRevision()) + "]");

            PlaceRepBridge prBridge = createPlaceRep(pBridge.getPlaceId());
            System.out.println(tName + " -- create rep: [" + (prBridge==null ? "" : prBridge.getRevision()) + "]");
            if (prBridge != null) {
                prBridge = updatePlaceRep(prBridge);
                System.out.println(tName + " -- update rep: [" + (prBridge==null ? "" : prBridge.getRevision()) + "]");
                prBridge = updatePlaceRep(prBridge);
                System.out.println(tName + " -- update rep: [" + (prBridge==null ? "" : prBridge.getRevision()) + "]");

                AttributeBridge aBridge = createAttribute(prBridge.getRepId());
                System.out.println(tName + " -- create attr: [" + (aBridge==null ? "" : aBridge.getRevision()) + "]");
                if (aBridge != null) {
                    aBridge = updateAttribute(aBridge);
                    System.out.println(tName + " -- update attr: [" + (aBridge==null ? "" : aBridge.getRevision()) + "]");
                    aBridge = updateAttribute(aBridge);
                    System.out.println(tName + " -- update attr: [" + (aBridge==null ? "" : aBridge.getRevision()) + "]");
                    int aTrx = deleteAttribute(aBridge);
                    System.out.println(tName + " -- Delete attr: [" + aTrx + "]");
                }

                CitationBridge cBridge = createCitation(prBridge.getRepId());
                System.out.println(tName + " -- create citn: [" + (cBridge==null ? "" : cBridge.getRevision()) + "]");
                if (cBridge != null) {
                    cBridge = updateCitation(cBridge);
                    System.out.println(tName + " -- update citn: [" + (cBridge==null ? "" : cBridge.getRevision()) + "]");
                    cBridge = updateCitation(cBridge);
                    System.out.println(tName + " -- update citn: [" + (cBridge==null ? "" : cBridge.getRevision()) + "]");
                    int cTrx = deleteCitation(cBridge);
                    System.out.println(tName + " -- Delete citn: [" + cTrx + "]");
                }
            }
        }
    }

    /**
     * Create and return a PLACE bridge, which is actually a place and a place-rep ..,
     */
    private static PlaceBridge createPlace() {
        int placeTypeId = random.nextInt(314) + 1;

        try {
            PlaceRepBridge prBridge = dbWService.createPlace(
                -1,
                1900,
                null,
                placeTypeId,
                "en",
                makeRepNames("en", "Quilly-"+placeTypeId, "fr", "FR-Quilly-"+placeTypeId),
                40.0,
                -111.1,
                true,
                true,
                null,
                1900,
                2000,
                makePlaceNames("en", "Quilly", "t", "en", "QuillyX", "f", "de", "DE-Quilly", "f", "fr", "FR-Quilly", "f", "es", "es-Quilly", "f"),
                username);
            return prBridge.getAssociatedPlace();
        } catch (PlaceDataException e) {
            System.out.println("Unable to create placep: " + e.getMessage());
            return null;
        }
    }

    /**
     * Update an existing PLACE bridge ...
     */
    private static PlaceBridge updatePlace(PlaceBridge pBridge) {
        try {
            return dbWService.updatePlace(
                pBridge.getPlaceId(),
                pBridge.getFromYear()+1,
                pBridge.getToYear()+1,
                makePlaceNames("en", "Quilly", "t", "en", "QuillyX", "f", "de", "DE-Quilly", "f", "fr", "FR-Quilly", "f", "es", "es-Quilly", "f"),
                username);
        } catch (PlaceDataException e) {
            System.out.println("Unable to update place-rep " + pBridge.getPlaceId() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Create and return a PLACE-REP bridge
     */
    private static PlaceRepBridge createPlaceRep(int placeId) {
        int placeTypeId = random.nextInt(314) + 1;

        try {
            return dbWService.createRep(
                placeId,
                -1,
                1800,
                1900,
                placeTypeId,
                "en",
                makeRepNames("en", "Nerf-"+placeTypeId, "fr", "FR-Nerf-"+placeTypeId),
                11.11,
                -22.22,
                true,
                true,
                null,
                username);
        } catch (PlaceDataException e) {
            System.out.println("Unable to create place-rep: " + e.getMessage());
            return null;
        }
    }

    /**
     * Update an existing PLACE-REP bridge
     */
    private static PlaceRepBridge updatePlaceRep(PlaceRepBridge prBridge) {
        try {
            return dbWService.updateRep(
                prBridge.getRepId(),
                prBridge.getPlaceId(),
                -1,
                prBridge.getJurisdictionFromYear()+1,
                prBridge.getJurisdictionToYear()+1,
                prBridge.getPlaceType().getTypeId(),
                prBridge.getDefaultLocale(),
                prBridge.getAllDisplayNames(),
                prBridge.getLatitude(),
                prBridge.getLongitude(),
                prBridge.isPublished(),
                prBridge.isValidated(),
                null,
                username);
        } catch (PlaceDataException e) {
            System.out.println("Unable to update place-rep " + prBridge.getRepId() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Create and return a new ATTRIBUTE bridge
     */
    private static AttributeBridge createAttribute(int repId) {
        int attrTypeId = random.nextInt(31) + 994;

        try {
            return dbWService.createAttribute(
                repId,
                attrTypeId,
                1910,
                "en",
                "value." + attrTypeId,
                username);
        } catch (PlaceDataException e) {
            System.out.println("Unable to create attribute: " + e.getMessage());
            return null;
        }
    }

    /**
     * Update an existing ATTRIBUTE bridge
     */
    private static AttributeBridge updateAttribute(AttributeBridge aBridge) {
        try {
            return dbWService.updateAttribute(
                aBridge.getAttributeId(),
                aBridge.getPlaceRep().getRepId(),
                aBridge.getType().getTypeId(),
                aBridge.getYear() + 1,
                aBridge.getLocale(),
                aBridge.getValue(),
                username);
        } catch (PlaceDataException e) {
            System.out.println("Unable to update attribute -- " + aBridge.getAttributeId() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Delete an existing ATTRIBUTE bridge
     */
    private static int deleteAttribute(AttributeBridge aBridge) {
        try {
            return dbWService.deleteAttribute(aBridge.getAttributeId(), aBridge.getPlaceRep().getRepId(), username);
        } catch (PlaceDataException e) {
            System.out.println("Unable to delete attribute -- " + aBridge.getAttributeId() + ": " + e.getMessage());
            return 0;
        }
    }

    /**
     * Create and return a new CITATION bridge
     */
    private static CitationBridge createCitation(int repId) {
        int citTypeId = random.nextInt(6) + 1050;
        int sourceId  = random.nextInt(378) + 1;

        try {
            return dbWService.createCitation(
                repId,
                citTypeId,
                sourceId,
                null,
                "Description",
                "Src-Ref" + citTypeId + "." + sourceId,
                username);
        } catch (PlaceDataException e) {
            System.out.println("Unable to create citation: " + e.getMessage());
            return null;
        }

    }

    /**
     * Update an existing CITATION bridge
     */
    private static CitationBridge updateCitation(CitationBridge cBridge) {
        try {
            return dbWService.updateCitation(
                cBridge.getCitationId(),
                cBridge.getPlaceRep().getRepId(),
                cBridge.getType().getTypeId(),
                cBridge.getSource().getSourceId(),
                cBridge.getDate(),
                cBridge.getDescription() + "-X",
                cBridge.getSourceRef(),
                username);
        } catch (PlaceDataException e) {
            System.out.println("Unable to update citation -- " + cBridge.getCitationId() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Delete an existing CITATION bridge
     */
    private static int deleteCitation(CitationBridge cBridge) {
        try {
            return dbWService.deleteCitation(cBridge.getCitationId(), cBridge.getPlaceRep().getRepId(), username);
        } catch (PlaceDataException e) {
            System.out.println("Unable to delete citation -- " + cBridge.getCitationId() + ": " + e.getMessage());
            return 0;
        }
    }
    /**
     * Make a bunch of place-names from locale/text/common triplets
     * @param values locale1, text1, is-common1, locale2, text2, is-common2...
     * @return set of PlaceNameDTO instances
     */
    private static List<VariantNameDef> makePlaceNames(String... values) {
        List<VariantNameDef> names = new ArrayList<>();

        for (int i=0;  i<values.length;  i+=3) {
            VariantNameDef vnDef = new VariantNameDef();
            vnDef.typeId = random.nextInt(25) + 1025;
            vnDef.locale = values[i];
            vnDef.text   = values[i+1];
            names.add(vnDef);
        }

        return names;
    }

    /**
     * Make a bunch of rep-names from locale/text pairs
     * @param values locale1, text1, locale2, text2, ...
     * @return map of locale -> text
     */
    private static Map<String,String> makeRepNames(String... values) {
        Map<String,String> names = new HashMap<String,String>();

        for (int i=0;  i<values.length;  i+=2) {
            names.put(values[i], values[i+1]);
        }

        return names;
    }
}
