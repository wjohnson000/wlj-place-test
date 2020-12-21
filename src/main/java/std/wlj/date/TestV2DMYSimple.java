/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.familysearch.standards.date.api.model.GenDateInterpResult;
import org.familysearch.standards.date.api.model.GenSimpleDate;
import org.familysearch.standards.date.common.DateUtil;

import std.wlj.date.v2.DMYGenSimpleDate;
import std.wlj.date.v2.DMYGenSimpleDateLookup;

/**
 * @author wjohnson000
 *
 */
public class TestV2DMYSimple {

    private static final Random random = new Random();

    private static final String[] MONTH_NAMES = {
        "Jan",
        "January",
        "Feb",
        "February",
        "Mar",
        "March",
        "Apr",
        "April",
        "May",
        "May",
        "Jun",
        "June",
        "Jul",
        "July",
        "Aug",
        "August",
        "Sep",
        "September",
        "Oct",
        "October",
        "Nov",
        "November",
        "Dec",
        "December",
//        "Juli",
//        "Oktober",
//        "Augustus",
//        "Februari",
//        "Maart",
//        "Mei",
//        "Februar",
//        "Junio",
//        "Mai",
//        "März",
//        "de octubre",
//        "septiembre",
//        "diciembre",
//        "enero",
//        "Marzo",
//        "február",
//        "Juni",
//        "Avril",
//        "febrero",
//        "Abril",
//        "Mayo",
    };

    public static void main(String... args) throws Exception {
        List<String> results = new ArrayList<>();

        List<String> textes = textesFromRaw(100_000);
        long time0 = System.nanoTime();
        for (String text : textes) {
            List<GenDateInterpResult> dates02 = new ArrayList<>();

//            try {
//                dates02 = DateUtil.interpDate(text, StdLocale.ENGLISH);
//            } catch (Exception e) {
//                System.out.println("  V2.ext: " + e.getMessage());
//            }
//
//            results.add("");
//            for (GenDateInterpResult date : dates02) {
//                System.out.println("  gx02: " + text + "|" + date.getDate().toGEDCOMX());
//                results.add(text + "|" + date.getDate().toGEDCOMX());
//            }

//            GenSimpleDate date01 = DMYGenSimpleDate.from(text);
            GenSimpleDate date02 = DMYGenSimpleDateLookup.from(text);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("  " + text);
//            System.out.println("    VA: " + ((date01 == null) ? "" : date01.toGEDCOMX()));
            System.out.println("    VB: " + ((date02 == null) ? "" : date02.toGEDCOMX()));
        }
        long time1 = System.nanoTime();
        System.out.println("\nTIME: " + (time1 - time0) / 1_000_000.0);
    }

    static List<String> textesFromRaw(int count) {
        List<String> textes = new ArrayList<>();

        for (int ndx=0;  ndx<count;  ndx++) {
            int yer = random.nextInt(1000) + 1100;
            int mon = random.nextInt(MONTH_NAMES.length);
            int day = random.nextInt(20) + 1;
            textes.add(day + "+" + MONTH_NAMES[mon] + "+" + yer);
        }

        return textes;
    }
}