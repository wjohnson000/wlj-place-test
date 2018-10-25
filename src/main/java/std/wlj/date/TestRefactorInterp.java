/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateServiceImpl;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.model.DateResult;
import org.familysearch.standards.date.ws.model.CalendarType;
import org.familysearch.standards.date.ws.model.Dates;
import org.familysearch.standards.date.ws.model.LocalizedDateFormat;
import org.familysearch.standards.date.mapper.DatesMapper;
import org.familysearch.standards.date.ws.services.DatesService;

import std.wlj.marshal.POJOMarshalUtil;

/**
 * @author wjohnson000
 *
 */
public class TestRefactorInterp {

    static LocalizedDateFormat[] LDF = {
        LocalizedDateFormat.abbreviated,
        LocalizedDateFormat.legacy,
        LocalizedDateFormat.legacyabbreviated,
        LocalizedDateFormat.localized,
        LocalizedDateFormat.longfmt,
    };

    static StdLocale[] LOCALE = {
        StdLocale.CHINESE,
        StdLocale.ENGLISH,
        StdLocale.FRENCH,
        StdLocale.GERMAN,
        StdLocale.JAPANESE,
        StdLocale.RUSSIAN,
        StdLocale.SPANISH,
    };

    public static void main(String...args) throws GenDateException {
        DatesService    dateOld = new DatesService();
        DateServiceImpl dateNew = new DateServiceImpl();
        DatesMapper     dateMap = new DatesMapper();
        
        int badCount = 0;
        List<String> textes = textesFromRaw();
        for (String text : textes) {
            for (LocalizedDateFormat ldf : LDF) {
                for (StdLocale locale : LOCALE) {
                     Dates      datesOld = dateOld.getDatesInterp(text.trim(), ldf, locale, locale, CalendarType.gregorian);
                     DateResult dateRes = dateNew.interpDate(text.trim(), locale);
                     Dates      datesNew = dateMap.fromDateResult(dateRes, ldf, locale, CalendarType.gregorian);

                     String     xmlOld = POJOMarshalUtil.toXML(datesOld);
                     String     xmlNew = POJOMarshalUtil.toXML(datesNew);
                     System.out.println(text + " | " + locale + " | " + ldf);
                     if (! xmlOld.trim().equals(xmlNew.trim())) {
                         badCount++;
                         System.out.println("\n\nOLD:\n" + xmlOld);
                         System.out.println("\nNEW:\n" + xmlNew);
                     }
                }
            }
        }

        System.out.println("\n\nBadCount=" + badCount);
    }

    static List<String> textesFromRaw() {
        List<String> textes = new ArrayList<>();

        textes.add("順帝三年七月七日");
        textes.add("順帝丙寅叄年七月七日");
        textes.add("金世宗大定2年5月5日");
        textes.add("安政5年6月8日");
        textes.add("清世祖順治元年1月1日");
        textes.add("清世祖順治1年1月1日");
        textes.add("陳文帝天嘉年1月1日");
        textes.add("吳大帝嘉禾年1月1日");
        textes.add("民國10年10月10日");
        textes.add("安政5年6月8");
        textes.add("西元1921年11月9日");
        textes.add("宣統三年十二月三十日");
        textes.add("宣統三年十二月三十一日");
        textes.add("光緖丁酉年十一月二十九日");
        textes.add("朝鮮太祖洪武壬申年七月十七日"); 
        textes.add("乾隆丙午年二月廿三日未時");
        textes.add("大正五年一月六號");
        textes.add("清世祖順治元年1月1日"); 

        textes.add("11/6/1975");
        textes.add("1975 11 6");
        textes.add("1957 1 23 to 1975 11 6");
        textes.add("1/23/1957 to 11/6/1975");
        textes.add("二〇〇三年");
        textes.add("二零零三年二月三");
        textes.add("二零零三年二月三日");
        textes.add("30 Floréal AN11");
        textes.add("2 complémentaire 3");
        textes.add("28 Brumaire AN04");
        textes.add("11 de Vendémiaire 04");
        textes.add("07 Frimaire AN08");
        textes.add("16 Illisible AN02");
        textes.add("00 Nivose AN12 ");
        textes.add("民國乙未（四十四）年五月五日");
        textes.add("民國乙未（四十四）五月五日");
        textes.add("民國乙未（四十四年）五月五日");

        textes.add("22 Feb/5 Mar 1752/3");
        textes.add("Sept 3/14, 1752");
        textes.add("10/21 Feb 1759/60");
        textes.add("1 Mar 1759/60");
        textes.add("1 Dec 1910 / 12 Jan 1911");
        textes.add("02/185");
        textes.add("14 Oct 1831 (age 71)");
        textes.add("4. jan 2000");

        textes.add("02.01.2005-2007");
        textes.add(". Född 1859-03-17");
        textes.add("*Abt 1846");
        textes.add("-20-1951");
        textes.add("00-00-1875");
        textes.add("03 maio y 1861");

        textes.add("between 14 and 16 Sep 1920");
        textes.add("15 y 16 Feb 1926");
        textes.add("B1590 C1591 C1598");
        textes.add("19 and 20 Jul 1910");
        textes.add("zw 25 und 28 Jul 1743");

        textes.add("31y");
        textes.add("31Y");
        textes.add("p31y");
        textes.add("P31Y");

        textes.add("31yr");
        textes.add("31YR");
        textes.add("p31yr");
        textes.add("P31YR");

        textes.add("31yrs");
        textes.add("31YRS");
        textes.add("p31yrs");
        textes.add("P31YRS");

        textes.add("33y");
        textes.add("33Y");
        textes.add("p33y");
        textes.add("P33Y");

        textes.add("private");
        textes.add("Private");

        textes.add("\\");
        textes.add("/");
        textes.add("1913");
        textes.add("-+-/-+-/1913");

        textes.add("1769-1858");
        textes.add("Est. 1769-1858");
        textes.add("WFT Est. 1769-1858");
        textes.add("1March 1583/84");
        textes.add("1March 1583/85");

        textes.add("２４FEB１９２２"); 
        textes.add("ＡＢＴ１６９０"); 

        textes.add("from 06-06 2000 to 06.07/2020");
        textes.add("from 06-06-2000 to 06-07-2020");
        textes.add("from 06 06 2000 to 06 07 2020");
        textes.add("from 06 06 2000 - 06 07 2020");
        textes.add("from 06/06/2000 - 06/07/2020");
        textes.add("from 06/16/2000 - 06/17/2020");
        textes.add("from 16/06/2000 - 17/06/2020");
        textes.add("from 16/06/2020 - 17/06/2000");
        textes.add("[], 1915");
        textes.add("Birth & Registration, 1915");
        textes.add("Birth # Registration, 1915");
        textes.add("Birth \\ Registration, 1915");
        textes.add("Birth | Registration, 1915");
        textes.add("Birth / Registration, 1915");
        textes.add("from 05 1916 to 01 1985");
        textes.add("-1500");
        textes.add("- 1500"); 

        textes.add("1900 & 1950");
        textes.add("1900 AND 1950");
        textes.add("1900 TO 1950");
        textes.add("1900 & 1950 & 2000");
        textes.add("1900 AND 1950 AND 2000");
        textes.add("From 1900 TO 1950");
        textes.add("Between 1900 AND 1950");
        textes.add("Between 1900 & 1950");

        textes.add("20 Jan 1999 AND 21 Jan 2001");
        textes.add("20 Jan 1999 & 21 Jan 2001");
        textes.add("20 Jan 1999 , 21 Jan 2001");
        textes.add("20 Jan 1999 AND 21 Jan 2001 AND 23 Jan 2003");
        textes.add("20 Jan 1999 & 21 Jan 2001 & 23 Jan 2003");
        textes.add("20 Jan 1999 , 21 Jan 2001 , 23 Jan 2003");

        textes.add("1900-2000,1833-1878");
        textes.add("1900-2000,1945");
        textes.add("1888, 1890-1900");

        textes.add("19??");
        textes.add("198-");
        textes.add("1900's");
        textes.add("1770s");
        textes.add("19??-1938");
        textes.add("1833-184?");
        textes.add("19?? - 1938");
        textes.add("1833 - 184?");
        textes.add("19-- to 1938");
        textes.add("1833 to 184?");
        textes.add("1833 to 1840s");
        textes.add("1820's - 1840s");
        textes.add("1820's to 1840s");

        textes.add("24 05 1668");
        textes.add("24 May 1668");
        textes.add("Before 24 05 1668");
        textes.add("Before 24 May 1668");

        textes.add("from 25 05 1885 to 1911");
        textes.add("from 05 25 1885 to 1911");
        textes.add("from 05 1885 to 1911");
        textes.add("from 05 25 1885 to 06 25 1911");

        textes.add("07 Frimaire AN08");
        textes.add("16 Pluviôse AN02");
        textes.add("07 FR AN08");
        textes.add("16 PL AN02");

        textes.add("1999's");
        textes.add("AN05 Ventôse 08");
        textes.add("AN05 VT 08");

        textes.add("199?");
        textes.add("1999?");

        textes.add("-1500");
        textes.add(" -1500");
        textes.add("- 1500");
        textes.add(" - 1500");

        textes.add("享保 17");
        textes.add("天保 8");
        textes.add("文政 7");
        textes.add("天保 12");
        textes.add("享保 17 - 天保 8");
        textes.add("文政 7 - 天保 12");
        textes.add("嘉永 4");
        textes.add("嘉永 4 [ 1851 ]");
        textes.add("享保 6");
        textes.add("享保 6 [ 1721 ]");
        textes.add("享保 17 - 天保 8 [ 1732 - 1837 ]");
        textes.add("文政 7 - 天保 12 [ 1824 - 1841 ]");

        textes.add("順帝三年七月七日 - 天保 8");

        textes.add("1921年11月9日");
        textes.add("西元1921年11月9日");
        textes.add("1921年11月9日 - 1941年03月19日");
        textes.add("西元1921年11月9日 - 西元1941年03月19日");
        textes.add("千九百二十一年十一月九日");
        textes.add("西元千九百二十一年十一月九日");
        textes.add("千九百二十一年十一月九 - 千九百四十一年三月十九日");
        textes.add("西元千九百二十一年十一月九 - 西元千九百四十一年三月十九日");

        textes.add("[191_]-1974");
        textes.add("[191?]-1974");
        textes.add("[1910's]-1974");
        textes.add("[191-]-1974");
        textes.add("[19__]-1974");
        textes.add("[19??]-1974");
        textes.add("[1900s]-1974");
        textes.add("[19--]-1974");

        textes.add("[1970]-1974");
        textes.add("[191_-1974]");
        textes.add("[191?-1974]");
        textes.add("[1910's-1974]");
        textes.add("[191--1974]");
        textes.add("[19__-1974]");
        textes.add("[19??-1974]");
        textes.add("[1900s-1974]");
        textes.add("[19---1974]");

        textes.add("21.3.02");
        textes.add("11.3.00");
        textes.add("11.3");

        textes.add("1901?");
        textes.add("24 Apr 1901?");
        textes.add("24 Apr 190?");

        textes.add("1900-2000,1833-1878");
        textes.add("1900-2000 ; 1833-1878");
        textes.add("1900-2000,1945"); 
        textes.add("1900-2000 ; Abt. 1945"); 
        textes.add("1888, 1890-1900 ");
        textes.add("1888 ; 1890-1900 ");
        textes.add("188? ; 1890-1900 , 1990's ");

        textes.add("10-10-10");
        textes.add("10-10-32");
        textes.add("2018 - jun-0");
        textes.add("17 Fev 1863 (vue 183)");

        textes.add("028 BC");
        textes.add("28 BC");
        textes.add("048 BC");
        textes.add("09.05.1817");
        textes.add("09.05.1817?");
        textes.add("09-05-1817?");
        textes.add("09/05/1817?");
        textes.add("09/05/1817 ?");
        textes.add("182-04-15");
        textes.add("1859sep5");
        textes.add("1998septiembre  07");
        textes.add("About 1825?");
        textes.add("abt 1700's");
        textes.add("from 1960s to 1980s");
        textes.add("late 1930s or possibly very early 1940s");
        textes.add("October 3rd 191*");

        textes.add("Sept. 5 1940?");

        textes.add("대략 10");
        textes.add("추정18");
        textes.add("約10年");
        textes.add("대략 1910");
        textes.add("추정1918");
        textes.add("約1910年");

        textes.add("8/17/18 12:31 am");
        textes.add("10/15/18 7:00 pm");
        textes.add("6/8/22");

        textes.add("19-25 March 1803");

        return textes;
    }
}
