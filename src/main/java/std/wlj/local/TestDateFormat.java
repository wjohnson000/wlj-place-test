package std.wlj.local;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class TestDateFormat {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    static {
        DATE_FORMAT.setLenient(false);
    }

    private static final Random random = new Random();

    static long nnow   = 0;
    static long time01 = 0;
    static long time02 = 0;


    public static void main(String... args) {

        Thread[] threads = new Thread[3];
        for (int i=0;  i<threads.length;  i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i=0;  i<1000000;  i++) {
                        int year  = random.nextInt(200) + 1800;
                        int month = random.nextInt(12) + 1;
                        int day   = random.nextInt(28) + 1;
                        String dateStr = year + "-" + month + "-" + day;

                        nnow = System.nanoTime();
                        Date aDate = formatToDateSafe(dateStr);
                        time01 += (System.nanoTime() - nnow);

                        nnow = System.nanoTime();
                        formatFromDateSafe(aDate);
                        time02 += (System.nanoTime() - nnow);
                    }
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        boolean running = true;
        while (running) {
            running = false;
            for (Thread thread : threads) {
                running |= thread.isAlive();
            }
            try { Thread.sleep(25L); } catch(Exception ex) { }
        }

        System.out.println("Time01: " + (time01 / 1000000.0));
        System.out.println("Time02: " + (time02 / 1000000.0));
}

    /**
     * Generate a DATE given a date-string in the format of "yyyy-mm-dd".
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
     * Generate at date-string given a DATE.
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
     * Generate a DATE given a date-string in the format of "yyyy-mm-dd".
     * 
     * @param dateStr Date string, in the format "yyyy-mm-dd"
     * @return Date
     */
    public static Date formatToDateSafe(String dateStr) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setLenient(false);
            return df.parse(dateStr);
        } catch(ParseException ex) {
            return null;
        }
    }

    /**
     * Generate at date-string given a DATE.
     * 
     * @param date Date to be formatted
     * @return String, in the format "yyyy-mm-dd"
     */
    public static String formatFromDateSafe(Date date) {
        if (date == null) {
            return "";
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setLenient(false);
            return df.format(date);
        }
    }

}
