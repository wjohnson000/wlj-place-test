package std.wlj.general;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.util.PlaceHelper;

public class TestNormalization {

    public static void main(String...args) throws IOException {
        List<String> placeNames = Files.readAllLines(Paths.get("C:/temp/important/places-search-text.txt"), StandardCharsets.UTF_8);
        System.out.println("PlaceNames.count=" + placeNames.size());

        List<String> oldNew = new ArrayList<>();

        long time0 = System.nanoTime();
        for (String name : placeNames) {
            String nName = PlaceHelper.normalize(name);
            oldNew.add(name + "|" + nName);
        }
        long time1 = System.nanoTime();
        System.out.println("Total: " + (time1-time0)/1_000_000.0);

        Files.write(Paths.get("C:/temp/normalize-new.txt"), oldNew, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
