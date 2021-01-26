/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * @author wjohnson000
 *
 */
public class TestIsMissingText {

    public static void main(String...args) {
        isMissing(null);
        isMissing(Arrays.asList("", ""));
        isMissing(Arrays.asList("AA", "BB", "CC"));
        isMissing(Arrays.asList("AA", "BB", "", ""));
        isMissing(Arrays.asList("AA", "", "BB"));
        isMissing(Arrays.asList("", "AA", ""));
    }

    static void isMissing(List<String> contentList) {
        System.out.println("\n\n==============================================================================================");
        System.out.println(contentList);
        if (contentList == null || contentList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()).isEmpty()) {
            System.out.println("Error: discovery event missing content for description.");
            return;
        }

        //log if content has empty values between set ones
        // e.g. [ setValue1, "", setValue2, setValue3 ] would log where as [setValue1, "", "", "" ] would not
        boolean emptyFound = false;
        for (String content : contentList) {
            if (StringUtils.isBlank(content)) {
                emptyFound = true;
            } else if (emptyFound) {
                System.out.println("There is content in the body being set with empty values between. " +  contentList);
                break;
            }
        }
    }
}
