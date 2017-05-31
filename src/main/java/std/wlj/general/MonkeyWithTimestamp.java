package std.wlj.general;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;

public class MonkeyWithTimestamp {
    public static void main(String...args) throws ParseException {
        String timeStr = "2010-01-01 18:19:20.1234567";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Timestamp ts = new java.sql.Timestamp(sdf.parse(timeStr).getTime());
            System.out.println("TS: " + ts);
        } catch(Exception ex) {
            System.out.println("Unable to parse time-string w/ SimpleDateFormat: " + ex.getMessage());
        }

        DateTimeFormatter dtf01 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.[SSS]").withResolverStyle(ResolverStyle.SMART);
        try {
            LocalDateTime ldt = LocalDateTime.parse(timeStr, dtf01);
            System.out.println("LDT: " + ldt);
        } catch(Exception ex) {
            System.out.println("Unable to parse time-string w/ LocalDateTime: " + ex.getMessage() + " --> " + ex.getClass().getName());
        }

        try {
            TemporalAccessor tacc = dtf01.parse(timeStr);
            System.out.println("TACC-01: " + tacc);
        } catch(Exception ex) {
            System.out.println("Unable to parse time-string w/ DateTimeFormatter.parse: " + ex.getMessage() + " --> " + ex.getClass().getName());
        }

        try {
            TemporalAccessor tacc = dtf01.parse(timeStr, new ParsePosition(0));
            System.out.println("TACC-02: " + tacc + " --> " + tacc.getClass().getName());
            LocalDateTime ldt = LocalDateTime.from(tacc);
            System.out.println(" LDT-02: " + ldt);
            Timestamp ts = Timestamp.valueOf(ldt); 
            System.out.println("  TS-02: " + ts);
        } catch(Exception ex) {
            System.out.println("Unable to parse time-string w/ DateTimeFormatter.parse: " + ex.getMessage());
        }
    }
}
