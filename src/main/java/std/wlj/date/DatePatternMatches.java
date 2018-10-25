/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.model.GenDateInterpResult;
import org.familysearch.standards.date.parser.handler.FrenchRepublicanHandler;
import org.familysearch.standards.date.shared.ThreadLocalExperiment;

/**
 * @author wjohnson000
 *
 */
public class DatePatternMatches {

    static String[] textes = {
//        "5 May 1902",
//        "05-May-1902",
//        "5 May abt 1902",
//        "5/5/1902",
//        "5 5 1902",
//        "19020505",
//        "1902/05/05",
//        "1902 May 05",
//        "1902 05 05",
//        "05/05/1902",
//        "05.05.1902",
//        "May 5, 1902",
//        "May June 1902",
//        "May/June 1902",
//        "1902",
//        "1900  1910  1920",
//        "1900/1920/1920",
//        "1910.1920.1930",
//        "May 5/16 1752",
//        "5/16 May 1759/60",
//        "22 Feb/5 Mar 1752/3",
//        "1352年2月6日",
//        "1352年2月",
//        "1352年",
//        "千三百五十二年二月六日",
//        "千三百五十二年二月",
//        "千三百五十二年",
//        "一九五一年元月",
//        "乾隆丙辰年八月初三日",
//        "1800-1894",
//        "Between 1845 1850",
//        "After 1900",
//        "5 May 1902 to 12 May 1904",
//        "7/23/2001 to 8/14/2001",
//        "2001/7/23 to 2001/8/14",
//        "5 May 2000 to 7 May 2000",
//        "1957 1 23 to 1975 11 6",
//        "光绪二十年辛丑五月二十三",
//        "光绪二十年辛丑五月二十三已时",
//        "漢朝文帝後元二年",      // Dynasty, Emperor, Reign
//        "漢朝文帝二年",          // Dynasty, Emperor
//        "漢朝二年",              // Dynasty
//

        "1910 1920",
        "1910/1920",
        "1910 and 1920",
        "1910 or 1920",
        "1910 & 1920",
        "1910 to 1920",

        "1910 1911 1912",
        "1910/1911/1912",
        "1910 and 1911 and 1912",
        "1910 or 1911 or 1912",
        "1910 & 1911 & 1912",

        "1910 1911 1912 1913",
        "1910/1911/1912/1913",
        "1910 and 1911 and 1912 and 1913",
        "1910 or 1911 or 1912 or 1913",
        "1910 & 1911 & 1912 & 1913",

        "1910 1911 1912 1913 1914",
        "1910/1911/1912/1913/1914",
        "1910 and 1911 and 1912 and 1913 and 1914",
        "1910 or 1911 or 1912 or 1913 or 1914",
        "1910 & 1911 & 1912 & 1913 & 1914",

        "1910-1920",
        "1910-1911-1912",
        "1910-1911-1912-1913",
        "1910-1911-1912-1913-1914",

        "1938年五月初五",                        // Date 1.0
        "1938年五月初五在湖北武昌抗战中为国捐躯",  // Date 1.0
        "元至正癸未年",
        "至正乙酉年十一月初八",
        "一九五一年元月",
        "一九五一年十月正",
        "一九五一年十月正日",
    };

    public static void main(String... args) throws Exception {
        ThreadLocalExperiment.set(new HashSet<>(Arrays.asList(FrenchRepublicanHandler.EXPERIMENT_ENABLE_V2)));
        runTests();
    }

    static void runTests() throws Exception {
        for (String text : textes) {
            try {
                System.out.println("\n============================================================\n" + text);
                List<GenDateInterpResult> dates = DateUtil.interpDate(text, StdLocale.ENGLISH);
                for (GenDateInterpResult date : dates) {
                    System.out.println("  date: " + date.getDate().toGEDCOMX());
                }
            } catch (GenDateException e) { }
        }
    }
}