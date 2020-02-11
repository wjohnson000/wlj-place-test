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
public class FindDuplicates {

    public static void main(String...args) {
        Collection<Integer> values = new ArrayList<>();
        values.add(1);
        values.add(2);
        values.add(3);
        values.add(2);
        values.add(5);
        values.add(6);
        values.add(5);
        values.add(5);
        values.add(7);
        values.add(7);
        values.add(7);
        values.add(2);

        System.out.println("DUPS: " + findDuplicatesI(values));
        System.out.println("DUPS: " + findDuplicatesII(values));

        long time1 = 0L;
        long time2 = 0L;
        for (int i=0;  i<4_000_000;  i++) {
            long timeA, timeB;

            timeA = System.nanoTime();
            findDuplicatesI(values);
            timeB = System.nanoTime();
            time1 += timeB - timeA;

            timeA = System.nanoTime();
            findDuplicatesII(values);
            timeB = System.nanoTime();
            time2 += timeB - timeA;
        }

        System.out.println("TIME-1: " + time1 / 1_000_000.0);
        System.out.println("TIME-2: " + time2 / 1_000_000.0);
    }

    static Set<Integer> findDuplicatesI(Collection<Integer> values) {
        Set<Integer> results = new HashSet<>();
        Set<Integer> found   = new HashSet<>();

        for (Integer value : values) {
            if (found.contains(value)) {
                results.add(value);
            }
            found.add(value);
        }
        
        return results;
    }

    static Set<Integer> findDuplicatesII(Collection<Integer> values) {
        Map<Integer, Long> valuesByCount = values.stream()
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()));

        return valuesByCount.entrySet().stream()
            .filter(entry -> entry.getValue() > 1L)
            .map(entry -> entry.getKey())
            .collect(Collectors.toSet());
    }
}
