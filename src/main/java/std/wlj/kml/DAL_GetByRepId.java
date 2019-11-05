package std.wlj.kml;

import java.util.List;

import org.familysearch.standards.place.dao.DAOFactory;
import org.familysearch.standards.place.dao.dbimpl.DAOFactoryImpl;
import org.familysearch.standards.place.dao.model.DbRepBoundary;

import std.wlj.util.DbConnectionManager;

public class DAL_GetByRepId {

    public static void main(String... args) {
        DAOFactory daoFactory = new DAOFactoryImpl(DbConnectionManager.getDataSourceSams());

        long then = System.nanoTime();
        List<DbRepBoundary> repBs = daoFactory.getRepBoundaryDAO().readByRepId(331);
        long nnow = System.nanoTime();
        System.out.println("TIME: " + (nnow-then)/1_000_000.0);

        for (DbRepBoundary repB : repBs) {
            System.out.println("b: " + repB.getId() + " --> " + repB.getRepId() + " [" + repB.getPointCount() + "]");
            System.out.println("" + repB.getGeographyData().substring(0, 1024) + " ...");
        }
    }
}
