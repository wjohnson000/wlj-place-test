package std.wlj.dbload;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.familysearch.standards.loader.helper.DbHelper;
import org.familysearch.standards.loader.helper.PlaceRepFileGenerator;

import std.wlj.datasource.DbConnectionManager;


public class RunDbDumpRange {

    static final File baseDir = new File("D:/tmp/flat-files/");
    static ExecutorService execService = Executors.newFixedThreadPool(4);
    
    public static void main(String... args) throws InterruptedException {
        DbHelper dbHelper = new DbHelper(DbConnectionManager.getDataSourceSams());

        long startRepId = 1;
        long endRepId   = 10000;
        File dataDirectory = new File(baseDir, "ten-thou");
        dataDirectory.mkdirs();

        System.out.println("Range: " + startRepId + " --> " + endRepId);
        Runnable runn = () -> {
            PlaceRepFileGenerator prfGenerator = new PlaceRepFileGenerator(dbHelper);
            prfGenerator.generateFiles(dataDirectory, startRepId, endRepId);
        };
        execService.submit(runn);
        execService.shutdown();
        execService.awaitTermination(1800, TimeUnit.SECONDS);
        
        System.out.println("Done generating files ...");
    }
}
