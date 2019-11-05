/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.familysearch.standards.place.RepChangeDetail;
import org.familysearch.standards.place.dao.ReportDAO;
import org.familysearch.standards.place.dao.dbimpl.ReportDAOImpl;

import std.wlj.util.DbConnectionManager;

/**
 * @author wjohnson000
 *
 */
public class ReportDAOChangeCountTest {

    @SuppressWarnings("deprecation")
    public static void main(String...args) {
        Map<String, Integer> userCounts;

//        ReportDAO reportDAO = new ReportDAOImpl(DbConnectionManager.getDataSourceSams());
        ReportDAO reportDAO = new ReportDAOImpl(DbConnectionManager.getDataSourceAwsDev());

        System.out.println("\n================================================");
        userCounts = reportDAO.getChangeCount(Arrays.asList("PauBosch", "wjohnson000", "danvreeves", "nobody"), new Date(119, 1, 1), new Date(119, 3, 10));
        userCounts.entrySet().forEach(System.out::println);

        System.out.println("\n================================================");
        userCounts = reportDAO.getChangeCount(Arrays.asList("PauBosch", "wjohnson000", "danvreeves", "nobody"), new Date(119, 1, 1), null);
        userCounts.entrySet().forEach(System.out::println);

        System.out.println("\n================================================");
        userCounts = reportDAO.getChangeCount(Arrays.asList("PauBosch", "wjohnson000", "danvreeves", "nobody"), null, null);
        userCounts.entrySet().forEach(System.out::println);
    }
}
