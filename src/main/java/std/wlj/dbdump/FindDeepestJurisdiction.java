/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

import org.familysearch.standards.loader.helper.DbHelper;
import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.datasource.DbConnectionManager;

/**
 * @author wjohnson000
 *
 */
public class FindDeepestJurisdiction {

    public static void main(String...args) {
        DbHelper dbHelper = new DbHelper(DbConnectionManager.getDataSourceAwsDev());
        dbHelper.seedPlaceChain();
        System.out.println("Done seeding chain ....");

        int maxLen = 5;
        Iterator<Entry<Integer, String>> iter = dbHelper.getChainIterator();
        while (iter.hasNext()) {
            Entry<Integer, String> entry = iter.next();
            String chain = entry.getValue();
            String[] chunks = PlaceHelper.split(chain, ',');
            if (chunks.length > maxLen) {
                maxLen = chunks.length;
                System.out.println();
                System.out.println("=====================================================================================");
                System.out.println("=====================================================================================");
                System.out.println("  " + entry.getKey() + " --> " + Arrays.toString(chunks));
            } else if (chunks.length == maxLen) {
                System.out.println("  " + entry.getKey() + " --> " + Arrays.toString(chunks));
            }
        }
    }

}
