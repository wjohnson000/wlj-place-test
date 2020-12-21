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

import org.familysearch.standards.date.api.DateRequest;
import org.familysearch.standards.date.api.DateService;
import org.familysearch.standards.date.api.DateServiceImpl;
import org.familysearch.standards.date.api.model.DateResult;

/**
 * @author wjohnson000
 *
 */
public class TestDateV2Lots {

    private static final int NUM_THREADS = 2; // 24;
    private static final ExecutorService exSvc = Executors.newFixedThreadPool(NUM_THREADS+1);
    private static final List<String> results = new ArrayList<>(200_000);

    public static void main(String...arg) throws Exception {
        List<String> datesToUse;
        List<String> datesToParse = Files.readAllLines(Paths.get("C:/temp/date-samples/pat-schone-dates.txt"), StandardCharsets.UTF_8);
        if (datesToParse.size() <= 500_000) {
            datesToUse = datesToParse;
        } else {
            datesToUse = datesToParse.subList(0, 500_000);
        }

        long time0 = System.nanoTime();
        for (int i=0;  i<NUM_THREADS;  i++) {
            final int blah = i;
            exSvc.submit(() -> runLots(datesToUse, 2+blah/5));
        }

        exSvc.shutdown();
        exSvc.awaitTermination(20, TimeUnit.MINUTES);
        long time1 = System.nanoTime();

        System.out.println("Total Time: " + (time1 - time0) / 1_000_000.0);
        Files.write(Paths.get("C:/temp/date-new-new.txt"), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static void runLots(List<String> datesToParse, int skip) {
        DateService dateService = new DateServiceImpl();

        for (int ndx=0;  ndx<datesToParse.size();  ndx+=skip) {
            try {
                String dateToParse = datesToParse.get(ndx);
                int ndxTab = dateToParse.indexOf('\t');
                if (ndxTab > 0) {
                    dateToParse = dateToParse.substring(0, ndxTab);
                }

                DateResult parseResult = dateService.interpDate(new DateRequest(dateToParse, "en"));
                String genDates = parseResult.getDates().stream()
                        .map(res -> res.getDate().toGEDCOMX())
                        .collect(Collectors.joining(", ", "  [", "]"));
                results.add(dateToParse + "|false|" + genDates);

                Thread.sleep(10L);
            } catch(Exception ex) { }
        }
    }
}
