package std.wlj.kml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.sql.DataSource;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.BoundaryBridge;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.familysearch.standards.place.ws.model.kml.*;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.marshal.POJOMarshalUtil;
import std.wlj.util.SolrManager;

public class SVC_CreateBoundary_Newberry {
    static final String kmlFileDir = "D:/postgis/newberry/rep-boundary-us";

    private static PlaceDataServiceImpl dataService;
    private static String wlj = "wjohnson000";

    public static void main(String... args) throws IOException {
        DataSource ds = DbConnectionManager.getDataSourceDev55();
        DbReadableService dbReadSvc = new DbReadableService(ds);
        DbWritableService dbWriteSvc = new DbWritableService(ds);
        SolrService solrSvc = SolrManager.localEmbeddedService();
        dataService = new PlaceDataServiceImpl(solrSvc, dbReadSvc, dbWriteSvc);

        String[] files = new File(kmlFileDir).list();
        Arrays.stream(files).forEach(file -> loadFile(dataService, file));

        solrSvc.shutdown();
        dbReadSvc.shutdown();
        dbWriteSvc.shutdown();
        System.exit(0);
    }

    private static void loadFile(PlaceDataServiceImpl dataService, String file) {
        int ndx = file.indexOf("-");
        int repId = Integer.parseInt(file.substring(0, ndx));

        System.out.println(file + " --> " + repId);
        String kmlData = readKmlData(file);
        KmlModel kmlModel = POJOMarshalUtil.fromXML(kmlData, KmlModel.class);

        long then = System.nanoTime();
        try {
            if (kmlModel.getDocument().getPlacemarksAll().isEmpty()  ||  kmlModel.getDocument().getPlacemarksAll().get(0).getGeometry() == null) {
                System.out.println("   No placemarks ... ");
            } else {
                PlacemarkModel pMark = kmlModel.getDocument().getPlacemarksAll().get(0);
                BoundaryBridge boundaryB = dataService.createBoundary(repId, pMark.getGeometryAsXML(), (int)pMark.getGeometry().getPointCount(), getFromYear(pMark), getToYear(pMark), wlj, null);
                long nnow = System.nanoTime();
                System.out.println("TIME: " + (nnow-then)/1_000_000.0 + " --> " + boundaryB.getBoundaryId());
            }
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

    protected static Integer getFromYear(PlacemarkModel pMark) {
        Integer fromYr = null;
        if (pMark.getTimeSpan() != null  &&  pMark.getTimeSpan().getBegin() != null) {
            fromYr = Integer.parseInt(pMark.getTimeSpan().getBegin().substring(0, 4));
        }
        return fromYr;
    }

    protected static Integer getToYear(PlacemarkModel pMark) {
        Integer toYr = null;
        if (pMark.getTimeSpan() != null  &&  pMark.getTimeSpan().getEnd() != null) {
            toYr = Integer.parseInt(pMark.getTimeSpan().getEnd().substring(0, 4));
        }
        return toYr;
    }
}
