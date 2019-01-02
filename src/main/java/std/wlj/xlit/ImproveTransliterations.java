package std.wlj.xlit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.familysearch.standards.place.util.PlaceHelper;

public class ImproveTransliterations {

    static final String BASE_DIR = "D:/xlit/km/truth";

    static final String[][] kmConsonants = {
        /* U+1780 */ { "ក" , "ខ" , "ក" , "ក" },
        /* U+1781 */ { "ខ" , "ខ" , "ក" , "ក" },
        /* U+1782 */ { "គ" , "គ" , "គ" , "គ" },
        /* U+1783 */ { "ឃ" , "ខ" , "ក" , "ក" },
        /* U+1784 */ { "ង" , "ង" , "ង" , "ង" },
        /* U+1785 */ { "ច" , "ឆ" , "ច" , "ច" },
        /* U+1786 */ { "ឆ" , "ឆ" , "ច" , "ច" },
        /* U+1787 */ { "ជ" , "ឈ" , "ជ" , "ជ" },
        /* U+1788 */ { "ឈ" , "ឈ" , "ជ" , "ជ" },
        /* U+1789 */ { "ញ" , "ញ" , "ញ" , "ញ" },
        /* U+178A */ { "ដ" , "ឍ" , "ឍ" , "ឍ" },
        /* U+178B */ { "ឋ" , "ឋ" , "ឋ" , "ឋ" },
        /* U+178C */ { "ឌ" , "ឍ" , "ឍ" , "ឍ" },
        /* U+178D */ { "ឍ" , "ឍ" , "ឍ" , "ឍ" },
        /* U+178E */ { "ណ" , "ណ" , "ណ" , "ណ" },
        /* U+178F */ { "ត" , "ឋ" , "ឋ" , "ឋ" },
        /* U+1790 */ { "ថ" , "ឋ" , "ឋ" , "ឋ" },
        /* U+1791 */ { "ទ" , "ឋ" , "ឋ" , "ឋ" },
        /* U+1792 */ { "ធ" , "ឋ" , "ឋ" , "ឋ" },
        /* U+1793 */ { "ន" , "ណ" , "ណ" , "ណ" },
        /* U+1794 */ { "ប" , "ព" , "ព" , "ព" },
        /* U+1795 */ { "ផ" , "ផ" , "ផ" , "ផ" },
        /* U+1796 */ { "ព" , "ព" , "ព" , "ព" },
        /* U+1797 */ { "ភ" , "ផ" , "ផ" , "ផ" },
        /* U+1798 */ { "ម" , "ម" , "ម" , "ម" },
        /* U+1799 */ { "យ", "យ" , "យ" , "យ" },  // Get clarification [y]
        /* U+179A */ { "រ" , "រ" , "រ" , "រ" },
        /* U+179B */ { "ល" , "ល" , "ល" , "ល" },
        /* U+179C */ { "វ" , "វ" , "វ" , "វ" },
        /* U+179D */ { "ឝ" },
        /* U+179E */ { "ឞ" },
        /* U+179F */ { "ស" , "ស" , "ស" , "ស" },
        /* U+17A0 */ { "ហ" , "ហ" , "ហ" , "ហ" },
        /* U+17A1 */ { "ឡ" , "ល" , "ល" , "ល" },
        /* U+17A2 */ { "អ" , "អ" , "អ" , "អ" },  // Get clarification ['a]
    };

    public static void main(String...args) throws Exception {
        showMappings();
        processFile("train.txt", "train-new.txt");
        processFile("truth.txt", "truth-new.txt");
    }

    static void processFile(String filename, String newFilename) throws IOException {
        List<String> results = new ArrayList<>();
        System.out.println("\n============================================================================");

        List<String> lines = Files.readAllLines(Paths.get(BASE_DIR, filename), StandardCharsets.UTF_8);
        for (String line : lines) {
            String[] en2km = PlaceHelper.split(line, '\t');
            if (en2km.length == 2) {
                String newKm = replaceKm(en2km[1]);
                results.add(en2km[0] + "\t" + newKm);
//                String enXlit = GoogleTranslateUtil.kmToEn(en2km[1])[1];
//                String newEnXlit = GoogleTranslateUtil.kmToEn(newKm)[1];
//                System.out.println(en2km[0] + "\t" + en2km[1] + enXlit + "\t" + newKm + "\t" + newEnXlit + "\t" + getUChars(en2km[1]) + "\t" + getUChars(newKm));
            }
        }

        Files.write(Paths.get(BASE_DIR, newFilename), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }

    static void showMappings() {
        for (String[] row : kmConsonants) {
            if (row.length < 4) {
                System.out.println(Arrays.toString(row));
            } else {
                System.out.println(Arrays.toString(row) + " :: " + Arrays.stream(row).map(km -> getUChars(km)).collect(Collectors.joining(" ", "[", "]")));
            }
        }
    }

    static String replaceKm(String name) {
        StringBuilder buff = new StringBuilder();

        for (int ndx=0;  ndx<name.length();  ndx++) {
            String letter = name.substring(ndx, ndx+1);

            String[] match = Arrays.stream(kmConsonants).filter(row -> row[0].equals(letter)).findFirst().orElse(null);
            if (match == null) {
                buff.append(letter);
            } else if (ndx == 0) {
                buff.append(match[1]);
            } else if (ndx == name.length()-1) {
                buff.append(match[3]);
            } else {
                buff.append(match[2]);
            }
        }

        return buff.toString();
    }

    static String getUChars(String text) {
        if (text == null) return "Uknown";
        return text.chars()
                .mapToObj(ch -> Integer.toHexString(ch).toUpperCase())
                .map(hex -> "U+" + StringUtils.leftPad(hex, 4, "0"))
                .collect(Collectors.joining(" "));
    }
}
