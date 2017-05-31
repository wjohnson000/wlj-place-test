package std.wlj.kml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import de.micromata.opengis.kml.v_2_2_0.*;

public class SplitKMLByFeature {

    static String[][] fileData = {
        { "cb_2015_us_state_5m.kml", "state-5m" },
        { "cb_2015_us_state_500k.kml", "state-500k" },
        { "cnt_us.kml", "county" },
    };

    public static void main(String...args) {
        Arrays.stream(fileData)
            .forEach(entry -> processKml(entry[0], entry[1]));
    }

    static void processKml(String fileName, String prefix) {
        Kml kml = Kml.unmarshal(new File("D:/postgis/" + fileName));
        Kml kmlClone = kml.clone();

        processDocument(prefix, (Document)kml.getFeature(), kmlClone);
    }

    static void processKmlOLD(String fileName, String prefix) {
        Kml kml = Kml.unmarshal(new File("D:/postgis/" + fileName));
        Kml kml2 = kml.clone();

        Feature kmlFeature01 = kml.getFeature();
        Document document01  = (Document)kmlFeature01;
        Feature docFeature01 = document01.getFeature().get(0);
        Folder  folder01     = (Folder)docFeature01;
        List<Feature> what   = folder01.getFeature();

        Feature kmlFeature02 = kml2.getFeature();
        Document document02  = (Document)kmlFeature02;
        Feature docFeature02 = document02.getFeature().get(0);
        Folder  folder02     = (Folder)docFeature02;

        for (Feature state : what) {
            Placemark placemark = (Placemark)state;
            String placemarkName = removeTags(placemark.getName());
            System.out.println("Processing: " + placemarkName);

            try {
                folder02.setFeature(Arrays.asList(state));
                kml2.marshal(new File("D:/postgis/files/" + prefix + "-" + placemarkName + ".kml"));
            } catch (FileNotFoundException e) {
                System.out.println("  Unable to marshal file ... " + e.getMessage());
            }
        }
    }

    static void processDocument(String prefix, Document document, Kml kmlClone) {
        List<Feature> folders = document.getFeature();
        for (Feature fFeature : folders) {
            Folder folder = (Folder) fFeature;
            List<Feature> placemarks = folder.getFeature();
            for (Feature fPlacemark : placemarks) {
                Placemark placemark = (Placemark)fPlacemark;
                processPlacemark(prefix, kmlClone, folder, placemark);
            }
        }
    }

    static void processPlacemark(String prefix, Kml kmlClone, Folder folder, Placemark placemark) {
        String folderName = removeTags(folder.getName());
        String placemarkName = removeTags(placemark.getName());
        String name = (placemarkName == null) ? folderName : placemarkName;

        Document cloneDoc = (Document)kmlClone.getFeature();
        Folder   cloneFolder = (Folder)cloneDoc.getFeature().get(0);
        cloneFolder.setFeature(Arrays.asList(placemark));
        cloneDoc.setFeature(Arrays.asList(cloneFolder));
        System.out.println("Processing: " + name);

        try {
            kmlClone.marshal(new File("D:/postgis/files/" + prefix + "-" + name + ".kml"));
        } catch (FileNotFoundException e) {
            System.out.println("  Unable to marshal file ... " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  Unable to save file ... " + e.getMessage());
        }
    }

    static String removeTags(String input) {
        if (input == null) {
            return null;
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
