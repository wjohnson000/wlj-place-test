/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wjohnson000
 *
 */
public class CompareLogOptions {

    public static void main(String... args) {
        Map<String, List<String>> headers = new TreeMap<>();
        headers.put("one", Arrays.asList("ABC"));
        headers.put("two", Arrays.asList("ABC"));
        headers.put("tre", Arrays.asList("ABC"));
        headers.put("for", Arrays.asList("abc-def-ghi"));

        long timeO = 0L;
        long timeX = 0L;
        long timeN = 0L;

        for (int i=0;  i<1_000_000;  i++) {
            long time0 = System.nanoTime();
            methodOld(headers);
            long time1 = System.nanoTime();
            methodNew(headers);
            long time2 = System.nanoTime();
            methodOld2(headers);
            long time3 = System.nanoTime();

            timeO += (time1 - time0);
            timeN += (time2 - time1);
            timeX += (time3 - time2);
        }

        System.out.println("OLD: " + timeO / 1_000_000.0);
        System.out.println("NEW: " + timeN / 1_000_000.0);
        System.out.println("O22: " + timeX / 1_000_000.0);
    }

    static void methodOld(Map<String, List<String>> headers) {
        List<String> values = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            StringBuilder buff = new StringBuilder();
            values.add(entry.getKey());
            for (String value : entry.getValue()) {
                buff.append(value);
                buff.append(",");
            }
            if (buff.charAt(buff.length()-1) == ',') {
                buff.setLength(buff.length()-1);
            }
            values.add(buff.toString());
        }
    }

    static void methodOld2(Map<String, List<String>> headers) {
        List<String> values = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            StringBuilder buff = new StringBuilder();
            values.add(entry.getKey());
            for (String value : entry.getValue()) {
                if (buff.length() > 0) {
                    buff.append(",");
                }
                buff.append(value);
            }
            values.add(buff.toString());
        }
    }

    static void methodNew(Map<String, List<String>> headers) {
        List<String> values = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            values.add(entry.getKey());
            if (entry.getValue().isEmpty()) {
                values.add("unknown");
            } else if (entry.getValue().size() == 1) {
                values.add(entry.getValue().get(0));
            } else {
                values.add(entry.getValue().stream().collect(Collectors.joining(",")));
            }
        }
    }
}
