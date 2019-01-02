package std.wlj.kml.newberry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Take the results from step 03 (MapKmlToRep) and determine which of the potentially man
 * boundaries for a single rep should be loaded.
 * 
 * <ul>
 *   <li>"true" if this is a boundary to load</li>
 *   <li>kml-file name<li>
 *   <li>state name<li>
 *   <li>placemark name<li>
 *   <li>timespan start<li>
 *   <li>timespan end<li>
 *   <li># of points in boundary<li>
 *   <li>latitude<li>
 *   <li>longitude<li>
 *   <li>rep id</li>
 *   <li>rep display name</li>
 *   <li>rep type</li>
 *   <li>parent rep id</li>
 *   <li>parent rep display name</li>
 *   <li>parent rep type</li>
 *   <li>rep from date</li>
 *   <li>rep to date</li>
 *   <li>rep latitude</li>
 *   <li>rep longitude</li>
 *   <li>distance between KML and DB centroid values</li>
 *   <li>is this a secondary match?</li>
 *   <li>description of change<li>
 * </ul>
 * 
 * @author wjohnson000
 *
 */
public class Analyze04_CollapseKmlFiles {

    static final int    SIX_YEARS  = 2000;
    static final int    SIX_MONTHS = 180;
    static final double EIGHT_KM   = 8.0;

    static final String pathToIn  = "D:/postgis/newberry/bndy-03-match.txt";
    static final String pathToOut = "D:/postgis/newberry/bndy-04-match.txt";

    static class KmlToRepData04 implements Comparable<KmlToRepData04> {
        String key;
        String line;
        String file;
        String folder;
        String pmName;
        String fromDate;
        String toDate;
        int    coordCnt;
        double lattd;
        double longtd;
        String repId;

        KmlToRepData04(String line) {
            String[] tokens = line.split("\\|");

            this.line = line;
            if (tokens.length < 9) {
                this.key = "no-match";
            } else {
                this.file = tokens[0];
                this.folder = tokens[1];
                this.pmName = tokens[2];
                this.fromDate = tokens[3];
                this.toDate = tokens[4];
                this.coordCnt = Integer.parseInt(tokens[5]);
                this.lattd    = Double.parseDouble(tokens[6]);
                this.longtd   = Double.parseDouble(tokens[7]);
                this.repId    = tokens[8];
                this.key      = this.file + "." + this.folder + "." + this.pmName;
            }
        }

        @Override public String toString() {
            return pmName + " (" + fromDate + "-" + toDate + ")";
        }

        @Override public int compareTo(KmlToRepData04 that) {
            return this.key.compareToIgnoreCase(that.key);
        }
    }

    static Map<String, List<KmlToRepData04>> kmlMap = new TreeMap<>();

    public static void main(String... args) throws IOException {
        loadMap();
        collapseKml();
    }

    static void loadMap() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(pathToIn), StandardCharsets.UTF_8);
        kmlMap.putAll(lines.stream()
                           .map(line -> new KmlToRepData04(line))
                           .collect(Collectors.groupingBy(kml -> kml.key)));
    }

    static void collapseKml() throws IOException {
        int discardCnt = 0;
        List<String> newKmlData = new ArrayList<>();

        for (Map.Entry<String, List<KmlToRepData04>> entry : kmlMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("no-match")) {
                ;  // do nothing
            } else if (entry.getValue().size() == 1) {
                KmlToRepData04 kmlData = entry.getValue().get(0);
                addData(true, kmlData, newKmlData);
            } else {
                List<KmlToRepData04> kmlDatas = entry.getValue();
                KmlToRepData04 kmlDataPrev = kmlDatas.remove(0);
                KmlToRepData04 kmlDataLast = kmlDatas.remove(kmlDatas.size()-1);

                addData(true, kmlDataPrev, newKmlData);
                for (KmlToRepData04 kmlData : kmlDatas) {
                    int duration = dayDuration(kmlData);
                    int ptDiff   = Math.abs(kmlData.coordCnt - kmlDataPrev.coordCnt);
                    double dist  = distance(kmlData, kmlDataPrev);
//                    if (duration > SIX_YEARS  ||  (duration > SIX_MONTHS  &&  (ptDiff > kmlDataPrev.coordCnt/20  ||  dist > EIGHT_KM))) {
                    if (ptDiff > kmlDataPrev.coordCnt/8  ||  dist > EIGHT_KM) {
                        addData(true, kmlData, newKmlData);
                        kmlDataPrev = kmlData;
                    } else {
                        discardCnt++;
                        addData(false, kmlData, newKmlData);
                    }
                }
                addData(true, kmlDataLast, newKmlData);
            }
        }

        newKmlData.add(0, "");
        Files.write(Paths.get(pathToOut), newKmlData, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("Discard ..." + discardCnt);
    }

    private static void addData(boolean addIt, KmlToRepData04 kmlData, List<String> newKmlData) {
        String keep = (addIt && kmlData.repId.length() > 0) ? "true" : "";
        newKmlData.add(keep + "|" + kmlData.line);
    }

    static int dayDuration(KmlToRepData04 kmlData) {
        int fromYr = Integer.parseInt(kmlData.fromDate.substring(0, 4));
        int fromMo = Integer.parseInt(kmlData.fromDate.substring(5, 7));
        int fromDa = Integer.parseInt(kmlData.fromDate.substring(8, 10));

        int toYr = Integer.parseInt(kmlData.toDate.substring(0, 4));
        int toMo = Integer.parseInt(kmlData.toDate.substring(5, 7));
        int toDa = Integer.parseInt(kmlData.toDate.substring(8, 10));

        return (toYr - fromYr) * 365  +  (toMo - fromMo) * 30  +  (toDa - fromDa);
    }

    static double distance(KmlToRepData04 kmlData01, KmlToRepData04 kmlData02) {
        double lattd1  = kmlData01.lattd;
        double longtd1 = kmlData01.longtd;
        double lattd2  = kmlData02.lattd;
        double longtd2 = kmlData02.longtd;

        if (Math.abs(lattd1) < 0.001  &&  Math.abs(longtd1) < 0.001) {
            return Double.MAX_VALUE;
        }
        if (Math.abs(lattd2) < 0.001  &&  Math.abs(longtd2) < 0.001) {
            return Double.MAX_VALUE;
        }

        double theta = longtd1 - longtd2;
        double dist = Math.sin(deg2rad(lattd1)) * Math.sin(deg2rad(lattd2)) + Math.cos(deg2rad(lattd1)) * Math.cos(deg2rad(lattd2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }

    static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
