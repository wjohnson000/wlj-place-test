/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.TimeZone;

/**
 * Eight ways to convert a JULIAN-DAY number to a Gregorian Calendar (hopefully proleptic!!)
 * 
 * @author wjohnson000
 */
public class AstroToGregorian {

    static int julianOffset = 2_440_589;  // Julian day number for Jan 01, 1970
    static int julianStart  = 2_299_161;  // Julian day number for Oct 15, 1582

    public static void main(String... args) {
        int julianDayNumber = 2208874;
        julianToGregorianBestA(julianDayNumber);
        julianToGregorianV2(julianDayNumber);
        julianToGregorianBestB(julianDayNumber);
        julianToGregorianV1(julianDayNumber);

//        julianToGregorianI(julianDayNumber);
//        julianToGregorianII(julianDayNumber);
//        julianToGregorianIII(julianDayNumber);
//        julianToGregorianIV(julianDayNumber);
//        julianToGregorianV(julianDayNumber);
//        julianToGregorianVI(julianDayNumber);
//        julianToGregorianVII(julianDayNumber);
//        julianToGregorianVIII(julianDayNumber);
    }

    /**
     * @param julianDate
     */
    static void julianToGregorianBestA(int julianDate) {
        LocalDate date = LocalDate.MIN.with(java.time.temporal.JulianFields.JULIAN_DAY , julianDate);
        System.out.println("AAA>> YR=" + date.getYear() + ";  MO=" + date.getMonthValue() + ";  DY=" + date.getDayOfMonth() + " --> " + date);
    }

    static void julianToGregorianV2(int julianDate) {
        int d, m, y;
        int c, r, x;

        // Offset the jday by 1753164 days (4800 years) during calculation.
        // the calculations use March as the first month in the year
        x = 4 * (julianDate - 1721119 + 1753164) - 1;
        c = x / 146097;// # of centuries
        r = x % 146097;// the remainder from the division
        x = (r | 3) / 1461;// 1461 = # of days in 4 years
        r = (r | 3) % 1461;// the remainder from the division
        y = 100 * (c - 48) + x;// remove the 4800 year offset
        x = 5 * (r / 4 + 1) - 3;
        m = x / 153;
        d = (x % 153) / 5 + 1;
        // adjust beginning of the year from March to January
        if (m < 10) {
          m += 3;
        }
        else {
          m -= 9;
          y++;
        }
        if (y < 1) {
          y--;// convert year to external numbering
        }

        System.out.println("VG1>> YR=" + y + ";  MO=" + m + ";  DY=" + d);
    }

    static void julianToGregorianV1(int julianDate) {
        int d, m, y;
        int c, r, x;

        // these calculations use March as the first month in the year
        x = 4 * (julianDate - 31 - 28) - 1;
        c = x / 146100;// 146100 = # of days in 4 centuries
        r = x % 146100;
        x = (r | 3) / 1461;// 1461 = # of days in 4 years
        r = (r | 3) % 1461;
        y = 100 * c + x - 4712;// = the Julian year
        x = 5 * (r / 4 + 1) - 3;
        m = x / 153;
        r = x % 153;
        d = r / 5 + 1;

        // adjust beginning of the year from March to January
        if (m < 10) {
          m += 3;
        }
        else {
          m -= 9;
          y++;
        }
        if (y < 1) {
          y--;// convert year to external numbering
        }

        System.out.println("VJ1>> YR=" + y + ";  MO=" + m + ";  DY=" + d);
    }

    /**
     * see https://www.mathworks.com/matlabcentral/answers/1303-convert-julian-date-to-calendar-days
     */
    static void julianToGregorianBestB(int julianDate) {
        int jd = julianDate;
        int year, month, day;
        int a, b, c, d, e;
        double z, fday;

        z = jd + .5;
        fday = jd + .5 - z;

        if (fday < 0) {
           fday = fday + 1;
           z = z - 1;
        }

        if (z < julianStart) {
           a = (int)z;
        } else {
           double alpha = Math.floor((z - 1867216.25) / 36524.25);
           a = (int)(z + 1 + alpha - Math.floor(alpha / 4));
        }

        b = a + 1524;
        c = (int)((b - 122.1) / 365.25);
        d = (int)(365.25 * c);
        e = (int)((b - d) / 30.6001);
        day = b - d - (int)((30.6001 * e) + fday);

        if (e < 14) {
           month = e - 1;
        } else { 
           month = e - 13;
        }
        
        if (month > 2) {
           year = c - 4716;
        } else {
           year = c - 4715;
        }

        day = (int)Math.floor(day);

        System.out.println("BBB>> YR=" + year + ";  MO=" + month + ";  DY=" + day);
    }

    /**
     * See http://aa.usno.navy.mil/faq/docs/JD_Formula.php
     * See https://www.hermetic.ch/cal_stud/jdn.htm
     * See https://www.aavso.org/jd-calculator (2208874 --> YR=1335  MO=8  DY=4)
     */
    static void julianToGregorianI(int julianDate) {
        int YEAR;
        int MONTH;
        int DAY;
        int I, J, K, L, N;

        L = julianDate + 68569;
        N = 4*L/146097;
        L = L-(146097*N+3)/4;
        I = 4000*(L+1)/1461001;
        L = L-1461*I/4+31;
        J = 80*L/2447;
        K = L-2447*J/80;
        L = J/11;
        J = J+2-12*L;
        I = 100*(N-49)+I+L;

        YEAR = I;
        MONTH = J;
        DAY = K;

        System.out.println("ONE>> YR=" + YEAR + ";  MO=" + MONTH + ";  DY=" + DAY);
    }

    /**
     * @param julianDate
     */
    @SuppressWarnings("deprecation")
    static void julianToGregorianII(int julianDate) {
        try {
            Date date = new SimpleDateFormat("D").parse(String.valueOf(julianDate - julianOffset));
            System.out.println("TWO>> YR=" + date.getYear() + ";  MO=" + date.getMonth() + ";  DY=" + date.getDate() + " --> " + date);
        } catch (ParseException e) {
            System.out.println("OOPS!!" + e.getMessage());
        }
    }

    /**
     * @param julianDate
     */
    static void julianToGregorianIII(int julianDate) {
        LocalDate date = LocalDate.MIN.with(java.time.temporal.JulianFields.JULIAN_DAY , julianDate);
        System.out.println("TRE>> YR=" + date.getYear() + ";  MO=" + date.getMonthValue() + ";  DY=" + date.getDayOfMonth() + " --> " + date);
    }

    /**
     * see https://it.toolbox.com/question/java-function-to-convert-julian-date-to-calendar-date-030608
     */
    static void julianToGregorianIV(int julianDate) {
        int aaaa = 0; 
        int mm = 0; 
        int jj = 0; 
        int A = 0; 

        double w = julianDate + 0.5; 
        int Z = (int)w; 
        double F = w - Z; 
        if (Z < 2299161){ 
            A = Z; 
        } else { 
            int alpha = (int)((Z - 1867216.25) / 36524.25); 
            A = Z + 1 + alpha - (int)(alpha / 4.0); 
        } 
        int B = A + 1524; 
        int C = (int)((B - 122.1) / 365.25); 
        int D = (int)(365.25 * C); 
        int E = (int)((B - D) / 30.6001); 

        // jour du mois en d?cimales 
        double jjd = B - D - (int)(30.6001 * E) + F; 
        jj = (int)jjd; 

        // Calcul mois 
        if (E < 13.5){ 
            mm = E - 1; 
        } else { 
            mm = E - 13; 
        } 
        // Calcul annee 
        if (mm > 2.5){ 
            aaaa = C - 4716; 
        } else { 
            aaaa = C - 4715; 
        }

        System.out.println("FOR>> YR=" + aaaa + ";  MO=" + mm + ";  DY=" + jj);
    }

    /**
     * see https://www.mathworks.com/matlabcentral/answers/1303-convert-julian-date-to-calendar-days
     */
    static void julianToGregorianV(int julianDate) {
        int jd = julianDate;
        int year, month, day;
        int hr, min, sec;
        int a, b, c, d, e;
        double alpha;
        double z, fday;

        z = jd + .5;
        fday = jd + .5 - z;

        if (fday < 0) {
           fday = fday + 1;
           z = z - 1;
        }

        if (z < 2299161) {
           a = (int)z;
        } else {
           alpha = Math.floor((z - 1867216.25) / 36524.25);
           a = (int)(z + 1 + alpha - Math.floor(alpha / 4));
        }

        b = a + 1524;
        c = (int)((b - 122.1) / 365.25);
        d = (int)(365.25 * c);
        e = (int)((b - d) / 30.6001);
        day = b - d - (int)((30.6001 * e) + fday);

        if (e < 14) {
           month = e - 1;
        } else { 
           month = e - 13;
        }
        
        if (month > 2) {
           year = c - 4716;
        } else {
           year = c - 4715;
        }

        hr = (int)Math.abs(day-Math.floor(day))*24;
        min = (int)Math.abs(hr-Math.floor(hr))*60;
        sec = (int)Math.abs(min-Math.floor(min))*60;
        day = (int)Math.floor(day);
        hr = (int)Math.floor(hr);
        min = (int)Math.floor(min);

        System.out.println("FIV>> YR=" + year + ";  MO=" + month + ";  DY=" + day);
    }

    /**
     * See http://www.rgagnon.com/javadetails/java-0506.html
     *   -- Numerical Recipes in C, 2nd ed., Cambridge University Press 1992
     * @param julianDate
     */
    static void julianToGregorianVI(int julianDate) {
         int JGREG= 15 + 31*(10+12*1582);
        double HALFSECOND = 0.5;

        int jalpha,ja,jb,jc,jd,je,year,month,day;
        double julian = julianDate + HALFSECOND / 86400.0;
        ja = (int) julian;
        if (ja>= JGREG) {
          jalpha = (int) (((ja - 1867216) - 0.25) / 36524.25);
          ja = ja + 1 + jalpha - jalpha / 4;
        }

        jb = ja + 1524;
        jc = (int) (6680.0 + ((jb - 2439870) - 122.1) / 365.25);
        jd = 365 * jc + jc / 4;
        je = (int) ((jb - jd) / 30.6001);
        day = jb - jd - (int) (30.6001 * je);
        month = je - 1;
        if (month > 12) month = month - 12;
        year = jc - 4715;
        if (month > 2) year--;
        if (year <= 0) year--;

        System.out.println("SIX>> YR=" + year + ";  MO=" + month + ";  DY=" + day);
    }

    /**
     * See DateConverter, Dirk Lehmann
     *   -- https://sourceforge.net/p/observation/bugs/_discuss/thread/ca079afa/b42e/attachment/DateConverter.java
     * @param julianDate
     */
    static void julianToGregorianVII(int julianDate) {
        double jDate = julianDate + 0.5;
        TimeZone zone = TimeZone.getTimeZone("GMT");
        int onlyDays = (int) Math.round(jDate);
        double onlyMinutes = jDate - onlyDays;
        double hours = 24 * onlyMinutes;
        int hour = (int) (Math.round(hours));
        int minute = (int) ((hours - hour) * 60);
        // int sec = (int)Math.round((hours * 3600) - ((minute * 60) + (hour * 3600)));
        int sec = (int) ((((hours - hour) * 60) - minute) * 60);
        double leapYear100 = (int) ((onlyDays - 1867216.25) / 36524.25);
        double daysLeapYear = onlyDays + 1 + leapYear100 - (int) (leapYear100 / 4);
        if (onlyDays < 2299161) {
          daysLeapYear = onlyDays;
        }
        double completeLeapDays = daysLeapYear + 1524;
        double completeYear = (int) ((completeLeapDays - 122.1) / 365.25);
        double completeDays = (int) (365.25 * completeYear);
        double completeMonths = (int) ((completeLeapDays - completeDays) / 30.6001);
        int day = (int) (completeLeapDays - completeDays - (int) (30.6001 * completeMonths) + onlyMinutes);
        int month = 0;
        if (completeMonths < 14) {
          month = (int) completeMonths - 1;
        } else {
          month = (int) completeMonths - 13;
        }
        int year = 0;
        if (month > 2) {
          year = (int) completeYear - 4716; // only AD years
        } else {
          year = (int) completeYear - 4715; // only AD years
        }
        java.util.Calendar gregorianDate = java.util.Calendar.getInstance(zone);
        gregorianDate.set(year, month - 1, day + 1);
        System.out.println("SEP>> YR=" + year + ";  MO=" + month + ";  DY=" + day + " --> " + new Date(gregorianDate.getTimeInMillis()));
    }

    /**
     * See http://www.ssec.wisc.edu/mcidas/software/v/javadoc/1.3/src-html/edu/wisc/ssec/mcidasv/data/adde/sgp4/Time.html
     * https://www.programcreek.com/java-api-examples/index.php?source_dir=WorldWind_Applet-master/src/Utilities/Time.java
     */

    static void julianToGregorianVIII(int julianDate) {
        Double jd2 = new Double(julianDate) + 0.5;
        long I = jd2.longValue();
        long A = 0;
        long B = 0;

        if (I > 2299160) {
            Double a1 = new Double(((double) I - 1867216.25) / 36524.25);
            A = a1.longValue();
            Double a3 = new Double((double) A / 4.0);
            B = I + 1 + A - a3.longValue();
        } else {
            B = I;
        }

        double C = (double) B + 1524;
        Double d1 = new Double((C - 122.1) / 365.25);
        long D = d1.longValue();
        Double e1 = new Double(365.25 * (double) D);
        long E = e1.longValue();
        Double g1 = new Double((double) (C - E) / 30.6001);
        long G = g1.longValue();
        Double h = new Double((double) G * 30.6001);
        long da = (long) C - E - h.longValue();

        Integer date = new Integer((int) da); // DATE

        Integer month;
        Integer year;

        if (G < 14L) {
            month = new Integer((int) (G - 2L));
        } else {
            month = new Integer((int) (G - 14L));
        }

        if (month.intValue() > 1) {
            year = new Integer((int) (D - 4716L));
        } else {
            year = new Integer((int) (D - 4715L));
        }

        // TODO -- Month is zero-based
        month++;
        System.out.println("OCT>> YR=" + year + ";  MO=" + month + ";  DY=" + date);
    }
}
