package std.wlj.date.v2;

import org.familysearch.standards.core.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A table containing calendar information for China.  Each entry consists of 3 integers:
 * <ul>
 *   <li>The Julian day number of the first day of the year</li>
 *   <li>A bitmap indicating the number of days in each month</li>
 *   <li>a number indicating which moon, if any, is the intercalary moon</li>
 * </ul>
 * 
 * <p/>
 * Most of this code is adapted from code Copyright (c) 1992-2001 Dandelion Corporation,
 * Licensed to Intellectual Reserve, Inc.
 *
 * @author Pete Blake
 * @author Wayne Johnson, May, 2018 (reworked)
 */
public class CJKCalendarTable {

    private static final Logger LOGGER = new Logger(CJKCalendarTable.class);
    private static final CJKCalendarTable CJKCT = new CJKCalendarTable();

    private static final String CJK_CALENDAR_FILENAME = "/std/wlj/date/v2/cjkCalendarTable.xml";

    // a known winter solstice -- 22 Dec 1990 03:08 UT
    private static final double SOLSTICE_0U = 2448247.63056;

    static final int FIRST_YEAR = 5; // first year in the table
    static final int LAST_YEAR = 2025; // last year from the table
    static final int FIRST_JDAY = 1722913; // 0005.01.29 Julian -- first day in the table
    public static final int LAST_JDAY = 2460705; // 2025.01.29 Gregorian -- last day in the table

    private final int[][] lunarTable;

    // hide private constructor
    private CJKCalendarTable() {
        lunarTable = init();
    }

    public static CJKCalendarTable getInstance() {
        return CJKCT;
    }

    public LunarYearDescription getLunarYear(int y) {
        if (y >= FIRST_YEAR  &&  y <= LAST_YEAR) {
            int x = y - FIRST_YEAR;
            return new LunarYearDescription(lunarTable[x][0], lunarTable[x][1], lunarTable[x][2]);
        }

        // y is out of the range of the table
        //  calculate the jday of the vernal equinox, then calculate a LunarYearDescription
        return getLunarYear(CalendarUtil.vernalEquinox(y));
    }

    public LunarYearDescription getLunarYear(AstroDay astro) {
        final int jday = astro.value();
        if (jday >= FIRST_JDAY && jday <= LAST_JDAY) {
            int xx = -1;
            for (int ndx=0;  ndx<lunarTable.length-1 && xx == -1;  ndx++) {
                if (jday >= lunarTable[ndx][0]  &&  jday < lunarTable[ndx+1][0]) {
                    xx = ndx;
                }
            }
            if (xx == -1  &&  jday >= lunarTable[lunarTable.length-1][0]) {
                xx = lunarTable.length-1;
            }

            if (xx >= 0) {
                return new LunarYearDescription(lunarTable[xx][0], lunarTable[xx][1], lunarTable[xx][2]);
            }
        }

        // ======== calculate the solstice before and after the date ========

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
        // moons[0] contains the moon immediately preceding the solstice
        // moons[1] contains the first moon after the solstice, etc.
        int[] moons = new int[15];
        moons[0] = (int) (dMoon - dDaysInMoon);// the moon preceding the winter solstice
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
        int intercalaryMoon = 0; // intercalary moon number
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
        } else {
            // before 1645 the mean length is used
            double termLength = daysInYear((int) dSolstice) / 12;
            for (int x = 0; x < term.length; x++) {
                term[x] = (int) dSolstice;
                dSolstice += termLength;// the beginning of the next term
            }
        }
        return term;
    }

    private double getBeijingOffset(double date) {
        // UT is local mean solar time, relative to midnight, at meridian 0 (Greenwich, England)
        // Beijing is 116°25´ east of Greenwich -- 7h45m40s, but standard time uses 8h0m0s
        // China adopted standard time in 1929 (2425612 == 1 jan 1929)
        double offset = date >= 2425613 ? 8.0 : 7.7611111;
        return offset / 24;
    }

    private double daysInMoon(int jday) {
        // # of days in the synodic month (it increases 0.02 seconds per century)
        return jday < FIRST_JDAY ? 29.53058204 : 29.53058885;
    }

    private double daysInYear(int jday) {
        // # of days in the tropical year (it decreases 0.46 seconds per century)
        return jday < FIRST_JDAY ? 365.2423664 : 365.2421897;
    }

    private double newMoonU(int jday) {
        // For a modern new moon -- 06 Jan 2000 18:14 UT = 2451550.25972
        // For an ancient new moon, we use the eclipse on the evening after the crucifiction
        // of Jesus on 1 Apr 33 about 20 minutes after sunset, ~6:20 pm Jerusalem time
        // = 1733204.26389 local, or 1733204.16611 UT
        // The moon was new 14.76529 days earlier = 1733189.4 +-
        return jday < FIRST_JDAY ? 1733189.4 : 2451550.25972;
    }

    private int[][] init() {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader parser;
        List<int[]> yearDetails = new ArrayList<>();

        try {
            Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(CJK_CALENDAR_FILENAME), StandardCharsets.UTF_8);
            parser = factory.createXMLStreamReader(reader);

            boolean stillReading = true;
            while (stillReading) {
                switch (parser.next()) {
                case XMLStreamConstants.END_DOCUMENT:
                    parser.close();
                    reader.close();
                    stillReading = false;
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    if (parser.getLocalName().equals("lunarmonth")) {
                        int[] data = new int[3];
                        data[0] = Integer.parseInt(parser.getAttributeValue(null, "jday"));
                        data[1] = Integer.parseInt(parser.getAttributeValue(null, "leapmoon"));
                        data[2] = Integer.parseInt(parser.getAttributeValue(null, "moonbits").substring(2), 16);
                        yearDetails.add(data);
                    }
                    break;
                }
            }
        } catch (XMLStreamException | IOException e) {
            String message = e.getClass().getName() + " (handled): getCJKCalendarReader()";
            LOGGER.error(message);
            throw new RuntimeException(message, e);
        }

        return yearDetails.stream().toArray(int[][]::new);
    }
}
