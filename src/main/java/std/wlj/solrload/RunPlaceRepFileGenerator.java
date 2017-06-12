package std.wlj.solrload;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.familysearch.standards.loader.helper.DbHelper;
import org.familysearch.standards.loader.helper.PlaceRepFileGenerator;

import std.wlj.datasource.DbConnectionManager;

public class RunPlaceRepFileGenerator {
	private static DbHelper dbService;

	public static void main(String... args) throws InterruptedException {
//	    dbService = new DbHelper(PGConnection.getDataSourceSams());
	    dbService = new DbHelper(DbConnectionManager.getDataSourceAwsDev());

	    showClassPaths();
//		dumpFilesAll();
//		dumpFilesOneMillion();
//		dumpFilesOneMillionNewer();
//		dumpFilesFourThreads();
		dumpFilesTenThousand();
	}

	private static void dumpFilesAll() {
		long startT = System.currentTimeMillis();
		File dirFile = new File("D:/tmp/flat-files/one-infinity");
		PlaceRepFileGenerator generator = new PlaceRepFileGenerator(dbService);
		generator.generateFiles(dirFile, 0, 0);
		System.out.println("\n\nTotal Time:" + (System.currentTimeMillis() - startT)/1000.0);
	}

	private static void dumpFilesOneMillion() {
		long startT = System.currentTimeMillis();
		File dirFile = new File("D:/tmp/flat-files/one-million-old");
		PlaceRepFileGenerator generator = new PlaceRepFileGenerator(dbService);
		generator.generateFiles(dirFile, 1, 1_000_000);
		System.out.println("\n\nTotal Time:" + (System.currentTimeMillis() - startT)/1000.0);
	}

	private static void dumpFilesFourThreads() throws InterruptedException {
		long startTT = System.currentTimeMillis();
		final ExecutorService pool = Executors.newFixedThreadPool(4);
		for (int i=0;  i<12;  i++) {
			final int count = i;
			final int rep00 = i*1_000_000 + 1;
			final int rep99 = i*1_000_000 + 1_000_000;

			final File dirFile = new File("D:/tmp/flat-files/one-million/dir-" + count);
			if (! dirFile.exists()) {
				dirFile.mkdirs();
			}

			Runnable rr = () -> {
				long startT = System.currentTimeMillis();
				PlaceRepFileGenerator generator = new PlaceRepFileGenerator(dbService);
				generator.generateFiles(dirFile, rep00, rep99);
				System.out.println("\n\nTask Time [" + count + "]:" + (System.currentTimeMillis() - startT)/1000.0);
			};
			pool.submit(rr);
			Thread.sleep(2500L);
		}
		pool.shutdown();
		pool.awaitTermination(30, TimeUnit.MINUTES);
		System.out.println("\n\nTotal Time:" + (System.currentTimeMillis() - startTT)/1000.0);
	}

    private static void dumpFilesTenThousand() {
        long startT = System.currentTimeMillis();
        File dirFile = new File("D:/tmp/flat-files/missing-rep");
        PlaceRepFileGenerator generator = new PlaceRepFileGenerator(dbService);
        generator.generateFiles(dirFile, 10576200, 10576250);
        System.out.println("\n\nTotal Time:" + (System.currentTimeMillis() - startT)/1000.0);
    }

    private static void showClassPaths() {
        String[] paths = System.getProperty("java.class.path").split(";");
        for (String path : paths) {
            System.out.println(path);
        }
    }

}
