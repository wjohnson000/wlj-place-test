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
        Path path01 = currFS.getPath("C:", "temp", "results-search-41k-local.txt");
        Path path02 = currFS.getPath("C:", "temp", "results-search-41k.txt");

        List<String> data01 = Files.readAllLines(path01, Charset.forName("UTF-8"));
        List<String> data02 = Files.readAllLines(path02, Charset.forName("UTF-8"));

        System.out.println("Rows01: " + data01.size());
        System.out.println("Rows02: " + data02.size());

        List<String> outData = new ArrayList<>();
        for (int i=0;  i<data01.size();  i++) {
            StringBuffer buff = new StringBuffer(512);
            String line01 = data01.get(i);
            String line02 = data02.get(i);
            String[] tokens01 = line01.split("\\|");
            String[] tokens02 = line02.split("\\|");

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

        Path outPath = currFS.getPath("C:", "temp", "results-search-compare.txt");
        Files.write(outPath, outData, Charset.forName("UTF-8"), StandardOpenOption.CREATE);

        System.exit(0);
    }
}
