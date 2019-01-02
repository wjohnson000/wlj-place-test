package std.wlj.solr;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ParseTomcatLogLogLog {
    public static void main(String... args) throws Exception {
        FileSystem currFS = FileSystems.getDefault();
        Path path = currFS.getPath("C:", "temp", "tomcat-new.log");

        Set<String> badOnes = new HashSet<>();
        Set<String> badTwos = new HashSet<>();
        List<String> data = Files.readAllLines(path, StandardCharsets.UTF_8);
        System.out.println("Rows01: " + data.size());

        double procTotal = 0;
        double mapTotal  = 0;
        for (String datum : data) {
            String text = getString(datum, "text=");
            double procTime = getTime(datum, "process=");
            double mapTime  = getTime(datum, "mapper=");
            if (text != null  &&  mapTime > procTime*5) {
                if (badTwos.contains(text)) {
                    procTotal += procTime;
                    mapTotal += mapTime;
                    System.out.println(datum);
                } else if (badOnes.contains(text)) {
                    badTwos.add(text);
                } else {
                    badOnes.add(text);
                }
            }
        }
 
        System.out.println("Proc Total: " + procTotal);
        System.out.println(" Map Total: " + mapTotal);
        System.exit(0);
    }

    private static String getString(String aLine, String key) {
        int ndx00 = aLine.indexOf(key);
        if (ndx00 <= 0) {
            return null;
        } else {
            String temp = aLine.substring(ndx00 + key.length() + 1);
            ndx00 = temp.indexOf('"');
            temp = temp.substring(0, ndx00);
            return temp;
        }
    }

    private static double getTime(String aLine, String key) {
        String dblValue = getString(aLine, key);
        return (dblValue == null) ? 0.0 : Double.parseDouble(dblValue);
    }

}