/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import org.familysearch.standards.date.calendar.CalendarUtil;
import org.familysearch.standards.date.common.SharedUtil;

/**
 * @author wjohnson000
 *
 */
public class TestCMIS6 {

    public static void main(String...args) {
        for (int i=0;  i<1_000_000;  i++) {
            String dateStr1 = "00000" + i;
            String dateStr2 = dateStr1.substring(dateStr1.length()-6);
            processDate(dateStr2);
        }
    }

    static void processDate(String date) {
        int month01 = SharedUtil.parseInt(date.substring(0, 2));  // For MMYYYY
        int year01  = SharedUtil.parseInt(date.substring(2, 6));
        int year02  = SharedUtil.parseInt(date.substring(0, 4));  // For YYYYMM
        int month02 = SharedUtil.parseInt(date.substring(4, 6));

        if (CalendarUtil.isValidYear(year01)  &&  CalendarUtil.isValidYear(year02)) {
            if (CalendarUtil.isValidMonth(month01, year01)  &&  CalendarUtil.isValidMonth(month02, year02)) {
                System.out.println("DD: " + date + " --> DUPLICATE  YEAR and MONTH");
            } else if (CalendarUtil.isValidMonth(month01, year01)  ||  month01 == 0) {
//                System.out.println("DD: " + date + " --> year01=" + year01 + ";  month01=" + month01);
            } else if (CalendarUtil.isValidMonth(month01, year02)  ||  month02 == 0) {
//                System.out.println("DD: " + date + " --> year02=" + year02 + ";  month02=" + month02);
            }
        } else if (CalendarUtil.isValidYear(year01)  &&  month01 == 0) {
//            System.out.println("DD: " + date + " --> year01=" + year01);
        } else if (CalendarUtil.isValidYear(year02)  &&  month01 == 0) {
//            System.out.println("DD: " + date + " --> year02=" + year02);
        } else {
//            System.out.println("DD: " + date + " --> none!!");
        }
    }
}
