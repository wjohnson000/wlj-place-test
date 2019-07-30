/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
//import java.util.stream.Collectors;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.model.DateResult;
import org.familysearch.standards.date.model.GenDateInterpResult;
import org.familysearch.standards.date.shared.SharedUtil;

import std.wlj.date.v1.DateV1Shim;

/**
 * @author wjohnson000
 *
 */
public class TestV2 {

    public static void main(String... args) throws Exception {
        List<String> results = new ArrayList<>();

        List<String> textes = textesFromRaw();

        long time0 = System.nanoTime();
        for (String text : textes) {
//            String hex = text.chars()
//                    .mapToLong(ch -> (long)ch)
//                    .mapToObj(ll -> Long.toHexString(ll))
//                    .collect(Collectors.joining(" ", "[", "]"));
//            System.out.println(text + " --> " + hex);
            List<GenDateInterpResult> dates01 = new ArrayList<>();
            DateResult                dates02 = new DateResult();

            System.out.println("\n" + text);

            try {
                dates01 = DateV1Shim.interpDate(text);
            } catch (Exception e) {
                System.out.println("  V1.ext: " + e.getMessage());
            }

            try {
                dates02 = DateUtil.interpDate(text, StdLocale.ENGLISH, null, null, null);
            } catch (Exception e) {
                System.out.println("  V2.ext: " + e.getMessage());
            }

            results.add("");
            for (GenDateInterpResult date : dates01) {
                System.out.println("  gx01: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
                results.add(text + "|Date 1.0|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
            }
            for (GenDateInterpResult date : dates02.getDates()) {
                System.out.println("  gx02: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
                results.add(text + "|Date 2.0|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
            }
            if (dates02.getDates().isEmpty()) {
                System.out.println("  gx02: " + text + "|<none>|<none>");
                results.add(text + "|Date 2.0|<none>|<none>");
            }
        }
        long time1 = System.nanoTime();

        System.out.println();
        System.out.println("========================================================================================================================");
        System.out.println("========================================================================================================================");
        System.out.println();
        results.forEach(System.out::println);
        System.out.println("\n\nTTT: " + (time1 - time0) / 1_000_000.0);
    }

    static List<String> textesFromFile(String filename) throws IOException {
        return Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
    }

    static List<String> textesFromRaw() {
        List<String> textes = new ArrayList<>();

//        textes.add("順帝三年七月七日 stuff deceased");
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
//        textes.add("2 complémentaire 3");
//        textes.add("28 Brumaire AN04");
//        textes.add("11 de Vendémiaire 04");
//        textes.add("07 Frimaire AN08");
//        textes.add("16 Illisible AN02");
//        textes.add("00 Nivose AN12 ");
//        textes.add("民國乙未（四十四）年五月五日");
//        textes.add("民國乙未（四十四）五月五日");
//        textes.add("民國乙未（四十四年）五月五日");
//
//        textes.add("22 Feb/5 Mar 1752/3");
//        textes.add("Sept 3/14, 1752");
//        textes.add("10/21 Feb 1759/60");
//        textes.add("1 Mar 1759/60");
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
//        textes.add("1913");
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
//
//        textes.add("from 06-06 2000 to 06.07/2020");
//        textes.add("from 06-06-2000 to 06-07-2020");
//        textes.add("from 06 06 2000 to 06 07 2020");
//        textes.add("from 06 06 2000 - 06 07 2020");
//        textes.add("from 06/06/2000 - 06/07/2020");
//        textes.add("from 06/16/2000 - 06/17/2020");
//        textes.add("from 16/06/2000 - 17/06/2020");
//        textes.add("from 16/06/2020 - 17/06/2000");
//        textes.add("[], 1915");
//        textes.add("Birth & Registration, 1915");
//        textes.add("Birth # Registration, 1915");
//        textes.add("Birth \\ Registration, 1915");
//        textes.add("Birth | Registration, 1915");
//        textes.add("Birth / Registration, 1915");
//        textes.add("from 05 1916 to 01 1985");
//        textes.add("-1500");
//        textes.add("- 1500");
//        textes.add("-5/-2/");
//        textes.add("--/-/--");
//
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
//
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
//
//        textes.add("24 05 1668");
//        textes.add("24 May 1668");
//        textes.add("Before 24 05 1668");
//        textes.add("Before 24 May 1668");
//
//        textes.add("from 25 05 1885 to 1911");
//        textes.add("from 05 25 1885 to 1911");
//        textes.add("from 05 1885 to 1911");
//        textes.add("from 05 25 1885 to 06 25 1911");
//
//        textes.add("07 Frimaire AN08");
//        textes.add("16 Pluviôse AN02");
//        textes.add("07 FR AN08");
//        textes.add("16 PL AN02");
//
//        textes.add("1999's");
//        textes.add("AN05 Ventôse 08");
//        textes.add("AN05 VT 08");
//
//        textes.add("199?");
//        textes.add("1999?");
//
//        textes.add("-1500");
//        textes.add(" -1500");
//        textes.add("- 1500");
//        textes.add(" - 1500");
//
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
//
//        textes.add("順帝三年七月七日 - 天保 8");
//
//        textes.add("1921年11月9日");
//        textes.add("西元1921年11月9日");
//        textes.add("1921年11月9日 - 1941年03月19日");
//        textes.add("西元1921年11月9日 - 西元1941年03月19日");
//        textes.add("千九百二十一年十一月九日");
//        textes.add("西元千九百二十一年十一月九日");
//        textes.add("千九百二十一年十一月九 - 千九百四十一年三月十九日");
//        textes.add("西元千九百二十一年十一月九 - 西元千九百四十一年三月十九日");
//
//        textes.add("[191_]-1974");
//        textes.add("[191?]-1974");
//        textes.add("[1910's]-1974");
//        textes.add("[191-]-1974");
//        textes.add("[19__]-1974");
//        textes.add("[19??]-1974");
//        textes.add("[1900s]-1974");
//        textes.add("[19--]-1974");
//
//        textes.add("[1970]-1974");
//        textes.add("[191_-1974]");
//        textes.add("[191?-1974]");
//        textes.add("[1910's-1974]");
//        textes.add("[191--1974]");
//        textes.add("[19__-1974]");
//        textes.add("[19??-1974]");
//        textes.add("[1900s-1974]");
//        textes.add("[19---1974]");
//
//        textes.add("21.3.02");
//        textes.add("11.3.00");
//        textes.add("11.3");
//
//        textes.add("1901?");
//        textes.add("24 Apr 1901?");
//        textes.add("24 Apr 190?");
//
//        textes.add("1900-2000,1833-1878");
//        textes.add("1900-2000 ; 1833-1878");
//        textes.add("1900-2000,1945"); 
//        textes.add("1900-2000 ; Abt. 1945"); 
//        textes.add("1888, 1890-1900 ");
//        textes.add("1888 ; 1890-1900 ");
//        textes.add("188? ; 1890-1900 , 1990's ");
//
//        textes.add("10-10-10");
//        textes.add("10-10-32");
//        textes.add("2018 - jun-0");
//        textes.add("17 Fev 1863 (vue 183)");
//
//        textes.add("028 BC");
//        textes.add("28 BC");
//        textes.add("048 BC");
//        textes.add("09.05.1817");
//        textes.add("09.05.1817?");
//        textes.add("09-05-1817?");
//        textes.add("09/05/1817?");
//        textes.add("09/05/1817 ?");
//        textes.add("182-04-15");
//        textes.add("1859sep5");
//        textes.add("1998septiembre  07");
//        textes.add("About 1825?");
//        textes.add("abt 1700's");
//        textes.add("from 1960s to 1980s");
//        textes.add("late 1930s or possibly very early 1940s");
//        textes.add("October 3rd 191*");
//
//        textes.add("Sept. 5 1940?");
//
//        textes.add("대략 10");
//        textes.add("추정18");
//        textes.add("約10年");
//        textes.add("대략 1910");
//        textes.add("추정1918");
//        textes.add("約1910年");
//
//        textes.add("8/17/18 12:31 am");
//        textes.add("10/15/18 7:00 pm");
//        textes.add("6/8/22");
//
//        textes.add("19-25 March 1803");
//
//        textes.add("光厳1年1月1日 ");
//        textes.add("応永1年1月1日");
//        textes.add("元和1年1月1日");
//        textes.add("嘉永1年1月1日");
//
//        textes.add("1509년");
//        textes.add("1509년?");
//        textes.add("1509년?(");
//        textes.add("1509년?(1");
//        textes.add("1509년?(15");
//        textes.add("1509년?(152");
//        textes.add("1509년?(1529");
//        textes.add("1509년?(1529년");
//
//        textes.add("10-20 Mar 2020");
//        textes.add("June to July 2000");
//        textes.add("June 2000 to July 2000");
//        textes.add("20 June to 22 July 2000");
//        textes.add("June 20 to July 22 2000");
//        textes.add("from June to July 2000");
//        textes.add("from June 2000 to July 2000");
//        textes.add("from 20 June to 22 July 2000");
//        textes.add("from June 20 to July 22 2000");
//
//        textes.add("단기");
//        textes.add("檀紀");
//        textes.add("천보");
//        textes.add("天保");
//        textes.add("홍화");
//        textes.add("弘化");
//        textes.add("가영");
//        textes.add("嘉永");
//        textes.add("안정");
//        textes.add("安政");
//        textes.add("만연");
//        textes.add("萬延");
//        textes.add("문구");
//        textes.add("文久");
//        textes.add("원치");
//        textes.add("元治");
//        textes.add("경응");
//        textes.add("慶應");
//        textes.add("명치");
//        textes.add("明治");
//        textes.add("대정");
//        textes.add("大正");
//        textes.add("소화");
//        textes.add("昭和");
//
//        textes.add("金世宗大定2年5月5日");
//        textes.add("西元1921年11月9日");
//        textes.add("1957 1 23 to 1975 11 6");
//        textes.add("22 Feb/5 Mar 1752/3");
//        textes.add("-+-/-+-/1913");
//        textes.add("20 Jan 1999 AND 21 Jan 2001");
//        textes.add("20 Jan 1999 AND 21 Jan 2001 AND 23 Jan 2003");
//        textes.add("24 Apr 190?");
//        textes.add("2018 - jun-0");
//
//        textes.add("14+July+1559");
//        textes.add("14+July+1559");
//        textes.add("6+July+1559");
//        textes.add("6 July 1559");
//
//        textes.add("세종2년");
//        textes.add("성종2년");
//        textes.add("철종2년");
//        textes.add("개국");
//
//        textes.add("2 NOV 598 B C");
//        textes.add("2 NOV 598 BC");
//        textes.add("19JUN1541-44");
//        textes.add("1426年3月24日");
//        textes.add("+1942?+1949?");
//
//        textes.add("? - abc 2000");
//        textes.add("? - 2000");
//        textes.add("- abc 2000");
//        textes.add("? abc 2000");
//        textes.add("4 juillet 1776");
//
//        textes.add("about between April 13, 1825 and November 26,1825");
//        textes.add("about between 1752 CE and 1823 CE");
//        textes.add("about before May, 1887 CE");
//        textes.add("about after July 11, 1976 CE");
//        textes.add("about before 1288 BCE");
//
//        textes.add("檀紀1");
//        textes.add("檀紀111");
//        textes.add("檀紀2333");
//        textes.add("檀紀2334");
//        textes.add("檀紀2335");
//        textes.add("檀紀2434");
//        textes.add("檀紀4288");
//        textes.add("檀紀4351");
//
//        textes.add("15.+toukokuuta+2018");
//        textes.add("24.+toukokuuta+2014");
//
//        textes.add("June+13+2011");
//        textes.add("19.+Dezember+1865");
//        textes.add("24.+Februar+2015");
//        textes.add("1.+Januar+2018");
//        textes.add("19.+Dezember+1865");
//        textes.add("20.+November+2015");
//        textes.add("23.+Mai+2017");
//        textes.add("20.+Juni+1836");
//        textes.add("18.+Februar+1857");
//        textes.add("1.+September+1594");
//        textes.add("4.+Juli+1852");
//        textes.add("20.+Dezember+2015");
//        textes.add("5.+februar+2016");
//        textes.add("29.+marts+2016");
//        textes.add("3.+März+1829");
//        textes.add("1.+April+2016");
//        textes.add("23.+Dezember+1914");
//        textes.add("10.+Februar+1892");
//        textes.add("17.+Juli+1962");
//        textes.add("22.+August+1901");
//        textes.add("3.+Januar+1873");
//        textes.add("18.+Oktober+1878");
//        textes.add("17.+Juli+1962");
//        textes.add("25.+Februar+1850");
//
//        textes.add("April+1914");
//        textes.add("+April+1914");
//
//        textes.add("between+estimated+493+BC+and+435+BC");
//        textes.add("大正12年8月31日");
//        textes.add("30 Sept 1980");
//        textes.add("30 Sept. 1980");
//
//        textes.add("1985–1990");
//        textes.add("1985-1990");
//
//        textes.add("民國");
//        textes.add("民國1年");
//        textes.add("民國前1年");
//        textes.add("民國34年");
//        textes.add("民國前34年");
//
//        textes.add("after 1980");
//        textes.add("despues 1980");
//        textes.add("despues de 1980");
//        textes.add("despues del 1980");
//        textes.add("después 1980");
//        textes.add("después de 1980");
//        textes.add("después del 1980");
//        textes.add("before 1980");
//        textes.add("antes 1980");
//        textes.add("antes de 1980");
//        textes.add("antes del 1980");
//
//        textes.add("25th September 1928");
//        textes.add("25rd September 1928");
//        textes.add("25 th September 1928");
//        textes.add("17th April 23 Henry VI");
//        textes.add("12 germinal 1806");
//        textes.add("24 floreal 1797");
//
//        textes.add("25 de Agost 1999");
//        textes.add("25 d'Agost 1999");
//        textes.add("25 d' Agost 1999");
//
//        textes.add("Saturday Jan. 14, 1989");
//        textes.add("Saturday (Jan. 14, 1989)");
//        textes.add("Saturday [Jan. 14, 1989]");
//        textes.add("Saturday {Jan. 14, 1989}");
//        textes.add("Saturday <Jan. 14, 1989>");
//        textes.add("(Jan. 14, 1989)");
//        textes.add("[Jan. 14, 1989]");
//        textes.add("{Jan. 14, 1989}");
//        textes.add("<Jan. 14, 1989>");
//
//        textes.add("ABOUT 1st day of October in the year of our Lord One Thousand eight hundred and twenty-two"); 
//        textes.add("26th day of February Anno Domini 1870"); 
//        textes.add("Twenty sixth day of February in the year of our Lord one thousand eight hundred and seventy");
//
//        textes.add("10-20-2015");
//
//        textes.add("first day of May one thou-   sand eight hundred and forty four");
//        textes.add("first day of April in the year of one Lord one thousand");
//        textes.add("October third eighteen hundred and seventy two");
//        textes.add("October third, eighteen hundred and seventy two");
//
//        textes.add("197?");
//        textes.add("1970's");
//        textes.add("about early 1970s");
//        textes.add("early 1970s");
//        textes.add("early 1970's");
//        textes.add("early 197?");
//        textes.add("mid 1970s");
//        textes.add("mid 1970's");
//        textes.add("mid 197?");
//        textes.add("late 1970s");
//        textes.add("late 1970's");
//        textes.add("late 197?");
//
//        textes.add("middle of 1970s");
//        textes.add("middle of 1970's");
//        textes.add("middle of 197?");
//        textes.add("end of 1970s");
//        textes.add("end of 1970's");
//        textes.add("end of 197?");
//
//        textes.add("middle 180?");
//        textes.add("middle of 18??");
//        textes.add("middle of 1000's");
//
//        textes.add("About 1825?");
//        textes.add("1999's");
//
//        textes.add("30 BC");
//        textes.add("about 30 BC");
//        textes.add("BEFORE 30 BC");
//        textes.add("after 30 BC");
//
//        textes.add("30 AD");
//        textes.add("about 30 AD");
//        textes.add("BEFORE 30 AD");
//        textes.add("after 30 AD");
//
//        textes.add("early 1890's");
//        textes.add("mid 1890's");
//        textes.add("late 1890's");
//        textes.add("early 1900's");
//        textes.add("mid 1900's");
//        textes.add("late 1900's");
//        textes.add("early 1000's");
//        textes.add("mid 1000's");
//        textes.add("late 1000's");
//
//        textes.add("3/11/00");
//        textes.add("3/11/01");
//
//        textes.add("遼太祖一年一月九日");    // Chinese
//        textes.add("元和一年一月九日");      // Japanese
//        textes.add("朝鮮世宗一年一月九日");  // Korean
//
//        textes.add("13 January 1613/1614");
//        textes.add("About 13 January 1613/1614");
//        textes.add("Before 13 January 1613/1614");
//        textes.add("After 13 January 1613/1614");
//        textes.add("13 January 1613/14");
//        textes.add("About 13 January 1613/14");
//        textes.add("Before 13 January 1613/14");
//        textes.add("After 13 January 1613/14");
//
//        textes.add("From 1 January 1914 to 31 December 1918");
//        textes.add("From ( Enlisted ) 1 January 1914 to ( Discharged ) 31 December 1918");
//        textes.add("From (Enlisted) 1 January 1914 to (Discharged) 31 December 1918");
//        textes.add("From [Enlisted] 1 January 1914 to [Discharged] 31 December 1918");
//        textes.add("From {Enlisted} 1 January 1914 to {Discharged} 31 December 1918");
//        textes.add("From {Enlisted} 1 January 1914 to {Discharged} 31 December 1918 (");
//
//        textes.add("266666666666666666666666666666666666666");
//        textes.add("23 24 12 49");
//        textes.add("15[6] 15[6] 15[6]");
//        textes.add("Between14 &20 10 86");
//
//        textes.add("I me 1");
//        textes.add("I me I");
//        textes.add("and I order my said son Isaack to pay all my just debts and to receive all my debts that are due unto me also I order my Indian Lydia to live with my son Isaac until he is one and twenty years of age,");
//
//        textes.add("세종2년");
//        textes.add("개국503");
//        textes.add("단기3753");
//
//        textes.add("+1918-06-26/+1904");
//
//        textes.add("19JUN1541-44");
//        textes.add("22 Mar 1568-70");
//        textes.add("22 Mar 1568 - 70");
//        textes.add("Feb 18 1755 to 54");
//
//        textes.add("嘉永1年1月1日");  // STD-6638
//
//        textes.add("元延祐1年1月15日");
//        textes.add("民國七十三甲子年");
//        textes.add("民國七十七戊辰年七月初六日");
//        textes.add("民國乙未（四十四）五月五日");
//        textes.add("民國甲子");
//        textes.add("哀帝建平四年夏");
//
//        textes.add("武成王");
//
//        textes.add("景帝孝");
//        textes.add("朝鮮太祖洪武七年九月五日");
//        textes.add("중천왕");
//        textes.add("朝鮮定宗建文庚辰年十一月十三日");
//
//        textes.add("朝鮮太宗永樂戊戌年");
//        textes.add("朝鮮世宗永樂己亥年");
//        textes.add("朝鮮世宗永樂庚子年");
//        textes.add("朝鮮世宗永樂辛丑年");
//        textes.add("朝鮮世宗永樂壬寅年");
//        textes.add("朝鮮世宗永樂癸卯年");
//
//        textes.add("After December 14, 1940");
//        textes.add("After Dec 14, 1940");
//        textes.add("After 14 December, 1940");
//        textes.add("After 14 Dec, 1940");
//
//        textes.add("宣惠王");
//        textes.add("康靖帝");
//        textes.add("大明敬帝");
//
//        textes.add("광무");
//
//        textes.add("12/20/01");
//        textes.add("12/20/00");
//        textes.add("12/20/01 BC");
//        textes.add("Feb 11 1 BC");
//        textes.add("Feb 11 0001 BC");
//        textes.add("Feb 11 2 BC");
//        textes.add("Feb 11 111 BC");
//        textes.add("3/11");
//        textes.add("04/08/00");
//
//        textes.add("31 juillet 1801");
//        textes.add("12 thermidor an IX");
//        textes.add("31 juillet 1801 (12 thermidor an IX)");
//
//        textes.add("1998年");
//        textes.add("1998年以前");
//        textes.add("1998年以後");
//        textes.add("after 1998");
//        textes.add("1998 after");
//
//        textes.add("1900-1905");          // STD-7119
//        textes.add("before 1900");
//        textes.add("before 1900-1905");   // STD-7119
//        textes.add("after 1900-1905");    // STD-7119
//        textes.add("February 30, 1712");  // STD-7122
//        textes.add("< 1718");             // STD-7123
//        textes.add(">= 1718");            // STD-7123
//        textes.add("-- ___ 1718");        // STD-7124
//        textes.add("Abt 03 Sep 1662 康熙壬寅");   // STD-7125
//        textes.add("明和８年（１７７１年）１０月１７日");  // STD-7126
//        textes.add("1 FEB 1758 BET 1 FEB 1758 AND 17 NOV 1761");  // STD-7127
//
//        textes.add("Jan 20");
//        textes.add("4 May");
//
//        textes.add("Jan 36");
//        textes.add("55 May");
//
//        textes.add("5/6/1900");
//        textes.add("6/5/1900");
//        textes.add("6 5 1900");
//        textes.add("6 25 1900");
//        textes.add("26 5 1900");
//        textes.add("1900/6/5");
//
//        textes.add("07 Vendémiaire AN08");
//        textes.add("07 VD AN08");
//        textes.add("07 VEND AN08");
//        textes.add("07 Brumiaire AN08");
//        textes.add("07 BR AN08");
//        textes.add("07 BRU AN08");
//        textes.add("07 BRUM AN08");
//        textes.add("07 Frimaire AN08");
//        textes.add("07 FM AN08");
//        textes.add("07 FR AN08");
//        textes.add("07 FRI AN08");
//        textes.add("07 FRIM AN08");
//        textes.add("07 Nivôse AN08");
//        textes.add("07 NI AN08");
//        textes.add("07 NIV AN08");
//        textes.add("07 NIVO AN08");
//        textes.add("07 Pluviose AN08");
//        textes.add("07 PL AN08");
//        textes.add("07 PLU AN08");
//        textes.add("07 PLUV AN08");
//        textes.add("07 Ventôse AN08");
//        textes.add("07 VT AN08");
//        textes.add("07 VENT AN08");
//        textes.add("07 Germinal AN08");
//        textes.add("07 GE AN08");
//        textes.add("07 GER AN08");
//        textes.add("07 GERM AN08");
//        textes.add("07 Floréal AN08");
//        textes.add("07 FL AN08");
//        textes.add("07 FLO AN08");
//        textes.add("07 FLOR AN08");
//        textes.add("07 Prairial AN08");
//        textes.add("07 PR AN08");
//        textes.add("07 PRA AN08");
//        textes.add("07 PRAI AN08");
//        textes.add("07 Messidor AN08");
//        textes.add("07 ME AN08");
//        textes.add("07 MES AN08");
//        textes.add("07 MESS AN08");
//        textes.add("07 Thermidor AN08");
//        textes.add("07 TH AN08");
//        textes.add("07 THE AN08");
//        textes.add("07 THER AN08");
//        textes.add("07 Fervidor AN08");
//        textes.add("07 Fructidor AN08");
//        textes.add("07 FT AN08");
//        textes.add("07 FRU AN08");
//        textes.add("07 FRUC AN08");
//
//        textes.add("Abt 03 Sep 1662");
//        textes.add("康熙壬寅");
//        textes.add("Abt 03 Sep 1662 康熙壬寅");
//        textes.add("康熙壬寅 Abt 03 Sep 1662");
//        textes.add("康熙壬寅 03 Sep 1662");
//        textes.add("康熙壬寅 Sep 03 1662");
//        textes.add("康熙壬寅3");
//        textes.add("康熙壬寅03");
//        textes.add("康熙壬寅  03");
//        textes.add("金世宗大定");
//        textes.add("金世宗大定2年5月5日");
//        textes.add("金世宗大定2");
//        textes.add("金世宗大定 2");
//
//        textes.add("– 1718");
//        textes.add("– ___ 1718");
//        textes.add("– birth 1718");
//
//        textes.add("Abt 03 Sep 1662 康熙壬寅");
//        textes.add("About 03 Sep 1662 康熙壬寅");
//        textes.add("03 Sep 1662 康熙壬寅");
//        
//        textes.add("– ___ 1718");
//
//        textes.add("太和一年三月十一日");
//        textes.add("太和一年三月");
//
//        textes.add("朱以海一年");
//        textes.add("朱聿𨮁一年");
//        textes.add("朱由榔一年");
//
//        textes.add("26th day of September, 1920");
//        textes.add("26th day of September 1920");
//        textes.add("26th of September 1920");
//
//        textes.add("Mar 1947-Oct 1950");
//        textes.add("1947 Mar-1950 Oct");
//
//        textes.add("Abt 03 Sep 1662");
//        textes.add("1804 約年");
//        textes.add("abt 1734 (at age 30)");
//        textes.add("3+May+1687+/+5+March+1687");
//        textes.add("1844 約年");
//        textes.add("5 August 1896 / 5 August 1896");
//        textes.add("2 mar 1901 Birth Place: Gudmundrå, Västernorrland, Sverige (Sweden)");
//        textes.add("01+Apr+1992-01+Jan+1998");
//        textes.add("02 Feb 1991-01 Jan 2004");
//
//        textes.add("3 Listopad 1215");
//
//        textes.add("Mar. 21 1841");
//
//        textes.add("世宗 一年");
//        textes.add("世宗一年");
//        textes.add("世宗 一年");
//        textes.add("朝鮮世宗永樂庚子年");
//
//        textes.add("2 feb. 1920");
//        textes.add("2 feb 1920");
//        textes.add("30 abr. 1930");
//        textes.add("30 abr 1930");

        textes.add("");
        textes.add("0");
        textes.add("00");
        textes.add("000");
        textes.add("0000");

        return textes;
    }
}