package std.wlj.date.v1;

import org.familysearch.standards.core.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A table containing calendar information for China. Each entry consists of 3 integers: 1. the Julian day number of the first day of the year, 2. a bitmap
 * indicating the number of days in each month, 3. a number indicating which moon, if any, is the intercalary moon.
 *
 * @author Pete Blake
 *         <p/>
 *         Most of this code is adapted from code Copyright (c) 1992-2001 Dandelion Corporation, Licensed to Intellectual Reserve, Inc.
 */
public class CJKCalendarTable {

  private static final Logger LOGGER = new Logger(CJKCalendarTable.class);
  private static final CJKCalendarTable[] CJKCT = new CJKCalendarTable[1];

  // a known winter solstice -- 22 Dec 1990 03:08 UT
  private static final double SOLSTICE_0U = 2448247.63056;

  static final int FIRST_YEAR = 5;// first year in the table
  static final int LAST_YEAR = 2025;// last year from the table
  static final int FIRST_JDAY = 1722913;// 0005.01.29 Julian -- first day in the table
  public static final int LAST_JDAY = 2460705;// 2025.01.29 Gregorian -- last day in the table

  private final Integer[] TABLE;

  // hide private constructor
  private CJKCalendarTable() {
    TABLE = init();
    System.out.println("TABLE-SIZE: " + TABLE.length);
  }

  public static CJKCalendarTable getInstance() {
    synchronized (CJKCT) {
      if (null == CJKCT[0]) {
        CJKCT[0] = new CJKCalendarTable();
      }
    }

    return CJKCT[0];
  }

  public LunarYearDescription getLunarYear(int y) {
    if (y >= FIRST_YEAR && y <= LAST_YEAR) {
      int x = (y - FIRST_YEAR) * 3;// 3 ints per item
      return new LunarYearDescription(TABLE[x], TABLE[x + 1], TABLE[x + 2]);
    }

    // y is out of the range of the table
    //  calculate the jday of the vernal equinox, then calculate a LunarYearDescription
    return getLunarYear(GregorianCalendar.getInstance().dayFromDayMonthYear(21, 3, y));
  }

  public LunarYearDescription getLunarYear(AstroDay astro) {

    final int jday = astro.value();
    if (jday >= FIRST_JDAY && jday <= LAST_JDAY) {
      int y = GregorianCalendar.getInstance().getYear(astro);
      // since lunar calendar years and Gregorian years don't begin on the same day,
      // if the jday as at the end of the lunar year, y may be the next Gregorian year (1 too high)
      int x = (y - FIRST_YEAR) * 3;// 3 ints per item -- x is the index into the table
      // TABLE[x] contains the first day of the lunar year
      if (TABLE[x] > jday) {// if we have gone too far, back up a year
        x -= 3;// back up one lunar year
      }
      return new LunarYearDescription(TABLE[x], TABLE[x + 1], TABLE[x + 2]);
    }

    // todo STD-221: We extended the calendar to the year 2025 to avoid hitting this code.  in some cases
    // the dates were a day off.
    // the date is not in the table, calculate a LunarYearDescription

    // ======== calculate the solstice before and after the date ========

    // n.b. names of variables in Universal Time end with U, those in local time end with L

    // calc the # of days from the reference solstice to the date
    double dDays = jday - SOLSTICE_0U;
    // calc the # of whole years to the solstice following the date
    final double dDaysInYear = daysInYear(jday);
    int nYears = (int) (dDays / dDaysInYear);
    int solstice1U = (int) (SOLSTICE_0U + nYears * dDaysInYear);
    while (solstice1U > jday) {
      nYears--;
      solstice1U = (int) (SOLSTICE_0U + nYears * dDaysInYear);
    }
    // the solstice following the date is 1 year more
    int solstice2U = (int) (SOLSTICE_0U + (nYears + 1) * dDaysInYear);
    // jday is between solstice1 and solstice2
    //  ---|--------------|-----------------------|---
    //     s1             jday                         s2

    // ======== calculate all the new moons between the solstices ========

    final double dDaysInMoon = daysInMoon(jday);

    final double dMoon0U = newMoonU(jday);// a reference moon

    // first calculate the moon immediately following solstice 1
    dDays = solstice1U - dMoon0U;
    int nMoons = (int) (dDays / dDaysInMoon);
    double dMoon = dMoon0U + nMoons * dDaysInMoon;
    // adjust it for Beijing time
    dMoon += getBeijingOffset(solstice1U);
    while (dMoon < solstice1U) {
      dMoon += dDaysInMoon;
    }

    // 武帝 (Wu Ti) -- Born 156 B.C.,  Emperor of China, 140-87 B.C.
    // Before emperor Wu, the solstice occurs in the first month of the year.
    // Beginning with him, the solstice occurs in the 11th month of the previous year.
    // Intercalary months are inserted to align the months to the solstice.
    int jdWuTi = 1670280;// beginning of Wu's reign (20 Dec 141 bc ?)
    // after WuTi, if moons[0] is the 11th moon, moon[2] is the first month
    int firstMoon = jday < jdWuTi ? 0 : 2;

    // keep track of the number of days in each month in a bit array
    int bits = 0;
    int theBit = 1;// theBit is 1if the moon has 30 days, 0 if 29 days

    // build an array containing all the moons within the solar year, with 1 before and 1 after
    // moons[0] contains the moon immediately preceeding the solstice
    // moons[1] contains the first moon after the solstice, etc.
    int[] moons = new int[15];
    moons[0] = (int) (dMoon - dDaysInMoon);// the moon preceeding the winter solstice
    for (nMoons = 1; nMoons < moons.length; nMoons++) {
      moons[nMoons] = (int) dMoon;
      double dPrev = dMoon;
      dMoon += dDaysInMoon;
      int nDays = (int) dMoon - (int) dPrev;
      if (nDays == 30) {
        bits |= theBit;// set the bit if the moon has 30 days
      }
      if (dMoon > solstice2U) {
        moons[nMoons + 1] = (int) dMoon;
        break;
      }
      theBit <<= 1;// the bit position for the next moon
    }
    // nMoons == the number of new moons in the solar year (12 or 13)

    // if we have 13 moons, determine which is the intercalary moon
    int intercalaryMoon = 0;// intercalary moon number
    if (nMoons == 13) {
      // the intercalary moon has no solar term
      int[] terms = calculateSolarTerms(solstice1U);
      // compare each solar term to each moon
      int moon = moons[0] < terms[0] ? 1 : 0;
      for (int term = 0; term < 12; term++) {
        if (moons[moon] >= terms[term] && moons[moon + 1] < terms[term + 1]) {
          intercalaryMoon = moon - firstMoon;
          break;
        }
        moon++;
      }
    }

    return new LunarYearDescription(moons[firstMoon], intercalaryMoon, bits);
  }

  private int[] calculateSolarTerms(double dSolstice) {
    // divide the year into 12 terms
    // save the date of the beginning of each term
    // also save the date of the first term in the following year
    int[] term = new int[13];

    // adjust the start of the solstice from UT to Beijing standard time
    dSolstice += getBeijingOffset(dSolstice);

    if (dSolstice >= 2321912) {
      // Chinese calendarists begain using true, not mean, position of the sun in 1645
      // todo: fix this  ---  we'll approximate it for now  --  this is a huge calculation
      double termLength = daysInYear((int) dSolstice) / 12;
      for (int x = 0; x < term.length; x++) {
        term[x] = (int) dSolstice;
        dSolstice += termLength;// the beginning of the next term
      }
    }
    else {// before 1645 the mean length is used
      double termLength = daysInYear((int) dSolstice) / 12;
      for (int x = 0; x < term.length; x++) {
        term[x] = (int) dSolstice;
        dSolstice += termLength;// the beginning of the next term
      }
    }
    return term;
  }

  // Calculate the time difference from UT to Beijing standard time
  private double getBeijingOffset(double date) {
    // UT is local mean solar time, relative to midnight, at meridian 0 (Greenwich, England)
    // Beijing is 116°25´ east of Greenwich -- 7h45m40s, but standard time uses 8h0m0s
    // China adopted standard time in 1929 (2425612 == 1 jan 1929)
    double offset = date >= 2425613 ? 8.0 : 7.7611111;
    return offset / 24;
  }

  // 0.0000001 days == 0.0086 seconds

  // # of days in the synodic month (it increases 0.02 seconds per century)

  private double daysInMoon(int jday) {
    return jday < FIRST_JDAY ? 29.53058204 : 29.53058885;
  }

  // # of days in the tropical year (it decreases 0.46 seconds per century)
  private double daysInYear(int jday) {
    return jday < FIRST_JDAY ? 365.2423664 : 365.2421897;
  }

  private double newMoonU(int jday) {
    // for a modern new moon -- 06 Jan 2000 18:14 UT = 2451550.25972
    // for an ancient new moon, we use the eclipse on the evening after the crucifiction
    // of Jesus on 1 Apr 33 about 20 minutes after sunset, ~6:20 pm Jerusalem time
    // = 1733204.26389 local, or 1733204.16611 UT
    // The moon was new 14.76529 days earlier = 1733189.4 +-
    return jday < FIRST_JDAY ? 1733189.4 : 2451550.25972;
  }

  private Integer[] init() {
    DateDataAccess dda = DateDataAccess.getInstance();
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader parser;
    List<Integer> ia = new ArrayList<Integer>();
    try {
      Reader reader = dda.getCJKCalendarReader();
      parser = factory.createXMLStreamReader(reader);

      boolean stillReading = true;
      while (stillReading) {
        int event = parser.next();
        switch (event) {
          case XMLStreamConstants.END_DOCUMENT:
            parser.close();
            reader.close();
            stillReading = false;
            break;
          case XMLStreamConstants.START_ELEMENT:
            if (parser.getLocalName().equals("lunarmonth")) {
              ia.add(Integer.parseInt(parser.getAttributeValue(null, "jday")));
              ia.add(Integer.parseInt(parser.getAttributeValue(null, "leapmoon")));
              ia.add(Integer.parseInt(parser.getAttributeValue(null, "moonbits").substring(2), 16));
            }
            break;
        }
      }
    }
    catch (FileNotFoundException e) {
      String message = "FileNotFoundException (handled): dda.getCJKCalendarReader()";
      LOGGER.error(message);
      throw new RuntimeException(message, e);
    }
    catch (XMLStreamException xse) {
      String message = "XMLStreamException (handled): dda.getCJKCalendarReader()";
      LOGGER.error(message);
      throw new RuntimeException(message, xse);
    }
    catch (IOException ioe) {
      String message = "IOException (handled): dda.getCJKCalendarReader()!";
      LOGGER.error(message);
      throw new RuntimeException(message, ioe);
    }


    return ia.toArray(new Integer[ia.size()]);
  }
}
