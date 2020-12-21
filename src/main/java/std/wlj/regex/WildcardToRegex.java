/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.regex;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author wjohnson000
 *
 */
public class WildcardToRegex {

    private static Set<String> values = new HashSet<>(1000);

    public static void main(String... args) {
        addStuff();

        match("Wayne");
        match("FOUR");
        match("t*");
        match("t*e");
        match("T*");
        match("T*e");
    }

    static void match(String key) {
        System.out.println("\n==================================================================");
        System.out.println("Match: >>" + key + "<<");
        if (key.contains("*")) {
            matchWildcard(key);
        } else {
            matchFull(key);
        }
    }

    static void matchFull(String key) {
        values.stream()
            .filter(vv -> vv.equalsIgnoreCase(key))
            .forEach(System.out::println);
    }

    static void matchWildcard(String key) {
        System.out.println("  Key: " + key);
        String regex = "(?i:" + key.replaceAll("\\*", "(.*)") + ")";
        Pattern pattern = Pattern.compile(regex);

        values.stream()
            .filter(vv -> pattern.matcher(vv).matches())
            .forEach(System.out::println);
    }

    static void addStuff() {
        values.add("One");
        values.add("Two");
        values.add("Three");
        values.add("Four");
        values.add("Five");
        values.add("Six");
        values.add("Seven");
        values.add("Eight");
        values.add("Nine");
        values.add("Ten");
        values.add("Eleven");
        values.add("Twelve");
        values.add("Peter");
        values.add("James");
        values.add("John");
        values.add("Throckmorton");
    }
}
