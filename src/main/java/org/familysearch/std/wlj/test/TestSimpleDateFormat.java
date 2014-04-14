package org.familysearch.std.wlj.test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Random;


public class TestSimpleDateFormat {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    static {
        DATE_FORMAT.setLenient(false);
    }

    private static Random random = new Random();


    /**
     * Convenience method to generate a DATE given a date-string in the format
     * of "yyyy-mm-dd".  If a non-conformant String is passed in, the return
     * value will be null.
     * 
     * @param dateStr Date string, in the format "yyyy-mm-dd"
     * @return Date
     */
    public static Date formatToDateSync(String dateStr) {
        try {
            synchronized(DATE_FORMAT) {
                return DATE_FORMAT.parse(dateStr);
            }
        } catch(ParseException ex) {
            return null;
        }
    }

    /**
     * Convenience method to generate a DATE given a date-string in the format
     * of "yyyy-mm-dd".  If a non-conformant String is passed in, the return
     * value will be null.
     * 
     * @param dateStr Date string, in the format "yyyy-mm-dd"
     * @return Date
     */
    public static Date formatToDate(String dateStr) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setLenient(false);
            return df.parse(dateStr);
        } catch(ParseException ex) {
            return null;
        }
    }

    /**
     * Convenience method to generate at date-string given a DATE.  If a null date
     * is passed in, the return value will be blank.
     * 
     * @param date Date to be formatted
     * @return String, in the format "yyyy-mm-dd"
     */
    public static String formatFromDateSync(Date date) {
        if (date == null) {
            return "";
        } else {
            synchronized(DATE_FORMAT) {
                return DATE_FORMAT.format(date);
            }
        }
    }

    /**
     * Convenience method to generate at date-string given a DATE.  If a null date
     * is passed in, the return value will be blank.
     * 
     * @param date Date to be formatted
     * @return String, in the format "yyyy-mm-dd"
     */
    public static String formatFromDate(Date date) {
        if (date == null) {
            return "";
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setLenient(false);
            return df.format(date);
        }
    }

    public static void main(String[] args) {
        Date aDate  = null;
        long timeXX = 0;
        long time01 = 0;
        long time02 = 0;
        long time03 = 0;
        long time04 = 0;

        for (int i=0;  i<500000;  i++) {
            int yr = random.nextInt(400) + 1600;
            int mo = random.nextInt(12) + 1;
            int dy = random.nextInt(28) + 1;
            String dateStr = yr + "-" + mo + "-" + dy;

            timeXX = System.nanoTime();
            aDate = formatToDateSync(dateStr);
            timeXX = System.nanoTime() - timeXX;
            time01 += timeXX;

            timeXX = System.nanoTime();
            String what01 = formatFromDateSync(aDate);
            timeXX = System.nanoTime() - timeXX;
            time02 += timeXX;

            timeXX = System.nanoTime();
            aDate = formatToDate(dateStr);
            timeXX = System.nanoTime() - timeXX;
            time03 += timeXX;

            timeXX = System.nanoTime();
            String what02 = formatFromDate(aDate);
            timeXX = System.nanoTime() - timeXX;
            time04 += timeXX;

            if (what01.equals(what02)) {
                continue;
            }
        }

        System.out.println("Time01: " + time01 / 1000000.0);
        System.out.println("Time02: " + time02 / 1000000.0);
        System.out.println("Time03: " + time03 / 1000000.0);
        System.out.println("Time04: " + time04 / 1000000.0);
    }
}
