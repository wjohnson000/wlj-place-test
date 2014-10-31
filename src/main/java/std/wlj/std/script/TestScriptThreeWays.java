package std.wlj.std.script;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class TestScriptThreeWays {
    public static void main(String... args) throws IOException {
        FileSystem fs = FileSystems.getDefault();
        Path placesPath = fs.getPath("C:/Users/wjohnson000/git/std-lib-metrics/std-algorithm-delivery/testing-files/places.txt");
        List<String> lines = Files.readAllLines(placesPath, Charset.forName("UTF-8"));
        System.out.println("N-lines: " + lines.size());

        long nnow = 0;
        long then = 0;
        long time01 = 0;
        long time02 = 0;
        long time03 = 0;
        for (int k=1;  k<100;  k++) {
            for (int i=0;  i<lines.size();  i++) {
                String line = lines.get(i);
                String[] tokens = line.split("\t");
                if (tokens.length > 9) {
                    String[] names = tokens[9].split("\\^");
                    for (String name : names) {
                        then = System.nanoTime();
                        List<Character.UnicodeBlock> list01 = getBlocksOne(name);
                        nnow = System.nanoTime();
                        time01 += (nnow - then);

                        then = System.nanoTime();
                        List<Character.UnicodeBlock> list02 = getBlocksTwo(name);
                        nnow = System.nanoTime();
                        time02 += (nnow - then);

                        then = System.nanoTime();
                        List<Character.UnicodeBlock> list03 = getBlocksThree(name);
                        nnow = System.nanoTime();
                        time03 += (nnow - then);

                        if (list01.size() != list02.size()) {
                            System.out.println("1.2.Line (" + i + ") . " + name + " --> " + list01.size() + " vs. " + list02.size());
                        }
                        if (list01.size() != list03.size()) {
                            System.out.println("1.3.Line (" + i + ") . " + name + " --> " + list01.size() + " vs. " + list03.size());
                        }
                        if (list02.size() != list03.size()) {
                            System.out.println("2.3.Line (" + i + ") . " + name + " --> " + list02.size() + " vs. " + list03.size());
                        }
                        if (list01.size() != name.length()) {
                            System.out.println("X.Line (" + i + ") . " + name + " --> " + list01.size() + " vs. " + name.length());
                        }
                    }
                }
            }
        }

        System.out.println("One: " + (time01 / 1000000.0));
        System.out.println("Two: " + (time02 / 1000000.0));
        System.out.println("Tre: " + (time03 / 1000000.0));
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

        for (int offset=0;  offset<str.length(); ) {
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
