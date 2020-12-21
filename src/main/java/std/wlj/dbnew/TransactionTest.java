/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbnew;

import java.util.Date;

import javax.sql.DataSource;

import org.familysearch.standards.place.dao.DAOFactory;
import org.familysearch.standards.place.dao.dbimpl.DAOFactoryImpl;

import std.wlj.util.DbConnectionManager;

/**
 * @author wjohnson000
 *
 */
public class TransactionTest {
    private static DataSource ds;
    private static DAOFactory daoFactory;

    public static void main(String... args) {
        System.out.println("JAVA: " + System.getProperty("java.version"));

        ds = DbConnectionManager.getDataSourceAwsDev();
        daoFactory = new DAOFactoryImpl(ds);

        testRep(daoFactory, 333);
        testRep(daoFactory, 10625001);
    }

    static void testRep(DAOFactory factory, int repId) {
        int rev0 = daoFactory.getPlaceRepDAO().getEarliestRevision(repId);
        int revX = daoFactory.getPlaceRepDAO().getLatestRevision(repId);

        String ccUser = daoFactory.getTransactionDAO().getUserOnRevision(rev0);
        Date   ccDate = daoFactory.getTransactionDAO().getDateOnRevision(rev0);
        String uuUser = daoFactory.getTransactionDAO().getUserOnRevision(revX);
        Date   uuDate = daoFactory.getTransactionDAO().getDateOnRevision(revX);

        System.out.println("\n===========================================");
        System.out.println("RepId: " + repId);
        System.out.println("    rev0: " + rev0 + " --> " + ccUser + " @ " + ccDate);
        System.out.println("    revX: " + revX + " --> " + uuUser + " @ " + uuDate);
    }
}
