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
import java.util.List;
import java.util.Scanner;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * Take the raw "place-attr-hhs.txt" file and put it into Canonical format.  More or less.
 * 
 * @author wjohnson000
 *
 */
public class TimelineAttrRawToCanonicalPopOnly {

    static final String fileBase    = "C:/D-drive/homelands/place-data";
    static final String inFileName  = "place-attr-hhs-population.txt";
    static final String outFileName = "place-population.csv";

    public static void main(String...args) throws Exception {
        List<String> rows = new ArrayList<>(5_000);
        
        try(FileInputStream fis = new FileInputStream(new File(fileBase, inFileName));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            int count = 0;
            while (scan.hasNextLine()) {
                String row = scan.nextLine();
                String fmt = createCanonicalRow(row);
                if (! fmt.isEmpty()  &&  ++count%379 == 0) {
                    rows.add(fmt);
                }
            }
        }

        Files.write(Paths.get(fileBase, outFileName), rows, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.exit(0);
    }

    static String createCanonicalRow(String row) {
        String result = "";

        String[] cols = PlaceHelper.split(row, '|');
        if (cols.length > 8) {
            StringBuilder buff = new StringBuilder(256);

            buff.append("");                    // ID
            buff.append("|").append(cols[ 3]);  // External-ID
            buff.append("|").append(cols[ 0]);  // Rep-ID
            buff.append("|").append("global");  // Language
            buff.append("|").append("PERSON");  // Item Type
            buff.append("|").append("COUNTRY_POPULATION");  // Category
            buff.append("|").append("");        // Subcategory
            buff.append("|").append("population");          // Title
            buff.append("|").append(cols[ 9]);  // Body (value)
            buff.append("|").append(cols[ 6]);  // From year
            buff.append("|").append(cols[ 7]);  // To year
            buff.append("|").append("");        // Numeric value
            buff.append("|").append("");        // Source
            buff.append("|").append("");        // Source2
            buff.append("|").append("");       // Attribution
            buff.append("|").append("");        // Rights note
            buff.append("|").append("");        // Rights proved
            buff.append("|").append("");        // Image name
            buff.append("|").append("");        // Image URL
            buff.append("|").append("PUBLIC");  // Visibility
            buff.append("|").append("");        // Tags
            buff.append("|").append(cols[ 1]);  // Region
            buff.append("|").append("");        // Action
            buff.append("|").append("");        // Status
            buff.append("|").append("");        // Reason

            result = buff.toString();
        }

        return result;
    }
}
