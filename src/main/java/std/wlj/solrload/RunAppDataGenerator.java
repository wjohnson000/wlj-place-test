package std.wlj.solrload;

import java.util.List;

import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.db.loader.helper.DbHelper;
import org.familysearch.standards.place.db.reader.AppDataReader;

import std.wlj.datasource.DbConnectionManager;

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
            if (appDoc.getId().equals("GROUP-HIERARCHY")) {
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
