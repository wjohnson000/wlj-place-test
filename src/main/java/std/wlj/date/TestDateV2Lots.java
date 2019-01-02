/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.familysearch.standards.core.LocalizedData;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.model.DateResult;
import org.familysearch.standards.date.parser.GenDateParser;

/**
 * @author wjohnson000
 *
 */
public class TestDateV2Lots {

    private static final int NUM_THREADS = 1; // 24;
    private static final ExecutorService exSvc = Executors.newFixedThreadPool(NUM_THREADS+1);
    private static final List<String> results = new ArrayList<>(200_000);

    public static void main(String...arg) throws Exception {
        List<String> datesToParse = Files.readAllLines(Paths.get("C:/temp/date-interp.txt"), StandardCharsets.UTF_8);

        long time0 = System.nanoTime();
        for (int i=0;  i<NUM_THREADS;  i++) {
            final int blah = i;
            exSvc.submit(() -> runLots(datesToParse, 2+blah/5));
        }

        exSvc.shutdown();
        exSvc.awaitTermination(20, TimeUnit.MINUTES);
        long time1 = System.nanoTime();

        System.out.println("Total Time: " + (time1 - time0) / 1_000_000.0);
        Files.write(Paths.get("C:/temp/date-new-new.txt"), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static void runLots(List<String> datesToParse, int skip) {
        GenDateParser parser = new GenDateParser();

        for (int ndx=0;  ndx<datesToParse.size();  ndx+=skip) {
            try {
                String dateToParse = datesToParse.get(ndx);
                DateResult parseResult = parser.parse(new LocalizedData<>(dateToParse, StdLocale.ENGLISH), null, null, null);
                String genDates = parseResult.getDates().stream()
                        .map(res -> res.getDate().toGEDCOMX())
                        .collect(Collectors.joining(", ", "  [", "]"));
                results.add(dateToParse + "|false|" + genDates);
            } catch(Exception ex) { }
        }
    }
}
