package std.wlj.flatfile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AnalyzeStJudeLog {
    public static void main(String... args) throws IOException {
        Path path = Paths.get("C:/Users/wjohnson000/Downloads/st-jude.csv");
        List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));
        System.out.println("CNT: " + lines.size());

        for (String line : lines) {
            String txt = getValue(line, "text");
            String res = getValue(line, "placeResult");

            if (txt.length() > 0  &&  res.isEmpty()) {
                System.out.println("===========================================================================");
                System.out.println("URL: " + getValue(line, "url"));
                System.out.println("STT: " + getValue(line, "status"));
                System.out.println("TXT: " + txt);
                System.out.println("RES: " + res);
                System.out.println("DAT: " + getValue(line, "x-request-id"));
            }
        }

        System.exit(0);
    }

    static String getValue(String line, String key) {
        String value = "";

        int ndx0 = line.indexOf(key + "=");
        if (ndx0 >= 0) {
            int ndx1 = line.indexOf("\"\"", ndx0+key.length()+2);
            if (ndx1 >= 0) {
                value = line.substring(ndx0 + key.length() + 1, ndx1);
                value = value.replace('"', ' ').trim();
            }
        }

        return value;
    }
}
