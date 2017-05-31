package std.wlj.solrload;

import java.io.File;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.familysearch.standards.place.db.util.FileResultSet;

public class HandleTransactionDates {

    private static long time0 = 0;
    private static long time1 = 0;
    private static Random random = new Random();

    public static void main(String... args) throws SQLException {
        File inputFile = new File("D:/tmp/flat-files/one-ten-thousand/all-transaction.txt");
        FileResultSet fileRS = new FileResultSet();
        fileRS.setSeparator("\\|");
        fileRS.openFile(inputFile);

        Map<Date, Integer> dateToTransxId  = new HashMap<>();
        Map<Integer, Date> transxIdToDateH = new HashMap<>();
        Map<Integer, Date> transxIdToDateT = new TreeMap<>();
        Map<int[], Date> rangeToDate       = new HashMap<>();

        startClock();
        int cnt = 0;
        Date prevDate = null;
        while (fileRS.next()) {
            cnt++;
            int transxId = fileRS.getInt("tran_id");
            Timestamp createTS = fileRS.getTimestamp("create_ts");

            Date minuteDate = truncateSeconds(createTS, 60);
            if (! minuteDate.equals(prevDate)) {
                dateToTransxId.put(minuteDate, transxId);
                transxIdToDateH.put(transxId, minuteDate);
                transxIdToDateT.put(transxId, minuteDate);
            }
            prevDate = minuteDate;
        }
        stopClock();
        showElapsedTime("Transaction-Load time:");

        int prevTransxId = 1;
        prevDate = null;
        for (Map.Entry<Integer, Date> entry : transxIdToDateT.entrySet()) {
            if (prevDate != null) {
                int[] range = { prevTransxId, entry.getKey()-1 };
                rangeToDate.put(range, prevDate);
            }
            prevTransxId = entry.getKey();
            prevDate = entry.getValue();
        }
        int[] range = { prevTransxId, Integer.MAX_VALUE };
        rangeToDate.put(range, prevDate);

        System.out.println("COUNT: " + cnt);
        System.out.println();
        System.out.println("Map.size.aa: " + dateToTransxId.size());
        System.out.println("Map.size.bb: " + transxIdToDateH.size());
        System.out.println("Map.size.cc: " + transxIdToDateT.size());
        System.out.println("Map.size.dd: " + rangeToDate.size());

        Date date01 = getDate01(dateToTransxId, 1111111);
        Date date02 = getDate02(transxIdToDateH, 1111111);
        Date date03 = getDate02(transxIdToDateT, 1111111);
        Date date04 = getDate03(rangeToDate, 1111111);
        System.out.println();
        System.out.println("Date 01: " + date01);
        System.out.println("Date 02: " + date02);
        System.out.println("Date 03: " + date03);
        System.out.println("Date 04: " + date04);

        System.out.println();
        System.out.println("==============================================================================================");
        System.out.println("Date --> Min(transxId)");
        System.out.println("==============================================================================================");
        startClock();
        int badCount = 0;
        for (int i=1;  i<100_000;  i++) {
            Date aDate = getDate01(dateToTransxId, random.nextInt(4_100_000));
            if (aDate == null) badCount++;
        }
        stopClock();
        showElapsedTime("Total time:");
        System.out.println("Bad count: " + badCount);

        System.out.println();
        System.out.println("==============================================================================================");
        System.out.println("Min(transxId) --> Date [HashMap]");
        System.out.println("==============================================================================================");
        startClock();
        badCount = 0;
        for (int i=1;  i<100_000;  i++) {
            Date aDate = getDate02(transxIdToDateH, random.nextInt(4_100_000));
            if (aDate == null) badCount++;
        }
        stopClock();
        showElapsedTime("Total time:");
        System.out.println("Bad count: " + badCount);

        System.out.println();
        System.out.println("==============================================================================================");
        System.out.println("Min(transxId) --> Date [TreeMap]");
        System.out.println("==============================================================================================");
        startClock();
        badCount = 0;
        for (int i=1;  i<100_000;  i++) {
            Date aDate = getDate02(transxIdToDateT, random.nextInt(4_100_000));
            if (aDate == null) badCount++;
        }
        stopClock();
        showElapsedTime("Total time:");
        System.out.println("Bad count: " + badCount);

        System.out.println();
        System.out.println("==============================================================================================");
        System.out.println("int[] range --> Date");
        System.out.println("==============================================================================================");
        startClock();
        badCount = 0;
        for (int i=1;  i<100_000;  i++) {
            Date aDate = getDate03(rangeToDate, random.nextInt(4_100_000));
            if (aDate == null) badCount++;
        }
        stopClock();
        showElapsedTime("Total time:");
        System.out.println("Bad count: " + badCount);

        fileRS.close();
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

    private static Date getDate01(Map<Date, Integer> transxMap, int transxId) {
        int matchTransxId = 0;
        Date matchDate = null;

        for (Map.Entry<Date, Integer> entry : transxMap.entrySet()) {
            if (transxId >= entry.getValue()  &&  entry.getValue() > matchTransxId) {
                matchDate = entry.getKey();
                matchTransxId = entry.getValue();
            }
        }
        return matchDate;
    }

    private static Date getDate02(Map<Integer, Date> transxMap, int transxId) {
        int matchTransxId = 0;
        Date matchDate = null;

        for (Map.Entry<Integer, Date> entry : transxMap.entrySet()) {
            if (transxId >= entry.getKey()  &&  entry.getKey() > matchTransxId) {
                matchDate = entry.getValue();
                matchTransxId = entry.getKey();
            }
        }
        return matchDate;
    }

    private static Date getDate03(Map<int[], Date> transxMap, int transxId) {
        Date matchDate = null;

        for (Map.Entry<int[], Date> entry : transxMap.entrySet()) {
            int[] range = entry.getKey();
            if (transxId >= range[0]  &&  transxId <= range[1]) {
                matchDate = entry.getValue();
                break;
            }
        }
        return matchDate;
    }
}
