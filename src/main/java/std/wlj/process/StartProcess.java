package std.wlj.process;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

/**
 * Start a system process, dump the results.  There are three different ways of listing the
 * contents of the "C:\temp" directory.
 * 
 * @author wjohnson000
 *
 */
public class StartProcess {
    public static void main(String... args) throws Exception {
        listTemp_version03();
    }

    static void listTemp_version03() throws IOException, InterruptedException, ExecutionException {
        ProcessBuilder listDirProc = new ProcessBuilder()
                .directory(Paths.get("C:/temp").toFile())
                .command("dir", "-l");

        listDirProc.inheritIO().start();
    }
}