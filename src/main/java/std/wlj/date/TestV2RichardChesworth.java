/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.model.DateResult;
import org.familysearch.standards.date.model.GenDateInterpResult;

import std.wlj.date.v1.DateV1Shim;

/**
 * @author wjohnson000
 *
 */
public class TestV2RichardChesworth {

    static final String[][] textVsExpected = {
        // testYear
        { "1968", "+1968" },
        { "1582", "+1582" },
        { " 道光14", "+1834" },
        { "道光甲午", "+1834" },
        { "甲午", "+2014" },

        // testYearRange
        { "1966-1970", "+1966/+1970" },
        { "1877-1883", "+1877/+1883" },
        { "1847/1848", "+1847/+1848" },
        { "1847/1849", "+1847/+1849" },
        { "от 1917 к 1918", "+1917/+1918" },
        { "06 JUN 1900 光緒26", "+1900-06-06" },

        // testBeforeDate
        { "before 1582", "/+1582" },
        { "Before 1900", "/+1900" },
        { "Bef 1900-1905", "/+1905" },

        // testAfterDate
        { "after 1582", "+1582/" },
        { "After 1900", "+1900/" },
        { "AfT 1900-1901", "+1900/" },    // no modifier on a range

        // testMonthSpecific
        { "1968 Nov", "+1968-11" },

        // testLeapYearMonth
        { "Feb 1966", "+1966-02" },    // handle leap years
        { "Feb 1968", "+1968-02" },

        // testDaySpecific
        { "13Nov1968", "+1968-11-13" },

        // testDaySpecific2
        { "13 11 1968", "+1968-11-13" },

        // testWithPlusSign
        { "+1968", "+1968" },

        // testWithNegativeSign
        { "-0196", "-0196" },

        // testBCDate
        { "196 BC", "-0195" },
        { "Abt 50 BC", "A-0049" },
        { "Abt. 50Bc", "A-0049" },
        { "0235 AM.  FOURTH  YEAR  of  the  SIXTH  WEEK  of  the  FIFTH  JUBILEE.  SAT  02  JAN  3765  BC  (PRATT)", "-3764-01-02" },

        // testNumberFormatException
        { "-20000000000000bc", "" },   // large number is bogus - catch NumberFormatException

        // testAvoidDuplicatedYearsInRange
        { "0130  AM.  (4Y.5W.3JB.  JB4).  after Abel's murder, when Adam was 130 years old, according to Genesis [0130  AM. (PRATT)]", "" },

        // testAboutYear
        { "abt 1968", "A+1968" },
        { "1866 ABT", "A+1866" },

        // testAboutMonth
        // this actually demonstrates a problem with DateInterpretation.  It seems to be just adding the number
        // of days in the month in both directions rather than going to the beginning of the previous and end
        // of the subsequent month.  When that bug is fixed, this result should change.
        { "abt Feb 1968", "A+1968-02" },
        { "est Feb 1968", "A+1968-02" },

        // testAboutDay
        { "abt Aug 2, 1856", "A+1856-08-02" },

        // testDualDay
        { "Feb 17/19, 1968", "+1968-02-17/+1968-02-19" },
        // approximate dates not handled on disjoint dates
        { "Approx Feb 17/19, 1968", "A+1968-02-17/+1968-02-19" },

        // testEarlyYear
        { "0003", "+0003" },

        // testFullDateRange
        // various range combinations that check all combinations
        { "01/01/1968-12/25/1968", "+1968-01-01/+1968-12-25" },
        { "01/01/1968-02/18/1968", "+1968-01-01/+1968-02-18" },
        { "Feb 01,1968-Feb 18,1968", "+1968-02-01/+1968-02-18" },
        { "01/01/1968-12/31/1970", "+1968-01-01/+1970-12-31" },
        // approximate dates not handled on ranges
        { "About 01/01/1968-12/31/1970", "A+1968-01-01/+1970-12-31" },

        { "12/31/1968-12/31/1970", "+1968-12-31/+1970-12-31" },
        { "mar 1968-mar 1969", "+1968-03/+1969-03" },
        { "mar 1968-may 1968", "+1968-03/+1968-05" },
        { "28 jan 1968 to 31 jan 1968", "+1968-01-28/+1968-01-31" },
        { "from 1 jan 1968 to 29 jan 1968", "+1968-01-01/+1968-01-29" },

        // testZeroDate
        { "0000", "" },

        // testAmbiguousDate
        { "03/07/1999", "+1999-03-07/+1999-07-03" },

        // testAmbiguousYear
        { "31 - 1 - 60", "" },

        // testExtraPunctuation
        { "1893<1893>", "+1893" },
        { "((1755)", "+1755" },
        { "(> 1868)", "+1868/" },
        { ">=1868", "+1868/" },
        { "-- ___ 1718", "+1718" },
        { "02 Mar 1933>", "+1933-03-02" },
        { "<02 Mar 1933", "/+1933-03-02" },
        { "<02 Mar 1933>", "+1933-03-02" },
        { "02 Dec, 1924`", "+1924-12-02" },
        { "1`0 April 1924", "+1924-04" },

        // testEmbeddedCharacter
        { "1l0Jul1558", "+1558-07" },

        // testDisjunctiveDatePlusModifier
        { "<6 MEI  1982", "+1982-04-06/+1982-05-06" },    // this comes out disjunctive right now because it is improperly standardized
        { "6 MEI  1982>", "+1982-04-06/+1982-05-06" },

        // testCMISFormat
        { "01112002", "+2002-11-01" },
        { "01001955", "+1955" },
        { "001600", "+1600" },
        { "012007", "+2007-01" },
        { "062454", "+2454-06" },

        // testEmbeddedHash
        { "9 Jul 1955, #135826", "+1955-07-09" },
        { "Find a Grave #10031910", "" },
        { "Film #1123", "" },

        // testStuffAroundDate
        { "Lic. 4Sep1957 # 19576 Weber Co.", "+1957-09-04" },
        { "18 Sep 1954 bur. no.B3473", "+1954-09-18" },
        { "2 Sep 1749 Abt 1001749", "1749-09-02" },

        // testStringOfDates
        { "1 FEB 1758BET 1 FEB 1758 AND 17 NOV 1761", "+1758-02-01/+1761-11-17" },

        // testFullWidthCharacters
        // these are unicode full-width characters
        { "１９４２０８１４", "+1942-08-14" },
        { "明和８年（１７７１年）１０月１７日", "+1771-10-17" },

        // testBadDay
        { "29 February 1893", "+1893-02" },

        // shortSentence
        { "He lived at 1909 Ramona Ave after 23 Aug 1976", "+1976-08-23" },

        // testEmbeddedChinese
        { "01 JAN 1206 開禧2", "+1206-01-01" },
        { "Abt 03 Sep 1662", "A+1662-09-03" },
        { "Abt 03 Sep 1662 康熙壬寅", "A+1662-09-03" },

        // testEstimatedAge
        { "ae: 42years, 10months, 0days", "" },

        // testDualOrRangeOrOdd
        { "1790-92", "+1790/+1792" },
        { "1665/66", "+1665/+1666" },
        { "Jul 1699/1702", "+1699-07/+1702-07" },
        { "Sep 1590-91", "+1590-09/+1591-09" },
        { "1517/18 Isle of Wight Co", "+1517/+1518" },
        { "24 JAN 1559/60", "+1560-01-24" },             //from dual to gregorian - gedx formal date is gregorian
        { "Feb1747/1748", "+1748-02" },                  //from dual to gregorian - gedx formal date is gregorian
        { "15MAR1189/90", "+1190-03-15" },   //from dual to gregorian - gedx formal date is gregorian
        { "10 MAR1451/52", "+1452-03-10" },  //from dual to gregorian - gedx formal date is gregorian
    };

    public static void main(String... args) throws Exception {
        List<String> results = new ArrayList<>();

        for (String text[] : textVsExpected) {
            System.out.println("\n" + Arrays.toString(text));
            results.add("");

            try {
                List<GenDateInterpResult> dates01 = DateV1Shim.interpDate(text[0]);
                for (GenDateInterpResult date : dates01) {
                    System.out.println("  gx01: " + text[0] + "|" + date.getDate().toGEDCOMX() + "|" + text[1]);
                    results.add(text[0] + "|Date 1.0|" + date.getDate().toGEDCOMX() + "|" + text[1] + "|" + date.getDate().toGEDCOMX().equalsIgnoreCase(text[1]));
                }
            } catch (Exception e) {
                System.out.println("  V1.ext: " + e.getMessage());
            }

            try {
                DateResult dates02 = DateUtil.interpDate(text[0], StdLocale.UNDETERMINED, null, null, null);
                for (GenDateInterpResult date : dates02.getDates()) {
                    System.out.println("  gx02: " + text[0] + "|" + date.getDate().toGEDCOMX() + "|" + text[1]);
                    results.add(text[0] + "|Date 2.0|" + date.getDate().toGEDCOMX() + "|" + text[1] + "|" + date.getDate().toGEDCOMX().equalsIgnoreCase(text[1]));
                }
            } catch (Exception e) {
                System.out.println("  V2.ext: " + e.getMessage());
            }
        }

        System.out.println();
        System.out.println("========================================================================================================================");
        System.out.println("========================================================================================================================");
        System.out.println();
        results.forEach(System.out::println);
    }

}