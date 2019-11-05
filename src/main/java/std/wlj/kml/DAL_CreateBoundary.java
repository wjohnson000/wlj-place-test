package std.wlj.kml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.familysearch.standards.place.dao.DAOFactory;
import org.familysearch.standards.place.dao.dbimpl.DAOFactoryImpl;
import org.familysearch.standards.place.dao.model.DbRepBoundary;

import std.wlj.util.DbConnectionManager;

public class DAL_CreateBoundary {

    static final String kmlFileDir = "D:/postgis/files";

    public static void main(String... args) throws IOException {
        Map<Integer,List<String>> repToFile = MatchKMLToPlaceRep.createMatchFileMap();
        DAOFactory daoFactory = new DAOFactoryImpl(DbConnectionManager.getDataSourceSams());

        repToFile.entrySet().forEach(
            entry ->
                entry.getValue().forEach(
                    file -> loadFile(daoFactory, entry.getKey(), file)));
    }

    private static void loadFile(DAOFactory daoFactory, Integer key, String file) {
        System.out.println(key + " --> " + file);
        String kmlData = readKmlData(file);
        KMLResource kmlResource = new KMLResource(kmlData);
        if (kmlResource.isValid()) {
            long then = System.nanoTime();
            DbRepBoundary repBoundary = new DbRepBoundary();
            repBoundary.setRepId(key.intValue());
            repBoundary.setPointCount(kmlResource.getPointCount());
            repBoundary.setFromYear(kmlResource.getFromYear());
            repBoundary.setToYear(kmlResource.getToYear());
            repBoundary.setGeographyData(kmlResource.getGeometryData());
            DbRepBoundary repBoundaryC = daoFactory.getRepBoundaryDAO().create(repBoundary);
            long nnow = System.nanoTime();
            System.out.println("TIME: " + (nnow-then)/1_000_000.0 + " --> " + repBoundaryC.getId());
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
