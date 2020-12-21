/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

/**
 * @author wjohnson000
 *
 */
public class TestFormat {
    public static void main(String... args) {
        System.out.println("FMT: " +   1 + " --> " + format02_A(1));
        System.out.println("FMT: " +  11 + " --> " + format02_A(11));
        System.out.println("FMT: " + 111 + " --> " + format02_A(111));
//        long time0 = System.nanoTime();
//        for (int ndx=0;  ndx<1_000_000;  ndx++) {
//            for (int min=0;  min<60;  min++) {
//                format02_B(min);
//            }
//        }
//        long time1 = System.nanoTime();
//        System.out.println("TT: " + (time1 - time0) / 1_000_000.0);

        long timeA = System.nanoTime();
        for (int ndx=0;  ndx<1_000_000;  ndx++) {
            for (int year=500;  year<2020;  year+=13) {
                format04_C(year);
            }
        }
        long timeB = System.nanoTime();
        System.out.println("TT: " + (timeB - timeA) / 1_000_000.0);
    }

    static String format02_A(int number) {
        return String.format("%02d", number);
    }

    static String format02_B(int number) {
        return (number < 10) ? ("0" + number) : ("" + number);
    }

    static String format04_A(int number) {
        return String.format("%04d", number);
    }

    static String format04_B(int number) {
        if (number < 10) {
            return "000" + number;
        } else if (number < 100) {
            return "00" + number;
        } else if (number < 1000) {
            return "0" + number;
        } else {
            return "" + number;
        }
    }

    static String format04_C(int number) {
        return formatXX_B(number, 4);
    }

    static String formatXX_B(int number, int length) {
        StringBuilder buff = new StringBuilder(length);
        buff.append(number);
        while (buff.length() < length) {
            buff.insert(0, '0');
        }
        return buff.toString();
    }
}
