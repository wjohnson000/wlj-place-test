package std.wlj.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrService;

public class SolrManager {

    private static final String URL_LOCAL_HOST_PROP         = "local.host.url";
    private static final String URL_LOCAL_HOST_55_PROP      = "local.host-55.url";

    private static final String AWS_URL_DEV55_MASTER_PROP   = "aws.dev-55.master.url";
    private static final String AWS_URL_DEV55_REPEATER_PROP = "aws.dev-55.repeater.url";

    private static final String AWS_URL_DEV_MASTER_PROP     = "aws.dev.master.url";
    private static final String AWS_URL_DEV_REPEATER_PROP   = "aws.dev.repeater.url";

    private static final String AWS_URL_INT_MASTER_PROP     = "aws.int.master.url";
    private static final String AWS_URL_INT_REPEATER_PROP   = "aws.int.repeater.url";

    private static final String AWS_URL_BETA_MASTER_PROP    = "aws.beta.master.url";
    private static final String AWS_URL_BETA_REPEATER_PROP  = "aws.beta.repeater.url";

    private static final String AWS_URL_PROD_MASTER_PROP    = "aws.prod.master.url";
    private static final String AWS_URL_PROD_REPEATER_PROP  = "aws.prod.repeater.url";

    private static Properties solrProps = new Properties();
    static {
        try {
            solrProps.load(new FileInputStream(new File("C:/Users/wjohnson000/.std-solr.props")));
        } catch (Exception e) {
            System.out.println("Unable to load Solr properties ... can't proceed ...");
        }
    }

    // ============================================================================================
    // Methods to return a 'SolrConnection' instance
    // ============================================================================================

    public static SolrConnection localEmbeddedConnection(String solrPath) {
        return doSetupForEmbeddedConnection(solrPath);
    }

    public static SolrConnection localHttpConnection() {
        return doSetupForHttpConnection(solrProps.getProperty(URL_LOCAL_HOST_PROP));
    }

    public static SolrConnection localHttp55Connection() {
        return doSetupForHttpConnection(solrProps.getProperty(URL_LOCAL_HOST_55_PROP));
    }

    public static SolrConnection awsDev55Connection(boolean useMaster) {
        return (doSetupForHttpConnection(useMaster ? solrProps.getProperty(AWS_URL_DEV55_MASTER_PROP) : solrProps.getProperty(AWS_URL_DEV55_REPEATER_PROP)));
    }

    public static SolrConnection awsDevConnection(boolean useMaster) {
        return (doSetupForHttpConnection(useMaster ? solrProps.getProperty(AWS_URL_DEV_MASTER_PROP) : solrProps.getProperty(AWS_URL_DEV_REPEATER_PROP)));
    }

    public static SolrConnection awsIntConnection(boolean useMaster) {
        return (doSetupForHttpConnection(useMaster ? solrProps.getProperty(AWS_URL_INT_MASTER_PROP) : solrProps.getProperty(AWS_URL_INT_REPEATER_PROP)));
    }

    public static SolrConnection awsBetaConnection(boolean useMaster) {
        return (doSetupForHttpConnection(useMaster ? solrProps.getProperty(AWS_URL_BETA_MASTER_PROP) : solrProps.getProperty(AWS_URL_BETA_REPEATER_PROP)));
    }

    public static SolrConnection awsProdConnection(boolean useMaster) {
        return (doSetupForHttpConnection(useMaster ? solrProps.getProperty(AWS_URL_PROD_MASTER_PROP) : solrProps.getProperty(AWS_URL_PROD_REPEATER_PROP)));
    }

    // ============================================================================================
    // Methods to return a 'SolrService' instance
    // ============================================================================================

    public static SolrService localEmbeddedService() {
        return doSetupForService("C:/D-drive/solr/standalone-7.7.1", "C:/D-drive/solr/standalone-7.7.1");
    }

    public static SolrService localEmbeddedService(String solrHome) {
        return doSetupForService(solrHome, solrHome);
    }

    public static SolrService localHttpService() {
        return doSetupForService(solrProps.getProperty(URL_LOCAL_HOST_PROP), solrProps.getProperty(URL_LOCAL_HOST_PROP));
    }

    public static SolrService awsService55() {
        return doSetupForService(solrProps.getProperty(AWS_URL_DEV55_MASTER_PROP), solrProps.getProperty(AWS_URL_DEV55_REPEATER_PROP));
    }

    public static SolrService awsDevService(boolean useMaster) {
        String baseUrl = (useMaster) ? solrProps.getProperty(AWS_URL_DEV_MASTER_PROP) : solrProps.getProperty(AWS_URL_DEV_REPEATER_PROP);
        return doSetupForService(baseUrl, baseUrl);
    }

    public static SolrService awsIntService(boolean useMaster) {
        String baseUrl = (useMaster) ? solrProps.getProperty(AWS_URL_INT_MASTER_PROP) : solrProps.getProperty(AWS_URL_INT_REPEATER_PROP);
        return doSetupForService(baseUrl, baseUrl);
    }

    public static SolrService awsBetaService(boolean useMaster) {
        String baseUrl = (useMaster) ? solrProps.getProperty(AWS_URL_BETA_MASTER_PROP) : solrProps.getProperty(AWS_URL_BETA_REPEATER_PROP);
        return doSetupForService(baseUrl, baseUrl);
    }

   public static SolrService awsProdService(boolean useMaster) {
        String baseUrl = (useMaster) ? solrProps.getProperty(AWS_URL_PROD_MASTER_PROP) : solrProps.getProperty(AWS_URL_PROD_REPEATER_PROP);
        return doSetupForService(baseUrl, baseUrl);
    }

    // ============================================================================================
    // Set-up methods
    // ============================================================================================
    
    private static SolrConnection doSetupForEmbeddedConnection(String solrPath) {
        System.setProperty("place.api.useCache", "true");
        System.setProperty("interp.cache.maxcount", "1000");
        System.setProperty("interp.cache.timeout", "600");

        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        System.setProperty("solr.skip.warmup", "true");
        System.setProperty("solr.replication.url", solrProps.getProperty(AWS_URL_BETA_REPEATER_PROP));
        
        System.setProperty("solr.solr.home", solrPath);
        try {
            return SolrConnection.connectToEmbeddedInstance(solrPath);
        } catch (PlaceDataException e) {
            return null;
        }
    }
    
    private static SolrConnection doSetupForHttpConnection(String solrUrl) {
        System.setProperty("place.api.useCache", "true");
        System.setProperty("interp.cache.maxcount", "1000");
        System.setProperty("interp.cache.timeout", "600");

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
        System.setProperty("place.api.useCache", "true");
        System.setProperty("interp.cache.maxcount", "1000");
        System.setProperty("interp.cache.timeout", "600");

        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        System.setProperty("solr.skip.warmup", "true");

        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", solrMaster);
        
        System.setProperty("solr.replication.url", "");
        return new SolrService();
    }
}
