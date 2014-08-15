package std.wlj.db2solr;

import org.familysearch.standards.place.solr.load.LoadSolrApp;

public class Step01LoadSolr {
    public static void main(String... args) {
        System.setProperty("solr.master.url", "");
        System.setProperty("solr.solr.home", "C:/tools/solr/data/tokoro");
        System.setProperty("solr.master", "false");
        System.setProperty("solr.master", "slave");

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
