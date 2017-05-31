package std.wlj.kml.newberry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import std.wlj.kml.model.*;
import std.wlj.marshal.POJOMarshalUtil;

public class AnalyzeUS_01_KmlAll {

    static String baseDir = "D:/postgis/newberry";
    static Map<String,String> boundaryDataGM = new TreeMap<>();
    static Map<String,String> boundaryDataPT = new TreeMap<>();

    public static void main(String...args) throws IOException {
        processKml(new File(baseDir, "US_HistCounties_Gen001.kml"));
        List<String> boundaryData = mergeGeomAndPoint();
        Files.write(Paths.get(baseDir, "bdy-kml-us.txt"), boundaryData, StandardOpenOption.CREATE);
    }

    static List<String> mergeGeomAndPoint() {
        List<String> results = new ArrayList<>();

        for (Map.Entry<String, String> entry : boundaryDataGM.entrySet()) {
            String kmlData  = entry.getKey();
            String geomData = entry.getValue();
            String ptData   = boundaryDataPT.get(kmlData);

            if (ptData == null) {
                ptData = "|";
                System.out.println("No PT data for: " + kmlData);
            }

            results.add(kmlData + geomData + ptData);
        }

        return results;
    }

    static void processKml(File file) {
        System.out.println("Processing file: " + file);
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            String kml = new String(bytes);
            kml = kml.replaceAll("earth.google.com", "www.opengis.net");
            KmlModel kmlModel = POJOMarshalUtil.fromXML(kml, KmlModel.class);
            processDocument(file.getName(), kmlModel.getDocument());
        } catch (Exception ex) {
            System.out.println("EX: " + ex);
        }
    }

    static void processDocument(String fileName, DocumentModel document) {
        String docName = removeTags(document.getName());
        if (document.getFolders() != null) {
            document.getFolders().forEach(folder -> processFolder(fileName, docName, folder));
        }
        if (document.getPlacemarks() != null) {
            document.getPlacemarks().forEach(mark -> processPlacemark(fileName, docName, "", mark));
        }
    }

    static void processFolder(String fileName, String docName, FolderModel folder) {
        String folderName = removeTags(folder.getName());
        if (folder.getFolders() != null) {
            folder.getFolders().forEach(folderX -> processFolder(fileName, docName, folderX));
        }
        if (folder.getPlacemarks() != null) {
            folder.getPlacemarks().forEach(mark -> processPlacemark(fileName, docName, folderName, mark));
        }
    }

    static void processPlacemark(String fileName, String docName, String folderName, PlacemarkModel placemark) {
        StringBuilder keyBuff = new StringBuilder();

        String placemarkName = placemark.getName();
        int ndx = placemarkName.lastIndexOf("(");
        if (ndx > 1) {
            try {
                String yr = placemarkName.substring(ndx+1, ndx+5);
                Integer.parseInt(yr);
                placemarkName = placemarkName.substring(0, ndx).trim();
            } catch(Exception ex) {
                // Do Nothing ...
            }
        }

        keyBuff.append(fileName);
        keyBuff.append("|").append(removeTags(folderName));
        keyBuff.append("|").append(removeTags(placemarkName));
        if (placemark.getTimeSpan() == null) {
            keyBuff.append("||");
        } else {
            keyBuff.append("|").append(placemark.getTimeSpan().getBegin());
            keyBuff.append("|").append(placemark.getTimeSpan().getEnd());
        }

        if (placemark.getGeometry() == null) {
            keyBuff.append("|");
            System.out.println("NO Geometry ...");
        } else if (placemark.getGeometry().getPointCount() == 1) {
            StringBuilder valBuff = new StringBuilder();
            if (placemark.getGeometry() instanceof PointModel) {
                PointModel centroid = (PointModel) placemark.getGeometry();
                String centroidStr = centroid.getCoordinates().replace('\r', ' ').replace('\n', ' ').trim();
                String[] longLat = centroidStr.split(",");
                valBuff.append("|").append(longLat[1]).append("|").append(longLat[0]);
            }
            boundaryDataPT.put(keyBuff.toString(), valBuff.toString());
        } else {
            StringBuilder valBuff = new StringBuilder();
            valBuff.append("|").append(placemark.getGeometry().getPointCount());
            boundaryDataGM.put(keyBuff.toString(), valBuff.toString());
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
