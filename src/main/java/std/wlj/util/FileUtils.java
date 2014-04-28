package std.wlj.util;

import java.io.*;


public class FileUtils {

    public static BufferedReader getReader(String filePath) throws IOException {
        return getReader(new File(filePath));
    }

    public static BufferedReader getReader(File file) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"), 1024 * 64);
    }

    public static PrintWriter getWriter(String filePath) throws IOException {
        return getWriter(new File(filePath));
    }

    public static PrintWriter getWriter(File file) throws IOException {
        return new PrintWriter(file, "UTF-8");
    }

}
