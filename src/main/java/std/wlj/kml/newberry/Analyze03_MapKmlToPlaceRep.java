package std.wlj.kml.newberry;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Take the results from step 01 (Analyze-KML) and step 2 (Analyze-DB) and attempt to
 * match boundaries with counties.  The results will be a new file:
 * <ul>
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
public class Analyze03_MapKmlToPlaceRep {

    static final String pathToKML = "D:/postgis/newberry/bndy-01-kml.txt";
    static final String pathToRep = "D:/postgis/newberry/bndy-02-rep.txt";
    static final String pathToOut = "D:/postgis/newberry/bndy-03-match.txt";

    static Map<String, String> stateAbbrMap = new TreeMap<>();
    static {
        stateAbbrMap.put("AL", "Alabama");
        stateAbbrMap.put("AK", "Alaska");
        stateAbbrMap.put("AZ", "Arizona");
        stateAbbrMap.put("AR", "Arkansas");
        stateAbbrMap.put("CA", "California");
        stateAbbrMap.put("CO", "Colorado");
        stateAbbrMap.put("CT", "Connecticut");
        stateAbbrMap.put("DE", "Delaware");
        stateAbbrMap.put("DT", "Dakota Territory");
        stateAbbrMap.put("DC", "District of Columbia");
        stateAbbrMap.put("FL", "Florida");
        stateAbbrMap.put("GA", "Georgia");
        stateAbbrMap.put("HI", "Hawaii");
        stateAbbrMap.put("ID", "Idaho");
        stateAbbrMap.put("IL", "Illinois");
        stateAbbrMap.put("IN", "Indiana");
        stateAbbrMap.put("IA", "Iowa");
        stateAbbrMap.put("KS", "Kansas");
        stateAbbrMap.put("KY", "Kentucky");
        stateAbbrMap.put("LA", "Louisiana");
        stateAbbrMap.put("ME", "Maine");
        stateAbbrMap.put("MD", "Maryland");
        stateAbbrMap.put("MA", "Massachusetts");
        stateAbbrMap.put("MI", "Michigan");
        stateAbbrMap.put("MN", "Minnesota");
        stateAbbrMap.put("MS", "Mississippi");
        stateAbbrMap.put("MO", "Missouri");
        stateAbbrMap.put("MT", "Montana");
        stateAbbrMap.put("NE", "Nebraska");
        stateAbbrMap.put("NV", "Nevada");
        stateAbbrMap.put("NH", "New Hampshire");
        stateAbbrMap.put("NJ", "New Jersey");
        stateAbbrMap.put("NM", "New Mexico");
        stateAbbrMap.put("NY", "New York");
        stateAbbrMap.put("NC", "North Carolina");
        stateAbbrMap.put("ND", "North Dakota");
        stateAbbrMap.put("OH", "Ohio");
        stateAbbrMap.put("OK", "Oklahoma");
        stateAbbrMap.put("OR", "Oregon");
        stateAbbrMap.put("PA", "Pennsylvania");
        stateAbbrMap.put("RI", "Rhode Island");
        stateAbbrMap.put("SC", "South Carolina");
        stateAbbrMap.put("SD", "South Dakota");
        stateAbbrMap.put("TN", "Tennessee");
        stateAbbrMap.put("TX", "Texas");
        stateAbbrMap.put("UT", "Utah");
        stateAbbrMap.put("VT", "Vermont");
        stateAbbrMap.put("VA", "Virginia");
        stateAbbrMap.put("WA", "Washington");
        stateAbbrMap.put("WV", "West Virginia");
        stateAbbrMap.put("WI", "Wisconsin");
        stateAbbrMap.put("WY", "Wyoming");

        stateAbbrMap.put("Alabama", "AL");
        stateAbbrMap.put("Alaska", "AK");
        stateAbbrMap.put("Arizona", "AZ");
        stateAbbrMap.put("Arkansas", "AR");
        stateAbbrMap.put("California", "CA");
        stateAbbrMap.put("Colorado", "CO");
        stateAbbrMap.put("Connecticut", "CT");
        stateAbbrMap.put("Dakota Territory", "DT");
        stateAbbrMap.put("Delaware", "DE");
        stateAbbrMap.put("District of Columbia", "DC");
        stateAbbrMap.put("Florida", "FL");
        stateAbbrMap.put("Georgia", "GA");
        stateAbbrMap.put("Hawaii", "HI");
        stateAbbrMap.put("Idaho", "ID");
        stateAbbrMap.put("Illinois", "IL");
        stateAbbrMap.put("Indiana", "IN");
        stateAbbrMap.put("Iowa", "IA");
        stateAbbrMap.put("Kansas", "KS");
        stateAbbrMap.put("Kentucky", "KY");
        stateAbbrMap.put("Louisiana", "LA");
        stateAbbrMap.put("Maine", "ME");
        stateAbbrMap.put("Maryland", "MD");
        stateAbbrMap.put("Massachusetts", "MA");
        stateAbbrMap.put("Michigan", "MI");
        stateAbbrMap.put("Minnesota", "MN");
        stateAbbrMap.put("Mississippi", "MS");
        stateAbbrMap.put("Missouri", "MO");
        stateAbbrMap.put("Montana", "MT");
        stateAbbrMap.put("Nebraska", "NE");
        stateAbbrMap.put("Nevada", "NV");
        stateAbbrMap.put("New Hampshire", "NH");
        stateAbbrMap.put("New Jersey", "NJ");
        stateAbbrMap.put("New Mexico", "NM");
        stateAbbrMap.put("New York", "NY");
        stateAbbrMap.put("North Carolina", "NC");
        stateAbbrMap.put("North Dakota", "ND");
        stateAbbrMap.put("Ohio", "OH");
        stateAbbrMap.put("Oklahoma", "OK");
        stateAbbrMap.put("Oregon", "OR");
        stateAbbrMap.put("Pennsylvania", "PA");
        stateAbbrMap.put("Rhode Island", "RI");
        stateAbbrMap.put("South Carolina", "SC");
        stateAbbrMap.put("South Dakota", "SD");
        stateAbbrMap.put("Tennessee", "TN");
        stateAbbrMap.put("Texas", "TX");
        stateAbbrMap.put("Utah", "UT");
        stateAbbrMap.put("Vermont", "VT");
        stateAbbrMap.put("Virginia", "VA");
        stateAbbrMap.put("Washington", "WA");
        stateAbbrMap.put("West Virginia", "WV");
        stateAbbrMap.put("Wisconsin", "WI");
        stateAbbrMap.put("Wyoming", "WY");
    }

    static class PlacemarkMini03 {
        String  file;
        String  state;
        String  name;
        String  fromDate;
        String  toDate;
        int     fromYear;
        int     toYear;
        int     pointCount;
        double  latitude;
        double  longitude;
        String  reason;
        RepMini03 matchedRep;
        RepMini03 matchedRepAlt;

        @Override public String toString() {
            return state + " . " + name;
        }
    }

    static class RepMini03 {
        int    level;
        int    repId;
        int    parentId;
        String name;
        String type;
        int    fromYear;
        int    toYear;
        double centerLong;
        double centerLattd;

        @Override public String toString() {
            return name;
        }
    }

    static Map<String, List<PlacemarkMini03>> statePmMap = new TreeMap<>();
    static Map<String, List<RepMini03>> stateRepMap = new HashMap<>();
    static Map<Integer, RepMini03> repMap = new HashMap<>();

    public static void main(String...args) throws IOException {
        loadPlacemarks();
        loadReps();
        associateLocationsToReps();
        saveData();
    }

    static void saveData() throws IOException {
        List<String> newKmlData = new ArrayList<>(150_000);

        int total = 0;
        int match = 0;
        for (List<PlacemarkMini03> pmList : statePmMap.values()) {
            for (PlacemarkMini03 pmMini : pmList) {
                total++;
                StringBuilder buff = new StringBuilder();
                buff.append(pmMini.file);
                buff.append("|").append(pmMini.state);
                buff.append("|").append(pmMini.name);
                buff.append("|").append(pmMini.fromDate);
                buff.append("|").append(pmMini.toDate);
                buff.append("|").append(pmMini.pointCount);
                buff.append("|").append(pmMini.latitude);
                buff.append("|").append(pmMini.longitude);
                String kmlPart = buff.toString();

                if (pmMini.matchedRep == null) {
                    newKmlData.add(kmlPart + "|||||||||||||" + pmMini.reason);
                }

                if (pmMini.matchedRep != null) {
                    match++;
                    RepMini03 rep = pmMini.matchedRep;
                    RepMini03 parent = repMap.get(rep.parentId);
                    double dist = distance(pmMini.latitude, pmMini.longitude, rep.centerLattd, rep.centerLong);

                    buff = new StringBuilder();
                    buff.append(kmlPart);
                    buff.append("|").append(rep.repId);
                    buff.append("|").append(rep.name);
                    buff.append("|").append(rep.type);
                    buff.append("|").append(rep.parentId);
                    buff.append("|").append((parent==null) ? "" : parent.name);
                    buff.append("|").append((parent==null) ? "" : parent.type);
                    buff.append("|").append(rep.fromYear);
                    buff.append("|").append(rep.toYear);
                    buff.append("|").append(rep.centerLattd);
                    buff.append("|").append(rep.centerLong);
                    buff.append("|").append(dist);
                    buff.append("|").append((pmMini.matchedRepAlt == null) ? "" : "Alternate-2");
                    buff.append("|").append(pmMini.reason);
                    newKmlData.add(buff.toString());
                }

                if (pmMini.matchedRepAlt != null) {
                    match++;
                    RepMini03 rep = pmMini.matchedRepAlt;
                    RepMini03 parent = repMap.get(rep.parentId);
                    double dist = distance(pmMini.latitude, pmMini.longitude, rep.centerLattd, rep.centerLong);

                    buff = new StringBuilder();
                    buff.append(kmlPart);
                    buff.append("|").append(rep.repId);
                    buff.append("|").append(rep.name);
                    buff.append("|").append(rep.type);
                    buff.append("|").append(rep.parentId);
                    buff.append("|").append((parent==null) ? "" : parent.name);
                    buff.append("|").append((parent==null) ? "" : parent.type);
                    buff.append("|").append(rep.fromYear);
                    buff.append("|").append(rep.toYear);
                    buff.append("|").append(rep.centerLattd);
                    buff.append("|").append(rep.centerLong);
                    buff.append("|").append(dist);
                    buff.append("|").append("Alternate-2");
                    buff.append("|").append(pmMini.reason);
                    newKmlData.add(buff.toString());
                }
            }
        }

        System.out.println("Total boundaries: " + total);
        System.out.println("PlaceRep matches: " + match);

        newKmlData.add(0, "");
        Files.write(Paths.get(pathToOut), newKmlData, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static void associateLocationsToReps() {
        // First pass ... match in the current state only ...
        for (Map.Entry<String, List<PlacemarkMini03>> entry : statePmMap.entrySet()) {
            System.out.println("Processing state: " + entry.getKey());
            List<PlacemarkMini03> pmList = entry.getValue();
            List<RepMini03> repList = stateRepMap.get(entry.getKey());

            if (pmList.isEmpty()  ||  repList == null  ||  repList.isEmpty()) {
                System.out.println("  >> No values to process!!");
                continue;
            }

            for (PlacemarkMini03 placemark : pmList) {
                List<RepMini03> matchedReps = repList.stream()
                    .filter(rep -> rep.name.equalsIgnoreCase(placemark.name))
                    .filter(rep -> rep.fromYear == 0  || rep.fromYear <= placemark.toYear)
                    .filter(rep -> rep.toYear == 0  ||  placemark.fromYear <= rep.toYear)
                    .collect(Collectors.toList());
                if (matchedReps.size() == 1) {
                    placemark.matchedRep = matchedReps.get(0);
                }
            }
        }

        // Second pass ... match in any state/territory, etc
        for (List<PlacemarkMini03> placemarks : statePmMap.values()) {
            for (PlacemarkMini03 placemark : placemarks) {
                if (placemark.matchedRep == null) {
                    List<RepMini03> matchedReps = repMap.values().stream()
                            .filter(rep -> rep.name.equalsIgnoreCase(placemark.name))
                            .filter(rep -> "County".equals(rep.type))
                            .filter(rep -> rep.fromYear == 0  || rep.fromYear <= placemark.toYear)
                            .filter(rep -> rep.toYear == 0  ||  placemark.fromYear <= rep.toYear)
                            .filter(rep -> distance(placemark.latitude, placemark.longitude, rep.centerLattd, rep.centerLong) < 15.0)
                            .collect(Collectors.toList());

                    if (matchedReps.size() == 1) {
                        placemark.matchedRep = matchedReps.get(0);
                    } else if (matchedReps.size() > 1) {
                        RepMini03 bestMatch = matchedReps.stream()
                            .filter(rep -> rep.fromYear == placemark.fromYear  &&  rep.toYear == placemark.toYear)
                            .findFirst()
                            .orElse(null);
                        if (bestMatch != null) {
                            placemark.matchedRep = bestMatch;
                        }
                    }
                }
            }
        }

        // Third pass ... match in any state/territory, widen the search ...
        for (List<PlacemarkMini03> placemarks : statePmMap.values()) {
            for (PlacemarkMini03 placemark : placemarks) {
                if (placemark.matchedRep == null) {
                    List<RepMini03> matchedReps = repMap.values().stream()
                            .filter(rep -> rep.name.equalsIgnoreCase(placemark.name))
                            .filter(rep -> "County".equals(rep.type))
                            .filter(rep -> rep.fromYear == 0  || rep.fromYear <= placemark.toYear)
                            .filter(rep -> rep.toYear == 0  ||  placemark.fromYear <= rep.toYear)
                            .filter(rep -> distance(placemark.latitude, placemark.longitude, rep.centerLattd, rep.centerLong) < 100.0)
                            .collect(Collectors.toList());

                    if (matchedReps.size() == 1) {
                        placemark.matchedRep = matchedReps.get(0);
                    } else if (matchedReps.size() == 2) {
                        int overlapYears1 = Math.max(placemark.toYear, matchedReps.get(0).toYear) - Math.max(placemark.fromYear, matchedReps.get(0).fromYear);
                        int overlapYears2 = Math.max(placemark.toYear, matchedReps.get(1).toYear) - Math.max(placemark.fromYear, matchedReps.get(1).fromYear);
                        if (overlapYears1 > 1  &&  overlapYears2 <= 1) {
                            placemark.matchedRep = matchedReps.get(0);
                        } else if (overlapYears1 <= 1  &&  overlapYears2 > 1) {
                            placemark.matchedRep = matchedReps.get(1);
                        } else {
                            placemark.matchedRep = matchedReps.get(0);
                            placemark.matchedRepAlt = matchedReps.get(1);
                        }
                    }
                }
            }
        }
    }

    static void loadPlacemarks() throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(pathToKML), Charset.forName("UTF-8"));
        for (String line : allLines) {
            String[] fields = line.split("\\|");
            if (fields.length < 8) {
                continue;
            }

            String fileName = fields[0];
            String stateAbbr = fileName.substring(0, 2);
            String stateName = stateAbbrMap.get(stateAbbr);

            PlacemarkMini03 pmMini = new PlacemarkMini03();
            pmMini.file = fileName;
            pmMini.state = stateName;
            pmMini.name = fields[1];
            pmMini.fromDate = fields[2];
            pmMini.toDate = fields[3];
            pmMini.fromYear = Integer.parseInt(fields[2].substring(0, 4));
            pmMini.toYear = Integer.parseInt(fields[3].substring(0, 4));
            pmMini.pointCount = Integer.parseInt(fields[4]);
            pmMini.latitude = Double.parseDouble(fields[5]);
            pmMini.longitude = Double.parseDouble(fields[6]);
            pmMini.reason = fields[7];

            List<PlacemarkMini03> placemarks = statePmMap.get(stateName);
            if (placemarks == null) {
                placemarks = new ArrayList<>();
                statePmMap.put(stateName, placemarks);
            }
            placemarks.add(pmMini);
        }
    }

    static void loadReps() throws IOException {
        int level00Id = -1;
        int level01Id = -1;
        String stateName = "";

        List<String> allLines = Files.readAllLines(Paths.get(pathToRep), Charset.forName("UTF-8"));
        for (String line : allLines) {
            String[] fields = line.split("\\|");
            if (fields.length < 8) {
                continue;
            }

            int parentId = -1;
            int level = Integer.parseInt(fields[0]);
            int repId = Integer.parseInt(fields[1]);

            if (level == 0) {
                level00Id = repId;
            } else if (level == 1) {
                parentId = level00Id;
                level01Id = repId;
                stateName = fields[2];
            } else if (level == 2) {
                parentId = level01Id;
            } else {
                System.out.println("BAD LINE: " + line);
                continue;
            }

            RepMini03 rep = new RepMini03();
            rep.level = level;
            rep.repId = repId;
            rep.parentId = parentId;
            rep.name = fields[2];
            rep.type = fields[3];
            try { rep.fromYear = Integer.parseInt(fields[4]); } catch(Exception ex) { }
            try { rep.toYear = Integer.parseInt(fields[5]); } catch(Exception ex) { }
            try { rep.centerLattd = Double.parseDouble(fields[6]); } catch(Exception ex) { }
            try { rep.centerLong = Double.parseDouble(fields[7]); } catch(Exception ex) { }

            repMap.put(rep.repId, rep);
            if (stateName != null) {
                List<RepMini03> stateReps = stateRepMap.get(stateName);
                if (stateReps == null) {
                    stateReps = new ArrayList<>();
                    stateRepMap.put(stateName, stateReps);
                }
                stateReps.add(rep);
            }
        }
    }

    static double distance(double lattd1, double longtd1, double lattd2, double longtd2) {
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
