package std.wlj.dbload;

import java.util.Arrays;

import org.familysearch.standards.loader.helper.DbHelper;

import std.wlj.util.DbConnectionManager;

public class RunDbHelperChain {

    public static void main(String... args) throws InterruptedException {
//        DbHelper dbHelper = new DbHelper(PGConnection.getDataSourceSams());
        DbHelper dbHelper = new DbHelper(DbConnectionManager.getDataSourceAwsDev());
        dbHelper.seedPlaceChain();

        int[] repIds = { 4553, 56, 7345028, 24718, 267, 8193107, 7099871, 9556046, 3497695 };
        Arrays.stream(repIds).forEach(rr -> System.out.println(rr + " :: " + dbHelper.getChain(rr)));
//        Iterator<Map.Entry<Integer, String>> chIter = dbHelper.getChainIterator();
//        while (chIter.hasNext()) {
//            Map.Entry<Integer, String> entry = chIter.next();
//            System.out.println("EN: " + entry);
//        }
        
        System.out.println("Done generating files ...");
    }
}
