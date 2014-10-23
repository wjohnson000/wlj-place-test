package std.wlj.solr;

import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;


public class Compare41k {
    public static void main(String... args) throws Exception {
        FileSystem currFS = FileSystems.getDefault();
        Path path01 = currFS.getPath("C:", "temp", "search-new-ordered.txt");
        Path path02 = currFS.getPath("C:", "temp", "search-new-random.txt");

        List<String> data01 = Files.readAllLines(path01, Charset.forName("UTF-8"));
        List<String> data02 = Files.readAllLines(path02, Charset.forName("UTF-8"));

        System.out.println("Rows01: " + data01.size());
        System.out.println("Rows02: " + data02.size());

        double totalCl01=0, totalSv01=0, totalId01=0, totalPa01=0, totalSc01=0;
        double totalCl02=0, totalSv02=0, totalId02=0, totalPa02=0, totalSc02=0;

        List<String> outData = new ArrayList<>();
        for (int i=0;  i<data01.size();  i++) {
            StringBuffer buff = new StringBuffer(512);
            String line01 = data01.get(i);
            String line02 = data02.get(i);
            String[] tokens01 = line01.split("\\|");
            String[] tokens02 = line02.split("\\|");

            if (tokens01[0].equals("METRICS")  &&  tokens02[0].equals("METRICS")) {
                continue;
            }

            totalCl01 += Double.parseDouble(tokens01[1]);
            totalSv01 += Double.parseDouble(tokens01[2]);
            totalId01 += Double.parseDouble(tokens01[3]);
            totalPa01 += Double.parseDouble(tokens01[4]);
            totalSc01 += Double.parseDouble(tokens01[5]);

            totalCl02 += Double.parseDouble(tokens02[1]);
            totalSv02 += Double.parseDouble(tokens02[2]);
            totalId02 += Double.parseDouble(tokens02[3]);
            totalPa02 += Double.parseDouble(tokens02[4]);
            totalSc02 += Double.parseDouble(tokens02[5]);

            boolean samePR = true;
            if (tokens01.length != tokens02.length) {
                samePR = false;
            } else {
                for (int j=7;  j<tokens01.length;  j++) {
                    if (! tokens01[j].equals(tokens02[j])) {
                        samePR = false;
                    }
                }
            }

            buff.append(tokens01[0]);
            for (int j=1;  j<=6;  j++) {
                buff.append("|").append(tokens01[j]);
            }

            if (! samePR) {
                for (int j=7;  j<tokens01.length;  j++) {
                    buff.append("|").append(tokens01[j]);
                }
            }
            outData.add(buff.toString());

            buff = new StringBuffer(512);
            for (int j=1;  j<=6;  j++) {
                buff.append("|").append(tokens02[j]);
            }

            if (! samePR) {
                for (int j=7;  j<tokens02.length;  j++) {
                    buff.append("|").append(tokens02[j]);
                }
            }
            outData.add(buff.toString());

            outData.add("");
        }

        outData.add("");
        outData.add("CLIENT: " + totalCl01 + " vs. " + totalCl02);
        outData.add("SERVER: " + totalSv01 + " vs. " + totalSv02);
        outData.add(" IDENT: " + totalId01 + " vs. " + totalId02);
        outData.add(" PARSE: " + totalPa01 + " vs. " + totalPa02);
        outData.add(" SCORE: " + totalSc01 + " vs. " + totalSc02);

        Path outPath = currFS.getPath("C:", "temp", "compare-new-metrics.txt");
        Files.write(outPath, outData, Charset.forName("UTF-8"), StandardOpenOption.CREATE);

        System.exit(0);
    }
}
