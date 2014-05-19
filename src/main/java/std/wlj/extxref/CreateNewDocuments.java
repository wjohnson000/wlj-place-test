package std.wlj.extxref;

import org.familysearch.standards.place.data.AppDataManager;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import edu.emory.mathcs.backport.java.util.Arrays;


/**
 * Create the 'EXT-XREF-TYPE' and 'RESOLUTION-TYPE' documents in SOLR, to match
 * what is in the database.
 * 
 * @author wjohnson000
 *
 */
public class CreateNewDocuments {

    private static String[] xrefData = {
        "1063|NGA|true|en|NGA|NGA Data",
        "1064|RANDMCNALLY|true|en|Rand McNally|Lots of maps and stuff and such"
    };

    private static String[] resnData = {
        "1060|LOW|true|en|Low|Low Resolution",
        "1061|MEDIUM|true|en|Medium|Medium Resolution",
        "1062|HIGH|true|en|High|High Resolution"
    };


    @SuppressWarnings("unchecked")
    public static void main(String... args) throws Exception {
        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://localhost:8983/solr/places");
//        SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance("C:/tools/Solr/data");

        PlaceRepDoc resnDoc = AppDataManager.makeAppDataDoc(AppDataManager.RESOLUTION_TYPE_ID, Arrays.asList(resnData));
        PlaceRepDoc xrefDoc = AppDataManager.makeAppDataDoc(AppDataManager.EXT_XREF_TYPE_ID, Arrays.asList(xrefData));
        solrConn.add(resnDoc);
        solrConn.add(xrefDoc);

        solrConn.commit();
        solrConn.shutdown();

        System.exit(0);
    }
}
