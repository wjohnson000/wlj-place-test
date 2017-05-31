package std.wlj.general;

import java.io.*;
import java.util.Arrays;

public class RunCLI {
    public static void main(String... args) throws IOException {
        String line;
        String[] params = { "/home/ec2-user/tmp/jjava/run-s3.sh" };

        Process process = new ProcessBuilder(params).start();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        System.out.printf("Output of running %s is:\n", Arrays.toString(params));

        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

        try {
            int exitValue = process.waitFor();
            System.out.println("\n\nExit Value is " + exitValue);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}

