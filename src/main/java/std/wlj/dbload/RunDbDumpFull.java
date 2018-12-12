package std.wlj.dbload;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.familysearch.standards.loader.helper.DbHelper;
import org.familysearch.standards.loader.helper.PlaceRepFileGenerator;

import std.wlj.datasource.DbConnectionManager;


public class RunDbDumpFull {

    static final File baseDir = new File("C:/temp/flat-file/load-test");
    static ExecutorService execService = Executors.newFixedThreadPool(2);
    
    public static void main(String... args) throws InterruptedException {
        DbHelper dbHelper = new DbHelper(DbConnectionManager.getDataSourceAwsDev());  // .getDataSourceSams());  // .getDataSourceDev55());

        for (long repid=1;  repid<10_999_999;  repid+=1_000_000) {
            long startRepId = repid;
            long endRepId   = repid + 999_999;
            File dataDirectory = new File(baseDir, "range-"+startRepId);
            dataDirectory.mkdirs();

            System.out.println("Range: " + startRepId + " --> " + endRepId);
            Runnable runn = () -> {
                PlaceRepFileGenerator prfGenerator = new PlaceRepFileGenerator(dbHelper);
                prfGenerator.generateFiles(dataDirectory, startRepId, endRepId);
            };
            execService.submit(runn);
            try { Thread.sleep(20_000L); } catch(Exception ex) { }
        }

        execService.shutdown();
        execService.awaitTermination(1800, TimeUnit.SECONDS);
        
        System.out.println("Done generating files ...");
    }
}
