/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v1;

/**
 * Calendar: a system for fixing the beginning, length, and divisions of the year and arranging the days and longer divisions of time (weeks, months) in a
 * definite order. (Merriam-Webster's Collegiate Dictionary). Calendars usually have some relation to, and try to model, the natural order of the earth, moon
 * and sun. Often, a religion and a calendar are closely tied. Many cultures use multiple calendars simultaneously: civil, religious, historical.
 * <p/>
 * n.b. The YEAR value is always relative to the Christian Era, and zero is not a valid value. In the Christian Era, BC years are negative: -1 == 1BC, -2 ==
 * 2BC, etc. and AD years are positive: 1, 2, 3.
 *
 * @author Pete Blake
 */
public interface Calendar {
    //Fixbugs change for Redundant Modifier  removal of Public it is are ready implied by the class type.
  /**
   * getCalendarType
   *
   * @return the type of this calendar
   */
  CalendarType getCalendarType();

  /**
   * The julian day number of the first day in the Gregorian Calendar.
   *
   * @return the julian day number of the day that the calendar change officially began.
   */
  int firstGregorian();

  /**
   * The year the Gregorian Calendar replaced this calendar
   *
   * @return the year that the calendar change officially began.
   */
  int firstGregorianYear();

  /**
   * @return true if this is a Lunar calendar
   */
  boolean isLunar();

  /**
   * Return the year from a AstroDay. The value returned is always relative to the Christian Era, and zero is not a valid value. BC years are negative: -1 ==
   * 1BC, -2 == 2BC, etc. and AD years are positive: 1, 2, 3.
   *
   * @param day AstroDay from which to derive the year
   * @return the year
   */
  int getYear(AstroDay day);

  /**
   * Return the month from a AstroDay.
   *
   * @param day AstroDay from which to derive the month
   * @return the month (1-12)
   */
  int getMonth(AstroDay day);

  /**
   * Return the day from a AstroDay.
   *
   * @param day AstroDay from which to derive the day of the month
   * @return the day (1-31)
   */
  int getDay(AstroDay day);

  /**
   * Calculate a new AstroDay from a AstroDay and a number of years, taking leap years into account, according to the rules of the calendar.  For example, to
   * calculate a jday for age 8, give the AstroDay for the birthday and 8 years. The result will be the AstroDay for the 8th birthday. The calculation accounts
   * for leap years.
   *
   * @param day AstroDay representing the base day
   * @param years (either + or -) from jday
   * @return a new jday 'years' years after 'jday'
   */
  AstroDay addYears(AstroDay day, int years);

  /**
   * IsValidDay
   *
   * @param day an integer value (1-31) for a day of the month
   * @param month an integer value (1-12) for the month of the year
   * @param year and integer value for the year
   * @return TRUE if d is a valid day in month m and year y
   */
  boolean isValidDay(int day, int month, int year);

  /**
   * IsValidMonth
   *
   *
   * @param month an integer value (1-12) for the month of the year
   * @param year and integer value for the year
   * @return TRUE if month m is a valid month in year y
   */
  boolean isValidMonth(int month, int year);

  /**
   * Is a valid year?
   *
   * @param year and integer value for the year
   * @return TRUE if y is a valid year in this calendar Note that all tables and calculations use the Christian Era year. But raw years entered by patrons may
   *         be from other eras, e.g. the Tangun Era.
   */
  boolean isValidYear(int year);

  /**
   * is a leap year?
   * @param year and integer value for the year
   * @return TRUE if year is a leap year in this calendar
   */
  boolean isLeapYear(int year);

  /**
   * getJDay
   *
   * @param day an integer value (1-31) for a day of the month
   * @param month an integer value (1-12) for the month of the year
   * @param year the Christian Era year in external form: 1 BC == -1, 2 BC == -2, &c
   * @return the AstroDay from day, month, year
   */
  AstroDay dayFromDayMonthYear(int day, int month, int year);

  /**
   * get an AstroDay from day, moon, year, and intercalary moon
   *
   * @param d day of the moon (1-30)
   * @param m moon number (1-12)
   * @param y the Christian Era year in external form: 1 BC == -1, 2 BC == -2, &c
   * @param n the # of the intercalary moon, or 0 if there is none (0-12)
   * @return the AstroDay calculated from the input parameters
   */
  AstroDay dayFromDayMonthYear(int d, int m, int y, int n);

  /**
   * jdayFromDMY
   *
   * @param dmy contains d, m, y, in this calendar
   * @param n the intercalary month number, or 0
   * @return the AstroDay from day, month, year, intercalary month
   */
  AstroDay dayFromDMY(DMY dmy, int n);

  /**
   * @param day AstroDay
   * @return a DMY containing the values of d, m, y in this calendar, for jday
   */
  DMY dmyFromDay(AstroDay day);

  /**
   * @return the number of the last month in the year
   */
  int lastMonth();

}
