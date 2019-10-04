package std.wlj.jira;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.dbdump.DumpTypes;

public class FindRepWithNoDisplayNamesOfPrefLocale {

    static final String inputBase   = "C:/temp/db-dump";
    static final String repFile     = "place-rep-all.txt";
    static final String nameFile    = "display-name-all.txt";

    private static final Map<String, String> repToLocale = new TreeMap<>();
    private static final Map<String, String> repToType   = new HashMap<>();
    private static final Map<String, String> repToXxxx   = new HashMap<>();
    private static final Map<String, String> typeToDescr = new HashMap<>();

    private static final List<String>  dataPPL = new ArrayList<>();
    private static final List<String>  dataNotPPL = new ArrayList<>();

    static int total = 0;
    static int notPP = 0;

    public static void main(String... args) throws Exception {
        mapRepToData();
        typeToDescr.putAll(DumpTypes.loadPlaceTypes());
        processRepNames();

        Files.write(Paths.get(inputBase, "mismatch-locale.txt"), dataNotPPL, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(Paths.get(inputBase, "mismatch-locale-ppl.txt"), dataPPL, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("TOTAL: " + total);
        System.out.println("NotPP: " + notPP);
    }

    static void mapRepToData() throws Exception {
        int lineCount = 0;
        System.out.println();

        try(FileInputStream fis = new FileInputStream(new File(inputBase, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {
            scan.nextLine();  // Skip the header line

            while (scan.hasNextLine()) {
                if (++lineCount % 500_000 == 0) System.out.println("REP.read: " + lineCount);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 11) {
                    String repId  = chunks[0];
                    String typeId = chunks[6];
                    String delId  = chunks[9];
                    String locale = chunks[10];
                    String isPub  = chunks[11];
                    String isVal  = chunks[12];

                    if (delId == null  ||  delId.trim().isEmpty()) {
                        repToLocale.put(repId, simplifyLocale(locale));
                        repToType.put(repId, typeId);
                        repToXxxx.put(repId, isPub+"|"+isVal);
                    }
                }
            }
        }
    }

    static void processRepNames() throws Exception {
        int lineCount = 0;
        System.out.println();

        String prevRepId = "";
        Map<String, String> repNames = new HashMap<>();
        Map<String, String> repNamesDel = new HashMap<>();

        try(FileInputStream fis = new FileInputStream(new File(inputBase, nameFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {
            scan.nextLine();  // Skip the header line

            while (scan.hasNextLine()) {
                if (++lineCount % 500_000 == 0) System.out.println("NAME.read: " + lineCount);

                String nameData = scan.nextLine();
                String[] chunks = PlaceHelper.split(nameData, '|');
                if (chunks.length > 5) {
                    String repId  = chunks[0];
                    String locale = chunks[2];
                    String text   = chunks[3];
                    String delFlg = chunks[5];

                    if (! repId.equals(prevRepId)) {
                        checkRep(prevRepId, repNames, repNamesDel);
                        repNames.clear();
                        repNamesDel.clear();
                    }

                    prevRepId = repId;
                    if ("t".equals(delFlg)) {
                        repNamesDel.put(simplifyLocale(locale), text);
                    } else {
                        repNames.put(simplifyLocale(locale), text);
                    }
                }
            }
        }

        checkRep(prevRepId, repNames, repNamesDel);
    }

    static void checkRep(String repId, Map<String, String> repNames, Map<String, String> repNamesDel) {
        String prefLocale = repToLocale.get(repId);
        String type       = repToType.get(repId);
        String xxxx       = repToXxxx.get(repId);

        if (prefLocale != null  &&  type != null  &&  xxxx != null  &&  ! repNames.containsKey(prefLocale)) {
            total++;
            String name = repNames.get("en");
            if (name == null) {
                name = repNames.entrySet().stream().findFirst().map(ee -> ee.getKey() + "|" + ee.getValue()).orElse("Unknown");
            } else {
                name = "en|" + name;
            }

            String delName = repNamesDel.getOrDefault(prefLocale, "");
            String repDetail = repId + "|" + prefLocale + "|" + delName + "|" + name + "|" + type + "|" + typeToDescr.get(type) + "|" + xxxx;

            if ("140".equals(type)) {
                notPP++;
                dataPPL.add(repDetail);
            } else {
                dataNotPPL.add(repDetail);
            }
        }
    }

    static String simplifyLocale(String locale) {
        int ndx = locale.indexOf('-');
        return (ndx == -1) ? locale : locale.substring(0, ndx);
    }
}
