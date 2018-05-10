/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v1;

/**
 * The Julian Astronomical Day class -- "Astronomers in recent centuries have avoided the confusing situation of date references on different calendars, each
 * with its idiosyncracies, by specifing moments in time by giving them in "julian days" or JD (sometimes "julian astronomical days" or J.A.D)." Reingold and
 * Dershowitz, "Calendrical Calculations", p.18.
 *
 * @author Pete Blake FSDate: Oct 11, 2004
 *         <p/>
 *         Copyright(c) 2004 Intellectual Reserve Inc. All rights reserved.  Unauthorized reproduction of this software is prohibited and is in violation of
 *         United States copyright laws.
 */
public class AstroDay {

  public static final AstroDay INVALID = new AstroDay(0);

  // the first valid AstroDay for genealogical purposes
  // 260186 = GregorianCalendar.jdayFromDayMonthYear(6, 4, -4001); // 4000 years before Christ
  public static final AstroDay FIRST_DAY = new AstroDay(260186);

  // the last valid AstroDay for genealogical purposes
  // 2816883 = GregorianCalendar.jdayFromDayMonthYear(6, 4, 3000); // 3000 years after Christ
  public static final AstroDay LAST_DAY = new AstroDay(2817152);

  private int julianDayNumber;

  /**
   * Construct an AstroDay from a Julian Day number
   *
   * @param jday - a Julian Day number values before 4000 BC and after 3000 AD are considered invalid.
   * @return an Astroday instance
   */
  public static AstroDay getInstance(int jday) {
    return new AstroDay(jday);
  }

  /**
   * Construct an AstroDay from a Julian Day number
   *
   * @param jday - a Julian Day number values before 4000 BC and after 3000 AD are considered invalid.
   */
  private AstroDay(int jday) {
    julianDayNumber = jday;
  }

  /**
   * hide the protected grammars constructor
   */
  protected AstroDay() {
    throw new UnsupportedOperationException("This method should not be called");
  }

  /**
   * Get the Julian Day number from the AstroDay
   *
   * @return the Julian Day Number value
   */
  public int value() {
    return julianDayNumber;
  }

  /**
   * check for a valid AstroDay
   *
   * @return true if valid, false if not
   */
  public boolean isValid() {
    return julianDayNumber >= FIRST_DAY.value() && julianDayNumber <= LAST_DAY.value();
  }

  /**
   * equals method override
   *
   * @param o Object to compare
   * @return true the objects are equal, false they are not
   *
   * @see Object
   */
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AstroDay)) {
      return false;
    }

    final AstroDay astroDay = (AstroDay) o;

    return julianDayNumber == astroDay.julianDayNumber;
  }

  /**
   * hashCode method override
   *
   * @return the generated hashCode
   *
   * @see Object
   */
  public int hashCode() {
    return julianDayNumber;
  }

}
