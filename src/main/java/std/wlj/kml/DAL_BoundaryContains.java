package std.wlj.kml;

import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.place.dao.DAOFactory;
import org.familysearch.standards.place.dao.dbimpl.DAOFactoryImpl;
import org.familysearch.standards.place.dao.model.DbRepBoundary;

import std.wlj.util.DbConnectionManager;

public class DAL_BoundaryContains {

    public static void main(String... args) {
        DAOFactory daoFactory = new DAOFactoryImpl(DbConnectionManager.getDataSourceSams());

        for (int i=2;  i<=101;  i+=10) {
            long then = System.nanoTime();
            List<DbRepBoundary> boundaries = daoFactory.getRepBoundaryDAO().containsBoundary(1, Arrays.asList(i, i+1, i+2, i+3, i+4, i+5, i+6, i+7, i+8, i+9));
            long nnow = System.nanoTime();
            System.out.println(i + " --> TIME: " + (nnow-then)/1_000_000.0);
            boundaries.forEach(bb -> System.out.println("  " + bb.getId() + " . " + bb.getPointCount()));
        }
    }
}
