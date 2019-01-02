package std.wlj.kml.newberry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import std.wlj.kml.model.*;
import std.wlj.marshal.POJOMarshalUtil;

public class AnalyzeUS_04_SplitKmlFiles {

    static class KmlToRep {
        String key;
        String file;
        String folder;
        String pmName;
        String fromDate;
        String toDate;
        String repId;

        @Override public String toString() {
            return pmName + " (" + fromDate + "-" + toDate + ")";
        }
    }

    static final String basePath = "D:/postgis/newberry/";
    static final String pathToIn = basePath + "boundary-match-us.txt";

    static Map<String, KmlToRep> kmlToRepMap = new HashMap<>();

    public static void main(String...args) throws IOException {
        loadMapFile();
        splitFile("US_HistCounties_Gen001.kml");
    }

    static void loadMapFile() throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(pathToIn), StandardCharsets.UTF_8);
        for (String line : allLines) {
            String[] fields = line.split("\\|");
            if (fields.length < 9) {
                continue;
            }

            KmlToRep kmlRep = new KmlToRep();
            kmlRep.file = fields[0];
            kmlRep.folder = fields[1];
            kmlRep.pmName = fields[2];
            kmlRep.fromDate = fields[3];
            kmlRep.toDate = fields[4];
            kmlRep.repId = fields[8];
            kmlRep.key = kmlRep.toString();

            kmlToRepMap.put(kmlRep.key, kmlRep);
        }
        System.out.println("Count: " + kmlToRepMap.size());
    }
    
    static void splitFile(String kmlFile) {
        System.out.println("Processing file: " + kmlFile);
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(basePath + kmlFile));
            String kml = new String(bytes);
            kml = kml.replaceAll("earth.google.com", "www.opengis.net");
            KmlModel kmlModel = POJOMarshalUtil.fromXML(kml, KmlModel.class);
            kmlModel.getDocument().getFolders().forEach(folder -> processFolder(folder));
//            kmlModel.getDocument().getPlacemarksAll().stream().forEach(pMark -> matchAndSave(pMark));
        } catch (Exception ex) {
            System.out.println("EX: " + ex);
        }
    }

    static void processFolder(FolderModel folder) {
        if (folder.getFolders() != null) {
            folder.getFolders().forEach(folderX -> processFolder(folderX));
        }

        if (folder.getPlacemarks() != null) {
            folder.getPlacemarks().forEach(pMark -> matchAndSave(pMark));
        }
    }

    static void matchAndSave(PlacemarkModel pMark) {
        if (! (pMark.getGeometry() instanceof PointModel)) {
            String pName = pMark.getName();
            int ndx = pName.indexOf('(');
            if (ndx > 1) {
                try {
                    String yr = pName.substring(ndx+1, ndx+5);
                    Integer.parseInt(yr);
                    pName = pName.substring(0, ndx).trim();
                } catch(Exception ex) {
                    // Do Nothing ...
                }
            }

            String key = pName + " (" + pMark.getTimeSpan().getBegin() + "-" + pMark.getTimeSpan().getEnd() + ")";
            KmlToRep kmlRep = kmlToRepMap.get(key);
            if (kmlRep != null) {
                saveKmlForRep(pMark, kmlRep);
            }
        }
    }

    static void saveKmlForRep(PlacemarkModel pMark, KmlToRep kmlRep) {
        pMark.setExtendedData(null);

        FolderModel folderModel = new FolderModel();
        folderModel.setName(kmlRep.pmName);
        folderModel.addPlacemark(pMark);

        DocumentModel docModel = new DocumentModel();
        docModel.setName("Generated file for place-rep " + kmlRep.repId + " (" + kmlRep.pmName + ")");
        docModel.addFolder(folderModel);

        KmlModel kmlModel = new KmlModel();
        kmlModel.setDocument(docModel);

        try {
            StringBuilder buff = new StringBuilder();
            buff.append(kmlRep.repId);
            buff.append("-").append(kmlRep.file.substring(0, 2));
            buff.append("-").append(kmlRep.fromDate);
            buff.append("-TO-").append(kmlRep.toDate).append(".kml");

            String kmlRaw = POJOMarshalUtil.toXML(kmlModel);
            Files.write(Paths.get(basePath, "rep-boundary-us", buff.toString()), kmlRaw.getBytes(), StandardOpenOption.CREATE_NEW);
        } catch(IOException ex) {
            System.out.println("Unable to save file for: " + kmlRep.pmName + " . " + kmlRep.repId + " --> " + ex.getMessage());
        }
    }
}
