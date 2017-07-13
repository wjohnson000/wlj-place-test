package std.wlj.general;

import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;

public class DumpUnicodeBlocks {

    public static void main(String...args) throws IOException {
        Map<UnicodeBlock, int[]> rangeMap = new HashMap<>();

        for (int ichar=Character.MIN_CODE_POINT;  ichar<Character.MAX_CODE_POINT;  ichar++) {
            UnicodeBlock block = UnicodeBlock.of(ichar);
            if (block != null) {
                int[] range = rangeMap.get(block);
                if (range == null) {
                    range = new int[] { ichar, ichar };
                    rangeMap.put(block, range);
                } else {
                    range[1] = ichar;
                }
            }
        }

        List<String> ranges = rangeMap.entrySet().stream()
            .map(entry -> asHex(entry.getValue()[0]) + "|" + asHex(entry.getValue()[1]) + "|" + prettify(entry.getKey().toString()))
            .collect(Collectors.toList());
        Collections.sort(ranges);
        ranges.forEach(System.out::println);
    }

    static String asHex(int val) {
        String hex = "00000" + Integer.toHexString(val).toUpperCase();
        return "U+" + hex.substring(hex.length()-5);
    }

    static String prettify(String name) {
        String[] chunks = PlaceHelper.split(name, '_');
        return Arrays.stream(chunks)
            .map(xx -> (xx.equals("CJK") ? xx : (xx.substring(0,1) + xx.substring(1).toLowerCase())))
            .collect(Collectors.joining(" "));
    }
}
