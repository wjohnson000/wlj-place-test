package std.wlj.general;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.familysearch.standards.core.LocalizedData;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.lang.transform.RemoveDiacriticsTransformer;

public class TestDiacritics {

    public static void main(String...args) throws IOException {
        List<String> placeNames = Files.readAllLines(Paths.get("C:/temp/important/places-search-text.txt"), StandardCharsets.UTF_8);
        System.out.println("PlaceNames.count=" + placeNames.size());

//        Matcher mm;
//        Pattern diacriticMarks = Pattern.compile(".*[ÆÐØÞ].*");
//
//        mm = diacriticMarks.matcher("Saugus town (south part incl. Cliftondale), Essex, Massachusetts, United States");
//        System.out.println("Has DIAC? " + mm.matches());
//
//        mm = diacriticMarks.matcher("Saugus town (south Ð part incl. Cliftondale), Essex, Massachusetts, United States");
//        System.out.println("Has DIAC? " + mm.matches());

        int badC = 0;
        StdLocale enLocale = StdLocale.ENGLISH;
        RemoveDiacriticsTransformer diacTransx = new RemoveDiacriticsTransformer();
        for (String name : placeNames) {
            LocalizedData<String> localName = new LocalizedData<>(name, enLocale);

            long time0o = System.nanoTime();
            LocalizedData<String> nNameO = diacTransx.transform(localName);
            long time1o = System.nanoTime();
            long timeEo = time1o - time0o;

//            long time0n = System.nanoTime();
//            LocalizedData<String> nNameN = diacTransx.transformX(localName);
//            long time1n = System.nanoTime();
//            long timeEn = time1n - time0n;

//            if (! nNameO.get().equals(nNameN.get())) {
//                System.out.println(" BAD ... " + name + " --> " + nNameO.get() + " vs. " + nNameN.get());
//            }

            if (timeEo > 50_000) {
                badC++;
                System.out.println();
                System.out.println(name + " --> " + nNameO.get() + ": " + timeEo/1_000_000.0);
//                System.out.println(name + " --> " + nNameN.get() + ": " + timeEn/1_000_000.0);
            }
        }

        System.out.println("\nBadCount=" + badC);
    }
}
