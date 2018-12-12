/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.familysearch.standards.place.util.PlaceHelper;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author wjohnson000
 *
 */
public class GetPatSchoneUnique {

    public static void main(String...args) throws Exception {
        int lineCnt = 0;
        Set<String> dates = new HashSet<>(1_000_000);
        try(FileInputStream fis = new FileInputStream(new File("C:/temp/date-samples/pat-schone-dates.txt"));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("DATE.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '\t');
                String cleanDate = cleanInput(chunks[0]);
                dates.add(cleanDate);
            }
        }

        System.out.println("\nLC: " + lineCnt);
        System.out.println("SZ: " + dates.size());

        Files.write(Paths.get("C:/temp/long-hand-dates-unique.txt"), dates, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static String cleanInput(String input) {
        return input.replace("\\n", " ").replace('\u2503', ' ');
    }
}
