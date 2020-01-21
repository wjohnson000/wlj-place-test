package std.wlj.general;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.familysearch.standards.place.util.PlaceHelper;

public class ParseSplunkLogForInterpData {

    public static void main(String... args) throws IOException {
        String filePath = "C:/temp/place-hint-usage.csv";
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);

        Set<String> results = new TreeSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"), 1024 * 64)) {
            while (reader.ready()) {
                String line = reader.readLine();
                String params = getValueForKey(line, "sParameters");
                if (params != null) {
                    int ndx01 = params.indexOf('?');
                    if (ndx01 >= 0) {
                        params = params.substring(ndx01+1);
                        String[] param = PlaceHelper.split(params, '&');
                        String name = Arrays.stream(param).filter(pp -> pp.startsWith("name")).findFirst().orElse(null);
                        String hint = Arrays.stream(param).filter(pp -> pp.startsWith("placeHint")).findFirst().orElse(null);
                        String date = Arrays.stream(param).filter(pp -> pp.startsWith("date")).findFirst().orElse(null);
                        results.add(name + "|" + hint + "|" + date);
                    }
                }
            }
        }

        results.forEach(System.out::println);
//        Files.write(Paths.get("C:/temp/place-all-simple.txt"), results);

        System.exit(0);
    }

    static String getValueForKey(String line, String key) {
        int ndx01 = line.indexOf(" " + key + "=");
        int ndx02 = line.indexOf(' ', ndx01+1);
        if (ndx01 > 0  &&  ndx02 > ndx01) {
            ndx01 = line.indexOf('=', ndx01+1);
            String value = line.substring(ndx01, ndx02);
            value = value.replace('"', ' ').trim();
            return value;
        }
        return null;
    }
}
