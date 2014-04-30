package std.wlj.boundary;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;


import std.wlj.util.FileUtils;


public class AnalyzeKMLFile {

    static List<List<String>> allPaths = new ArrayList<List<String>>();

    public static void main(String... args) throws IOException {
        File kmlDir = new File("C:/tools/gis-files/kml-files");
        File[] files = kmlDir.listFiles();
        for (File aFile : files) {
            System.out.println("Processing file: " + aFile);
            if (aFile.getAbsolutePath().endsWith(".kml")) {
                parseFile(aFile);
            }
        }

        for (List<String> aPath : allPaths) {
            for (String tag : aPath) {
                System.out.print(tag + "  ");
            }
            System.out.println();
        }
    }

    private static void parseFile(File aFile) throws IOException {
        Stack<String> tagStack = new Stack<String>();
        BufferedReader reader = FileUtils.getReader(aFile);

        boolean inCdata = false;
        boolean inComment = false;
        String line;
        while ((line = reader.readLine()) != null) {
            int ndx = 0;

            // If we're in a "cdata" block, or a comment block, look for the end; if not found, loop
            if (inCdata) {
                ndx = line.indexOf("]]>");
                if (ndx == -1) {
                    continue;
                }
                inCdata = false;
                ndx += 3;
            } else if (inComment) {
                ndx = line.indexOf("-->");
                if (ndx == -1) {
                    continue;
                }
                inComment = false;
                ndx += 3;
            }

            ndx = line.indexOf('<', ndx);
            while (ndx >= 0) {
                if (line.charAt(ndx+1) == '?') {
                    ndx = -1;
                    continue;
                } else if (line.charAt(ndx+1) == '/') {
                    String eTag = line.substring(ndx+2);
                    int xdx = eTag.indexOf('>');
                    eTag = eTag.substring(0, xdx);
                    if (! tagStack.isEmpty()) {
                        String bTag = tagStack.pop();
                        if (! bTag.equals(eTag)) {
                            System.out.println("OOPS ..." + bTag + " --> " + eTag);
                            return;
                        }
                    }
                    ndx += xdx;
                } else if (line.substring(ndx).startsWith("<![CDATA[")) {
                    ndx += 8;
                    ndx = line.indexOf("]]>", ndx);
                    if (ndx == -1) {
                        inCdata = true;
                        continue;
                    }
                    inCdata = false;
                    ndx += 3;
                } else if (line.substring(ndx).startsWith("<!--")) {
                    ndx += 4;
                    ndx = line.indexOf("-->", ndx);
                    if (ndx == -1) {
                        inComment = true;
                        continue;
                    }
                    inComment = false;
                    ndx += 3;
                } else {
                    String bTag = line.substring(ndx+1);
                    int xdx = bTag.indexOf('>');
                    bTag = bTag.substring(0, xdx);
                    int ydx = bTag.indexOf(' ');
                    if (ydx > 0) {
                        bTag = bTag.substring(0, ydx);
                    }
                    if (bTag.equals("Placemark")  ||  ! tagStack.isEmpty()) {
                        tagStack.push(bTag);
                        addPath(tagStack);
                    }
                    ndx += xdx;
                }
                ndx = line.indexOf('<', ndx);
            }
        }

        reader.close();
    }

    private static void addPath(Stack<String> currStack) {
        List<String> path = new ArrayList<String>();
        for (Iterator<String> iter=currStack.iterator();  iter.hasNext(); ) {
            String tag = iter.next();
            path.add(tag);
        }

        boolean addIt = true;
        for (List<String> aPath : allPaths) {
            if (path.size() == aPath.size()) {
                boolean isMatch = true;
                for (int i=0;  i<aPath.size();  i++) {
                    isMatch = isMatch & path.get(i).equals(aPath.get(i));
                }
                if (isMatch) addIt = false;
            }
        }

        if (addIt) {
            allPaths.add(path);
        }
        
    }
}
