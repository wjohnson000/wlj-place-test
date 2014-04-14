package org.familysearch.std.wlj.util;

import org.familysearch.standards.place.data.solr.SolrConfig;
import org.familysearch.standards.place.data.solr.SolrDataService;

public class SolrManager {

    public static SolrDataService getLocalEmbedded() {
        SolrConfig config = new SolrConfigManual("C:/tools/Solr/data", "");
        return new SolrDataService(config);
    }

    public static SolrDataService getLocalHttp() {
        String url = "http://localhost:8983/solr/places";
        SolrConfig config = new SolrConfigManual(url, url);
        return new SolrDataService(config);
    }

    public static SolrDataService getAwsHttp() {
        String url = "http://ec2-107-21-173-161.compute-1.amazonaws.com:8983/solr/places";
        SolrConfig config = new SolrConfigManual(url, url);
        return new SolrDataService(config);
    }
}
