package std.wlj.dbload;

import org.familysearch.standards.place.db.loader.LoadManager;

import std.wlj.datasource.DbConnectionManager;

public class RunLoadManager {
    public static void main(String... args) {
        System.setProperty("solr.load.count", "1000");
        System.setProperty("solr.load.tempdir", "D:/tmp/flat-files/load-test");

        String solrURL = "http://localhost:8080/solr/places";
        LoadManager.startLoadManager(solrURL, DbConnectionManager.getDataSourceWLJ());
    }

}
