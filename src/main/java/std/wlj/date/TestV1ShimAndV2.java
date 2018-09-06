/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.exception.GenDateParseException;
import org.familysearch.standards.date.shared.SharedUtil;
import org.familysearch.standards.date.v1.DateV1Shim;

/**
 * @author wjohnson000
 *
 */
public class TestV1ShimAndV2 {

//    static String[] textes = {
//        "順帝三年七月七日",
//        "順帝丙寅叄年七月七日",
//        "金世宗大定2年5月5日",
//        "安政5年6月8日",
//        "清世祖順治元年1月1日",
//        "清世祖順治1年1月1日",
//        "陳文帝天嘉年1月1日",
//        "吳大帝嘉禾年1月1日",
//        "民國10年10月10日",
//        "安政5年6月8",
//        "西元1921年11月9日",
//        "宣統三年十二月三十日",
//        "宣統三年十二月三十一日",
//        "光緖丁酉年十一月二十九日",
//        "朝鮮太祖洪武壬申年七月十七日", 
//        "乾隆丙午年二月廿三日未時",
//        "大正五年一月六號",
//        "清世祖順治元年1月1日", 
//    };

    public static void main(String... args) throws Exception {
        runTests();
    }

    static void runTests() throws Exception {
        List<String> results = new ArrayList<>();
//        List<String> textes = Files.readAllLines(Paths.get("C:/temp/zh-dates-more.txt"), Charset.forName("UTF-8"));
        List<String> textes = new ArrayList<>();
        textes.clear();
//        textes.add("11/6/1975");
//        textes.add("1975 11 6");
//        textes.add("1957 1 23 to 1975 11 6");
//        textes.add("1/23/1957 to 11/6/1975");
//        textes.add("二〇〇三年");
//        textes.add("二零零三年二月三");
//        textes.add("二零零三年二月三日");
//        textes.add("30 Floréal AN11");
//        textes.add("28 Brumaire AN04");
//        textes.add("11 Vendémiaire AN04");
//        textes.add("07 Frimaire AN08");
//        textes.add("16 Illisible AN02");
//        textes.add("00 Nivose AN12 ");
//        textes.add("民國乙未（四十四）年五月五日");
//        textes.add("民國乙未（四十四）五月五日");
//        textes.add("民國乙未（四十四年）五月五日");

//        textes.add("1 Dec 1910 / 12 Jan 1911");
//        textes.add("02/185");
//        textes.add("14 Oct 1831 (age 71)");
//        textes.add("4. jan 2000");

//        textes.add("02.01.2005-2007");
//        textes.add(". Född 1859-03-17");
//        textes.add("*Abt 1846");
//        textes.add("-20-1951");
//        textes.add("00-00-1875");
//        textes.add("03 maio y 1861");

//        textes.add("between 14 and 16 Sep 1920");
//        textes.add("15 y 16 Feb 1926");
//        textes.add("B1590 C1591 C1598");
//        textes.add("19 and 20 Jul 1910");
//        textes.add("zw 25 und 28 Jul 1743");

//        textes.add("31y");
//        textes.add("31Y");
//        textes.add("p31y");
//        textes.add("P31Y");
//
//        textes.add("31yr");
//        textes.add("31YR");
//        textes.add("p31yr");
//        textes.add("P31YR");
//
//        textes.add("31yrs");
//        textes.add("31YRS");
//        textes.add("p31yrs");
//        textes.add("P31YRS");
//
//        textes.add("33y");
//        textes.add("33Y");
//        textes.add("p33y");
//        textes.add("P33Y");
//
//        textes.add("private");
//        textes.add("Private");

        textes.add("\\");
        textes.add("/");
        textes.add("//1913");
        textes.add("-+-/-+-/1913");

        for (String text : textes) {
            List<GenDateInterpResult> dates01 = new ArrayList<>();
            List<GenDateInterpResult> dates02 = new ArrayList<>();

            System.out.println("\n" + text);

            try {
                dates01 = DateV1Shim.interpDate(text);
            } catch (Exception e) {
                System.out.println("  V1.ext: " + e.getMessage());
            }

            try {
                dates02 = DateUtil.interpDate(text, StdLocale.CHINESE);
            } catch (Exception e) {
                System.out.println("  V2.ext: " + e.getMessage());
            }

            results.add("");
            results.add(text);
            for (GenDateInterpResult date : dates01) {
                System.out.println("  gx01: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1));
                results.add("  gx01: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1));
            }
            for (GenDateInterpResult date : dates02) {
                System.out.println("  gx02: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1));
                results.add("  gx02: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1));
            }
        }

        results.forEach(System.out::println);
    }
}