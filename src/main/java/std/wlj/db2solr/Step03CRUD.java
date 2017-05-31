package std.wlj.db2solr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.PlaceBridge;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceNameBridge;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.WritableDataService.VariantNameDef;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.service.DbReadableService;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.datasource.DbConnectionManager.DbServices;
import std.wlj.util.SolrManager;

/**
 * Create, Update and Delete lots of stuff ...
 * 
 * @author wjohnson000
 *
 */
public class Step03CRUD {

    private static DbReadableService readService;
    private static PlaceDataServiceImpl dataService;
    private static String wlj = "wjohnson000";

    public static void main(String... args) {
        DbServices dbServices = DbConnectionManager.getDbServicesSams();
        SolrService solrService = SolrManager.localHttpService();
        readService = dbServices.readService;
        dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

        try {
            createAustralia();
            createBloomington();

            editPlaceRep(5, "eu");
            editPlaceRep(12, "eu");
            editPlaceRep(16, "eu");
            editPlaceRep(19, "eu");

            createAndEditHorsens();

            editProvoPlace();

            editPlaceRep(5, "sl");
            editPlaceRep(12, "sl");
            editPlaceRep(16, "sl");
            editPlaceRep(19, "sl");

            createAndDeleteProvo();

            editPlaceRep(5, "xh");
            editPlaceRep(12, "xh");
            editPlaceRep(16, "xh");
            editPlaceRep(19, "xh");
            
        } catch(PlaceDataException ex) {
            System.out.println("EX: " + ex.getMessage());
            ex.printStackTrace();
        }

        dataService.shutdown();
        dbServices.shutdown();

        System.exit(0);
    }

    /**
     * Edit a place-rep
     * 
     * @param repId place-rep identifier
     * @param locale a locale for a new name
     * @param iter iteration number, which will control some data
     * @throws PlaceDataException if something bad happens
     */
    private static void editPlaceRep(int repId, String locale) throws PlaceDataException {
        PlaceRepBridge repB = readService.getRep(repId);

        Double lattd = repB.getLatitude();
        Double longt = repB.getLongitude();
        if (lattd != null) lattd += 1;
        if (longt != null) longt -= 1;

        Map<String,String> dispNames = repB.getAllDisplayNames();
        String enName = dispNames.get("en");
        if (enName != null) {
            dispNames.put(locale, enName+"." + locale);
            dispNames.put("en", enName+".x");
        }

        int[] jurisIds = repB.getJurisdictionIdentifiers();
        int parentId = (jurisIds == null  ||  jurisIds.length < 2) ? -1 : jurisIds[1];
        Integer groupId = (repB.getChildConstraintTypeGroup() == null) ? null : repB.getChildConstraintTypeGroup().getGroupId();

        dataService.updateRep(
            repId,
            repB.getPlaceId(),
            parentId,
            repB.getJurisdictionFromYear(),
            repB.getJurisdictionToYear(),
            repB.getPlaceType().getTypeId(),
            repB.getDefaultLocale(),
            dispNames,
            lattd,
            longt,
            repB.isPublished(),
            repB.isValidated(),
            groupId,
            null,
            wlj,
            null);
    }

    /**
     * Create a new "Australia" place and place-rep.
     */
    private static void createAustralia() throws PlaceDataException {
        List<VariantNameDef> varNames = new ArrayList<>();
        varNames.add(makeNameDef(0, "en", "australia", 458));
        varNames.add(makeNameDef(0, "bi-Latn", "Ostrelia", 458));
        varNames.add(makeNameDef(0, "fr", "Australie", 458));
        varNames.add(makeNameDef(0, "fo", "Avstralia", 437));
        varNames.add(makeNameDef(0, "rn", "Ostraliya", 437));
        varNames.add(makeNameDef(0, "tt-Latn", "Awstraliä", 440));
        varNames.add(makeNameDef(0, "en", "AUSTRAILA", 454));

        Map<String,String> dispNames = new HashMap<>();
        dispNames.put("en", "Australia");
        dispNames.put("es", "Australia");
        dispNames.put("it", "Australia");
        dispNames.put("pl", "Australia");
        dispNames.put("ro", "Australia");
        dispNames.put("sw-Latn", "Australia");

        dataService.createPlace(
            -1,
            1700,
            null,
            198,
            "en",
            dispNames,
            -25.0,
            135.0,
            true,
            true,
            null,
            1700,
            null,
            varNames,
            wlj,
            null);
    }

    /**
     * Create a new Bloomington ...
     * @throws PlaceDataException
     */
    private static void createBloomington() throws PlaceDataException {
        Map<String,String> dispNames = new HashMap<>();
        dispNames.put("en", "Bloomington");
        dispNames.put("de", "Bloomingdorp");

        dataService.createRep(
            4,
            3,
            1830,
            null,
            186,
            "en",
            dispNames,
            -86.526,
            39.162,
            true, 
            true,
            null,
            wlj,
            null);
    }

    /**
     * Create a new "Horsens" place and place-rep.  Edit the place.  Edit the place-rep.
     */
    private static void createAndEditHorsens() throws PlaceDataException {
        List<VariantNameDef> varNames = new ArrayList<>();
        varNames.add(makeNameDef(0, "en", "Horsens", 458));
        varNames.add(makeNameDef(0, "da", "Horsens", 434));
        varNames.add(makeNameDef(0, "da", "Horsens Kommune", 437));
        varNames.add(makeNameDef(0, "da", "Horsens Sogn", 454));

        Map<String,String> dispNames = new HashMap<>();
        dispNames.put("en", "Horsens");
        dispNames.put("da", "Horsens");

        PlaceRepBridge repB = dataService.createPlace(
            20,
            1600,
            null,
            201,
            "da",
            dispNames,
            9.866667,
            55.866667,
            true,
            true,
            null,
            1700,
            null,
            varNames,
            wlj,
            null);

        PlaceBridge placeB = readService.getPlace(repB.getPlaceId());
//        PlaceBridge placeBX = repB.getAssociatedPlace();

        // Get the current place, modify the start-data, save it
        varNames = new ArrayList<>();
        for (PlaceNameBridge pNameB : placeB.getAllVariantNames()) {
            varNames.add(makeNameDef(pNameB.getNameId(), String.valueOf(pNameB.getName().getLocale()), pNameB.getName().get(), pNameB.getType().getTypeId()));
        }
        dataService.updatePlace(placeB.getPlaceId(), placeB.getFromYear()-100, placeB.getToYear(), varNames, "wlj", null);

        // Modify the place-rep, save it
        Double lattd = repB.getLatitude();
        Double longt = repB.getLongitude();
        if (lattd != null) lattd += 1;
        if (longt != null) longt -= 1;

        dispNames = repB.getAllDisplayNames();
        String enName = dispNames.get("en");
        if (enName != null) {
            dispNames.put("eu", enName+".eu");
        }

        int[] jurisIds = repB.getJurisdictionIdentifiers();
        int parentId = (jurisIds == null  ||  jurisIds.length < 2) ? -1 : jurisIds[1];
        Integer groupId = (repB.getChildConstraintTypeGroup() == null) ? null : repB.getChildConstraintTypeGroup().getGroupId();

        dataService.updateRep(
            repB.getRepId(),
            repB.getPlaceId(),
            parentId,
            repB.getJurisdictionFromYear(),
            repB.getJurisdictionToYear(),
            repB.getPlaceType().getTypeId(),
            repB.getDefaultLocale(),
            dispNames,
            lattd,
            longt,
            repB.isPublished(),
            repB.isValidated(),
            groupId,
            null,
            "wlj",
            null);
    }

    /**
     * Modify "Provo" place, by changing the start-year just a tad ...
     * 
     * @throws PlaceDataException
     */
    private static void editProvoPlace() throws PlaceDataException {
        // Get the current place, modify the start-data, save it
        PlaceBridge placeB = readService.getPlace(8);

        List<VariantNameDef> varNames = new ArrayList<>();
        for (PlaceNameBridge pNameB : placeB.getAllVariantNames()) {
            varNames.add(makeNameDef(pNameB.getNameId(), String.valueOf(pNameB.getName().getLocale()), pNameB.getName().get(), pNameB.getType().getTypeId()));
        }

        dataService.updatePlace(placeB.getPlaceId(), 1850, placeB.getToYear(), varNames, wlj, null);
    }

    /**
     * Create a new "Provo" place and place-rep.  Delete the place-rep, which should
     * delete the place.
     */
    private static void createAndDeleteProvo() throws PlaceDataException {
        PlaceBridge placeB = readService.getPlace(8);
        PlaceRepBridge repB = readService.getRep(10);

        int[] jurisIds = repB.getJurisdictionIdentifiers();
        int parentId = (jurisIds == null  ||  jurisIds.length < 2) ? -1 : jurisIds[1];
        Integer groupId = (repB.getChildConstraintTypeGroup() == null) ? null : repB.getChildConstraintTypeGroup().getGroupId();

        List<VariantNameDef> varNames = new ArrayList<>();
        for (PlaceNameBridge pNameB : placeB.getAllVariantNames()) {
            varNames.add(makeNameDef(pNameB.getNameId(), String.valueOf(pNameB.getName().getLocale()), pNameB.getName().get(), pNameB.getType().getTypeId()));
        }

        PlaceRepBridge newRepB = dataService.createPlace(
            parentId,
            repB.getJurisdictionFromYear(),
            repB.getJurisdictionToYear(),
            repB.getPlaceType().getTypeId(),
            repB.getDefaultLocale(),
            repB.getAllDisplayNames(),
            repB.getLatitude(),
            repB.getLongitude(),
            repB.isPublished(),
            repB.isValidated(),
            groupId,
            placeB.getFromYear(),
            placeB.getToYear(),
            varNames,
            "wlj",
            null);

        dataService.deleteRep(newRepB.getRepId(), 8, wlj, null);
    }

    /**
     * Construct a new PlaceNameDTO instance.
     * 
     * @param locale locale
     * @param name name
     * @param nameType name type
     * @return new PlaceNameDTO
     */
    private static VariantNameDef makeNameDef(int nameId, String locale, String name, int nameType) {
        VariantNameDef vnDef = new VariantNameDef();

        vnDef.id     = nameId;
        vnDef.locale = locale;
        vnDef.text   = name;
        vnDef.typeId = nameType;

        return vnDef;
    }
}
