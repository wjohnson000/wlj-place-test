package std.wlj.kml;

import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.place.dao.DAOFactory;
import org.familysearch.standards.place.dao.dbimpl.DAOFactoryImpl;

import std.wlj.datasource.DbConnectionManager;

public class DAL_PlaceRepContained {

    public static void main(String... args) {
        DAOFactory daoFactory = new DAOFactoryImpl(DbConnectionManager.getDataSourceSams());

        long then = System.nanoTime();
        List<Integer> placeReps = daoFactory.getRepBoundaryDAO().containsPlaceRep(1, Arrays.asList(333, 335, 337, 339, 341, 343, 345, 347, 349, 351, 11));
        long nnow = System.nanoTime();
        System.out.println("TIME: " + (nnow-then)/1_000_000.0);
        placeReps.forEach(repId -> System.out.println("  " + repId));
    }
}
