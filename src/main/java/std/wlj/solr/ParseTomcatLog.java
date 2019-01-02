package std.wlj.solr;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class ParseTomcatLog {
    public static void main(String... args) throws Exception {
        FileSystem currFS = FileSystems.getDefault();
        Path path = currFS.getPath("C:", "temp", "tomcat-log4j2.log");

        List<String> data = Files.readAllLines(path, StandardCharsets.UTF_8);
        System.out.println("Rows01: " + data.size());

        double procTotal = 0;
        double mapTotal  = 0;
        for (String datum : data) {
            double procTime = getTime(datum, "process=");
            double mapTime  = getTime(datum, "mapper=");
            procTotal += procTime;
            mapTotal += mapTime;
            if (mapTime > procTime*5) {
                System.out.println(datum);
            }
        }
 
        System.out.println("Proc Total: " + procTotal);
        System.out.println(" Map Total: " + mapTotal);
        System.exit(0);
    }

    private static double getTime(String aLine, String key) {
        int ndx00 = aLine.indexOf(key);
        if (ndx00 <= 0) {
            return 0.0;
        } else {
            String temp = aLine.substring(ndx00 + key.length() + 1);
            ndx00 = temp.indexOf('"');
            temp = temp.substring(0, ndx00);
            return Double.parseDouble(temp);
        }
    }
}
