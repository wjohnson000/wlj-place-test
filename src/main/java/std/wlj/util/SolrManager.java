package std.wlj.util;

import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrService;

public class SolrManager {

    private static final String URL_LOCAL_HOST         = "http://localhost:8080/solr/places";
    private static final String URL_LOCAL_HOST_55      = "http://localhost:8080/solr-55/places";

    private static final String AWS_URL_DEV55_MASTER   = "http://ws-55.solr.std.cmn.dev.us-east-1.dev.fslocal.org/places";
    private static final String AWS_URL_DEV55_REPEATER = "http://ws-55.solr-repeater.std.cmn.dev.us-east-1.dev.fslocal.org/places";

    private static final String AWS_URL_DEV_MASTER     = "http://ws.solr.std.cmn.dev.us-east-1.dev.fslocal.org/places";
    private static final String AWS_URL_DEV_REPEATER   = "http://ws.solr-repeater.std.cmn.dev.us-east-1.dev.fslocal.org/places";

    private static final String AWS_URL_INT_MASTER     = "http://ws.solr.std.cmn.int.us-east-1.dev.fslocal.org/places";
    private static final String AWS_URL_INT_REPEATER   = "http://ws.solr-repeater.std.cmn.int.us-east-1.dev.fslocal.org/places";

//    private static final String AWS_URL_BETA_MASTER    = "http://ws.solr.std.cmn.beta.us-east-1.test.fslocal.org/places";
//    private static final String AWS_URL_BETA_REPEATER  = "http://ws.solr-repeater.std.cmn.beta.us-east-1.test.fslocal.org/places";
    private static final String AWS_URL_BETA_MASTER    = "https://beta.familysearch.org/int-solr/places";
    private static final String AWS_URL_BETA_REPEATER  = "https://beta.familysearch.org/int-solr-repeater/places";

    private static final String AWS_URL_PROD_MASTER    = "https://familysearch.org/int-solr/places";
    private static final String AWS_URL_PROD_REPEATER  = "https://familysearch.org/int-solr-repeater/places";

    // ============================================================================================
    // Methods to return a 'SolrConnection' instance
    // ============================================================================================

    public static SolrConnection localEmbeddedConnection(String solrPath) {
        return doSetupForEmbeddedConnection(solrPath);
    }

    public static SolrConnection localHttpConnection() {
        return doSetupForHttpConnection(URL_LOCAL_HOST);
    }

    public static SolrConnection localHttp55Connection() {
        return doSetupForHttpConnection(URL_LOCAL_HOST_55);
    }

    public static SolrConnection awsDev55Connection(boolean useMaster) {
        return (doSetupForHttpConnection(useMaster ? AWS_URL_DEV55_MASTER : AWS_URL_DEV55_REPEATER));
    }

    public static SolrConnection awsDevConnection(boolean useMaster) {
        return (doSetupForHttpConnection(useMaster ? AWS_URL_DEV_MASTER : AWS_URL_DEV_REPEATER));
    }

    public static SolrConnection awsIntConnection(boolean useMaster) {
        return (doSetupForHttpConnection(useMaster ? AWS_URL_INT_MASTER : AWS_URL_INT_REPEATER));
    }

    public static SolrConnection awsBetaConnection(boolean useMaster) {
        return (doSetupForHttpConnection(useMaster ? AWS_URL_BETA_MASTER : AWS_URL_BETA_REPEATER));
    }

    public static SolrConnection awsProdConnection(boolean useMaster) {
        return (doSetupForHttpConnection(useMaster ? AWS_URL_PROD_MASTER : AWS_URL_PROD_REPEATER));
    }

    // ============================================================================================
    // Methods to return a 'SolrService' instance
    // ============================================================================================

    public static SolrService localEmbeddedService() {
        return doSetupForService("D:/solr/newbie-6.5.0", "D:/solr/newbie-6.5.0");
    }

    public static SolrService localEmbeddedService(String solrHome) {
        return doSetupForService(solrHome, solrHome);
    }

    public static SolrService localHttpService() {
        return doSetupForService(URL_LOCAL_HOST, URL_LOCAL_HOST);
    }

    public static SolrService awsService55() {
        return doSetupForService("http://place-ws-dev.dev.fsglobal.org/int-solr-55/places", "http://place-ws-dev.dev.fsglobal.org/int-solr-55/places");
    }

    public static SolrService awsDevService(boolean useMaster) {
        String baseUrl = (useMaster) ? AWS_URL_DEV_MASTER : AWS_URL_DEV_REPEATER;
        return doSetupForService(baseUrl, baseUrl);
    }

    public static SolrService awsIntService(boolean useMaster) {
        String baseUrl = (useMaster) ? AWS_URL_INT_MASTER : AWS_URL_INT_REPEATER;
        return doSetupForService(baseUrl, baseUrl);
    }

    public static SolrService awsBetaService(boolean useMaster) {
        String baseUrl = (useMaster) ? AWS_URL_BETA_MASTER : AWS_URL_BETA_REPEATER;
        return doSetupForService(baseUrl, baseUrl);
    }

   public static SolrService awsProdService(boolean useMaster) {
        String baseUrl = (useMaster) ? AWS_URL_PROD_MASTER : AWS_URL_PROD_REPEATER;
        return doSetupForService(baseUrl, baseUrl);
    }

    // ============================================================================================
    // Set-up methods
    // ============================================================================================
    
    private static SolrConnection doSetupForEmbeddedConnection(String solrPath) {
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        System.setProperty("solr.skip.warmup", "true");
        System.setProperty("solr.replication.url", "http://beta.familysearch.org/int-solr/places");
        
        System.setProperty("solr.solr.home", solrPath);
        try {
            return SolrConnection.connectToEmbeddedInstance(solrPath);
        } catch (PlaceDataException e) {
            return null;
        }
    }
    
    private static SolrConnection doSetupForHttpConnection(String solrUrl) {
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        System.setProperty("solr.skip.warmup", "true");
        
        System.setProperty("solr.solr.home", solrUrl);
        System.setProperty("solr.master.url", solrUrl);
        try {
            return SolrConnection.connectToRemoteInstance(solrUrl);
        } catch (Exception e) {
            return null;
        }
    }

    private static SolrService doSetupForService(String solrHome, String solrMaster) {
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        System.setProperty("solr.skip.warmup", "true");

        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", solrMaster);
        
        System.setProperty("solr.replication.url", "");
        return new SolrService();
    }
}
