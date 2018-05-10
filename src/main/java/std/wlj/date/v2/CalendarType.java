package std.wlj.date.v2;

/**
 * An enumeration of the calendars available in the Unified System
 *
 * @author Pete Blake
 * @author Wayne Johnson, May, 2018 (reworked)
 */
public enum CalendarType {

    /**
     * Western calendar -- the generic calendar used in Western culture. It uses Gregorian calendar
     * rules for Julian Day number 2299161 (15 Oct 1582) and later, and Julian calendar rules for
     * 2299160 and earlier.
     */
    WESTERN(1, "Western"),

    /**
     * Gregorian calendar -- introduced by Pope Gregory in 1582 to overcome defects in the Julian calendar,
     * i.e. leap year calculations.
     */
    GREGORIAN(2, "Gregorian"),

    /**
     * Julian calendar -- introduced by Julius Caesar in 45 BC.
     */
    JULIAN(3, "Julian"),

    /**
     * The generic lunar calendar used in Asia.
     */
    CJK_LUNAR(4, "CJK Lunar"),

    /**
     * The lunar calendar used in China prior to the adoption of the Gregorian.
     */
    CHINESE_LUNAR(5, "Chinese Lunar"),

    /**
     * The lunar calendar used in Japan prior to the adoption of the Gregorian.
     */
    JAPANESE_LUNAR(6, "Japanese Lunar"),

    /**
     * The lunar calendar used in Korea prior to the adoption of the Gregorian.
     */
    KOREAN_LUNAR(7, "Korean Lunar"),
    KOREAN_DAN_KI(8, "Korean Dan-Ki"),

    MIXED(10, "Mixed");

    CalendarType(int ordinal, String name) {
        this.ordinal = ordinal;
        this.name = name;
    }

    private int ordinal;
    private String name;

    public String toString() {
        return name;
    }

    public int getID() {
        return ordinal;
    }

}

