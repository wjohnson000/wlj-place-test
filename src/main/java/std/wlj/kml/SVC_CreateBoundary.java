package std.wlj.kml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.BoundaryBridge;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.datasource.DbConnectionManager.DbServices;
import std.wlj.util.SolrManager;

public class SVC_CreateBoundary {

    static final String kmlFileDir = "D:/postgis/files";

    private static PlaceDataServiceImpl dataService;
    private static String wlj = "wjohnson000";

    public static void main(String... args) throws IOException {
        DbServices dbServices = DbConnectionManager.getDbServicesSams();
        SolrService solrService = SolrManager.localEmbeddedService();
        dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

        Map<Integer,List<String>> repToFile = MatchKMLToPlaceRep.createMatchFileMap();
        repToFile.entrySet().forEach(
                entry ->
                    entry.getValue().forEach(
                        file -> loadFile(dataService, entry.getKey(), file)));

        solrService.shutdown();
        dbServices.shutdown();
        System.exit(0);
    }

    private static void loadFile(PlaceDataServiceImpl dataService, Integer key, String file) {
        System.out.println(key + " --> " + file);
        String kmlData = readKmlData(file);
        KMLResource kmlResource = new KMLResource(kmlData);
        long then = System.nanoTime();
        try {
            BoundaryBridge boundaryB = dataService.createBoundary(key.intValue(), kmlData, kmlResource.getPointCount(), kmlResource.getFromYear(), kmlResource.getToYear(), wlj, null);
            long nnow = System.nanoTime();
            System.out.println("TIME: " + (nnow-then)/1_000_000.0 + " --> " + boundaryB.getBoundaryId());
        } catch (PlaceDataException ex) {
            System.out.println("EXEX: " + ex.getMessage());
        }
    }

    protected static String readKmlData(String file) {
        Path path = Paths.get(kmlFileDir, file);
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException ex) {
            System.out.println("ReadKMLData@IO-EX: " + ex.getMessage());
            return null;
        }
    }

}
