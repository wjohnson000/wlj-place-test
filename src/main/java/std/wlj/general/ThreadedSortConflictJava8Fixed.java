package std.wlj.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ThreadedSortConflictJava8Fixed {
    static class Mungo {
        int id;
        String str;
        String otherStr;
        public Mungo(int id, String str, String otherStr) {
            this.id = id;
            this.str = str;
            this.otherStr = otherStr;
        }
        @Override
        public String toString() {
            return id + " :: " + str;
        }
    }

    static List<Mungo> mungos = new ArrayList<>(Arrays.asList(
        new Mungo( 1, "one", "another one"),
        new Mungo( 2, "two", "another two"),
        new Mungo( 3, "three", "another three"),
        new Mungo( 4, "four", "another four"),
        new Mungo( 5, "five", "another five"),
        new Mungo( 6, "six", "another six"),
        new Mungo( 7, "seven", "another seven"),
        new Mungo( 8, "eight", "another eight"),
        new Mungo( 9, "nine", "another nine"),
        new Mungo(10, "ten", "another ten"),
        new Mungo(11, "eleven", "another eleven"),
        new Mungo(12, "twelve", "another twelve")
    ));

    public static void main(String... args) throws InterruptedException {
        sortAndPrintA();
        sortAndPrintB();

        for (int i=0;  i<10;  i++) {
            Runnable runn = () -> { for (int j=0;  j<333;  j++) { sortAndPrintA(); sortAndPrintB(); } };
            Thread thr = new Thread(runn);
            thr.setName("thread-" + i);
            thr.start();

            if (i%3 == 2)  deleteOne();
        }

        Thread.sleep(3333L);
        System.out.println("DONE ... !!");
        System.exit(0);
    }

    static void deleteOne() {
        System.out.println("... HERE ...");
        mungos.remove(5);
        System.out.println("... DONE ...");
    }

    static void sortAndPrintA() {
        List<Mungo> newMungos = mungos.stream()
            .sorted((m1, m2) -> m1.str.compareToIgnoreCase(m2.str))
            .collect(Collectors.toList());

        StringBuilder buff = new StringBuilder();
        buff.append(Thread.currentThread().getName() + " --> A.");
        for (Mungo mungo : newMungos) {
            buff.append("|" + mungo);
        }
        System.out.println(buff.toString());
    }

    static void sortAndPrintB() {
        List<Mungo> newMungos = mungos.stream()
            .sorted((m1, m2) -> m1.id - m2.id)
            .collect(Collectors.toList());

        StringBuilder buff = new StringBuilder();
        buff.append(Thread.currentThread().getName() + " --> B.");
        for (Mungo mungo : newMungos) {
            buff.append("|" + mungo);
        }
        System.out.println(buff.toString());
    }
}
