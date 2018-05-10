/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v1;

/**
 * @author Pete Blake
 *         <p/>
 *         Most of this code is adapted from code Copyright (c) 1992-2001 Dandelion Corporation, Licensed to Intellectual Reserve, Inc.
 *         <p/>
 *         The Gregorian Calendar was introduced in 1582 by Pope Gregory XIII. It was legally adopted in Great Britain and the colonies in 1752.
 *         <p/>
 *         Gregorian algorithms below were developed by P T Blake, Dandelion Corporation. See also Robert Tantzen, Communications of the ACM, v6, #8, August,
 *         1963, p. 444. See also Fliegel and van Flandern, CACM, v11, #10, p. 657. Julian Day 0 == 1 Jan 4713 B.C. (Julian calendar) == 24 Nov -4712
 *         (Gregorian)
 */

public class GregorianCalendar extends JulianCalendar {

  private static final GregorianCalendar THE_CALENDAR = new GregorianCalendar();

  /**
   * Enforce singleton pattern.
   */
  protected GregorianCalendar() {
  }

  public static Calendar getInstance() {
    return THE_CALENDAR;
  }

  public CalendarType getCalendarType() {
    return CalendarType.GREGORIAN;
  }

  /**
   * This overrides the Julian version
   */
  public boolean isLeapYear(int y) {
    if (y < 0) {// if BC, convert year to internal numbering
      y += 1 + 4800;// convert 1 BC from -1 to 0, etc., make it positive
    }

    return (y % 4 == 0) && (y % 100 != 0) || (y % 400 == 0);
  }

  private int getDaysSinceMarch1(int m) {
    if (m < DAYS_SINCE_MARCH_1.length) {
      return DAYS_SINCE_MARCH_1[m];
    }
    return 0;
  }

  // ===========================================================

  /**
   * Convert a date in the Gregorian calendar to a Julian Day number
   *
   * @param d day of the month:  1 - 31
   * @param m month of the year, relative to March. March == 0.
   * @param y internal form + 4800
   * @return the Julian Day number from day, month, year
   */
  protected AstroDay day(int d, int m, int y) {

    // Leap years happen every 4 years -- 4, 8, etc. so we can
    // shift right 2 bits to divide by 4.
    // Leap year happens every 4 years except not in century years,
    // but century years divisible by 400 are leap years,
    // so 1700, 1800, 1900 are not leap years, but 2000 is.
    int c = y / 100;// # of whole centuries
    return AstroDay.getInstance(d + getDaysSinceMarch1(m) + y * 365 + (y >> 2) - c + (c >> 2)
      + 1721119// day # of 29 Feb 0000 (1 BC)
      - 1753164);// remove the 4800 year offset ( = 12*146097 )
  }

  /**
   * Convert the Julian Day number to Gregorian calendar day, month, year.
   *
   * @param day Julian Day number
   * @return the values of day, month, year
   */
  protected DMY dmy(AstroDay day) {

    int jday = day.value();
    if (jday <= 367) {
      return dm(day);
    }

    int d, m, y;
    int c, r, x;

    // Offset the jday by 1753164 days (4800 years) during calculation.
    // the calculations use March as the first month in the year
    x = 4 * (jday - 1721119 + 1753164) - 1;
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

    return new DMY(d, m, y);// there is no intercalary month in Gregorian
  }

  public String toString() {
    return "Gregorian calendar";
  }

}
