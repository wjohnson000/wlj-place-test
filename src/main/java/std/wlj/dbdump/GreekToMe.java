package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * Find all occurrences of locales that begin with "grk" --> these are to become "el".
 * 
 * @author wjohnson000
 */
public class GreekToMe {

    static final String baseDir  = "C:/temp";
    static final String vNamFile = "db-variant-name-all.txt";
    static final String repFile  = "db-place-rep-all.txt";
    static final String dNamFile = "db-display-name-all.txt";
    static final String attrFile = "db-attribute-all.txt";

    public static void main(String...args) throws IOException {
        doPlaceName();
        doPlaceRep();
        doRepDisplayName();
    }

    static void doPlaceName() throws IOException {
        Map<String, Integer> grkCount = new TreeMap<>();

        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, vNamFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("PLC.NAME.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 5) {
                    String locale = chunks[2];
                    String delete = chunks[5];
                    if (locale.toLowerCase().contains("grk")  &&  ! delete.toLowerCase().startsWith("t")) {
                        Integer count = grkCount.getOrDefault(locale, Integer.valueOf(0));
                        grkCount.put(locale, count+1);
                    }
                }
            }
        }

        System.out.println("\nPlaceNames");
        grkCount.entrySet().forEach(System.out::println);
        System.out.println("\n\n\n");
    }

    static void doPlaceRep() throws IOException {
        Map<String, Integer> grkCount = new TreeMap<>();

        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("REP.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 11) {
                    String deleteId = chunks[9];
                    String locale   = chunks[10];
                    if (deleteId != null  &&  (deleteId.trim().isEmpty()  ||  "null".equals(deleteId))  &&  locale.toLowerCase().contains("grk")) {
                        Integer count = grkCount.getOrDefault(locale, Integer.valueOf(0));
                        grkCount.put(locale, count+1);
                    }
                }
            }
        }

        System.out.println("\nPlaceReps");
        grkCount.entrySet().forEach(System.out::println);
        System.out.println("\n\n\n");
    }

    static void doRepDisplayName() throws IOException {
        Map<String, Integer> grkCount = new TreeMap<>();

        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, dNamFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("REP.DISPNAME.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 4) {
                    String locale = chunks[2];
                    String delete = chunks[5];
                    if (locale.toLowerCase().contains("grk")  &&  ! delete.toLowerCase().startsWith("t")) {
                        Integer count = grkCount.getOrDefault(locale, Integer.valueOf(0));
                        grkCount.put(locale, count+1);
                    }
                }
            }
        }

        System.out.println("\nRepDisplayNames");
        grkCount.entrySet().forEach(System.out::println);
        System.out.println("\n\n\n");
    }
}
