package std.wlj.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.AltJurisdictionBridge;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.WritableDataService.VariantNameDef;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.DbConnectionManager;
import std.wlj.util.SolrManager;
import std.wlj.util.DbConnectionManager.DbServices;


public class PlaceAndRepAndAltJurisdictionTest {

	/** DB and SOLR services */
	private static final String fakeUser = "wjohnson000";

    private static Random random = new Random();


    public static void main(String... args) {
        PlaceDataServiceImpl dataService = null;
        DbServices dbServices = null;
        SolrService solrService = null;

        try {
            dbServices = DbConnectionManager.getDbServicesWLJ();
            solrService = SolrManager.awsIntService(true);
            dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

            PlaceRepBridge placeRepB01 = createQuilly(dataService);
            int repId = placeRepB01.getRepId();

            AltJurisdictionBridge altJurisB01 = dataService.createAltJurisdiction(repId, 1000011, 514, "wjohnson000", 0);
            AltJurisdictionBridge altJurisB02 = dataService.createAltJurisdiction(repId, 1000012, 514, "wjohnson000", 0);
            AltJurisdictionBridge altJurisB03 = dataService.createAltJurisdiction(repId, 1000013, 515, "wjohnson000", 0);

            dataService.deleteAltJurisdiction(altJurisB01.getAltJurisdictionId(), repId, 1000011, 514, "wjohnson000", 0);

            AltJurisdictionBridge altJurisB04 = dataService.createAltJurisdiction(repId, 1000014, 514, "wjohnson000", 0);

            dataService.deleteAltJurisdiction(altJurisB03.getAltJurisdictionId(), repId, 1000013, 515, "wjohnson000", 0);

            AltJurisdictionBridge altJurisB05 = dataService.createAltJurisdiction(repId, 1000015, 514, "wjohnson000", 0);
            AltJurisdictionBridge altJurisB06 = dataService.createAltJurisdiction(repId, 1000016, 515, "wjohnson000", 0);
            AltJurisdictionBridge altJurisB07 = dataService.createAltJurisdiction(repId, 1000017, 515, "wjohnson000", 0);

            dataService.deleteAltJurisdiction(altJurisB06.getAltJurisdictionId(), repId, 1000016, 515, "wjohnson000", 0);
            dataService.createAltJurisdiction(repId, 1000011, 514, "wjohnson000", 0); // This should using an existing alt-jurisdiction-id

            PlaceRepBridge placeRepB03 = dbServices.readService.getRep(repId);
            List<AltJurisdictionBridge> altJurisBs = placeRepB03.getAllAltJurisdictions();
            System.out.println("PLACE-REP: " + placeRepB03.getRepId() + "." + placeRepB03.getRevision());
            for (AltJurisdictionBridge altJurisB : altJurisBs) {
                System.out.println("ALT-JURIS: " + altJurisB.getAltJurisdictionId() + "." + altJurisB.getPlaceRep().getRepId() + " :: " + altJurisB.getRelatedPlaceRep().getRepId() + " :: " + altJurisB.getType().getCode());
            }
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            System.out.println("Shutting down ...");
            if (dataService != null) dataService.shutdown();
            if (dbServices != null) dbServices.shutdown();
            if (solrService != null) solrService.shutdown();
        }

        System.exit(0);
    }


    /**
     * Create "Quilly", the top-level place + place-rep in our neat-o new tree
     * @return
     * @throws PlaceDataException 
     */
    private static PlaceRepBridge createQuilly(PlaceDataServiceImpl dataService) throws PlaceDataException {
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
            fakeUser,
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

}
