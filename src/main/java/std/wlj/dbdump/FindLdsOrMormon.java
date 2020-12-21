/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author wjohnson000
 *
 */
public class FindLdsOrMormon {

    private static final String baseDir  = "C:/temp/db-dump";

    public static void main(String...args) throws IOException {
        List<Path> files = Files.list(Paths.get(baseDir))
            .collect(Collectors.toList());

        for (Path file : files) {
            int lineCnt = 0;
            try(FileInputStream fis = new FileInputStream(new File(file.toAbsolutePath().toString()));
                    Scanner scan = new Scanner(fis, "UTF-8")) {

                System.out.println("\n=================================================================");
                System.out.println("Processing: " + file);
                while (scan.hasNextLine()) {
                    if (++lineCnt % 500_000 == 0) System.out.println(file.getFileName() + ": " + lineCnt);
                    String line = scan.nextLine();
                    String lowerLine = line.toLowerCase();
                    if (lowerLine.contains("|lds ")  ||  lowerLine.contains("|lds|")  ||  lowerLine.contains(" lds|")  ||
                            lowerLine.contains("|mormon ")  ||  lowerLine.contains("|mormon|")  ||  lowerLine.contains(" mormon|")) {
                        System.out.println(line);
                    } else if ((lowerLine.contains("|latter ")  ||  lowerLine.contains("|latter|")  ||  lowerLine.contains(" latter|"))
                            &&  ! lowerLine.contains("church of jesus christ")) {
                        System.out.println(line);
                    }
                }

                System.out.println("Done --> line-count: " + lineCnt);
            }
        }
 
    }
}
