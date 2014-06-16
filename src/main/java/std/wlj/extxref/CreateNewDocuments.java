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

    private static String[] priorityData = {
        "ODM_VAR|1",
        "LDS_OTHER|1",
        "ABRV|4",
        "SORT|2",
        "ISOCNTRYCD|7",
        "FULLD|1",
        "ETHNIC|1",
        "ODM_STD|2",
        "COMMON|10",
        "ISONAME|7",
        "OLD_STABRV|8",
        "LDS_2LTMPL|1",
        "ISO2LCD|7",
        "SHORT|4",
        "DSPLY|5",
        "NONDC|2",
        "GENERIC|2",
        "LDS_5LTMPL|1",
        "ISO3LCD|7",
        "US_STABRV|8",
        "US_QUALIFIED|4",
        "FULLN|3",
        "VAR|2",
        "UND|1",
        "CONV|3"  
    };

    @SuppressWarnings("unchecked")
    public static void main(String... args) throws Exception {
//        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://localhost:8983/solr/places");
//        SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance("C:/tools/Solr/data");
//        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://ec2-107-21-173-161.compute-1.amazonaws.com:8983/solr/places");
        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://107.21.173.161:8983/solr/places");

        PlaceRepDoc resnDoc  = AppDataManager.makeAppDataDoc(AppDataManager.RESOLUTION_TYPE_ID, Arrays.asList(resnData));
        PlaceRepDoc xrefDoc  = AppDataManager.makeAppDataDoc(AppDataManager.EXT_XREF_TYPE_ID, Arrays.asList(xrefData));
        PlaceRepDoc priorDoc = AppDataManager.makeAppDataDoc(AppDataManager.NAME_PRIORITY_ID, Arrays.asList(priorityData));
//        solrConn.add(resnDoc);
//        solrConn.add(xrefDoc);
        solrConn.add(priorDoc);

        solrConn.commit();
        solrConn.shutdown();

        System.exit(0);
    }
}
