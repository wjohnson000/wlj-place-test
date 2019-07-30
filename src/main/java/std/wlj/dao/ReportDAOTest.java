/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.familysearch.standards.place.RepChangeDetail;
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
        Map<Date, List<RepChangeDetail>> myChanges;

//        ReportDAO reportDAO = new ReportDAOImpl(DbConnectionManager.getDataSourceSams());
        ReportDAO reportDAO = new ReportDAOImpl(DbConnectionManager.getDataSourceAwsDev());


        System.out.println("\n================================================");
        myChanges = reportDAO.getRepChanges("PauBosch", new Date(119, 5, 1), new Date(119, 5, 10));
        for (Map.Entry<Date, List<RepChangeDetail>> myChange : myChanges.entrySet()) {
            System.out.println("\nDate: " + myChange.getKey());
            myChange.getValue().forEach(vl -> System.out.println("  " + vl));
        }
    }
}
