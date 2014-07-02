package std.wlj.db2solr;

import org.familysearch.standards.place.solr.load.LoadSolrApp;

public class Step08bLoadSolrX {
    public static void main(String... args) {
        String[] appArgs = {
            "--dbHost", "localhost",
            "--dbSchema", "standards",
            "--dbUser", "postgres",
            "--dbPassword", "admin",
            "--solrHome", "C:/tools/solr/data",
            "--baseDir", "C:/temp/flat-filex",
            "--deleteAll",
            "--solrLoadOnly"
        };

        LoadSolrApp.main(appArgs);

        System.exit(0);
    }
}
