/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * See:
 *    https://en.wikipedia.org/wiki/List_of_Emperors_of_Japan
 *    https://www.jref.com/articles/list-of-japanese-emperors.91/
 *    https://en.wikipedia.org/wiki/List_of_Japanese_era_names
 *    https://wiki.samurai-archives.com/index.php?title=Emperors_of_Japan
 * @author wjohnson000
 *
 */
public class JA_CreateDictionary {

    static final String JA_EMPEROR_FILE  = "C:/temp/ja-emperors.txt";
    static final String JA_REIGN_FILE    = "C:/temp/ja-eras.txt";

    static final String XXX_GROUP_FORMAT = "  <word-group lang=\"ja\" type=\"%s\" meta=\"%s\">";
    static final String DYN_WORD_FORMAT  = "    <word lang=\"ja\" meta=\"%s\">%s</word> <!-- %s -->";
    static final String EMP_WORD_FORMAT  = "    <word lang=\"ja\" type=\"%s\" meta=\"%s\">%s</word>";

    static final List<String[]> dynasties = new ArrayList<>();
    static final Map<String[], List<String[]>> dyn2emp = new HashMap<>();
    static final Map<String[], List<String[]>> emp2era = new HashMap<>();
    static final Map<String[], String> empIdentifier = new HashMap<>();

    static int counter = 1;

    public static void main(String... args) throws Exception {
        // Read and process the DYNASTY --> EMPEROR file
        List<String> dynEmpData = Files.readAllLines(Paths.get(JA_EMPEROR_FILE), StandardCharsets.UTF_8);

        String[] dynasty = null;
        for (String datum :dynEmpData) {
            if (datum.startsWith("Dyn")) {
                dynasty = PlaceHelper.split(datum, '|');
                dynasties.add(0, dynasty);
                dyn2emp.put(dynasty, new ArrayList<>(10));
            } else if (datum.startsWith("Emp")) {
                String[] emperor = PlaceHelper.split(datum, '|');
                if (emperor.length == 6) {
                    List<String[]> emps = dyn2emp.get(dynasty);
                    emps.add(0, emperor);
                    emp2era.put(emperor, new ArrayList<>(10));
                }
            }
        }

        // Read and process the EMPEROR --> ERA/REIGN file
        String[] prevEra = null;
        List<String> empEraData = Files.readAllLines(Paths.get(JA_REIGN_FILE), StandardCharsets.UTF_8);
        for (String datum : empEraData) {
            String[] era = PlaceHelper.split(datum, '|');
            if (era.length > 3) {
                int year = Integer.parseInt(era[0]);
                String[] emp = findEmpForEra(year);
                if (emp == null) {
                    System.out.println("Era not matched: " + datum);
                } else {
                    if (prevEra != null  &&  prevEra.length > 2) {
                        prevEra[2] = era[0];
                    }
                    List<String[]> eras = emp2era.get(emp);
                    eras.add(era);
                }
            }
            prevEra = era;
        }

        // Dump stuff out just for fun ...
        for (String[] dyn : dynasties) {
            System.out.println();
            System.out.println(Arrays.toString(dyn));
            List<String[]> emps = dyn2emp.getOrDefault(dyn, Collections.emptyList());
            for (String[] emp : emps) {
                System.out.println("  " + Arrays.toString(emp));
                List<String[]> eras = emp2era.getOrDefault(emp, Collections.emptyList());
                for (String[] era : eras) {
                    System.out.println("    " + Arrays.toString(era));
                }
            }
        }

        // Create the final XML file
        createXml();

        System.exit(0);
    }

    static String[] findEmpForEra(int year) {
        for (String[] dyn : dynasties) {
            List<String[]> emps = dyn2emp.getOrDefault(dyn, Collections.emptyList());
            List<String[]> matches = new ArrayList<>();

            for (String[] emp : emps) {
                int startYear = Integer.parseInt(emp[4]);
                int endYear   = (emp[5].isEmpty()) ? startYear : Integer.parseInt(emp[5]);
                if (year >= startYear  &&  year <= endYear) {
                    matches.add(emp);
                }
            }

            if (matches.size() == 1) {
                return matches.get(0);
            } else if (matches.size() > 1) {
                return matches.get(matches.size()-1);
            }
        }

        return null;
    }

    static void createXml() {
        System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        System.out.println("<words>");
        System.out.println("  <!--");
        System.out.println("    https://en.wikipedia.org/wiki/List_of_Emperors_of_Japan");
        System.out.println("    https://www.jref.com/articles/list-of-japanese-emperors.91/");
        System.out.println("    https://en.wikipedia.org/wiki/List_of_Japanese_era_names");
        System.out.println("    https://wiki.samurai-archives.com/index.php?title=Emperors_of_Japan");
        System.out.println("  -->");
        System.out.println();

        createDynasty();
        for (String[] dynasty : dynasties) {
            createEmperor(dynasty);
        }
        System.out.println("</words>");
    }

    static void createDynasty() {
        System.out.println("  <word-group lang=\"ja\" type=\"dynasty\">");
        dynasties.forEach(JA_CreateDictionary::dynastyWord);
        System.out.println("  </word-group>");
    }

    static void createEmperor(String[] dynasty) {
        System.out.println();

        String dynastyId = dynastyId(dynasty);
        if (dynasty[2].isEmpty()) {
            dynastyId = "no-dynasty";
        }

        System.out.println(String.format(XXX_GROUP_FORMAT, "emperor", dynastyId));
        List<String[]> emps = dyn2emp.getOrDefault(dynasty, Collections.emptyList());
        for (String[] emp : emps) {
            String empId = PlaceHelper.normalize(emp[1]).toLowerCase() + "-" + counter++;
            empIdentifier.put(emp, empId);
            String meta = empId + "|" + emp[4] + "|" + emp[5];
            System.out.println(String.format(EMP_WORD_FORMAT, dynastyId, meta, emp[2]));
        }
        System.out.println("  </word-group>");

        int counter2 = 1;
        System.out.println(String.format(XXX_GROUP_FORMAT, "reign", dynastyId));
        for (String[] emp : emps) {
            String empId = empIdentifier.get(emp);
            List<String[]> reigns = emp2era.getOrDefault(emp, Collections.emptyList());
            for (String[] rgn : reigns) {
                String rgnId = dynastyId + "" + counter2++; 
                String meta = rgnId + "|" + rgn[0] + "|" + rgn[2];
                System.out.println(String.format(EMP_WORD_FORMAT, empId, meta, rgn[1]));
            }
        }
        System.out.println("  </word-group>");
    }

    static void dynastyWord(String[] dynasty) {
        String dynastyId = dynastyId(dynasty);
        if (! dynastyId.isEmpty()  &&  ! dynasty[2].isEmpty()) {
            String meta = dynastyId + "|" + dynasty[3] + "|" + dynasty[4];
            System.out.println(String.format(DYN_WORD_FORMAT, meta, dynasty[2], dynasty[1]));
        }
    }

    static String dynastyId(String[] dynasty) {
        String id = dynasty[1];

        int pos = id.indexOf(" ");
        if (pos > 0) {
            id = id.substring(0, pos);
        }

        pos = id.indexOf("-");
        if (pos > 0) {
            id = id.substring(0, pos);
        }

        return id.toLowerCase();
    }
}
