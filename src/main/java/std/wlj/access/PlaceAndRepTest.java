package std.wlj.access;

import java.util.*;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.*;
import org.familysearch.standards.place.data.WritableDataService.VariantNameDef;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.DbConnectionManager;
import std.wlj.util.SolrManager;
import std.wlj.util.DbConnectionManager.DbServices;


/**
 * This class creates three places in a hierarchy, and five place-reps, one each for 
 * the first two levels, three for the last level.  We can run updates and deletes
 * for each one ...
 * 
 * To clean up the goo, run the following SQL:
 *    SET SCHEMA 'sams_place';
 * 
 *    DELETE FROM place_name WHERE name_id >= 1000000;
 *    DELETE FROM rep_display_name WHERE rep_id >= 1000000;
 *    DELETE FROM place_rep WHERE rep_id >= 1000000;
 *    DELETE FROM place WHERE place_id >= 1000000;
 * 
 *    ALTER SEQUENCE seq_place RESTART WITH 1000000;
 *    ALTER SEQUENCE seq_place_name RESTART WITH 1000000;
 *    ALTER SEQUENCE seq_place_rep RESTART WITH 1000000;
 * 
 * @author wjohnson000
 *
 */
public class PlaceAndRepTest {

    private static Random random = new Random();

    private static DbServices dbServices;
    private static SolrService solrService;
    private static PlaceDataServiceImpl dataService;

    public static void main(String[] args) throws Exception {
        dbServices = DbConnectionManager.getDbServicesWLJ();
        solrService = SolrManager.awsIntService(true);
        dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

        createPlacesAndReps();

        if (dataService != null) dataService.shutdown();
        if (dbServices != null) dbServices.shutdown();
        if (solrService != null) solrService.shutdown();

        System.exit(0);
    }

    /**
     * Read place-reps by place [parent] id
     */
    private static void createPlacesAndReps() {
        long nnow = System.currentTimeMillis();

        try {
            PlaceRepBridge placeRepB01 = createQuilly();
            PlaceBridge    placeB01a   = placeRepB01.getAssociatedPlace();
            PlaceBridge    placeB01b   = dbServices.readService.getPlace(placeB01a.getPlaceId());

            PlaceRepBridge placeRepB02 = createQually(placeRepB01.getRepId());
            PlaceBridge    placeB02a   = placeRepB02.getAssociatedPlace();
            PlaceBridge    placeB02b   = dbServices.readService.getPlace(placeB02a.getPlaceId());

            PlaceRepBridge placeRepB03 = createNerf01(placeRepB02.getRepId());
            PlaceBridge    placeB03a   = placeRepB03.getAssociatedPlace();
            PlaceBridge    placeB03b   = dbServices.readService.getPlace(placeB03a.getPlaceId());

            PlaceRepBridge placeRepB04 = createNerfRep02(placeB03a.getPlaceId(), placeRepB02.getRepId());
            PlaceRepBridge placeRepB05 = createNerfRep03(placeB03a.getPlaceId(), placeRepB02.getRepId());

            PlaceRepBridge placeRepB01R = dbServices.readService.getRep(placeRepB01.getRepId());
            PlaceRepBridge placeRepB02R = dbServices.readService.getRep(placeRepB02.getRepId());
            PlaceRepBridge placeRepB03R = dbServices.readService.getRep(placeRepB03.getRepId());
            PlaceRepBridge placeRepB04R = dbServices.readService.getRep(placeRepB04.getRepId());
            PlaceRepBridge placeRepB05R = dbServices.readService.getRep(placeRepB05.getRepId());

            // Update a place
            PlaceBridge    placeB01U    = updateQuillyPlace(placeB01a.getPlaceId());
            PlaceBridge    placeB01Ra   = dbServices.readService.getPlace(placeB01U.getPlaceId());

            // Update a place-rep
            PlaceRepBridge placeRepB03U  = updateNerfPlaceRep01(placeRepB03.getRepId(), placeB03a.getPlaceId(), placeRepB02.getRepId());
            PlaceRepBridge placeRepB03UR = dbServices.readService.getRep(placeRepB03U.getRepId());

            // Retrieve children of PlaceRep01, anyone that has Place01 as the owner
            List<PlaceRepBridge> placeRepB01C  = placeB01a.getContainedPlaceReps();
            List<PlaceRepBridge> placeRebP01CX = placeRepB01.getChildren();

            // Delete a place-rep
            PlaceRepBridge placeRepB03D  = dataService.deleteRep(placeRepB03.getRepId(), placeRepB04.getRepId(), "wjohnson000", null);
            PlaceRepBridge placeRepB03DX = dbServices.readService.getRep(placeRepB03.getRepId());
            PlaceRepBridge placeRepB04DX = dbServices.readService.getRep(placeRepB04.getRepId());

            List<PlaceRepBridge> placeRepB01C02  = placeB01a.getContainedPlaceReps();
            List<PlaceRepBridge> placeRebP01CX02 = placeRepB01.getChildren();

            System.out.println("Place01: Created, Read, Updated, Read, [Deleted] ...");
            output(placeRepB01);
            output(placeB01a);
            output(placeB01b);
            output(placeRepB01R);

            System.out.println("\nPlace02: Created, Read, Target of Update ...");
            output(placeRepB02);
            output(placeB02a);
            output(placeB02b);
            output(placeRepB02R);

            System.out.println("\nPlace03: Created ...");
            output(placeRepB03);
            output(placeB03a);
            output(placeB03b);
            output(placeRepB03R);

            System.out.println("\nRep01: Created, Read ...");
            output(placeRepB04);
            output(placeRepB04R);

            System.out.println("\nRep02: Created, Read ...");
            output(placeRepB05);
            output(placeRepB05R);

            System.out.println("\nPlace01: Updated, Read ...");
            output(placeB01U);
            output(placeB01Ra);

            System.out.println("\nRep03b: Updated, Read ...");
            output(placeRepB03U);
            output(placeRepB03UR);

            System.out.println("\nRep03: Deleted, Read ...");
            output(placeRepB03D);
            output(placeRepB03DX);
            output(placeRepB04DX);

            System.out.println("\nOwner01: Before ...");
            for (PlaceRepBridge placeRepB : placeRepB01C) {
                output(placeRepB);
            }

            System.out.println("\nOwner01: After ...");
            for (PlaceRepBridge placeRepB : placeRepB01C02) {
                output(placeRepB);
            }

            System.out.println("\nChild01: Before ...");
            for (PlaceRepBridge placeRepB : placeRebP01CX) {
                output(placeRepB);
            }

            System.out.println("\nChild01: After ...");
            for (PlaceRepBridge placeRepB : placeRebP01CX02) {
                output(placeRepB);
            }
        } catch (PlaceDataException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nRun-Time: " + (System.currentTimeMillis()-nnow));
        System.exit(0);
    }

    /**
     * Create "Quilly", the top-level place + place-rep in our neat-o new tree
     * @return
     * @throws PlaceDataException 
     */
    private static PlaceRepBridge createQuilly() throws PlaceDataException {
        return dataService.createPlace(
            -1,
            1900,
            null,
            281,
            "en",
            makeRepNames("en", "Quilly", "fr", "FR-Quilly", "de", "DE-Quilly"),
            40.0,
            -111.1,
            true,
            true,
            null,
            1900,
            2000,
            makePlaceNames("en", "Quilly", "t", "en", "QuillyX", "f", "de", "DE-Quilly", "f", "fr", "FR-Quilly", "f", "es", "es-Quilly", "f"),
            "wjohnson000",
            null);
    }

    /**
     * Create an updated "Quilly", the top-level place in our neat-o new tree
     * @return
     * @throws PlaceDataException 
     */
    private static PlaceBridge updateQuillyPlace(int placeId) throws PlaceDataException {
        return dataService.updatePlace(
            placeId,
            1925,
            null,
            makePlaceNames("en", "Quilly", "t", "en", "QuillyX", "f", "de", "DE-Quilly", "f", "fr", "FR-Quilly", "t", "da", "da-Quilly", "f"),
            "wjohnson000",
            null
        );
    }

    /**
     * Create "Qually", the second-level place + place-rep in our neat-o new tree
     * @return
     * @throws PlaceDataException 
     */
    private static PlaceRepBridge createQually(int parentId) throws PlaceDataException {
        return dataService.createPlace(
            parentId,
            1900,
            null,
            281,
            "en",
            makeRepNames("en", "Qually", "fr", "FR-Qually", "de", "DE-Qually"),
            40.0,
            -111.2,
            true,
            true,
            null,
            1900,
            2000,
            makePlaceNames("en", "Qually", "t", "en", "QuallyX", "f", "de", "DE-Qually", "f", "fr", "FR-Qually", "f", "es", "es-Qually", "f"),
            "wjohnson000",
            null);
    }

    /**
     * Create "Nerf", the third-level place + place-rep in our neat-o new tree
     * @return
     * @throws PlaceDataException 
     */
    private static PlaceRepBridge createNerf01(int parentId) throws PlaceDataException {
        return dataService.createPlace(
            parentId,
            1910,
            1925,
            281,
            "en",
            makeRepNames("en", "Nerf Territory", "fr", "FR-Nerf-Terr", "da", "DA-Nerf-Terr"),
            40.0,
            -111.4,
            true,
            true,
            null,
            null,
            null,
            makePlaceNames("en", "Nerf", "f", "en", "NerfX", "f", "de", "DE-Nerf", "f", "fr", "FR-Nerf", "f", "es", "es-Nerf", "f"),
            "wjohnson000",
            null);
    }

    /**
     * Create "Nerf02" place-rep
     * 
     * @param jurisChain jurisdictionChain
     * @return
     * @throws PlaceDataException 
     */
    private static PlaceRepBridge createNerfRep02(int placeId, int parentId) throws PlaceDataException {
        return dataService.createRep(
            placeId,
            parentId,
            1925,
            1968,
            281,
            "en",
            makeRepNames("en", "Nerf Province", "fr", "FR-Nerf-Prov", "de", "DE-Nerf-Prov"),
            40.0,
            -111.3,
            true,
            true,
            null,
            "wjohnson000",
            null
        );
    }

    /**
     * Create "Nerf03" place-rep
     * 
     * @param jurisChain jurisdictionChain
     * @return
     * @throws PlaceDataException 
     */
    private static PlaceRepBridge createNerfRep03(int placeId, int parentId) throws PlaceDataException {
        return dataService.createRep(
            placeId,
            parentId,
            1968,
            null,
            281,
            "en",
            makeRepNames("en", "Nerf State", "fr", "FR-Nerf-State", "de", "DE-Nerf-State"),
            40.0,
            -111.3,
            true,
            true,
            null,
            "wjohnson000",
            null
        );
    }

    private static PlaceRepBridge updateNerfPlaceRep01(int repId, int placeId, int parentId) throws PlaceDataException {
        return dataService.updateRep(
            repId,
            placeId,
            parentId,
            1910,
            1925,
            281,
            "en",
            makeRepNames("en", "Nerf Territory", "fr", "FR-Nerf-Terr", "da", "DA-Nerf-Terr"),
            40.0,
            -111.4,
            true,
            true,
            null,
            null,
            "wjohnson000",
            null);
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
            vnDef.typeId = random.nextInt(25) + 434;
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

    /**
     * Dump out the details of a Place in a sorta' nice format
     * @param placeB
     */
    private static void output(PlaceBridge placeB) {
        if (placeB == null) {
            System.out.println("PLACE: " + null);
        } else {
            System.out.println("PLACE: " + placeB.getPlaceId() + "|" + placeB.getPlaceRevision() + "|" +
                    placeB.getFromYear() + "|" + placeB.getToYear());
            for (PlaceNameBridge nameB : placeB.getAllVariantNames()) {
                System.out.println("       " + nameB.getNameId() + "|" + nameB.getType().getCode() + "|" +
                    nameB.getName().getLocale() + "|" + nameB.getName().get());
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
            System.out.println("REP: " + placeRepB.getRepId() + "|" + niceJurisdiction(placeRepB.getJurisdictionIdentifiers()) + "|" + 
                placeRepB.getRevision() + "|" + placeRepB.getPlaceId() + "|" + placeRepB.getDefaultLocale() + "|" +
                placeRepB.getPlaceType().getCode() + "|" + placeRepB.getLatitude() + "|" + placeRepB.getLongitude() + "|" +
                placeRepB.getJurisdictionFromYear() + "|" + placeRepB.getJurisdictionToYear() + "|" + placeRepB.isPublished() + "|" +
                placeRepB.isValidated() + "|" + placeRepB.getUUID() + "|" + placeRepB.getChildConstraintTypeGroup());
            for (Map.Entry<String,String> entry : placeRepB.getAllDisplayNames().entrySet()) {
                System.out.println("     " + entry.getKey() + "|" + entry.getValue());
            }
        }
    }

    /**
     * Collect the jurisdiction chain ... 
     * @param jurisChain jurisdiction chain
     * @return
     */
    private static String niceJurisdiction(int[] jurisChain) {
        StringBuilder buff = new StringBuilder();
        for (int id : jurisChain) {
            if (buff.length() > 0) {
                buff.append(",");
            }
            buff.append(id);
        }
        return buff.toString();
    }

}
