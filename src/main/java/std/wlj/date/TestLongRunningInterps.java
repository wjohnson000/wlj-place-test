/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.date.common.DateUtil;
import org.familysearch.standards.date.api.model.DateResult;
import org.familysearch.standards.date.api.model.GenDateInterpResult;

import std.wlj.date.v1.DateV1Shim;

/**
 * Dec 04, 2018 -- based on the following SPLUNK query:
 *     index=production host=std-ws-date-prod* sourcetype=fs_tc_catalina status=200  path="/dates/interp" time>10 | table gedcomx matchType accept_language text
 * pull out the last two fields, pretty-ify the results, and run them here.
 * <p/>
 * 
 * Based on this first list, we need to treat a slash ("/") as a multiple-date separator.  Sigh ...
 * 
 * @author wjohnson000
 *
 */
public class TestLongRunningInterps {

    static final String[][] textes = {
//        { "en", "30+October+1849+/+31+October+1849" },
//        { "fi", "1+May+1756+/+5+January+1756" },
//        { "fr", "31+décembre+1917" },
//        { "fi", "11+May+1887+/+5+November+1887" },
//        { "en", "3.+November+1747" },
//        { "en-US", "9+June+1468+/+3+November+1469" },
//        { "ko", "5+June+1993+/+10+May+1933+/+12+May+1873+/+16+May+1813+/+18+May+1753+/+20+May+1693+/+23+May+1633+/+16+May+1573+/+20+May+1513+/+24+May+1453+/+26+May+1393+/+29+May+1333+/+4+May+1273+/+8+May+1213+/+11+May+1153+/+13+May+1093+/+17+May+1033+/+21+May+0973+/+24+May+0913+/+27+May+0853+/+30+May+0793+/+2+June+0733+/+7+May+0673+/+10+May+0613+/+14+May+0553+/+17+May+0493+/+20+May+0433+/+23+May+0373+/+27+May+0313+/+30+May+0253+/+3+June+0193+/+6+June+0133+/+10+June+0073+/+15+April+0013" },
//        { "en", "31+July+1912+or+13+July+1912" },
//        { "nb-NO", "15.+april+2016" },
//        { "en-US", "January+1900+/+February+1900" },
//        { "en-US", "5.+Februar+1883" },
//        { "de", "1829" },
        { "en-US", "14+Dec+0168+/+17+Dec+0108+/+21+Nov+0106" },
        { "en-US", "14+Dec+0168+/+17+Dec+0108+/+21+Nov+0106 BC" },
//        { "en-US", "16+December+1968+/+20+November+1908+/+22+November+1848+/+24+November+1788+/+28+November+1728+/+30+November+1668+/+4+December+1608+/+26+November+1548+/+30+November+1488+/+3+December+1428+/+7+December+1368+/+10+November+1308+/+13+November+1248+/+17+November+1188+/+21+November+1128+/+24+November+1068+/+27+November+1008+/+30+November+0948+/+4+December+0888+/+7+December+0828+/+10+December+0768+/+13+December+0708+/+17+November+0648+/+21+November+0588+/+24+November+0528+/+27+November+0468+/+30+November+0408+/+4+December+0348+/+7+December+0288+/+11+December+0228+/+14+December+0168+/+17+December+0108+/+21+November+0048+/+13+December+0012+BC" },
//        { "en-US", "16+December+1968+/+20+November+1908+/+22+November+1848+/+24+November+1788+/+28+November+1728+/+30+November+1668+/+4+December+1608+/+26+November+1548+/+30+November+1488+/+3+December+1428+/+7+December+1368+/+10+November+1308+/+13+November+1248+/+17+November+1188+/+21+November+1128+/+24+November+1068+/+27+November+1008+/+30+November+0948+/+4+December+0888+/+7+December+0828+/+10+December+0768+/+13+December+0708+/+17+November+0648+/+21+November+0588+/+24+November+0528+/+27+November+0468+/+30+November+0408+/+4+December+0348+/+7+December+0288+/+11+December+0228+/+14+December+0168+/+17+December+0108+/+21+November+0048+/+13+December+0012" },
//        { "ja", "1853年7月13日" },
//        { "nb", "from+1839+to+1941" },
//        { "en-US", "10+March+1650" },
//        { "en", "April+1882+/+June+1882" },
//        { "ko", "16+December+1968+/+20+November+1908+/+22+November+1848+/+24+November+1788+/+28+November+1728+/+30+November+1668+/+4+December+1608+/+26+November+1548+/+30+November+1488+/+3+December+1428+/+7+December+1368+/+10+November+1308+/+13+November+1248+/+17+November+1188+/+21+November+1128+/+24+November+1068+/+27+November+1008+/+30+November+0948+/+4+December+0888+/+7+December+0828+/+10+December+0768+/+13+December+0708+/+17+November+0648+/+21+November+0588+/+24+November+0528+/+27+November+0468+/+30+November+0408+/+4+December+0348+/+7+December+0288+/+11+December+0228+/+14+December+0168+/+17+December+0108+/+21+November+0048+/+13+December+0012+BC" },
//        { "ko", "5+June+1993+/+10+May+1933+/+12+May+1873+/+16+May+1813+/+18+May+1753+/+20+May+1693+/+23+May+1633+/+16+May+1573+/+20+May+1513+/+24+May+1453+/+26+May+1393+/+29+May+1333+/+4+May+1273+/+8+May+1213+/+11+May+1153+/+13+May+1093+/+17+May+1033+/+21+May+0973+/+24+May+0913+/+27+May+0853+/+30+May+0793+/+2+June+0733+/+7+May+0673+/+10+May+0613+/+14+May+0553+/+17+May+0493+/+20+May+0433+/+23+May+0373+/+27+May+0313+/+30+May+0253+/+3+June+0193+/+6+June+0133+/+10+June+0073+/+15+April+0013" },
//        { "de-DE", "2+June+1660+/+6+February+1660" },
//        { "no", "3+December+1900" },
//        { "no", "27+March+2005" },
//        { "fi", "15+June+1773" },
//        { "ko", "5+June+1993+/+10+May+1933+/+12+May+1873+/+16+May+1813+/+18+May+1753+/+20+May+1693+/+23+May+1633+/+16+May+1573+/+20+May+1513+/+24+May+1453+/+26+May+1393+/+29+May+1333+/+4+May+1273+/+8+May+1213+/+11+May+1153+/+13+May+1093+/+17+May+1033+/+21+May+0973+/+24+May+0913+/+27+May+0853+/+30+May+0793+/+2+June+0733+/+7+May+0673+/+10+May+0613+/+14+May+0553+/+17+May+0493+/+20+May+0433+/+23+May+0373+/+27+May+0313+/+30+May+0253+/+3+June+0193+/+6+June+0133+/+10+June+0073+/+15+April+0013" },
//        { "ko", "병술" },
//        { "en", "October 5", " 19" },
//        { "fr", "13+décembre+1844" },
//        { "nl-NL", "17+December+1652" },
//        { "de", "25.+Februar+1823" },
//        { "en-US", "27+janeiro+1993" },
//        { "en-US", "3+August+1902+/+8+March+1902" },
//        { "en", "Bef+1648" },
//        { "en-US", "1+agosto+1941+/+8+janeiro+1941" },
//        { "pt", "8+October+1791" },
//        { "fi", "10+January+1861" },
//        { "nb", "26+September+1792" },
//        { "ko", "16+December+1968+/+20+November+1908+/+22+November+1848+/+24+November+1788+/+28+November+1728+/+30+November+1668+/+4+December+1608+/+26+November+1548+/+30+November+1488+/+3+December+1428+/+7+December+1368+/+10+November+1308+/+13+November+1248+/+17+November+1188+/+21+November+1128+/+24+November+1068+/+27+November+1008+/+30+November+0948+/+4+December+0888+/+7+December+0828+/+10+December+0768+/+13+December+0708+/+17+November+0648+/+21+November+0588+/+24+November+0528+/+27+November+0468+/+30+November+0408+/+4+December+0348+/+7+December+0288+/+11+December+0228+/+14+December+0168+/+17+December+0108+/+21+November+0048+/+13+December+0012+BC" },
//        { "nb", "10+February+1888" },
//        { "nb", "10+July+1933" },
//        { "en", "8+January+1920+/+9+January+1920" },
//        { "nb", "5.+september+2015" },
//        { "ko", "5+June+1993+/+10+May+1933+/+12+May+1873+/+16+May+1813+/+18+May+1753+/+20+May+1693+/+23+May+1633+/+16+May+1573+/+20+May+1513+/+24+May+1453+/+26+May+1393+/+29+May+1333+/+4+May+1273+/+8+May+1213+/+11+May+1153+/+13+May+1093+/+17+May+1033+/+21+May+0973+/+24+May+0913+/+27+May+0853+/+30+May+0793+/+2+June+0733+/+7+May+0673+/+10+May+0613+/+14+May+0553+/+17+May+0493+/+20+May+0433+/+23+May+0373+/+27+May+0313+/+30+May+0253+/+3+June+0193+/+6+June+0133+/+10+June+0073+/+15+April+0013" },
//        { "und", "from 1822 to 1863" },
//        { "fr", "4+janvier+1733" },
//        { "en-US", "January+1909+/+March+1909" },
//        { "ja", "1927年9月20日" },
//        { "en", "January+1857+/+March+1857" },
//        { "fi", "30+September+1874" },
//        { "fi", "8+June+1875" },
//        { "ko", "2017년+10월+29일+(일)" },
//        { "ja", "1878年9月" },
//        { "ko", "을미" },
//        { "en-US", "7+August+1768+/+19+August+1768" },
//        { "nb", "6+May+1907" },
//        { "en", "11+January+1936+/+1+November+1936" },
//        { "ko", "5+June+1993+/+10+May+1933+/+12+May+1873+/+16+May+1813+/+18+May+1753+/+20+May+1693+/+23+May+1633+/+16+May+1573+/+20+May+1513+/+24+May+1453+/+26+May+1393+/+29+May+1333+/+4+May+1273+/+8+May+1213+/+11+May+1153+/+13+May+1093+/+17+May+1033+/+21+May+0973+/+24+May+0913+/+27+May+0853+/+30+May+0793+/+2+June+0733+/+7+May+0673+/+10+May+0613+/+14+May+0553+/+17+May+0493+/+20+May+0433+/+23+May+0373+/+27+May+0313+/+30+May+0253+/+3+June+0193+/+6+June+0133+/+10+June+0073+/+15+April+0013" },
//        { "ko", "5+June+1993+/+10+May+1933+/+12+May+1873+/+16+May+1813+/+18+May+1753+/+20+May+1693+/+23+May+1633+/+16+May+1573+/+20+May+1513+/+24+May+1453+/+26+May+1393+/+29+May+1333+/+4+May+1273+/+8+May+1213+/+11+May+1153+/+13+May+1093+/+17+May+1033+/+21+May+0973+/+24+May+0913+/+27+May+0853+/+30+May+0793+/+2+June+0733+/+7+May+0673+/+10+May+0613+/+14+May+0553+/+17+May+0493+/+20+May+0433+/+23+May+0373+/+27+May+0313+/+30+May+0253+/+3+June+0193+/+6+June+0133+/+10+June+0073+/+15+April+0013" },
//        { "en-US", "5+June+1993+/+10+May+1933+/+12+May+1873+/+16+May+1813+/+18+May+1753+/+20+May+1693+/+23+May+1633+/+16+May+1573+/+20+May+1513+/+24+May+1453+/+26+May+1393+/+29+May+1333+/+4+May+1273+/+8+May+1213+/+11+May+1153+/+13+May+1093+/+17+May+1033+/+21+May+0973+/+24+May+0913+/+27+May+0853+/+30+May+0793+/+2+June+0733+/+7+May+0673+/+10+May+0613+/+14+May+0553+/+17+May+0493+/+20+May+0433+/+23+May+0373+/+27+May+0313+/+30+May+0253+/+3+June+0193+/+6+June+0133+/+10+June+0073+/+15+April+0013" },
//        { "ko", "5+June+1993+/+10+May+1933+/+12+May+1873+/+16+May+1813+/+18+May+1753+/+20+May+1693+/+23+May+1633+/+16+May+1573+/+20+May+1513+/+24+May+1453+/+26+May+1393+/+29+May+1333+/+4+May+1273+/+8+May+1213+/+11+May+1153+/+13+May+1093+/+17+May+1033+/+21+May+0973+/+24+May+0913+/+27+May+0853+/+30+May+0793+/+2+June+0733+/+7+May+0673+/+10+May+0613+/+14+May+0553+/+17+May+0493+/+20+May+0433+/+23+May+0373+/+27+May+0313+/+30+May+0253+/+3+June+0193+/+6+June+0133+/+10+June+0073+/+15+April+0013" },
//        { "ko", "건양병신" },
//        { "fr", "15+octobre+1914" },
//        { "nb", "6.+januar+2018" },
//        { "ja", "29+January+1924" },
    };

    public static void main(String... args) throws Exception {
        List<String> results = new ArrayList<>();

        long time0 = System.nanoTime();
        for (String[] text : textes) {
            List<GenDateInterpResult> dates01 = new ArrayList<>();
            DateResult                dates02 = new DateResult();

            System.out.println("\n" + text[1]);

            try {
                dates01 = DateV1Shim.interpDate(text[1]);
            } catch (Exception e) {
                System.out.println("  V2.ext: " + e.getMessage());
            }

            try {
                dates02 = DateUtil.interpDate(text[1], text[0], null, null, null);
            } catch (Exception e) {
                System.out.println("  V2.ext: " + e.getMessage());
            }

            results.add("");
            results.add(text[1]);
            for (GenDateInterpResult date : dates01) {
                System.out.println("  gx01: " + text[1] + "|" + date.getDate().toGEDCOMX());
                results.add("Date 1.0|" + date.getDate().toGEDCOMX());
            }
            for (GenDateInterpResult date : dates02.getDates()) {
                System.out.println("  gx02: " + text[1] + "|" + date.getDate().toGEDCOMX());
                results.add("Date 2.0|" + date.getDate().toGEDCOMX());
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
}