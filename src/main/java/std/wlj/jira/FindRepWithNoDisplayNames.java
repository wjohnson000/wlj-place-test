package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.familysearch.standards.place.util.PlaceHelper;

public class FindRepWithNoDisplayNames {
    public static void main(String... args) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get("D:/important/display-name-all.txt"), Charset.forName("UTF-8"));
        System.out.println("Number of display-names: " + allLines.size());

        boolean prevIsOK  = true;
        String  prevRepId = "";

        for (String line : allLines) {
            String[] chunks = PlaceHelper.split(line, '|');
            if (chunks.length > 4) {
                String repId = chunks[0];
                String isDel = chunks[4];
                if (repId.equals(prevRepId)) {
                    if ("false".equals(isDel)) {
                        prevIsOK = true;
                    }
                } else {
                    if (! prevIsOK) {
                        System.out.println("Bad rep? " + prevRepId);
                    }
                    prevRepId = repId;
                    prevIsOK  = "false".equals(isDel);
                }
            }
        }

        if (! prevIsOK) {
            System.out.println("Bad rep? " + prevRepId);
        }
    }
}
