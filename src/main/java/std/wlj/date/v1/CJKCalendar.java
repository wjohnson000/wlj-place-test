package std.wlj.date.v1;

/**
 * @author Pete Blake
 *         <p/>
 *         Most of this code is adapted from code Copyright (c) 1992-2001 Dandelion Corporation, Licensed to Intellectual Reserve, Inc.
 */
public abstract class CJKCalendar implements Calendar {

  public boolean isLunar() {
    return true;
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
   * Calculate a new jday from a jday and a number of years, taking leap years into account, according to calendar rules.  For example, to calculate a jday for
   * age 8, give the jday for the birthday and 8 years. The result will be the jday for the 8th birthday. The calculation accounts for leap years.
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

  /**
   * Determine of the year is a leap year, i.e. a year containing a leap month
   *
   * @return true if the year contains a leap month, else false
   */
  public boolean isLeapYear(int y) {
    AstroDay astro = dayFromDayMonthYear(1, 1, y);
    return CJKCalendarTable.getInstance().getLunarYear(astro).getLeapMoon() > 0;
  }

  public boolean isValidYear(int y) {
    return y >= -4000 && y < 3000;
  }

  public boolean isValidMonth(int m, int y) {
    // in the cjk calendar, the year is irrelevant
    return m >= 1 && m <= 12;
  }

  /**
   * Determine if d is a valid day in m and y
   *
   * @return true if d is a valid day in month m and year y, else false
   */
  public boolean isValidDay(int d, int m, int y) {
    if (!isValidMonth(m, y)) {
      return false;
    }
    if (d < 1 || d > 30) {
      return false;
    }
    // everything from 1 to 29 is valid
    if (d != 30) {
      return true;
    }
    // is 30 a valid day in month m ?
    AstroDay astro = dayFromDayMonthYear(d, m, y);
    LunarYearDescription lyd = CJKCalendarTable.getInstance().getLunarYear(astro);
    int bits = lyd.getMoonBits();// something like 0000101011010101
    // get the bit for month m
    int bit = (bits >> (m - 1)) & 1;// shift right m-1 bits
    return 1 == bit;// bit will be 1 if month m has 30 days
  }

  public AstroDay dayFromDMY(DMY dmy, int n) {
    return dayFromDayMonthYear(dmy.getDay(), dmy.getMonth(), dmy.getYear(), n);
  }

  public AstroDay dayFromDayMonthYear(int d, int m, int y) {
    return dayFromDayMonthYear(d, m, y, 0);
  }

  /**
   * Calculate a Julian day number from the day, month and year in this calendar
   *
   * @param d - the day of the lunar month (1-30)
   * @param m - lunar month (moon) number (1-12)
   * @param y - the Christian era year of the first day of the lunar year, in external form: 1 BC == -1, 1 AD == 1
   * @param i - month number of the intercalary month, or 0 (0-12)
   * @return the Astronomical (Julian) Day number from CJK day, month, year, intercalary month
   */
  public AstroDay dayFromDayMonthYear(int d, int m, int y, int i) {
    if (y == 0) {
      String msg = "Invalid year " + y;
      throw new IllegalArgumentException(msg);
    }
    if (m < 0 || m > 12) {
      String msg = "Invalid month " + m;
      throw new IllegalArgumentException(msg);
    }
    if (d < 0 || d > 30) {
      String msg = "invalid day " + d;
      throw new IllegalArgumentException(msg);
    }

    // ==== lunar calendar calculations ====

    LunarYearDescription lyd = CJKCalendarTable.getInstance().getLunarYear(y);

    boolean missingDay = false, missingMonth = false;

    // use 1 for missing month
    if (m == 0) {
      missingMonth = true;
      m = 1;
      d = 0;// ignore any day value if the month is missing
    }
    // use 1 for missing day
    if (d == 0) {
      missingDay = true;
      d = 1;
    }

    // begin calculations at the first day of the year
    int jday = lyd.getJDay();

    // add the number of days in each month
    int moon = m;// the month number from the original string
    int leapMonth = lyd.getLeapMoon();
    // if the intercalary month from the date does not agree
    // with the intercalary month from the calendar
    // ignore the intercalary character in the date
    if (i != 0 && leapMonth != m) {
      // todo: throw error if given and calculated intercalary moons not equal?
      i = 0;// ignore the intercalary character
    }
    // any month after the intercalary month needs 1 added
    if (leapMonth > 0 && m > leapMonth) {
      moon++;// convert month to moon
    }
    else
      // if we have an intercalary month,
      // and it is the same as the one from the table
      if (i != 0 && leapMonth == m) {
        moon++;// add one
      }
    // get the first day of the month by adding 29 or 30
    // for each month previous to the one we want.
    int bits = lyd.getMoonBits();
    while (--moon > 0) {
      jday += 29 + (bits & 1);
      bits >>= 1;
    }

    // add the day of the month
    jday += d > 0 ? d - 1 : 0;

    // if month or day are missing, adjust the jday to be the mid point of the next larger piece
    if (missingMonth) {
      jday += lyd.nDaysInYear() / 2;// adjust the jday to the middle of the year
    }
    else {// the month is good, just find the middle day
      if (missingDay) {
        jday += lyd.nDaysInMonth(m, i > 0) / 2;// adjust the jday to the middle of the month;
      }
    }
    return AstroDay.getInstance(jday);
  }

  /**
   * Calculate Chinese, Japanese, Korean d, m, y from a julian day number
   *
   * @return a DMY object
   */

  public DMY dmyFromDay(AstroDay aday) {
System.out.println("CJKCalendar.dmyFromAstroDay");
    // if the day is later than the table, revert to Gregorian
    if (aday.value() > CJKCalendarTable.LAST_JDAY) {
      return GregorianCalendar.getInstance().dmyFromDay(aday);
    }

    LunarYearDescription lyd = CJKCalendarTable.getInstance().getLunarYear(aday);
    // calculate the moon (convert moon to month# below)
    // add days to the item jday until we equal the parameter
    int m, d;
    int jday = aday.value();// the target date
    // beginning with the first day of the lunar year
    // add the days in each month until we reach the target month
    int jd = lyd.getJDay();// the first day of the lunar year
    int bits = lyd.getMoonBits();
    for (m = 1; m <= 13; m++) {
      d = 29 + (bits & 1);// days in month m
      bits >>= 1;// shift the next month into place
      if ((jd + d) > jday) {
        break;
      }
      jd += d;// add the days in the current month
    }
    d = jday - jd + 1;
    //    ASSERT( (unsigned) d <= (29+(bits&1)) );

    // === Convert moon to month#, adjust for the intercalary month ===
    // Suppose the intercalary month is 7. That means that moon == month
    // thru the 7th moon, and the 8th moon is called intercalary
    // month 7. So when m is 8 or higher, we return 7. Then
    // m is decremented for any moon greater than 7 so the correct
    // date string is generated. The date for the normal 7th month is
    // EEnnY7MnnD whereas the intercalary 7th month (8th moon) is
    // EEnnYI7MnnD, where I is the intercalary character U+958f.
    // Return the intercalary value  to show that m is the intercalary month.
    int n = lyd.getLeapMoon();// the intercalary month or 0
    int r = (m - 1) == n ? n : 0;// the intercalary month, or 0
    // If there is an intercalary month
    // and the moon is greater that the intercalary month
    // then the month number is one less than the moon number.
    if (n > 0 && m > n) {
      m--;// decr m if it is past the leap month
    }

    // get the Christian year, then add month and day
    Calendar calendar = jday >= firstGregorian()
      ? GregorianCalendar.getInstance()
      : JulianCalendar.getInstance();
    DMY dmy = calendar.dmyFromDay(aday);// yields d, m, y on the western calendar
    dmy.setMonth(m);// lumar month
    dmy.setDay(d);// lunar day
    dmy.setIntercalary(r > 0);// intercalary ?
    return dmy;
  }

  /**
   * @return the number of the last month in the year
   */
  public int lastMonth() {
    return 12;
  }

}
