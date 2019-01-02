package std.wlj.general;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Analyze "Splunk" data base on the following query over 24 hours:
 *    index=production "GC Thread Dump" host="std-ws-place-prod-ws-*"
 * 
 * @author wjohnson000
 */
public class AnalyzeSplunkGC {

    static final String SERVER_KEY = "server";
    static final String MSG_KEY    = "msg";
    static final String SERVER_PREFIX = "std-ws-place-prod";

    static final String[] KEYS = { SERVER_KEY, MSG_KEY, "Name", "GC count", "GC time" };


    public static void main(String...args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("C:/temp/splunk-gc-results.txt"), StandardCharsets.UTF_8);

//        Map<String,List<Map<String,String>>> allStats = new HashMap<>();
//        for (String line : lines) {
//            Map<String,String> keyValue = Arrays.stream(KEYS).collect(Collectors.toMap(k -> k, k -> getValue(line, k)));
//            if (keyValue.getOrDefault("server", "").length() > 0  &&  keyValue.getOrDefault("msg", "").length() > 0) {
//                String server = keyValue.get("server");
//                List<Map<String,String>> kvList = allStats.get(server);
//                if (kvList == null) {
//                    kvList = new ArrayList<>();
//                    allStats.put(server, kvList);
//                }
//                kvList.add(keyValue);
//            }
//        }

        Map<String,List<Map<String,String>>> allStats = lines.stream()
                .filter(line -> getServer(line).length() > 0)
                .map(line -> Arrays.stream(KEYS).collect(Collectors.toMap(k -> k, k -> getValue(line, k))))
                .filter(kv -> kv.getOrDefault(MSG_KEY, "").length() > 0)
                .collect(Collectors.groupingBy(
                    kv -> kv.get(SERVER_KEY),
                    HashMap::new,
                    Collectors.mapping(kv -> { kv.remove(SERVER_KEY); return kv; }, Collectors.toList())));

        for (Map.Entry<String,List<Map<String,String>>> entry : allStats.entrySet()) {
            System.out.println("\n\n");
            System.out.println("=============================================================================");
            System.out.println(entry.getKey());
            System.out.println("=============================================================================");
            entry.getValue().forEach(System.out::println);
        }
    }

    protected static String getValue(String line, String key) {
        if (key.equals(SERVER_KEY)) {
            return getServer(line);
        }

        int ndx0 = line.indexOf(key + "=\"\"");
        if (ndx0 > 0) {
            int ndx1 = line.indexOf("\"\"", ndx0+key.length()+2);
            return line.substring(ndx0+key.length()+2, ndx1).replace('"', ' ').trim();
        }
        return "";
    }

    protected static String getServer(String line) {
        int ndx0 = line.indexOf(SERVER_PREFIX);
        if (ndx0 > 0) {
            int ndx1 = line.indexOf('"', ndx0);
            return line.substring(ndx0, ndx1).trim();
        }
        return "";
    }
}
