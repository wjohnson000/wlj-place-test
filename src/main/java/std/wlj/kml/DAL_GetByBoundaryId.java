package std.wlj.kml;

import org.familysearch.standards.place.dao.DAOFactory;
import org.familysearch.standards.place.dao.dbimpl.DAOFactoryImpl;
import org.familysearch.standards.place.dao.model.DbRepBoundary;

import std.wlj.util.DbConnectionManager;

public class DAL_GetByBoundaryId {

    public static void main(String... args) {
        DAOFactory daoFactory = new DAOFactoryImpl(DbConnectionManager.getDataSourceSams());

        long then = System.nanoTime();
        DbRepBoundary repB = daoFactory.getRepBoundaryDAO().read(11);
        long nnow = System.nanoTime();
        System.out.println("TIME: " + (nnow-then)/1_000_000.0);

        System.out.println("b: " + repB.getId() + " --> " + repB.getRepId() + " [" + repB.getPointCount() + "]");
        System.out.println("" + repB.getGeographyData().substring(0, 2048) + " ...");
    }
}
