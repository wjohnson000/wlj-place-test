package std.wlj.kml.newberry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AnalyzeUS_03_MapKmlToPlaceRep {

    static class PlacemarkMini {
        String  state;
        String  file;
        String  name;
        String  fromDate;
        String  toDate;
        int     fromYear;
        int     toYear;
        int     pointCount;
        double  latitude;
        double  longitude;
        RepMini matchedRep;
        RepMini matchedRepAlt;

        @Override public String toString() {
            return state + " . " + name;
        }
    }

    static class RepMini {
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

    static final String pathToKML = "D:/postgis/newberry/bdy-kml-us.txt";
    static final String pathToRep = "D:/postgis/newberry/bdy-rep.txt";
    static final String pathToOut = "D:/postgis/newberry/boundary-match-us.txt";

    static Map<String, List<PlacemarkMini>> statePmMap = new TreeMap<>();
    static Map<String, List<RepMini>> stateRepMap = new HashMap<>();
    static Map<Integer, RepMini> repMap = new HashMap<>();

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
        for (List<PlacemarkMini> pmList : statePmMap.values()) {
            for (PlacemarkMini pmMini : pmList) {
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
                    newKmlData.add(kmlPart);
                }

                if (pmMini.matchedRep != null) {
                    match++;
                    RepMini rep = pmMini.matchedRep;
                    RepMini parent = repMap.get(rep.parentId);
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
                    if (pmMini.matchedRepAlt != null) {
                        buff.append("|").append("Alternate-1");
                    }
                    newKmlData.add(buff.toString());
                }

                if (pmMini.matchedRepAlt != null) {
                    match++;
                    RepMini rep = pmMini.matchedRepAlt;
                    RepMini parent = repMap.get(rep.parentId);
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
                    newKmlData.add(buff.toString());
                }
            }
        }

        System.out.println("Total boundaries: " + total);
        System.out.println("PlaceRep matches: " + match);

        Files.write(Paths.get(pathToOut), newKmlData, StandardOpenOption.CREATE);
    }

    static void associateLocationsToReps() {
        // First pass ... match in the current state only ...
        for (Map.Entry<String, List<PlacemarkMini>> entry : statePmMap.entrySet()) {
            System.out.println("Processing state: " + entry.getKey());
            List<PlacemarkMini> pmList = entry.getValue();
            List<RepMini> repList = stateRepMap.get(entry.getKey());

            if (pmList.isEmpty()  ||  repList == null  ||  repList.isEmpty()) {
                System.out.println("  >> No values to process!!");
                continue;
            }

            for (PlacemarkMini placemark : pmList) {
                List<RepMini> matchedReps = repList.stream()
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
        for (List<PlacemarkMini> placemarks : statePmMap.values()) {
            for (PlacemarkMini placemark : placemarks) {
                if (placemark.matchedRep == null) {
                    List<RepMini> matchedReps = repMap.values().stream()
                            .filter(rep -> rep.name.equalsIgnoreCase(placemark.name))
                            .filter(rep -> "County".equals(rep.type))
                            .filter(rep -> rep.fromYear == 0  || rep.fromYear <= placemark.toYear)
                            .filter(rep -> rep.toYear == 0  ||  placemark.fromYear <= rep.toYear)
                            .filter(rep -> distance(placemark.latitude, placemark.longitude, rep.centerLattd, rep.centerLong) < 15.0)
                            .collect(Collectors.toList());

                    if (matchedReps.size() == 1) {
                        placemark.matchedRep = matchedReps.get(0);
                    } else if (matchedReps.size() > 1) {
                        RepMini bestMatch = matchedReps.stream()
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
        for (List<PlacemarkMini> placemarks : statePmMap.values()) {
            for (PlacemarkMini placemark : placemarks) {
                if (placemark.matchedRep == null) {
                    List<RepMini> matchedReps = repMap.values().stream()
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
        List<String> allLines = Files.readAllLines(Paths.get(pathToKML), StandardCharsets.UTF_8);
        for (String line : allLines) {
            String[] fields = line.split("\\|");
            if (fields.length < 8) {
                continue;
            }

            PlacemarkMini pmMini = new PlacemarkMini();
            pmMini.file = fields[0];
            pmMini.state = fields[1];
            pmMini.name = fields[2];
            pmMini.fromDate = fields[3];
            pmMini.toDate = fields[4];
            pmMini.fromYear = Integer.parseInt(fields[3].substring(0, 4));
            pmMini.toYear = Integer.parseInt(fields[4].substring(0, 4));
            pmMini.pointCount = Integer.parseInt(fields[5]);
            pmMini.latitude = Double.parseDouble(fields[6]);
            pmMini.longitude = Double.parseDouble(fields[7]);

            List<PlacemarkMini> placemarks = statePmMap.get(fields[1]);
            if (placemarks == null) {
                placemarks = new ArrayList<>();
                statePmMap.put(fields[1], placemarks);
            }
            placemarks.add(pmMini);
        }
    }

    static void loadReps() throws IOException {
        int level00Id = -1;
        int level01Id = -1;
        String stateName = "";

        List<String> allLines = Files.readAllLines(Paths.get(pathToRep), StandardCharsets.UTF_8);
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

            RepMini rep = new RepMini();
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
                List<RepMini> stateReps = stateRepMap.get(stateName);
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
