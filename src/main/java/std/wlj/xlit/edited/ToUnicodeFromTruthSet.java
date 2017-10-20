package std.wlj.xlit.edited;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;

public class ToUnicodeFromTruthSet {

    public static void main(String... args) {
        List<String> results = new ArrayList<>();
        List<String> truthStuff = UtilStuff.readFile("truth.txt");

        for (String truth : truthStuff) {
            String[] chunks = PlaceHelper.split(truth, '\t');
            if (chunks.length > 2) {
                String oldKM = chunks[1];
                String newKM = (chunks[2].trim().isEmpty()) ? chunks[1] : chunks[2];
                List<String> mappedValues = Arrays.asList(
                    chunks[0],
                    (oldKM.equals(newKM)) ? "" : "NEW",
                    oldKM,
                    UtilStuff.getUChars(oldKM),
                    newKM,
                    UtilStuff.getUChars(newKM));
                results.add(mappedValues.stream().collect(Collectors.joining("\t")));
            }
        }

        UtilStuff.writeFile("truth-unicode.txt", results);
    }

}
