package std.wlj.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.CitationBridge;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.WritableDataService.VariantNameDef;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class PlaceAndRepAndCitnTest {

	/** DB and SOLR services */
	private static final String fakeUser = "wjohnson000";

	private static BasicDataSource ds = null;
	private static PlaceDataServiceImpl dataService = null;

    private static Random random = new Random();


    public static void main(String... args) {
//        System.setProperty("solr.master.url", "C:/tools/solr/data/tokoro");
//        System.setProperty("solr.solr.home", "C:/tools/solr/data/tokoro");
        System.setProperty("solr.master.url", "http://localhost:8983/solr/places");
        System.setProperty("solr.solr.home", "http://localhost:8983/solr/places");
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");

        ApplicationContext appContext = null;
        try {
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost-wlj.xml");
            ds = (BasicDataSource)appContext.getBean("dataSource");
            SolrService       solrService = new SolrService();
            DbReadableService dbRService  = new DbReadableService(ds);
            DbWritableService dbWService  = new DbWritableService(ds);
            dataService = new PlaceDataServiceImpl(solrService, dbRService, dbWService);

            PlaceRepBridge placeRepB01 = createQuilly();
            int repId = placeRepB01.getRepId();

            CitationBridge citnB01 = dataService.createCitation(repId, 460, 1, null, "desc-c01", "ref-c01", fakeUser, null);
            CitationBridge citnB02 = dataService.createCitation(repId, 460, 1, null, "desc-c02", "ref-c02", fakeUser, null);
            CitationBridge citnB03 = dataService.createCitation(repId, 460, 1, null, "desc-c03", "ref-c03", fakeUser, null);

            dataService.deleteCitation(citnB02.getCitationId(), repId, fakeUser, null);

            CitationBridge citnB04 = dataService.createCitation(repId, 460, 1, null, "desc-c04", "ref-c04", fakeUser, null);

            dataService.deleteCitation(citnB03.getCitationId(), repId, fakeUser, null);

            CitationBridge citnB05 = dataService.createCitation(repId, 460, 1, null, "desc-c05", "ref-c05", fakeUser, null);
            CitationBridge citnB06 = dataService.createCitation(repId, 460, 1, null, "desc-c06", "ref-c06", fakeUser, null);
            CitationBridge citnB07 = dataService.createCitation(repId, 460, 1, null, "desc-c07", "ref-c07", fakeUser, null);

            dataService.deleteCitation(citnB06.getCitationId(), repId, fakeUser, null);
            dataService.deleteCitation(citnB01.getCitationId(), repId, fakeUser, null);

            PlaceRepBridge placeRepB03 = dbRService.getRep(repId, null);
            List<CitationBridge> citnBs = placeRepB03.getAllCitations();
            System.out.println("PLACE-REP: " + placeRepB03.getRepId() + "." + placeRepB03.getVersion() + "." + placeRepB03.getRevision());
            for (CitationBridge citnB : citnBs) {
                System.out.println("CITN: " + citnB.getCitationId() + "." + citnB.getPlaceRep().getRepId() + " :: " + citnB.getSourceRef() + " :: " + citnB.getDescription());
            }
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            System.out.println("Shutting down ...");
            ((ClassPathXmlApplicationContext)appContext).close();
            if (dataService != null) dataService.shutdown();
        }

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
