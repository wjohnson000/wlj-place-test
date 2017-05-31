package std.wlj.kml.newberry;

import java.io.File;
import java.util.List;

import de.micromata.opengis.kml.v_2_2_0.*;

public class AnalyzeKML_OpenGIS {
    public static void main(String...args) {
//        Kml kml = Kml.unmarshal(new File("D:/postgis/newberry/AL_Historical_Counties.kml"));
        Kml kml = Kml.unmarshal(new File("D:/postgis/files/county-AL_Baldwin.kml"));
        System.out.println("KML: " + kml);

        Feature document = kml.getFeature();
        processDocument((Document)document);
    }

    static void processDocument(Document document) {
        System.out.println("DOC: " + removeTags(document.getName()));

        List<Feature> folders = document.getFeature();
        for (Feature folder : folders) {
            processFolder((Folder)folder);
        }
    }

    static void processFolder(Folder folder) {
        System.out.println("  Folder: " + removeTags(folder.getName()));

        List<Feature> placemarks = folder.getFeature();
        for (Feature placemark : placemarks) {
            processPlacemark((Placemark)placemark);
        }
    }

    static void processPlacemark(Placemark placemark) {
        System.out.println("    Placemark: " + removeTags(placemark.getName()));

        Geometry geometry = placemark.getGeometry();
        if (geometry instanceof Polygon) {
            Polygon polygon = (Polygon)geometry;
            Boundary boundary = polygon.getOuterBoundaryIs();
            System.out.println("      PY.ob: " + boundary + " --> " + boundary.getLinearRing().getCoordinates().size());
        } else if (geometry instanceof MultiGeometry) {
            MultiGeometry multiGeometry = (MultiGeometry)geometry;
            for (Geometry geometryX : multiGeometry.getGeometry()) {
                if (geometryX instanceof Polygon) {
                    Polygon polygon = (Polygon)geometryX;
                    Boundary boundary = polygon.getOuterBoundaryIs();
                    System.out.println("      MG.PY.ob: " + boundary + " --> " + boundary.getLinearRing().getCoordinates().size());
                }
            }
        }

    }

    static String removeTags(String input) {
        if (input == null) {
            return "";
        }

        String tInput = input;
        int ndx0 = tInput.indexOf('<');
        while (ndx0 >= 0) {
            int ndx1 = tInput.indexOf('>');
            String tBeg = (ndx0 == 0) ? "" : tInput.substring(0, ndx0);
            String tEnd = (ndx1 == tInput.length()) ? "" : tInput.substring(ndx1+1);
            tInput = tBeg + " " + tEnd;
            ndx0 = tInput.indexOf('<');
        }
        return tInput.trim();
    }
}
