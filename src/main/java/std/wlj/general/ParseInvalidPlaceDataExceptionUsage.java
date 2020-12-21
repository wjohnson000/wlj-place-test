/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class ParseInvalidPlaceDataExceptionUsage {

    public static void main(String...args) throws Exception {
        List<String> output = new ArrayList<>(400);
        List<String> lines = Files.readAllLines(Paths.get("C:/temp/bad-request-usage.txt"), StandardCharsets.UTF_8);

        String prevLib = "";
        String prevMod = "";
        String prevSrc = "";
        int    srcCount = 0;

        for(String line : lines) {
            int ndx = line.indexOf(' ');
            if (ndx < 1) continue;

            String fileInfo = line.substring(0, ndx).trim();
            String exInfo   = line.substring(ndx).trim();
            if (exInfo.startsWith("import")) continue;

            String[] fileChunks = PlaceHelper.split(fileInfo, '\\');
            if (fileChunks.length > 2) {
                String lib = fileChunks[0];
                String mod = fileChunks[1];
                String src = fileChunks[fileChunks.length-1];
                String[] srcChunks = PlaceHelper.split(src, ':');

                if (! lib.equals(prevLib)) {
                    srcCount++;
                    output.add("");
                    output.add(lib + "|" + mod + "|" + srcChunks[0] + "|" + srcChunks[1]);
                } else if (! mod.equals(prevMod)) {
                    srcCount++;
                    output.add("");
                    output.add("|" + mod + "|" + srcChunks[0] + "|" + srcChunks[1]);
                } else if (! srcChunks[0].equals(prevSrc)) {
                    srcCount++;
                    output.add("");
                    output.add("||" + srcChunks[0] + "|" + srcChunks[1]);
                } else {
                    output.add("|||" + srcChunks[1]);
                }

                prevLib = lib;
                prevMod = mod;
                prevSrc = srcChunks[0];
            }
        }

        output.forEach(System.out::println);
        System.out.println("\n\nNumber of class files: " + srcCount);
    }
}
