package std.wlj.dbload;

import org.familysearch.standards.loader.LoadManager;
import org.familysearch.standards.loader.impl.FullLoader;

import std.wlj.datasource.DbConnectionManager;

public class RunLoadManager {
    public static void main(String... args) {
        System.setProperty("solr.load.count", "1000");
        System.setProperty("solr.load.tempdir", "C:/temp/flat-file");

        String solrURL = "http://localhost:8080/solr/places";
//        LoadManager.getInstance().init(solrURL, DbConnectionManager.getDataSourceSams(8));
        LoadManager.getInstance().init(solrURL, DbConnectionManager.getDataSourceAwsDev());
//        LoadManager.getInstance().acceptLoader(new FullLoader("some-user", true, 2, true));
        LoadManager.getInstance().acceptLoader(new FullLoader("some-user", true, 2));

        while (! LoadManager.getInstance().isActive()) {
            try { Thread.sleep(10_000L); } catch(Exception ex) { }
        }

        while (LoadManager.getInstance().isActive()) {
            try { Thread.sleep(60_000L); } catch(Exception ex) { }
            System.out.println("Loader is active ... " + LoadManager.getInstance().getActiveLoaderDetails());
        }

        LoadManager.getInstance().shutdown();
        System.exit(0);
   }
}
