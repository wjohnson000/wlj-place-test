package std.wlj.general;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TestHashCodePerformance {

    private static final Random random = new Random();

    public static void main(String... args) {
        Set<Integer> deletedRepIds = new HashSet<>();
        for (int i=1;  i<45_000;  i++) {
            deletedRepIds.add(random.nextInt(10_000_000));
        }
        System.out.println("Count: " + deletedRepIds.size());

        int  match = 0;
        long time0 = System.nanoTime();
        for (int i=1;  i<=10_000_000;  i++) {
            if (deletedRepIds.contains(i)) match++;
        }
        long time1 = System.nanoTime();
        System.out.println("Match: " + match);
        System.out.println("rTime: " + (time1 - time0) / 1_000_000D);
        System.exit(0);
    }
}
