package std.wlj.interpretation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ParseRequestSplunkLogs {

    static final String baseDir = "C:/D-drive/request-splunk";
    static final String outFile = "place-search-text.txt";

    public static void main(String... args) throws IOException {
        List<String> allResults = new ArrayList<>(2_000_000);

        String[] filenames = new File(baseDir).list((dir, name) -> name.endsWith(".csv"));
        for (String filename : filenames) {
            allResults.addAll(parseFile(filename));
        }

        Collections.sort(allResults);
        Files.write(Paths.get(baseDir, outFile), allResults, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.exit(0);
    }

    static List<String> parseFile(String filename) throws IOException {
        System.out.println("\nFile: " + filename);
        List<String> results = new ArrayList<>(2_000_000);

        int lineCnt =0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, filename));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 50_000 == 0) System.out.println("  Lines." + filename + ": " + lineCnt);

                String line =  scan.nextLine();
                String text   = getValueForKey(line, "text");
                if (! text.isEmpty()) {
                    String reqLang = getValueForKey(line, "requested-lang");
                    
                    StringBuilder buff = new StringBuilder();
                    buff.append(text.replace('+', ' '));
                    buff.append("|").append(reqLang);
                    results.add(buff.toString());
                }
            }
        }

        return results;
    }

    static String getValueForKey(String line, String key) {
        int ndx01 = line.indexOf(" " + key + "=");
        int ndx02 = line.indexOf("\"\"", ndx01+1);
        int ndx03 = line.indexOf("\"\"", ndx02+1);
        if (ndx01 > 0  &&  ndx02 > ndx01  &&  ndx03 > ndx02) {
            String value = line.substring(ndx02, ndx03);
            value = value.replace('=', ' ').replace('"', ' ').trim();
            return value;
        }
        return "";
    }

    static String getTypeFromParse(String parse) {
        StringBuilder buff = new StringBuilder();
        int ndx0 = parse.indexOf("[type");
        while (ndx0 >= 0) {
            int ndx1 = parse.indexOf("]", ndx0);
            String temp = parse.substring(ndx0+5, ndx1);
            if (buff.length() > 0) buff.append(":");
            buff.append(temp.trim());
            ndx0 = parse.indexOf("[type", ndx1+1);
        }
        return buff.toString();
    }
}
