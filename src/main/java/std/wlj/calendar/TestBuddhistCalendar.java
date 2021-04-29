/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.calendar;

import java.util.Calendar;
import java.util.GregorianCalendar;

import sun.util.BuddhistCalendar;

/**
 * @author wjohnson000
 *
 */
@SuppressWarnings("restriction")
public class TestBuddhistCalendar {

    public static void main(String...args) {
        Calendar thaiCal = new BuddhistCalendar();
        thaiCal.set(Calendar.YEAR, 2347);
        thaiCal.set(Calendar.MONTH, 1);
        thaiCal.set(Calendar.DAY_OF_MONTH, 29);
        System.out.println(thaiCal.toInstant());

        Calendar gregCal = GregorianCalendar.getInstance();
        gregCal.setTimeInMillis(thaiCal.getTimeInMillis());
        System.out.println(gregCal.toInstant());

        
    }
}
