package std.wlj.kml.newberry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import std.wlj.kml.model.*;
import std.wlj.marshal.POJOMarshalUtil;

public class AnalyzeKML {
    public static void main(String...args) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get("D:/postgis/newberry/US_HistCounties_Gen05.kml"));
        String kml = new String(bytes);
        kml = kml.replaceAll("earth.google.com", "www.opengis.net");
        KmlModel kmlModel = POJOMarshalUtil.fromXML(kml, KmlModel.class);

        processDocument(kmlModel.getDocument());
    }

    static void processDocument(DocumentModel document) {
        System.out.println("DOC: " + removeTags(document.getName()));
        if (document.getFolders() != null) {
            document.getFolders().forEach(folder -> processFolder(folder));
        }
        if (document.getPlacemarks() != null) {
            document.getPlacemarks().forEach(mark -> processPlacemark(mark));
        }
    }

    static void processFolder(FolderModel folder) {
        System.out.println("  Folder: " + removeTags(folder.getName()));
        if (folder.getFolders() != null) {
            folder.getFolders().forEach(folderX -> processFolder(folderX));
        }
        if (folder.getPlacemarks() != null) {
            folder.getPlacemarks().forEach(mark -> processPlacemark(mark));
        }
    }

    static void processPlacemark(PlacemarkModel placemark) {
        if (placemark.getGeometry().getPointCount() > 4) {
            String name = removeTags(placemark.getName());
            String span = "";
            String gtyp = "";
            long   pcnt = 0;
            
            if (placemark.getTimeSpan() != null) {
                span = placemark.getTimeSpan().getBegin() + " .. " + placemark.getTimeSpan().getEnd();
            }
            gtyp = placemark.getGeometry().getClass().getSimpleName();
            pcnt = placemark.getGeometry().getPointCount();

            System.out.println("  MARK: " + name + "|" + span + "|" + gtyp + "|" + pcnt);
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
