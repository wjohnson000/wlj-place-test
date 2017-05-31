package std.wlj.general;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ParseSplunkLog {

    static class SillyDate {
        int hour;
        int min;
        int sec;
        int micro;

        @Override
        public String toString() {
            return hour + ":" + min + ":" + sec + "," + micro;
        }
    }

    public static void main(String... args) throws IOException {
        int totCount = 0;
        int totMatch = 0;
        String filePath = "C:/temp/place-all-yikes.csv";
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);

        List<String> results = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"), 1024 * 64)) {
            while (reader.ready()) {
                totCount++;
                String line = reader.readLine();
                SillyDate sDate = getDate(line);
                if (sDate != null) {
                    String endpoint = getValueForKey(line, "endpoint");
                    if (endpoint != null) {
                        totMatch++;
                        String text     = getValueForKey(line, "text");
                        String time     = getValueForKey(line, "time");
                        String from     = getValueForKey(line, "from");
                        String status   = getValueForKey(line, "status");
                        if (from != null  &&  from.startsWith("DL-FH-Partner-")) {
                            from = "DL-FH-Partner";
                        }

                        results.add(sDate + "|" + endpoint + "|" + time + "|" + from + "|" + status + "|" + text);
                        if (time != null  &&  time.length() > 2) {
                            System.out.println(sDate + "|" + endpoint + "|" + time + "|" + from + "|" + status + "|" + text);
                        } else if (! endpoint.equals("places/request")) {
                            System.out.println(sDate + "|" + endpoint + "|" + time + "|" + from + "|" + status + "|" + text);
                        }
                    }
                }
            }
        }

        System.out.println("Count: " + totCount);
        System.out.println("Match: " + totMatch);
        Files.write(Paths.get("C:/temp/place-all-simple.txt"), results);

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

    static SillyDate getDate(String line) {
        int ndx01 = line.indexOf("2016-");
        int ndx02 = line.indexOf(' ', ndx01);
        if (ndx01 > 0  &&  ndx02 > ndx01) {
            int ndx03 = line.indexOf(' ', ndx02+1);
            try {
                String timeStr = line.substring(ndx02+1, ndx03);
                SillyDate sDate = new SillyDate();
                sDate.hour  = Integer.parseInt(timeStr.substring(0, 2));
                sDate.min   = Integer.parseInt(timeStr.substring(3, 5));
                sDate.sec   = Integer.parseInt(timeStr.substring(6, 8));
                sDate.micro = Integer.parseInt(timeStr.substring(9));
                return sDate;
            } catch(Exception ex) {
                // no worries ...
            }
        }
        return null;
    }
}
