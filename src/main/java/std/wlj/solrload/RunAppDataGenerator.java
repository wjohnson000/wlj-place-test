package std.wlj.solrload;

import java.util.List;

import org.familysearch.standards.loader.helper.DbHelper;
import org.familysearch.standards.loader.reader.AppDataReader;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;

import std.wlj.util.DbConnectionManager;

public class RunAppDataGenerator {
	private static DbHelper dbService;

	public static void main(String... args) throws InterruptedException {
	    dbService = new DbHelper(DbConnectionManager.getDataSourceSams());

	    showClassPaths();
		loadAppData();
	}

    private static void loadAppData() {
        AppDataReader appReader = new AppDataReader(dbService);
        List<PlaceRepDoc> appDocs = appReader.getAppDocs("|");
        for (PlaceRepDoc appDoc : appDocs) {
            System.out.println("==========================================================================");
            System.out.println("ID: " + appDoc.getId());
            if (appDoc.getId().equals("NAME-TYPE")  ||  appDoc.getId().equals("NAME-PRIORITY")) {
                for (String appData : appDoc.getAppData()) {
                    System.out.println("  " + appData);
                }
            }
            System.out.println("\n\n");
        }
    }

    private static void showClassPaths() {
        String[] paths = System.getProperty("java.class.path").split(";");
        for (String path : paths) {
            System.out.println(path);
        }
    }

}
