/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

/**
 * DMY - date structure class for representing a day, month, year. Used with methods that must modify the
 * day, month, and year for a date object.
 * <p/>
 * If the value of any element is zero, then the corresponding piece was missing in the original date string.
 * <p/>
 * Years are in "external format"
 * <ul>
 *   <li>1 == 1AD</li>
 *   <li>-1 == 1BC</li>
 *   <li>0 == missing year</li>
 * </ul>
 *
 * NOTE: this class is not intended to be immutable, hence the presence of "setXxx(...)" methods.
 * 
 * @author Pete Blake
 * @author Wayne Johnson, May, 2018 (reworked)
 */
public class DMY {

    private int day;
    private int month;
    private int year;
    private boolean isIntercalaryMonth;  // true if month is the intercalary month in year

    public DMY(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
        isIntercalaryMonth = false;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDay() {
        return this.day;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getMonth() {
        return this.month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return this.year;
    }

    public void setIntercalary(boolean b) {
        this.isIntercalaryMonth = b;
    }

    /**
     * An intercalary month is added to a year about every 3 years to keep the beginning of the year
     * aligned with the Winter Solstice. The month has the same number as the one preceding it, so
     * this check is needed to distinguish between the two.
     *
     * @return true if this month is the intercalary month in year, false if it is a normal month.
     */
    public boolean isIntercalary() {
        return isIntercalaryMonth;
    }
}
