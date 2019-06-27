/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dao;

import java.util.Date;
import java.util.Map;

import org.familysearch.standards.place.dao.ReportDAO;
import org.familysearch.standards.place.dao.dbimpl.ReportDAOImpl;

import std.wlj.datasource.DbConnectionManager;

/**
 * @author wjohnson000
 *
 */
public class ReportDAOTest {

    @SuppressWarnings("deprecation")
    public static void main(String...args) {
        Map<Date, Map<Integer, Integer>> myUpdates;
//        ReportDAO reportDAO = new ReportDAOImpl(DbConnectionManager.getDataSourceSams());
        ReportDAO reportDAO = new ReportDAOImpl(DbConnectionManager.getDataSourceAwsDev());

//        myUpdates = reportDAO.getRepUpdates("wjohnson000", null);
//        for (Map.Entry<Date, Set<Integer>> myUpdate : myUpdates.entrySet()) {
//            System.out.println("\nDate: " + myUpdate.getKey());
//            myUpdate.getValue().forEach(vl -> System.out.println("  " + vl));
//        }

        System.out.println("\n================================================");
        myUpdates = reportDAO.getRepUpdates("PauBosch", new Date(119, 5, 1), new Date(119, 5, 10));
        for (Map.Entry<Date, Map<Integer, Integer>> myUpdate : myUpdates.entrySet()) {
            System.out.println("\nDate: " + myUpdate.getKey());
            myUpdate.getValue().entrySet().forEach(vl -> System.out.println("  " + vl));
        }
    }
}
