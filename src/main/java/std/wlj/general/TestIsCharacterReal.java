package std.wlj.general;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.familysearch.standards.place.util.PlaceHelper;

public class TestIsCharacterReal {
    public static void main(String...args) throws IOException {
        Map<int[], String> ranges = getUnicodeRanges();
        
        System.out.println("Characters that are not LETTERs but are ALPHABETIC ...");
        for (int ichar=Character.MIN_CODE_POINT;  ichar<Character.MAX_CODE_POINT;  ichar++) {
            if (! Character.isLetter(ichar)  &&  Character.isAlphabetic(ichar)) {
                final int icharx = ichar;
                String range = ranges.entrySet().stream()
                        .filter(rr -> icharx >= rr.getKey()[0] && icharx <= rr.getKey()[1])
                        .findFirst()
                        .map(rr -> rr.getValue())
                        .orElse("Unknown");

                String format = "U+000%s|%d|%s|%s|%s";
                if (icharx < '\u0100') {
                    format = "U+00%s|%d|%s|%s|%s";
                } else if (icharx < '\u1000') {
                    format = "U+0%s|%d|%s|%s|%s";
                } else {
                    format = "U+0%s|%d|%s|%s|%s";
                }

                char[] chars = Character.toChars(ichar);
                String whatever = String.format(format, Integer.toHexString(icharx), icharx, String.valueOf(chars), Character.getName(icharx), range);
                System.out.println(whatever);
            }
        }
//        for (char ch ='\u0000';  ch<='\uFFFE';  ch++) {
//            if (! Character.isLetter(ch)  &&  Character.isAlphabetic(ch)) {
//                final char chx = ch;
//                String range = ranges.entrySet().stream()
//                    .filter(rr -> chx >= rr.getKey()[0] && chx <= rr.getKey()[1])
//                    .findFirst()
//                    .map(rr -> rr.getValue())
//                    .orElse("Unknown");
//
//                String whatever = "";
//                if (ch < '\u0100') {
//                    whatever = String.format("U+00%s|%d|%s|%s", Integer.toHexString(ch), (int)ch, String.valueOf(ch), range);
//                } else if (ch < '\u1000') {
//                    whatever = String.format("U+0%s|%d|%s|%s", Integer.toHexString(ch), (int)ch, String.valueOf(ch), range);
//                } else {
//                    whatever = String.format("U+%s|%d|%s|%s", Integer.toHexString(ch), (int)ch, String.valueOf(ch), range);
//                }
//                System.out.println(whatever);
//            }
//        }
    }

    static Map<int[], String> getUnicodeRanges() throws IOException {
        Map<int[], String> results = new HashMap<>();

        List<String> ranges = Files.readAllLines(Paths.get("C:/temp/unicode-chart.txt"), Charset.forName("UTF-8"));
        for (String range : ranges) {
            String[] chunks = PlaceHelper.split(range, '|');
            if (chunks.length == 3) {
                String from = chunks[0].trim().substring(2);
                String to   = chunks[1].trim().substring(2);
                String name = chunks[2];
                int[] key = new int[] { Integer.parseInt(from, 16), Integer.parseInt(to, 16) };
                results.put(key, name);
            }
        }

        return results;
    }

    static Map<int[], String> getUnicodeRanges_OLD() throws IOException {
        Map<int[], String> results = new HashMap<>();

        List<String> ranges = Files.readAllLines(Paths.get("C:/temp/unicode-chart.txt"), Charset.forName("UTF-8"));
        for (String range : ranges) {
            int ndx0 = range.indexOf('(');
            int ndx1 = range.indexOf(')');
            if (ndx0 > 0  &&  ndx1 > ndx0) {
                String yeah = range.substring(ndx0+1, ndx1);
                String[] chunks = PlaceHelper.split(yeah, ',');
                if (chunks.length == 3) {
                    String from = chunks[0].trim().substring(2);
                    String to   = chunks[1].trim().substring(2);
                    String name = chunks[2].replace('\'', ' ').trim();
                    int[] key = new int[] { Integer.parseInt(from, 16), Integer.parseInt(to, 16) };
                    results.put(key, name);
                }
            }
        }

        return results;
    }
}
