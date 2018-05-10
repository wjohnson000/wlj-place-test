package std.wlj.date.v1;

/**
 * @author Pete Blake
 *         <p/>
 *         The Julian Calendar was instituted on Jan 1, 45 BC, (709 A.U.C. [ab urbe condita] or [anno urbis conditae], the traditional founding of Rome in 754
 *         BC)
 */
public class JulianCalendar implements Calendar {

  // number of days in each month in this calendar
  protected static final short DAYS_IN_MONTH[] = {
    0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
  // number of days from the 1st of March to the 1st of month m; March is 0
  protected static final short DAYS_SINCE_MARCH_1[] = {
    0, 31, 61, 92, 122, 153, 184, 214, 245, 275, 306, 337, 367 };

  private static final JulianCalendar THE_CALENDAR = new JulianCalendar();

  /**
   * Enforce singleton pattern.
   */
  protected JulianCalendar() {
  }

  public static Calendar getInstance() {
    return THE_CALENDAR;
  }

  public CalendarType getCalendarType() {
    return CalendarType.JULIAN;
  }

  public int firstGregorian() {
    return 2299161;// 15 Oct 1582 -- the first day of the Gregorian calendar
  }

  public int firstGregorianYear() {
    return 1583;// the first full year of the Gregorian Calendar
  }

  public boolean isLunar() {
    return false;// this is not a lunar calendar
  }

  public int getYear(AstroDay day) {
    return dmyFromDay(day).getYear();
  }

  public int getMonth(AstroDay day) {
    return dmyFromDay(day).getMonth();
  }

  public int getDay(AstroDay day) {
    return dmyFromDay(day).getDay();
  }

  /**
   * Calculate a new jday from a jday and a number of years, taking leap years into account, according to Western calendar rules.  For examole, to calculate a
   * jday for age 8, give the jday for the birthday and 8 years. The result will be the jday for the 8th birthday. The calculation accounts for leap years.
   *
   * @param years (either + or -) from jday
   * @return a new jday 'years' years after 'jday'
   */
  public AstroDay addYears(AstroDay day, int years) {
    if (day.value() < 1000) {
      return AstroDay.INVALID;
    }
    DMY dmy = dmyFromDay(day);
    dmy.setYear(dmy.getYear() + years);
    return dayFromDMY(dmy, 0);
  }

  public boolean isLeapYear(int y) {
    // At the inception of the Julian calendar in 45 BC, every
    // fourth year was supposed to be a leap year. Historical
    // evidence indicates that instead, every third year
    // was taken as a leap year from 45 BC to 9 BC, and to
    // correct the error, leap years were omitted until 8 AD.
    // (skipped:  5 BC, 1 BC, 4 AD).
    if (y >= 8) {
      return (y % 4) == 0;// 'normal' leap year
    }
    if (y >= 0) {
      return false;// no leap years from 1 to 7, ( or 0, which is invalid )
    }
    y++;// y < 0, make 1 BC == 0, 2 BC == -1, etc.
    if (y > -8) {
      return false;// no leap years from 8 BC thru 1 BC
    }
    if (y > -44) {// find third years from 45 BC to 9 BC
      return (y % 3) == 0;
    }

    // revert to 'normal' for years before 45 BC, (proleptic Julian calendar)
    return (y % 4) == 0;
  }

  public boolean isValidYear(int y) {
    return y >= -4000 && y <= 3000 && y != 0;
  }

  public boolean isValidMonth(int m, int y) {
    // the year is irrelevant in a Julian calendar
    return m >= 1 && m <= 12;
  }

  public boolean isValidDay(int d, int m, int y) {
    // return TRUE if d is a valid day in month m and year y
    if (!isValidMonth(m, y)) {
      return false;
    }
    int max = DAYS_IN_MONTH[m];
    if (m == 2 && isLeapYear(y)) {
      max++;
    }
    return d >= 1 && d <= max;
  }

  public int getDaysInMonth(int m) {
    if (m < DAYS_IN_MONTH.length) {
      return DAYS_IN_MONTH[m];
    }
    return 0;
  }

  public AstroDay dayFromDMY(DMY dmy, int i) {
    // ignore the intercalary month i
    return dayFromDayMonthYear(dmy.getDay(), dmy.getMonth(), dmy.getYear());
  }

  public AstroDay dayFromDayMonthYear(int d, int m, int y, int i) {
    // ignore the intercalary month i, it is only used in CJK lunar calendars
    return dayFromDayMonthYear(d, m, y);
  }

  public AstroDay dayFromDayMonthYear(int d, int m, int y) {

    if (0 == d && 0 == m && 0 == y) {
      return AstroDay.INVALID;
    }

    if (y < -4001 || y > 3000) {
      return AstroDay.INVALID;
    }

    if (y == 0) {// special case for missing year
      return day(d, m);
    }

    boolean missingDay = false, missingMonth = false;

    // use Jan for missing month
    if (m == 0) {
      missingMonth = true;
      m = 1;
    }
    // use 1 for missing day
    if (d == 0) {
      missingDay = true;
      d = 1;
    }

    if (!isValidDay(d, m, y)) {
      return AstroDay.INVALID;
    }

    // convert year to internal numbering
    if (y < 0) {// if BC
      y++;// make 1 BC == 0, 2 BC == -1, etc.
    }
    y += 4800;// add a big multiple of 400 so y is positive

    // renumber months so March is month 0, then use the nifty formula below
    if (m > 2) {
      m -= 3;
    }
    else {
      m += 9;
      y--;
    }
    // Jan and Feb are now month 10 and 11 in the previous year

    int jday = day(d, m, y).value();// remember, Gregorian extends Julian

    // if month or day are missing, adjust the jday to be the mid point of the next larger piece
    if (missingMonth && jday > 1000) {
      jday += 182;// adjust the jday to the middle of the year
    }
    else {// the month is good, just do the day
      if (missingDay && jday > 1000) {
        jday += 14;// adjust the jday to the 15th of the month;
      }
    }
    return AstroDay.getInstance(jday);// jday of the center of the date
  }

  /**
   * Convert a date in the Julian calendar to a Julian Day number
   *
   * @param d day of the month:  1 - 31
   * @param m month of the year, relative to March. March == 0.
   * @param y internal form + 4800
   * @return the Julian Day number from day, month, year
   */
  protected AstroDay day(int d, int m, int y) {

    // Mar is month 0, Jan and Feb are month 10 and 11 in the previous year,
    // and the other month numbers are adjusted accordingly

    // Note that calculations here follow the rules of the calendar as
    // they were specified by Sosigenes and decreed by Julius Caesar.
    // The muddling of the leap years by the pontifices from 45 - 9 BC is NOT
    // accounted for here. (But see isLeapYear).

    y += 4712 - 4800;// Julian day number base is 1 Jan 4713 BC, make it positive
    // The julian day number =
    // # of days in month m (the value in d)
    // + days for each month preceeding month m, beginning with March
    // + 365 days in each year preceeding year y,
    // + 1 for each leap year
    // + 59 days for Jan and Feb in -4712 (31 + 28),
    return AstroDay.getInstance(d + getDaysSinceMarch1(m) + y * 365 + (y >> 2) + 59);
  }

  public AstroDay day(int d, int m) {
    // special case where year is missing
    // we do a simple calculation for a JDay

    if (m > 2) {
      m -= 3;
    }
    else {
      m += 9;
    }
    // Jan and Feb are now month 10 and 11
    // Calculate the # days from 1st of Mar to 1st of month m
    // then add the # of days in the month
    // if the day is 0, use 1
    if (d < 1) {
      d = 1;
    }
    return AstroDay.getInstance(getDaysSinceMarch1(m)+ d);
    // the value returned is 1 <= jday <= 367
  }

  private int getDaysSinceMarch1(int m) {
    if (m < DAYS_SINCE_MARCH_1.length) {
      return DAYS_SINCE_MARCH_1[m];
    }
    return 0;
  }


  public DMY dmyFromDay(AstroDay day) {
    return dmy(day);
  }

  /**
   * Convert the Julian Day number to Julian calendar day, month, year.
   *
   * @param day Julian Day number
   * @return the values of day, month, year in a DMY
   */
  protected DMY dmy(AstroDay day) {

    int jday = day.value();
    if (jday <= 367) {
      return dm(day);
    }

    int d, m, y;
    int c, r, x;

    // these calculations use March as the first month in the year
    x = 4 * (jday - 31 - 28) - 1;
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

    return new DMY(d, m, y);
  }

  public DMY dm(AstroDay day) {
    // special case for missing year

    if (day.value() < 1) {
      return new DMY(0, 0, 0);
    }

    if (day.value() > 367) {
      return dmy(day);
    }

    // find the month
    int m = 0;
    while (day.value() > getDaysSinceMarch1(m)) {
      m++;
    }
    m--;
    int d = day.value() - getDaysSinceMarch1(m);
    if (m < 10) {
      m += 3;
    }
    else {
      m -= 9;
    }
    return new DMY(d, m, 0);
  }

  /**
   * @return the number of the last month in the year
   */
  public int lastMonth() {
    return 12;
  }

  public String toString() {
    return "Julian calendar";
  }

}
