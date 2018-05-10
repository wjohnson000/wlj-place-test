package std.wlj.date.v1;

/**
 * @author Pete Blake
 */
public class WesternCalendar extends GregorianCalendar {

  private static final int BEGIN_GREGORIAN = 2299161;// 1582.10.15

  private static final WesternCalendar THE_CALENDAR = new WesternCalendar();

  /**
   * Enforce singleton pattern.
   */
  protected WesternCalendar() {
  }

  public static Calendar getInstance() {
    return THE_CALENDAR;
  }


  public DMY dmyFromDay(AstroDay day) {
    Calendar calendar = day.value() >= BEGIN_GREGORIAN
      ? GregorianCalendar.getInstance()
      : JulianCalendar.getInstance();
    return calendar.dmyFromDay(day);
  }

  public int getYear(AstroDay day) {
    Calendar calendar = day.value() >= BEGIN_GREGORIAN
      ? GregorianCalendar.getInstance()
      : JulianCalendar.getInstance();
    return calendar.dmyFromDay(day).getYear();
  }

  public int getMonth(AstroDay day) {
    Calendar calendar = day.value() >= BEGIN_GREGORIAN
      ? GregorianCalendar.getInstance()
      : JulianCalendar.getInstance();
    return calendar.dmyFromDay(day).getMonth();
  }

  public int getDay(AstroDay day) {
    Calendar calendar = day.value() >= BEGIN_GREGORIAN
      ? GregorianCalendar.getInstance()
      : JulianCalendar.getInstance();
    return calendar.dmyFromDay(day).getDay();
  }

  /**
   * Calculate a new jday from a jday and a number of years, taking leap years into account, according to Western calendar rules.  For examole, to calculate a
   * jday for age 8, give the jday for the birthday and 8 years. The result will be the jday for the 8th birthday. The calculation accounts for leap years.
   *
   * @param day a julian day number
   * @param years (either + or -) from jday
   * @return a new jday 'years' years after 'jday'
   */
  public AstroDay addYears(AstroDay day, int years) {
    if (day.value() < 1000) {
      return AstroDay.INVALID;
    }
    Calendar calendar = day.value() >= BEGIN_GREGORIAN
      ? GregorianCalendar.getInstance()
      : JulianCalendar.getInstance();
    DMY dmy = calendar.dmyFromDay(day);
    int newYear = dmy.getYear() + years;
    // do the 29 feb fixup -- change it to 1 Mar if not a leap year
    if ((29 == dmy.getDay()) && (2 == dmy.getMonth())) {
      if (!calendar.isLeapYear(newYear)) {
        dmy.setDay(1);
        dmy.setMonth(3);
      }
    }
    // there is no zeroth year so...// set it to 1 AD instead if adding
    if (0 == newYear) {
      if (years > 0) {
        // set it to 1 AD instead if adding
        newYear = 1;
      }
      else {
        // set to 1BC if subtracting
        newYear = -1;
      }
    }

    dmy.setYear(newYear);

    return calendar.dayFromDMY(dmy, 0);
  }
}
