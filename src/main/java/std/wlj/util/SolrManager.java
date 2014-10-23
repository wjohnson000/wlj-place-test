package std.wlj.util;

import org.familysearch.standards.place.data.solr.SolrService;

public class SolrManager {

    public static SolrService getLocalEmbedded() {
        System.setProperty("solr.solr.home", "C:/tools/Solr/data");
        System.setProperty("solr.master.url", "C:/tools/Solr/data");
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        return new SolrService();
    }

    public static SolrService getLocalTokoro() {
        System.setProperty("solr.master.url", "");
        System.setProperty("solr.solr.home", "C:/tools/solr/data/tokoro");
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        return new SolrService();
    }

    public static SolrService getLocalEmbedded(String solrHome) {
        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", solrHome);
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        return new SolrService();
    }

    public static SolrService getLocalHttp() {
        System.setProperty("solr.solr.home", "http://localhost:8983/solr/places");
        System.setProperty("solr.master.url", "http://localhost:8983/solr/places");
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        return new SolrService();
    }

    public static SolrService getAwsHttp() {
        System.setProperty("solr.solr.home", "http://ec2-107-21-173-161.compute-1.amazonaws.com:8983/solr/places");
        System.setProperty("solr.master.url", "http://ec2-107-21-173-161.compute-1.amazonaws.com:8983/solr/places");
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        return new SolrService();
    }
}
