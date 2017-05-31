package std.wlj.dbnew;

import java.util.Date;

import javax.sql.DataSource;

import org.familysearch.standards.place.dao.DAOFactory;
import org.familysearch.standards.place.dao.PlaceRepDAO;
import org.familysearch.standards.place.dao.dbimpl.DAOFactoryImpl;

import std.wlj.datasource.DbConnectionManager;

public class TestTransactionDates {

    private static DataSource ds;
    private static DAOFactory daoFactory;

    public static void main(String... args) {
        System.out.println("JAVA: " + System.getProperty("java.version"));

        ds = DbConnectionManager.getDataSourceWLJ();
        daoFactory = new DAOFactoryImpl(ds);

        PlaceRepDAO placeRepDao = daoFactory.getPlaceRepDAO();
        Date cDate = placeRepDao.getCreateDate(10625001);
        Date uDate = placeRepDao.getLastUpdateDate(10625001);
        System.out.println("C-Date: " + cDate);
        System.out.println("U-Date: " + uDate);
    }
}
