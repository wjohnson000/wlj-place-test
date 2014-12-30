package std.wlj.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.AttributeBridge;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.WritableDataService.VariantNameDef;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class PlaceAndRepAndAttrTest {

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
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            ds = (BasicDataSource)appContext.getBean("dataSource");
            SolrService       solrService = new SolrService();
            DbReadableService dbRService  = new DbReadableService(ds);
            DbWritableService dbWService  = new DbWritableService(ds);
            dataService = new PlaceDataServiceImpl(solrService, dbRService, dbWService);

            PlaceRepBridge placeRepB01 = createQuilly();
            int repId = placeRepB01.getRepId();

            AttributeBridge attrB01 = dataService.createAttribute(repId, 433, 2001, "fr", "attr-value-01", fakeUser);
            AttributeBridge attrB02 = dataService.createAttribute(repId, 433, 2002, "fr", "attr-value-02", fakeUser);
            AttributeBridge attrB03 = dataService.createAttribute(repId, 433, 2003, "fr", "attr-value-03", fakeUser);

            dataService.deleteAttribute(attrB02.getAttributeId(), repId, fakeUser);

            AttributeBridge attrB04 = dataService.createAttribute(repId, 433, 2004, "fr", "attr-value-04", fakeUser);

            dataService.deleteAttribute(attrB03.getAttributeId(), repId, fakeUser);

            AttributeBridge attrB05 = dataService.createAttribute(repId, 433, 2005, "fr", "attr-value-05", fakeUser);
            AttributeBridge attrB06 = dataService.createAttribute(repId, 433, 2006, "fr", "attr-value-06", fakeUser);
            AttributeBridge attrB07 = dataService.createAttribute(repId, 433, 2007, "fr", "attr-value-07", fakeUser);

            dataService.deleteAttribute(attrB06.getAttributeId(), repId, fakeUser);
            dataService.deleteAttribute(attrB01.getAttributeId(), repId, fakeUser);

            PlaceRepBridge placeRepB03 = dbRService.getRep(repId, null);
            List<AttributeBridge> attrBs = placeRepB03.getAllAttributes();
            System.out.println("PLACE-REP: " + placeRepB03.getRepId() + "." + placeRepB03.getVersion() + "." + placeRepB03.getRevision());
            for (AttributeBridge attrB : attrBs) {
                System.out.println("ATTR: " + attrB.getAttributeId() + "." + attrB.getPlaceRep().getRepId() + " :: " + attrB.getLocale() + " :: " + attrB.getValue());
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
            fakeUser);
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
