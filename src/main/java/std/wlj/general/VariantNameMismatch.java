package std.wlj.general;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.util.PlaceHelper;

public class VariantNameMismatch {

    public static void main(String...args) throws IOException {
        List<String> variants = Files.readAllLines(Paths.get("C:/temp/place-name-all.txt"), StandardCharsets.UTF_8);
        System.out.println("CNT: " + variants.size());

        int badCnt = 0;
        for (String variant : variants) {
            String[] chunks = PlaceHelper.split(variant, '|');
            if (chunks.length > 2) {
                String nameOne = PlaceHelper.normalize(chunks[2]).toLowerCase(new StdLocale(chunks[1]).getLocale());
                String nameTwo = PlaceHelper.normalize(chunks[2]).toLowerCase();
                if (! nameOne.equals(nameTwo)) {
                    badCnt++;
                    System.out.println(variant + "  >>" + nameOne + "<<   >>" + nameTwo + "<<");
                }
            }
        }
        System.out.println("\nBadCount=" + badCnt);
    }
}
