package std.wlj.util;

import org.familysearch.standards.place.data.solr.SolrDataService;

public class SolrManager {

    public static SolrDataService getLocalEmbedded() {
        System.setProperty("solr.solr.home", "C:/tools/Solr/data");
        System.setProperty("solr.master.url", "");
        return new SolrDataService();
    }

    public static SolrDataService getLocalHttp() {
        System.setProperty("solr.solr.home", "http://localhost:8983/solr/places");
        System.setProperty("solr.master.url", "http://localhost:8983/solr/places");
        return new SolrDataService();
    }

    public static SolrDataService getAwsHttp() {
        System.setProperty("solr.solr.home", "http://ec2-107-21-173-161.compute-1.amazonaws.com:8983/solr/places");
        System.setProperty("solr.master.url", "http://ec2-107-21-173-161.compute-1.amazonaws.com:8983/solr/places");
        return new SolrDataService();
    }
}
