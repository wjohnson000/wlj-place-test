package std.wlj.flatfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;

/**
 * The {@link FileResultSet} class is smart enough to handle a row of data that's split across
 * multiple lines in a file.  This class looks for such ugliness to help identify where it's
 * happening so that the queries can be smartified to remove CR/LF characters. 
 * @author wjohnson000
 *
 */
public class LookForSplitFiles {
    public static void main(String...args) throws IOException {
        File parentDir = new File("D:/tmp/flat-files/load-test");
        processFileOrDir(parentDir);
    }

    private static void processFileOrDir(File fileOrDir) throws IOException {
        if (fileOrDir.exists()) {
            if (fileOrDir.isDirectory()) {
                for (File aFile : fileOrDir.listFiles()) {
                    processFileOrDir(aFile);
                }
            } else {
                processFile(fileOrDir);
            }
        }
    }

    private static void processFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"), 64 * 1024);
        String header = reader.readLine();
        int colCount = howManyColumns(header);

        while (reader.ready()) {
            String line = reader.readLine();
            if (line != null) {
                int cCount = howManyColumns(line);
                if (cCount < colCount) {
                    System.out.println("Split lines: " + file.getAbsolutePath() + " --> " + line);
                    break;
                }
            }
        }
        reader.close();
    }

    private static int howManyColumns(String aLine) {
        return StringUtils.countMatches(aLine, "|");
    }
}
