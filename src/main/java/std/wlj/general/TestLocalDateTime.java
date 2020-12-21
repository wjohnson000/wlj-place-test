/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author wjohnson000
 *
 */
public class TestLocalDateTime {

    public static void main(String...args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        System.out.println("LDT: " + LocalDateTime.now().toString());
        System.out.println("LDT: " + LocalDateTime.now().format(formatter));
    }
}
