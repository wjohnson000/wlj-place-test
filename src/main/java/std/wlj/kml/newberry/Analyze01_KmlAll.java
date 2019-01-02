package std.wlj.kml.newberry;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import std.wlj.kml.model.*;
import std.wlj.marshal.POJOMarshalUtil;

/**
 * Find all ".kml" files in the source directory, excluding those which are US-wide, and
 * generate a file with the following data fields:
 * <ul>
 *   <li>kml-file name<li>
 *   <li>placemark name<li>
 *   <li>timespan start<li>
 *   <li>timespan end<li>
 *   <li># of points in boundary<li>
 *   <li>latitude<li>
 *   <li>longitude<li>
 *   <li>description of change<li>
 * </ul>
 * 
 * @author wjohnson000
 *
 */
public class Analyze01_KmlAll {

    static String baseDir = "D:/postgis/newberry";
    static String outputFileName = "bndy-01-kml.txt";

    static Map<String,String> boundaryDataGM = new TreeMap<>();
    static Map<String,String> boundaryDataPT = new TreeMap<>();
    static Map<String,String> boundaryDataCH = new TreeMap<>();

    public static void main(String...args) throws IOException {
        File baseDirFile = new File(baseDir);
        String[] allFiles = baseDirFile.list();
        Arrays.stream(allFiles)
            .filter(ff -> ff.endsWith("kml"))
            .filter(ff -> ! ff.startsWith("US"))
            .forEach(ff -> processKml(new File(baseDirFile, ff)));

        List<String> boundaryData = mergeGeomAndPoint();
        boundaryData.add(0, "");
        Files.write(Paths.get(baseDir, outputFileName), boundaryData, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static List<String> mergeGeomAndPoint() {
        List<String> results = new ArrayList<>();

        for (Map.Entry<String, String> entry : boundaryDataGM.entrySet()) {
            String kmlData  = entry.getKey();
            String geomData = entry.getValue();
            String ptData   = boundaryDataPT.get(kmlData);
            String chData   = boundaryDataCH.get(kmlData);

            if (ptData == null) {
                ptData = "|";
                System.out.println("No PT data for: " + kmlData);
            }

            if (chData == null) {
                chData = "|";
                System.out.println("No CH data for: " + kmlData);
            }

            results.add(kmlData + geomData + ptData + chData);
        }

        return results;
    }

    static void processKml(File file) {
        System.out.println("Processing file: " + file + "; Geoms=" + boundaryDataGM.size() + "; PTS=" + boundaryDataPT.size());
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
        keyBuff.append("|").append(removeTags(placemarkName));
        if (placemark.getTimeSpan() == null) {
            keyBuff.append("||");
        } else {
            keyBuff.append("|").append(placemark.getTimeSpan().getBegin());
            keyBuff.append("|").append(placemark.getTimeSpan().getEnd());
        }

        String change = "";
        if (placemark.getExtendedData() != null) {
            ExtendedDataModel extndData = placemark.getExtendedData();
            if (extndData.getSchemaData() != null) {
                SchemaDataModel schemaData = extndData.getSchemaData();
                if (schemaData != null) {
                    List<SimpleDataModel> simpleDatas = schemaData.getSimpleData();
                    if (simpleDatas != null) {
                        change = simpleDatas.stream()
                                    .filter(sdModel -> "CHANGE".equals(sdModel.getName()))
                                    .map(sdModel -> sdModel.getValue())
                                    .findFirst()
                                    .orElse("");
                    }
                }
            }
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
            boundaryDataCH.put(keyBuff.toString(), "|" + change);
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
