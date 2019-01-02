package std.wlj.sourcecode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;


public class FindDuplicateLiterals {

    /** Simple class to keep track of where a literal is found */
    private static class SrcLoc {
        String sourceFile;
        int    lineNum;
    }

    /** Directories to skip */
    private static String[] skipDirectories = {
        "test",
        "target",
        "qa"
    };

    static Map<String,List<SrcLoc>> literalMap = new TreeMap<>();

    /**
     * Walk the File Tree starting with the given project.  Each JAVA file is processed,
     * though anything with a "test" in the directory name is skipped.
     * @param args
     * @throws IOException
     */
    public static void main(String... args) throws IOException {
        FileSystem fs = FileSystems.getDefault();
        Path parent = fs.getPath("C:", "Users", "wjohnson000", "git", "std-ws-place", "place-webservice");

        // Walk the file tree, processing all ".java" files
        Files.walkFileTree(parent, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                    BasicFileAttributes attrs) throws IOException {
                for (String skipDirectory : skipDirectories) {
                    if (dir.toString().contains(skipDirectory)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                processFile(file);
                return FileVisitResult.CONTINUE;
            }
        });

        // Print results
        for (Map.Entry<String,List<SrcLoc>> entry : literalMap.entrySet()) {
            if (entry.getValue().size() > 3) {
                System.out.println(entry.getKey() + " --> " + entry.getValue().size());
                for (SrcLoc srcLoc : entry.getValue()) {
                    System.out.println("   " + srcLoc.sourceFile + ":" + srcLoc.lineNum);
                }
            }
        }
    }

    private static void processFile(Path someFile) throws IOException {
        if (someFile.toString().endsWith(".java")) {
            List<String> lines = Files.readAllLines(someFile, StandardCharsets.UTF_8);

            int lineNum = 0;
            boolean inCmt = false;
            for (String line : lines) {
                lineNum++;
                int ndx01, ndx02;
                if (inCmt) {
                    ndx01 = line.indexOf("*/");
                    if (ndx01 >= 0) {
                        inCmt = false;
                        line = line.substring(ndx01+2);
                    }
                }

                if (! inCmt) {
                    ndx01 = line.indexOf("//");
                    if (ndx01 >= 0) {
                        line = line.substring(0, ndx01);
                    }

                    ndx01 = line.indexOf("/*");
                    if (ndx01 >= 0) {
                        ndx02 = line.indexOf("*/", ndx01);
                        if (ndx02 > ndx01) {
                            line = line.substring(ndx02+2);
                        } else {
                            inCmt = true;
                            line = "";
                        }
                    }
                    while (line.length() > 0) {
                        ndx01 = line.indexOf('"');
                        if (ndx01 >= 0) {
                            ndx02 = line.indexOf('"', ndx01+1);
                            if (ndx02 > ndx01) {
                                String literal = line.substring(ndx01+1, ndx02);
                                if (literal.length() > 0) {
                                    SrcLoc srcLoc = new SrcLoc();
                                    srcLoc.sourceFile = someFile.getFileName().toString();
                                    srcLoc.lineNum = lineNum;
                                    List<SrcLoc> srcList = literalMap.get(literal);
                                    if (srcList == null) {
                                        srcList = new ArrayList<>();
                                        literalMap.put(literal, srcList);
                                    }
                                    srcList.add(srcLoc);
                                }
                                line = line.substring(ndx02+1);
                            } else {
                                break;
                            }
                        } else {
                            line = "";
                        }
                    }
                }
            }
        }
    }

}
