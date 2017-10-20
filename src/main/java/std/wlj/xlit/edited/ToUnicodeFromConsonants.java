package std.wlj.xlit.edited;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;

public class ToUnicodeFromConsonants {

    public static void main(String... args) {
        List<String> results = new ArrayList<>();
        List<String> truthStuff = UtilStuff.readFile("consonants.txt");

        truthStuff.remove(0);
        for (String truth : truthStuff) {
            String[] chunks = PlaceHelper.split(truth, '\t');
            if (chunks.length > 9) {
                List<String> mappedValues = Arrays.asList(
                    chunks[0],
                    chunks[1],
                    chunks[2],
                    chunks[4],
                    UtilStuff.getUChars(chunks[4]),
                    chunks[5],
                    UtilStuff.getUChars(chunks[5]),
                    chunks[6],
                    UtilStuff.getUChars(chunks[6]),
                    chunks[7],
                    UtilStuff.getUChars(chunks[7]),
                    chunks[8],
                    UtilStuff.getUChars(chunks[8]),
                    chunks[9],
                    UtilStuff.getUChars(chunks[9])
                );
                results.add(mappedValues.stream().collect(Collectors.joining("\t")));
            }
        }

        UtilStuff.writeFile("consonants-unicode.txt", results);
    }

}
