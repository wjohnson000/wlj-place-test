/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dao;

import java.util.List;

import org.familysearch.standards.place.dao.PlaceNameDAO;
import org.familysearch.standards.place.dao.dbimpl.PlaceNameDAOImpl;
import org.familysearch.standards.place.dao.model.DbPlaceName;

import std.wlj.util.DbConnectionManager;

/**
 * @author wjohnson000
 *
 */
public class PlaceVariantNameTest {

    public static void main(String...args) {
        PlaceNameDAO dispNameDAO = new PlaceNameDAOImpl(DbConnectionManager.getDataSourceAwsDev());

        System.out.println("\n================================================");
        List<DbPlaceName> names = dispNameDAO.readByPlaceId(3606657);
        for (DbPlaceName name : names) {
            System.out.println("Name: " + name.getId() + " . " + name.getLocale() + " . " + name.getText() + " . " + name.isDeleted());
        }

        System.out.println("\n================================================");
        names = dispNameDAO.readAllByPlaceId(3606657);
        for (DbPlaceName name : names) {
            System.out.println("Name: " + name.getId() + " . " + name.getLocale() + " . " + name.getText() + " . " + name.isDeleted());
        }
    }
}
