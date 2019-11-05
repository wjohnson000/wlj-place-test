package std.wlj.dao;

import java.util.Comparator;
import java.util.List;

import org.familysearch.standards.place.dao.dbimpl.PlaceRepDAOImpl;
import org.familysearch.standards.place.dao.model.DbPlaceRep;

import std.wlj.util.DbConnectionManager;

public class PlaceRepByParent {
    public static void main(String...args) {
        int parentId = 262;
        PlaceRepDAOImpl prDAO = new PlaceRepDAOImpl(DbConnectionManager.getDataSourceWLJ());
        boolean isPar = prDAO.isParent(parentId);
        List<DbPlaceRep> children = prDAO.readChildren(parentId);
        System.out.println(parentId + " is parent? " + isPar);
        System.out.println("Children count: " + children.size());
        children.stream()
            .sorted(Comparator.comparingInt(DbPlaceRep::getId))
            .limit(15)
            .forEach(System.out::println);
    }
}
