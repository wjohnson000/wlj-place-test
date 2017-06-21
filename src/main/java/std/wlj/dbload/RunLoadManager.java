package std.wlj.dbload;

import org.familysearch.standards.loader.LoadManager;
import org.familysearch.standards.loader.impl.FullLoader;

import std.wlj.datasource.DbConnectionManager;

public class RunLoadManager {
    public static void main(String... args) {
        System.setProperty("solr.load.count", "1000");
        System.setProperty("solr.load.tempdir", "D:/tmp/flat-files/load-test");

        String solrURL = "http://localhost:8080/solr/places";
        LoadManager.getInstance().init(solrURL, DbConnectionManager.getDataSourceWLJ());
        LoadManager.getInstance().acceptLoader(new FullLoader("some-user"));
   }
}
