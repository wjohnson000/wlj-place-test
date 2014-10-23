package std.wlj.db2solr;

import org.familysearch.standards.place.solr.load.LoadSolrApp;

import std.wlj.util.SolrManager;

public class Step01LoadSolr {
    public static void main(String... args) {
        SolrManager.getLocalTokoro();

        String[] appArgs = {
            "--dbHost", "localhost",
            "--dbSchema", "standards",
            "--dbUser", "postgres",
            "--dbPassword", "admin",
            "--solrHome", "C:/tools/solr/data/tokoro",
            "--baseDir", "C:/temp/flat-file",
            "--deleteAll"
        };

        LoadSolrApp.main(appArgs);

        System.exit(0);
    }
}
