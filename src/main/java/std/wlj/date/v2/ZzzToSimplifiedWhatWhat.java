/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import org.familysearch.standards.core.lang.util.ChineseVariants;
import org.familysearch.standards.core.lang.util.TraditionalToSimplifiedChineseMapper;

/**
 * @author wjohnson000
 *
 */
public class ZzzToSimplifiedWhatWhat {

    private static String[] ZH_CHARS = {
        "西元前",
        "公元前",
        "主前",
        "紀元前",
        "西元",
        "公元",
        "紀元",

        "西元後",
        "公元後",
        "紀元後"
    };


    public static void main(String... args) {
        TraditionalToSimplifiedChineseMapper mapper = new TraditionalToSimplifiedChineseMapper();
        for (String zhChar : ZH_CHARS) {
            String script  = String.valueOf(ChineseVariants.isTraditionalOrSimplified(zhChar));
            String nZhChar = mapper.mapTraditionalToSimplified(zhChar);
            String nScript  = String.valueOf(ChineseVariants.isTraditionalOrSimplified(nZhChar));
            
            System.out.println(zhChar + "|" + script + "|" + nZhChar + "|" + nScript);
        }
    }
}
