/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

/**
 * This class describes a lunar year: its beginning (jday), the length of each moon, and an optional
 * intercalary moon number.  Note that if leapMoon is not zero, there are 13 moons in the year.
 * 
 * @author Pete Blake
 * @author Wayne Johnson, May, 2018 (reworked)
 */
public class LunarYearDescription {

    private int jday;      // jday of the first day of the lunar year (must be an int, not a class)
    private int leapMoon;  // # of the leap moon, or 0 if this year does not have a leap moon
    private int moonBits;  // lsb (least-significant-bit) is 1st moon; bit is on if moon has 30 days, off if 29 days

    LunarYearDescription(int day, int leap, int bits) {
        this.jday = day;
        this.leapMoon = leap;
        this.moonBits = bits;
    }

    public int getJDay() {
        return jday;
    }

    public int getLeapMoon() {
        return leapMoon;
    }

    public int getMoonBits() {
        return moonBits;
    }

    /**
     * Calculate the number of days in a Chinese Lunar year. This can vary from 354 to 384 days.
     */
    public int nDaysInYear() {
        int days = 0;
        int bits = moonBits;
        int nMoons = leapMoon > 0 ? 13 : 12;
        for (int moon=0;  moon<nMoons;  moon++) {
            days += 29 + (bits & 1);
            bits >>= 1;
        }
        return days;
    }

    /**
     * Calculate the number of days in a month, either 29 or 30.
     *
     * @param month -- the month to calculate for
     * @param isIntercalry -- true if m is intercalary
     */
    public int nDaysInMonth(int month, boolean isIntercalry) {
        int m = month;

        // Increment month number if "month" is on or after intercalary month
        if (leapMoon > 0  &&  m > leapMoon) {
            m++;
        } else if (isIntercalry  &&  m == leapMoon) {
            m++;
        }

        // shift the bit array right to the month we want
        int bits = moonBits;
        if (m > 1) {
            bits >>= (month - 1);
        }

        return 29 + (bits & 1);
    }
}