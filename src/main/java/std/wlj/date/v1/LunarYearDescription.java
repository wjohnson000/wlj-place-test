/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v1;

/**
 * @author Pete Blake
 *         <p/>
 *         This class describes a lunar year: its beginning (a jday), the length of each moon, the intercalary moon number, if any. Note that if leapMoon is not
 *         zero, there are 13 moons in the year.
 */
public class LunarYearDescription {

  private int jday;// jday of the first day of the lunar year -- ptb: this must be an int, don't substitute a class here
  private int leapMoon;// # of the leap moon, or 0 if this year does not have a leap moon
  private int moonBits;// lsb is 1st moon; bit is on if moon has 30 days, off if 29 days

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
    int n = 0;
    int bits = moonBits;
    int nMoons = leapMoon > 0 ? 13 : 12;
    for (int x = 0; x < nMoons; x++) {
      n += 29 + (bits & 1);
      bits >>= 1;
    }
    return n;
  }

  /**
   * Calculate the number of days in a month, either 29 or 30.
   *
   * @param m -- the month to calculate for
   * @param i -- true if m is intercalary
   */
  public int nDaysInMonth(int m, boolean i) {
    // any month after the intercalary month needs 1 added
    if (leapMoon > 0 && m > leapMoon) {
      m++;// convert month to moon
    }
    else
      // if m is an intercalary month,
      // and it is the same as the one from the table
      if (i && leapMoon == m) {
        m++;// add one
      }
    // shift the bit array right to the month we want
    int bits = moonBits;
    if (m > 1) {
      bits >>= (m - 1);
    }
    return 29 + (bits & 1);
  }
}