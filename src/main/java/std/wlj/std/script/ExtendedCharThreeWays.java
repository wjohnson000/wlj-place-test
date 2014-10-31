package std.wlj.std.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ExtendedCharThreeWays {
    public static void main(String... args) throws IOException {
        int cp = 0x10400;
        String text = "test \uD801\uDC00𐐀𐐀";
        System.out.println("text:  " + text);
        System.out.println("cp:    " + cp);
        System.out.println("found: " + text.codePointAt(5));
        System.out.println("len:   " + text.length());

        List<Character.UnicodeBlock> list01 = getBlocksOne(text);
        List<Character.UnicodeBlock> list02 = getBlocksTwo(text);
        List<Character.UnicodeBlock> list03 = getBlocksThree(text);
        if (list01.size() != list02.size()) {
            System.out.println("1.2 " + text + " --> " + list01.size() + " vs. " + list02.size());
        }
        if (list01.size() != list03.size()) {
            System.out.println("1.3 " + text + " --> " + list01.size() + " vs. " + list03.size());
        }
        if (list02.size() != list03.size()) {
            System.out.println("2.3 " + text + " --> " + list02.size() + " vs. " + list03.size());
        }

        if (list01.size() != text.length()) {
            System.out.println("1.X " + text + " --> " + list01.size() + " vs. " + text.length());
        }

        if (list02.size() != text.length()) {
            System.out.println("2.X " + text + " --> " + list02.size() + " vs. " + text.length());
        }

        if (list03.size() != text.length()) {
            System.out.println("3.X " + text + " --> " + list03.size() + " vs. " + text.length());
        }

        System.out.println("\nOne ........................................");
        for (Character.UnicodeBlock block: list01) {
            System.out.println("  " + block);
        }

        System.out.println("\nTwo ........................................");
        for (Character.UnicodeBlock block: list02) {
            System.out.println("  " + block);
        }

        System.out.println("\nThree ........................................");
        for (Character.UnicodeBlock block: list03) {
            System.out.println("  " + block);
        }
    }

    private static List<Character.UnicodeBlock> getBlocksOne(String str) {
        List<Character.UnicodeBlock> blockList = new ArrayList<>();

        for (int i=0;  i<str.length();  i++) {
            int codePoint = str.codePointAt(i);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(codePoint);
            blockList.add(block);
        }

        return blockList;
    }

    private static List<Character.UnicodeBlock> getBlocksTwo(String str) {
        List<Character.UnicodeBlock> blockList = new ArrayList<>();

        final int length = str.length();
        for (int offset=0;  offset<length; ) {
            int codePoint = str.codePointAt(offset);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(codePoint);
            blockList.add(block);
            offset += Character.charCount(codePoint);
        }

        return blockList;
    }

    private static List<Character.UnicodeBlock> getBlocksThree(String str) {
        List<Character.UnicodeBlock> blockList = new ArrayList<>();

        int offset = 0;
        int codePoint = str.codePointAt(offset);
        Character.UnicodeBlock block = Character.UnicodeBlock.of(codePoint);
        blockList.add(block);

        while (str.length() > (offset = str.offsetByCodePoints(offset, 1))) {
            codePoint = str.codePointAt(offset);
            block = Character.UnicodeBlock.of(codePoint);
            blockList.add(block);
        }

        return blockList;
    }
}
