package std.wlj.general;

import java.io.*;

public class AnalyzeTransactionTimes {
    public static void main(String... args) throws IOException {
        int totalCount  = 0;
        int milliCount  = 0;
        int secondCount = 0;
        int minuteCount = 0;
        int hourCount   = 0;

        String prevMilli = "";
        String prevSecond = "";
        String prevMinute = "";
        String prevHour   = "";

        BufferedReader rBuf = new BufferedReader(new FileReader(new File("C:/temp/transaction-all.txt")));
        String line = rBuf.readLine();
        while (line != null) {
            totalCount++;

            int ndx01 = line.indexOf('|');
            int ndx02 = line.indexOf('|', ndx01+1);
            if (ndx01 > 0  &&  ndx02 > ndx01) {
                String dTime = line.substring(ndx01+1, ndx02);
                ndx01 = dTime.indexOf(' ');
                String time = dTime.substring(ndx01+1);

                String tMilli = time;
                String tSecond = time.substring(0, 8);
                String tMinute = time.substring(0, 5);
                String tHour = time.substring(0, 2);

                if (!tMilli.equals(prevMilli)) milliCount++;
                if (!tSecond.equals(prevSecond)) secondCount++;
                if (!tMinute.equals(prevMinute)) minuteCount++;
                if (!tHour.equals(prevHour)) hourCount++;

                prevMilli  = tMilli;
                prevSecond = tSecond;
                prevMinute = tMinute;
                prevHour   = tHour;
            }

            line = rBuf.readLine();
        }
        rBuf.close();

        System.out.println("Total:  " + totalCount);
        System.out.println("Milli:  " + milliCount);
        System.out.println("Second: " + secondCount);
        System.out.println("Minute: " + minuteCount);
        System.out.println("Hour:   " + hourCount);
    }
}
