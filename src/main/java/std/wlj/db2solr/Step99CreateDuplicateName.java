package std.wlj.db2solr;

import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.PlaceBridge;
import org.familysearch.standards.place.data.PlaceNameBridge;
import org.familysearch.standards.place.data.WritableDataService.VariantNameDef;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.service.DbReadableService;

import std.wlj.util.DbConnectionManager;
import std.wlj.util.SolrManager;
import std.wlj.util.DbConnectionManager.DbServices;

/**
 * Create, Update and Delete lots of stuff ...
 * 
 * @author wjohnson000
 *
 */
public class Step99CreateDuplicateName {

    private static DbReadableService readService;
    private static PlaceDataServiceImpl dataService;
    private static String wlj = "wjohnson000";

    public static void main(String... args) {
        DbServices dbServices = DbConnectionManager.getDbServicesSams();
        SolrService solrService = SolrManager.localEmbeddedService("C:/tools/solr/data/tokoro");
        readService = dbServices.readService;
        dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

        try {
            editProvoPlace();
        } catch(PlaceDataException ex) {
            System.out.println("EX: " + ex.getMessage());
            ex.printStackTrace();
        }

        dataService.shutdown();
        dbServices.shutdown();

        System.exit(0);
    }

    /**
     * Modify "Provo" place: change the start-year just a tad, and add a duplicate name
     * 
     * @throws PlaceDataException
     */
    private static void editProvoPlace() throws PlaceDataException {
        PlaceBridge placeB = readService.getPlace(8);

        // Get the current place, modify the start-data, save it
        List<VariantNameDef> varNames = new ArrayList<>();
        for (PlaceNameBridge pNameB : placeB.getAllVariantNames()) {
            varNames.add(makeNameDef(pNameB.getNameId(), String.valueOf(pNameB.getName().getLocale()), pNameB.getName().get(), pNameB.getType().getTypeId()));
        }
        varNames.add(makeNameDef(0, "en", "provo", 445));
        varNames.add(makeNameDef(0, "en", "provo", 445));

        dataService.updatePlace(placeB.getPlaceId(), placeB.getFromYear(), placeB.getToYear(), varNames, wlj, null);
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
