package std.wlj.general;

import java.io.*;

/**
 * Splunk log file contains entries for interpretations that took longer than 200 ms to complete.
 * 
 * @author wjohnson000
 *
 */
public class ParseSplunkLogTimeGT200ms {

    public static void main(String... args) throws IOException {
        int totCount = 0;
        String filePath = "C:/temp/request-gt-200ms.csv";
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"), 1024 * 64)) {
            while (reader.ready()) {
                totCount++;
                String line = reader.readLine();
                String text     = getValueForKey(line, "text");
                if (null != text) {
                    String time     = getValueForKey(line, "time");
                    String status   = getValueForKey(line, "status");
                    String fullTime = getValueForKey(line, "FULL_PARSE_PATH_TIME");
                    String fullFCnt = getValueForKey(line, "FULL_PARSE_PATH_FOUND_COUNT");
                    
                    System.out.println(text + "|" + time + "|" + status + "|" + fullTime + "|" + fullFCnt);
                }
            }
        }

        System.out.println("Count: " + totCount);
        System.exit(0);
    }

    static String getValueForKey(String line, String key) {
        int ndx01 = line.indexOf(" " + key + "=");
        int ndx02 = line.indexOf(' ', ndx01+1);
        if (ndx01 > 0  &&  ndx02 > ndx01) {
            ndx01 = line.indexOf('=', ndx01+1);
            String value = line.substring(ndx01, ndx02);
            value = value.replace('=', ' ').replace('"', ' ').trim();
            return value;
        }
        return null;
    }
}
