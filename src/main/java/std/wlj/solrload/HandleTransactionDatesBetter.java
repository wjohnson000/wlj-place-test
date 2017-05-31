package std.wlj.solrload;

import java.io.File;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.familysearch.standards.place.db.util.FileResultSet;

public class HandleTransactionDatesBetter {

    private static class RangeDate {
        int rangeStart = 0;
        int rangeEnd   = 0;
        Date date      = null;

        public RangeDate(int rangeStart, int rangeEnd, Date date) {
            this.rangeStart = rangeStart;
            this.rangeEnd   = rangeEnd;
            this.date       = date;
        }
    }

    private static long time0 = 0;
    private static long time1 = 0;
    private static Random random = new Random();

    public static void main(String... args) throws SQLException {
        List<RangeDate>  rangeAndDateX     = new ArrayList<>();

        File inputFile = new File("D:/tmp/flat-files/one-ten-thousand/all-transaction.txt");
        FileResultSet fileRS = new FileResultSet();
        fileRS.setSeparator("\\|");
        fileRS.openFile(inputFile);

        startClock();
        Date prevDate = null;
        RangeDate rangeDate = null;
        while (fileRS.next()) {
            int transxId = fileRS.getInt("tran_id");
            Timestamp createTS = fileRS.getTimestamp("create_ts");
            Date minuteDate = truncateSeconds(createTS, 20);
            if (! minuteDate.equals(prevDate)) {
                rangeDate = new RangeDate(transxId, transxId, minuteDate);
                rangeAndDateX.add(rangeDate);
            } else if (rangeDate != null) {
                rangeDate.rangeEnd = transxId;
            }
            prevDate = minuteDate;
        }
        if (rangeDate != null) {
            rangeDate.rangeEnd = Integer.MAX_VALUE;
        }
        fileRS.close();
        stopClock();

        showElapsedTime("Transaction-Load time:");
        System.out.println("Map.size.ff: " + rangeAndDateX.size());

        int badCount = 0;

        System.out.println();
        System.out.println("==============================================================================================");
        System.out.println("int[] range &&& Date");
        System.out.println("==============================================================================================");
        startClock();
        badCount = 0;
        for (int i=1;  i<25_000_000;  i++) {
            int transxId = random.nextInt(4_100_000) + 1;
            Date aDate = getDateForTransactionId(rangeAndDateX, transxId);
            if (aDate == null) {
                badCount++;
                System.out.println("  bad: " + transxId);
            }
        }
        stopClock();
        showElapsedTime("Total time:");
        System.out.println("Bad count: " + badCount);
    }

    private static Date truncateSeconds(Timestamp ts, int granularity) {
        long millis = ts.getTime();
        long seconds = millis / 1000;
        long minutes = seconds / granularity;
        return new Date(minutes * granularity * 1000);
    }

    private static void startClock() {
        time0 = System.nanoTime();
    }

    private static void stopClock() {
        time1 = System.nanoTime();
    }

    private static void showElapsedTime(String message) {
        System.out.println(message + " --> " + (time1 - time0)/1_000_000.0);
    }

    private static Date getDateForTransactionId(List<RangeDate> rangeAndDate, int transxId) {
        int ndx0 = 0;
        int ndx1 = rangeAndDate.size() - 1;
        int mid = (ndx0 + ndx1) / 2;
        int prevMid = mid-1;
        Date result = null;

        while (mid != prevMid) {
            RangeDate rangeDate = rangeAndDate.get(mid);
            if (transxId >= rangeDate.rangeStart  &&  transxId <= rangeDate.rangeEnd) {
                result = rangeDate.date;
                break;
            } else if (transxId < rangeDate.rangeStart) {
                ndx1 = mid - 1;
            } else {
                ndx0 = mid + 1;
            }
            prevMid = mid;
            mid = (ndx0 + ndx1) / 2;
        }

        return result;
    }

}
