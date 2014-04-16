package std.wlj.util;

import java.io.*;


public class ParseLog {
    public static void main(String... args) throws Exception {
        int lineCnt = 0;
        int st200Cnt = 0;
        int st401Cnt = 0;
        int st404Cnt = 0;

        File baseDir = new File("C:/temp/place-2.0-logs");
        for (String fName : baseDir.list()) {
            if (fName.startsWith("localhost_access")) {
                System.out.println("Processing file: " + fName);
                File logFile = new File(baseDir, fName);
                BufferedReader reader = new BufferedReader(new FileReader(logFile));
                String line = reader.readLine();
                while (line != null) {
                    lineCnt++;
                    if (line.indexOf("GET ") > 0) {
                        String[] tokens = line.split(" ");
                        if (tokens[tokens.length-2].equals("200")) {
                            st200Cnt++;
                        } else if (tokens[tokens.length-2].equals("401")) {
                            st401Cnt++;
                        } else if (tokens[tokens.length-2].equals("404")) {
                            st404Cnt++;
                            System.out.println(fName + " --> " + line);
                        } else {
                            System.out.println("??? " + tokens[tokens.length-2]);
                        }
                    }
                    line = reader.readLine();
                }
                reader.close();
            }
        }

        System.out.println("Line Cnt: " + lineCnt);
        System.out.println(" 200 Cnt: " + st200Cnt);
        System.out.println(" 401 Cnt: " + st401Cnt);
        System.out.println(" 404 Cnt: " + st404Cnt);

        System.exit(0);
    }
}
