package std.wlj.general;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ParseSplunkForRedisLogging {

    static Map<String, Map<String, String>> logRequest = new HashMap<>();
    static Map<String, Map<String, String>> logRequestRedis = new HashMap<>();
    static Map<String, Map<String, String>> logInterp = new HashMap<>();
    static Map<String, Map<String, String>> logInterpRedis = new HashMap<>();

    public static void main(String...args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("C:/temp/blah.txt"), Charset.forName("UTF-8"));

        for (String line : lines) {
            if (! line.trim().isEmpty()) {
                int ndx = line.indexOf("module");
                if (ndx > 0) line = line.substring(ndx);
                Map<String, String> logParam = getKeyValue(line);

                String url = logParam.get("url");
                String isRedis = logParam.getOrDefault("RedisHit", "FALSE");
                String text = logParam.getOrDefault("name", logParam.get("text"));
                if (text != null  &&  url != null) {
                    if (url.contains("interp")) {
                        if ("TRUE".equalsIgnoreCase(isRedis)) {
                            logInterpRedis.put(text, logParam);
                        } else {
                            logInterp.put(text, logParam);
                        }
                    } else {
                        if ("TRUE".equalsIgnoreCase(isRedis)) {
                            logRequestRedis.put(text, logParam);
                        } else {
                            logRequest.put(text, logParam);
                        }
                    }
                }
            }
        }

        System.out.println("REQUEST comparison ...");
        compare(logRequest, logRequestRedis);

        System.out.println("\n\n\nINTERP comparison ...");
        compare(logInterp, logInterpRedis);
    }

    static Map<String,String> getKeyValue(String line) {
        Map<String, String> params = new TreeMap<>();

        String tLine = line;
        while (tLine != null) {
            int ndx0 = tLine.indexOf('=');
            int ndx1 = tLine.indexOf('"', ndx0+1);
            int ndx2 = tLine.indexOf('"', ndx1+1);

            if (ndx0 > 0  &&  ndx1 > ndx0  &&  ndx2 > ndx1) {
                String key = tLine.substring(0, ndx0).trim();
                String val = tLine.substring(ndx1+1, ndx2).trim();
                params.put(key, val);
                tLine = (ndx2 < tLine.length()-1) ? tLine.substring(ndx2+1) : null;
            } else {
                tLine = null;
            }
        }

        return params;
    }

    static void compare(Map<String, Map<String, String>> logNoRedis, Map<String, Map<String, String>> logRedis) {
        System.out.println();
        for (String text : logNoRedis.keySet()) {
            System.out.println();
            Map<String, String> noRedisParam = logNoRedis.get(text);
            Map<String, String> redisParam   = logRedis.get(text);
            if (redisParam != null) {
                Set<String> keys = new TreeSet<>(noRedisParam.keySet());
                keys.addAll(redisParam.keySet());
                for (String key : keys) {
                    if (key.contains("TIME")  ||  key.contains("COUNT")  ||  key.contains("SCORE")  ||  key.contains("TOKEN")  ||  key.contains("TYPE")) {
                        ; // Ignore for now ...
                    } else {
                        String val01 = noRedisParam.getOrDefault(key, "");
                        String val02 = redisParam.getOrDefault(key, "");
                        System.out.println(key + "|" + val01 + "|" + val02 + "|" + val01.equalsIgnoreCase(val02));
                    }
                }
            }
        }
    }
}
