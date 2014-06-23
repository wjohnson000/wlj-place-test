package std.wlj.db2solr;

import org.familysearch.standards.place.solr.loadx.LoadSolrApp;

public class ZzzDumpTeamDbX {
    public static void main(String... args) {
        String[] appArgs = {
            "--dbHost", "fh2-std-place-db-int.cqbtyzjgnvqo.us-east-1.rds.amazonaws.com",
            "--dbSchema", "p124",
            "--dbUser", "sams_place",
            "--dbPassword", "sams_place",
            "--solrHome", "C:/tools/solr/data",
            "--baseDir", "C:/temp/flat-filex/new",
            "--dbDumpOnly"
        };

        LoadSolrApp.main(appArgs);

        System.exit(0);
    }
}
