/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.familysearch.standards.loader.sql.FileResultSet;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * Take the raw "place-attr-hhs.txt" file and put it into Canonical format.  More or less.
 * 
 * @author wjohnson000
 *
 */
public class TimelineAttrRawToCanonical {

    static final String DELIMITER     = "\\|";

    static final String fileBase      = "C:/D-drive/homelands/place-data";
    static final String repFileName   = "place-rep-hhs.txt";   // raw PlaceRep details (full name, jurisdiction chain, lat/long)
    static final String attrFileName  = "place-attr-hhs.txt";  // raw Attribute data
    static final String outFile01Name = "place-canonical.csv";
    static final String outFile02Name = "place-canonical-CR.csv";
    static final String outFile03Name = "place-canonical-note.csv";
    static final String outFile04Name = "place-canonical-denom.csv";

    static final Map<Integer, String> repDisplayName = new TreeMap<>();
    static final Map<Integer, String> repJurisChain  = new HashMap<>();
    static final Map<Integer, String> repLatLong     = new HashMap<>();

    static final Map<String, Integer> regularCount = new TreeMap<>();
    static final Map<String, Integer> countryCount = new TreeMap<>();

    static final int[] mainUrlCount    = { 0, 0 };
    static final int[] countryUrlCount = { 0, 0 };
    static final int[] noteUrlCount    = { 0, 0 };
    static final int[] denomUrlCount   = { 0, 0 };

    public static void main(String...args) throws Exception {
        List<String> rows01 = new ArrayList<>(75_000);
        List<String> rows02 = new ArrayList<>(75_000);
        List<String> rows03 = new ArrayList<>(75_000);
        List<String> rows04 = new ArrayList<>(75_000);

        loadExistingRepData();

        try(FileInputStream fis = new FileInputStream(new File(fileBase, attrFileName));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            // Skip the header line
            if (scan.hasNextLine()) {
                scan.nextLine();
            }

            while (scan.hasNextLine()) {
                String row = scan.nextLine();
                String fmt = createCanonicalRow(row);
                if (fmt.contains("CountryReports")) {
                    rows02.add(fmt);
                } else if (fmt.contains("|NOTE|")) {
                    rows03.add(fmt);
                } else if (fmt.contains("|DENOM|")) {
                    rows04.add(fmt);
                } else if (fmt.contains("|PARISH_REGISTER|")) {
                    rows04.add(fmt);
                } else {
                    rows01.add(fmt);
                }
            }
        }

        Files.write(Paths.get(fileBase, outFile01Name), rows01, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(Paths.get(fileBase, outFile02Name), rows02, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(Paths.get(fileBase, outFile03Name), rows03, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(Paths.get(fileBase, outFile04Name), rows04, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println();
        Set<String> types = new TreeSet<>();
        types.addAll(regularCount.keySet());
        types.addAll(countryCount.keySet());
        for (String type : types) {
            int countR = regularCount.getOrDefault(type, 0);
            int countC = countryCount.getOrDefault(type, 0);
            System.out.println(type + "\t" + countR + "\t" + countC);
        }

        System.out.println();
        System.out.println("Main counts: " + Arrays.toString(mainUrlCount));
        System.out.println("Ctry counts: " + Arrays.toString(countryUrlCount));
        System.out.println("Note counts: " + Arrays.toString(noteUrlCount));
        System.out.println("Denm counts: " + Arrays.toString(denomUrlCount));

        System.exit(0);
    }

    static void loadExistingRepData() throws Exception {
        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(fileBase, repFileName));

            while (rset.next()) {
                int repId       = rset.getInt("rep_id");
                String fullName = rset.getString("full_name");
                String jChain   = rset.getString("juris_chain");
                String latLong  = rset.getString("lat_long");

                repDisplayName.put(repId, fullName);
                repJurisChain.put(repId, jChain);
                repLatLong.put(repId, latLong);
            }
        } catch(Exception ex) {
            System.out.println("Unable to load existing data: " + ex.getMessage());
        }

        System.out.println("Existing data size=" + repDisplayName.size());
    }

    static String createCanonicalRow(String row) {
        String result = "";

        String[] cols = PlaceHelper.split(row, '|');
        if (cols.length > 13) {
            StringBuilder buff = new StringBuilder(256);
            int repId = Integer.parseInt(cols[0]);

            buff.append("");                    // ID
            buff.append("|").append(cols[ 3]);  // External-ID
            buff.append("|").append(cols[ 0]);  // Rep-ID
            buff.append("|").append(cols[10]);  // Language
            buff.append("|").append("FACT");    // Item Type
            buff.append("|").append(cols[ 5]);  // Category (the type "CODE" for now)
            buff.append("|").append("");        // Subcategory -- TBD
            buff.append("|").append(cols[ 8]);  // Title
            buff.append("|").append(cols[ 9]);  // Body (value)
            buff.append("|").append(cols[ 6]);  // From year
            buff.append("|").append(cols[ 7]);  // To year
            buff.append("|").append("");        // Numeric value
            buff.append("|").append(cols[11]);  // Source
            buff.append("|").append(cols[12]);  // Source2
            buff.append("|").append(cols[13]);  // Attribution
            buff.append("|").append("");        // Rights note
            buff.append("|").append("");        // Rights proved
            buff.append("|").append("");        // Image name
            buff.append("|").append("");        // Image URL
            buff.append("|").append("PUBLIC");  // Visibility
            buff.append("|").append("");        // Tags
//            buff.append("|").append("");        // Region
//            buff.append("|").append("");        // Action
//            buff.append("|").append("");        // Status
//            buff.append("|").append("");        // Reason
            buff.append("|").append(repLatLong.getOrDefault(repId, ""));
            buff.append("|").append(repJurisChain.getOrDefault(repId, ""));
            buff.append("|").append(repDisplayName.getOrDefault(repId, ""));

            result = buff.toString();

            if (result.contains("CountryReports")) {
                int cnt = countryCount.getOrDefault(cols[5], Integer.valueOf(0));
                countryCount.put(cols[5], cnt+1);
            } else {
                int cnt = regularCount.getOrDefault(cols[5], Integer.valueOf(0));
                regularCount.put(cols[5], cnt+1);
            }

            if (result.contains("CountryReports")) {
                if (! cols[11].trim().isEmpty()) countryUrlCount[0]++;
                if (! cols[12].trim().isEmpty()) countryUrlCount[1]++;
            } else if (result.contains("|NOTE|")) {
                if (! cols[11].trim().isEmpty()) noteUrlCount[0]++;
                if (! cols[12].trim().isEmpty()) noteUrlCount[1]++;
            } else if (result.contains("|DENOM|")) {
                if (! cols[11].trim().isEmpty()) denomUrlCount[0]++;
                if (! cols[12].trim().isEmpty()) denomUrlCount[1]++;
            } else if (result.contains("|PARISH_REGISTER|")) {
                if (! cols[11].trim().isEmpty()) denomUrlCount[0]++;
                if (! cols[12].trim().isEmpty()) denomUrlCount[1]++;
            } else {
                if (! cols[11].trim().isEmpty()) mainUrlCount[0]++;
                if (! cols[12].trim().isEmpty()) mainUrlCount[1]++;
            }

        }

        return result;
    }
}
