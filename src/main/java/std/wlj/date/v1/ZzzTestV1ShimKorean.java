/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v1;

import java.util.List;

import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.GenDateParsingException;
import org.familysearch.standards.date.v1.DateV1Shim;

/**
 * @author wjohnson000
 *
 */
public class ZzzTestV1ShimKorean {

    static String[] textes = {
        "朝鮮太祖洪武元年七月十七日",
        "朝鮮太祖洪武壬申年",
        "朝鮮太祖洪武元年七月二十八日",
        "朝鮮太祖洪武七年九月五日",
        "朝鮮太祖洪武戊寅年",
        "朝鮮定宗建文元年九月五日",
        "朝鮮定宗建文二年十一月十三日",
        "朝鮮定宗建文庚辰年",
        "朝鮮太宗永樂十八年",
        "朝鮮太宗永樂戊戌年",
        "朝鮮世宗永樂卽位年",
        "朝鮮世宗永樂元年",
        "朝鮮世宗永樂一年",
        "朝鮮世宗永樂二年",
        "朝鮮世宗永樂三年",
        "朝鮮世宗永樂四年",
        "朝鮮世宗永樂五年",
        "朝鮮世宗永樂癸卯年",
        "朝鮮世宗景泰三十二年二月十七日",
        "朝鮮世宗景泰庚午年",
        "朝鮮文宗景泰元年二月十七日",
        "朝鮮文宗景泰二年五月十四日",
        "朝鮮文宗景泰壬申年",
        "朝鮮端宗景泰元年五月十四日",
    };

    public static void main(String... args) {
        for (String text : textes) {
            try {
                List<GenDateInterpResult> dates = DateV1Shim.interpDate(text);
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                System.out.println(text);
                for (GenDateInterpResult date : dates) {
                    System.out.println(" date: " + date.getDate().toGEDCOMX());
                }
                System.out.println();
            } catch (GenDateParsingException e) {
                System.out.println("Oops!! " + e.getMessage());
            }
        }
    }
}
