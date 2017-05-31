package std.wlj.kml.newberry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

import std.wlj.kml.model.*;
import std.wlj.marshal.POJOMarshalUtil;

public class AnalyzeKmlTazewell {

    static String baseDir = "D:/postgis/newberry";
    static String currDocName = "";
    static String currFolderName = "";
    static DocumentModel docModel;

    public static void main(String...args) throws IOException {
        docModel = new DocumentModel();
        docModel.setFolders(new ArrayList<>());

        KmlModel kmlModel = new KmlModel();
        kmlModel.setDocument(docModel);

        File baseDirFile = new File(baseDir);
        String[] allFiles = baseDirFile.list();
        Arrays.stream(allFiles)
            .filter(ff -> ff.endsWith("kml"))
            .filter(ff -> ff.startsWith("IL") || ff.startsWith("US"))
            .forEach(ff -> processKml(new File(baseDirFile, ff)));

        String kml = POJOMarshalUtil.toXML(kmlModel);
        Files.write(Paths.get(baseDir, "tazewell.kml"), kml.getBytes(), StandardOpenOption.CREATE);
    }

    static void processKml(File file) {
        System.out.println("Processing file: " + file);
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            String kml = new String(bytes);
            kml = kml.replaceAll("earth.google.com", "www.opengis.net");
            KmlModel kmlModel = POJOMarshalUtil.fromXML(kml, KmlModel.class);
            processDocument(kmlModel.getDocument());
        } catch (Exception ex) {
            System.out.println("EX: " + ex);
        }
    }

    static void processDocument(DocumentModel document) {
        String docName = removeTags(document.getName());
        if (document.getFolders() != null) {
            document.getFolders().forEach(folder -> processFolder(docName, folder));
        }
        if (document.getPlacemarks() != null) {
            document.getPlacemarks().forEach(mark -> processPlacemark(docName, "", mark));
        }
    }

    static void processFolder(String docName, FolderModel folder) {
        String folderName = removeTags(folder.getName());
        if (folder.getFolders() != null) {
            folder.getFolders().forEach(folderX -> processFolder(docName, folderX));
        }
        if (folder.getPlacemarks() != null) {
            folder.getPlacemarks().forEach(mark -> processPlacemark(docName, folderName, mark));
        }
    }

    static void processPlacemark(String docName, String folderName, PlacemarkModel placemark) {
        String placemarkName = removeTags(placemark.getName());
        long pointCount = placemark.getGeometry().getPointCount();

        if (pointCount > 4  &&  placemarkName.contains("TAZEWELL (1841-02-27)")) {
            System.out.println(docName + " . " + folderName + " . " + placemarkName);

            FolderModel folderModel = new FolderModel();
            folderModel.setName(folderName + " .. " + pointCount);
            folderModel.setPlacemarks(Arrays.asList(placemark));

            docModel.getFolders().add(folderModel);
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
