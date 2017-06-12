package std.wlj.solrload;

import java.io.File;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.familysearch.standards.loader.sql.FileResultSet;

public class HandleTransactionDatesGranularity {

    private static long time0 = 0;
    private static long time1 = 0;

    public static void main(String... args) throws SQLException {
        File inputFile = new File("D:/tmp/flat-files/one-ten-thousand/all-transaction.txt");
        FileResultSet fileRS = new FileResultSet();
        fileRS.setSeparator("\\|");
        fileRS.openFile(inputFile);

        startClock();
        Map<Date, Integer> dateToTransxIdMap60Sec = new HashMap<>();
        Map<Date, Integer> dateToTransxIdMap20Sec = new HashMap<>();
        Map<Date, Integer> dateToTransxIdMap10Sec = new HashMap<>();

        int cnt = 0;
        Date prevDate60 = null;
        Date prevDate20 = null;
        Date prevDate10 = null;
        while (fileRS.next()) {
            cnt++;
            int transxId = fileRS.getInt("tran_id");
            Timestamp createTS = fileRS.getTimestamp("create_ts");

            Date minuteDate = truncateSeconds60(createTS);
            if (! minuteDate.equals(prevDate60)) {
                dateToTransxIdMap60Sec.put(minuteDate, transxId);
            }
            prevDate60 = minuteDate;

            minuteDate = truncateSeconds20(createTS);
            if (! minuteDate.equals(prevDate20)) {
                dateToTransxIdMap20Sec.put(minuteDate, transxId);
            }
            prevDate20 = minuteDate;

            minuteDate = truncateSeconds10(createTS);
            if (! minuteDate.equals(prevDate10)) {
                dateToTransxIdMap10Sec.put(minuteDate, transxId);
            }
            prevDate10 = minuteDate;
        }
        stopClock();

        showElapsedTime("Transaction-Load time:");
        System.out.println("COUNT: " + cnt);
        System.out.println("Map.size.60: " + dateToTransxIdMap60Sec.size());
        System.out.println("Map.size.20: " + dateToTransxIdMap20Sec.size());
        System.out.println("Map.size.10: " + dateToTransxIdMap10Sec.size());

        checkDate(1, dateToTransxIdMap60Sec, dateToTransxIdMap20Sec, dateToTransxIdMap10Sec);
        checkDate(101, dateToTransxIdMap60Sec, dateToTransxIdMap20Sec, dateToTransxIdMap10Sec);
        checkDate(10101, dateToTransxIdMap60Sec, dateToTransxIdMap20Sec, dateToTransxIdMap10Sec);
        checkDate(1010101, dateToTransxIdMap60Sec, dateToTransxIdMap20Sec, dateToTransxIdMap10Sec);
        checkDate(101010101, dateToTransxIdMap60Sec, dateToTransxIdMap20Sec, dateToTransxIdMap10Sec);

        fileRS.close();
    }

    private static Date truncateSeconds60(Timestamp ts) {
        long millis = ts.getTime();
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        return new Date(minutes * 60 * 1000);
    }

    private static Date truncateSeconds20(Timestamp ts) {
        long millis = ts.getTime();
        long seconds = millis / 1000;
        long minutes = seconds / 20;
        return new Date(minutes * 20 * 1000);
    }

    private static Date truncateSeconds10(Timestamp ts) {
        long millis = ts.getTime();
        long seconds = millis / 1000;
        long minutes = seconds / 10;
        return new Date(minutes * 10 * 1000);
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

    private static void checkDate(int transxId, Map<Date,Integer> map60, Map<Date,Integer> map20, Map<Date,Integer> map10) {
        Date date60 = getDateForTransxId(map60, transxId);
        Date date20 = getDateForTransxId(map20, transxId);
        Date date10 = getDateForTransxId(map10, transxId);
        System.out.println("Match for " + transxId + ": " + date60 + " .. " + date20 + " .. " + date10);
    }

    private static Date getDateForTransxId(Map<Date, Integer> dateToTransxIdMap, int transxId) {
        int matchTransxId = 0;
        Date matchDate = null;

        for (Map.Entry<Date, Integer> entry : dateToTransxIdMap.entrySet()) {
            if (transxId >= entry.getValue()  &&  entry.getValue() > matchTransxId) {
                matchDate = entry.getKey();
                matchTransxId = entry.getValue();
            }
        }
        return matchDate;
    }
}
