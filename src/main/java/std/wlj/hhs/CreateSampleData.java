/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import std.wlj.json.JsonUtility;

/**
 * @author wjohnson000
 *
 */
public class CreateSampleData {

    private static final String BASE_DIR  = "C:/D-drive/homelands/WICF/csv";
    private static final List<String> results = new ArrayList<>(1000);
    private static final List<String> keyOrder = Arrays.asList("type", "countryId", "countryCode", "repid");

    public static void main(String...args) throws Exception {
        String[] filenames = (new File(BASE_DIR)).list((dir, name) -> name.endsWith(".txt"));
        for (String filename : filenames) {
            process(filename);
        }

        results.forEach(System.out::println);
    }

    static void process(String filename) throws Exception {
        List<String> rows = Files.readAllLines(Paths.get(BASE_DIR, filename), StandardCharsets.UTF_8);

        if (! rows.isEmpty()) {
            Map<String, String> kvData = getKV(rows.get(0));
            List<String> keys = new ArrayList<>(kvData.keySet());
            printHead(keys);
            rows.stream().limit(17).forEach(row -> printData(keys, row));
        }
    }

    static void printHead(List<String> keys) {
        results.add("");
        results.add("");
        StringBuilder buff = new StringBuilder();

        buff.append(keyOrder.stream().collect(Collectors.joining("^")));
        keys.stream()
            .filter(kk -> ! keyOrder.contains(kk))
            .forEach(kk -> buff.append("^").append(kk));
        results.add(buff.toString());
    }

    static void printData(List<String> keys, String row) {
        StringBuilder buff = new StringBuilder();

        Map<String, String> kvData = getKV(row);
        buff.append(keyOrder.stream()
            .map(kk -> kvData.getOrDefault(kk, ""))
            .collect(Collectors.joining("^")));
        keys.stream()
            .filter(kk -> ! keyOrder.contains(kk))
            .map(kk -> kvData.getOrDefault(kk, ""))
            .forEach(vv -> buff.append("^").append(vv));
        results.add(buff.toString());
    }

    static Map<String, String> getKV(String json) {
        try {
            return JsonUtility.getAllFields(json);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
