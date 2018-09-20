/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.shared.SharedUtil;

/**
 * @author wjohnson000
 *
 */
public class TestV2Only {
    public static void main(String... args) throws Exception {
        List<String> results = new ArrayList<>();

        List<String> textes = textesFromRaw();
        for (String text : textes) {
//            String hex = text.chars()
//                    .mapToLong(ch -> (long)ch)
//                    .mapToObj(ll -> Long.toHexString(ll))
//                    .collect(Collectors.joining(" ", "[", "]"));
//            System.out.println(text + " --> " + hex);
            List<GenDateInterpResult> dates02 = new ArrayList<>();

            System.out.println("\n" + text);

            try {
                dates02 = DateUtil.interpDate(text, StdLocale.CHINESE);
            } catch (Exception e) {
                System.out.println("  V2.ext: " + e.getMessage());
            }

            results.add("");
            for (GenDateInterpResult date : dates02) {
                System.out.println("  gx02: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1));
                results.add(text + "|Date 2.0|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1));
            }
        }

        System.out.println();
        System.out.println("========================================================================================================================");
        System.out.println("========================================================================================================================");
        System.out.println();
        results.forEach(System.out::println);
    }

    static List<String> textesFromFile(String filename) throws IOException {
        return Files.readAllLines(Paths.get(filename), Charset.forName("UTF-8"));
    }

    static List<String> textesFromRaw() {
        List<String> textes = new ArrayList<>();

//        textes.add("順帝三年七月七日");
//        textes.add("順帝丙寅叄年七月七日");
//        textes.add("金世宗大定2年5月5日");
//        textes.add("安政5年6月8日");
//        textes.add("清世祖順治元年1月1日");
//        textes.add("清世祖順治1年1月1日");
//        textes.add("陳文帝天嘉年1月1日");
//        textes.add("吳大帝嘉禾年1月1日");
//        textes.add("民國10年10月10日");
//        textes.add("安政5年6月8");
//        textes.add("西元1921年11月9日");
//        textes.add("宣統三年十二月三十日");
//        textes.add("宣統三年十二月三十一日");
//        textes.add("光緖丁酉年十一月二十九日");
//        textes.add("朝鮮太祖洪武壬申年七月十七日"); 
//        textes.add("乾隆丙午年二月廿三日未時");
//        textes.add("大正五年一月六號");
//        textes.add("清世祖順治元年1月1日"); 
//
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
//
//        textes.add("1 Dec 1910 / 12 Jan 1911");
//        textes.add("02/185");
//        textes.add("14 Oct 1831 (age 71)");
//        textes.add("4. jan 2000");
//
//        textes.add("02.01.2005-2007");
//        textes.add(". Född 1859-03-17");
//        textes.add("*Abt 1846");
//        textes.add("-20-1951");
//        textes.add("00-00-1875");
//        textes.add("03 maio y 1861");
//
//        textes.add("between 14 and 16 Sep 1920");
//        textes.add("15 y 16 Feb 1926");
//        textes.add("B1590 C1591 C1598");
//        textes.add("19 and 20 Jul 1910");
//        textes.add("zw 25 und 28 Jul 1743");
//
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
//
//        textes.add("\\");
//        textes.add("/");
//        textes.add("//1913");
//        textes.add("-+-/-+-/1913");
//
//        textes.add("1769-1858");
//        textes.add("Est. 1769-1858");
//        textes.add("WFT Est. 1769-1858");
//        textes.add("1March 1583/84");
//        textes.add("1March 1583/85");
//
//        textes.add("２４FEB１９２２"); 
//        textes.add("ＡＢＴ１６９０"); 

//        textes.add("from 06-06 2000 to 06.07/2020");
//        textes.add("from 06-06-2000 to 06-07-2020");
//        textes.add("from 06 06 2000 to 06 07 2020");
//        textes.add("from 06 06 2000 - 06 07 2020");
//        textes.add("from 06/06/2000 - 06/07/2020");
//        textes.add("from 06/16/2000 - 06/17/2020");
//        textes.add("from 16/06/2000 - 17/06/2020");
//        textes.add("from 16/06/2020 - 17/06/2000");
//        textes.add("[], 1915");  // OK ??
//        textes.add("Birth & Registration, 1915");
//        textes.add("Birth # Registration, 1915");
//        textes.add("Birth \\ Registration, 1915");
//        textes.add("Birth | Registration, 1915");
//        textes.add("Birth / Registration, 1915");
//        textes.add("from 05 1916 to 01 1985");
//        textes.add("-1500");    // Thrown back to Juan
//        textes.add("- 1500");   // Thrown back to Juan
//        textes.add("-5/-2/");   // Thrown back to Dan
//        textes.add("--/-/--");  // Thrown back to Dan

//        textes.add("1900 & 1950");
//        textes.add("1900 AND 1950");
//        textes.add("1900 TO 1950");
//        textes.add("1900 & 1950 & 2000");
//        textes.add("1900 AND 1950 AND 2000");
//        textes.add("From 1900 TO 1950");
//        textes.add("Between 1900 AND 1950");
//        textes.add("Between 1900 & 1950");
//
//        textes.add("20 Jan 1999 AND 21 Jan 2001");
//        textes.add("20 Jan 1999 & 21 Jan 2001");
//        textes.add("20 Jan 1999 , 21 Jan 2001");
//        textes.add("20 Jan 1999 AND 21 Jan 2001 AND 23 Jan 2003");
//        textes.add("20 Jan 1999 & 21 Jan 2001 & 23 Jan 2003");
//        textes.add("20 Jan 1999 , 21 Jan 2001 , 23 Jan 2003");
//
//        textes.add("1900-2000,1833-1878");
//        textes.add("1900-2000,1945");
//        textes.add("1888, 1890-1900");

//        textes.add("19??");
//        textes.add("198-");
//        textes.add("1900's");
//        textes.add("1770s");
//        textes.add("19??-1938");
//        textes.add("1833-184?");
//        textes.add("19?? - 1938");
//        textes.add("1833 - 184?");
//        textes.add("19-- to 1938");
//        textes.add("1833 to 184?");
//        textes.add("1833 to 1840s");
//        textes.add("1820's - 1840s");
//        textes.add("1820's to 1840s");

//        textes.add("24 05 1668");
//        textes.add("24 May 1668");
//        textes.add("Before 24 05 1668");
//        textes.add("Before 24 May 1668");

//        textes.add("from 25 05 1885 to 1911");
//        textes.add("from 05 25 1885 to 1911");
//        textes.add("from 05 1885 to 1911");
//        textes.add("from 05 25 1885 to 06 25 1911");

//        textes.add("07 Frimaire AN08");
//        textes.add("16 Pluviôse AN02");
//        textes.add("07 FR AN08");
//        textes.add("16 PL AN02");

//        textes.add("1999's");
//        textes.add("AN05 Ventôse 08");
//        textes.add("AN05 VT 08");

//        textes.add("199?");
//        textes.add("1999?");

//        textes.add("-1500");
//        textes.add(" -1500");
//        textes.add("- 1500");
//        textes.add(" - 1500");

//        textes.add("享保 17");
//        textes.add("天保 8");
//        textes.add("文政 7");
//        textes.add("天保 12");
//        textes.add("享保 17 - 天保 8");
//        textes.add("文政 7 - 天保 12");
//        textes.add("嘉永 4");
//        textes.add("嘉永 4 [ 1851 ]");
//        textes.add("享保 6");
//        textes.add("享保 6 [ 1721 ]");
//        textes.add("享保 17 - 天保 8 [ 1732 - 1837 ]");
//        textes.add("文政 7 - 天保 12 [ 1824 - 1841 ]");

//        textes.add("順帝三年七月七日 - 天保 8");

        textes.add("1921年11月9日");
        textes.add("西元1921年11月9日");
        textes.add("1921年11月9日 - 1941年03月19日");
        textes.add("西元1921年11月9日 - 西元1941年03月19日");
        textes.add("千九百二十一年十一月九日");
        textes.add("西元千九百二十一年十一月九日");
        textes.add("千九百二十一年十一月九 - 千九百四十一年三月十九日");
        textes.add("西元千九百二十一年十一月九 - 西元千九百四十一年三月十九日");


        return textes;
    }
}