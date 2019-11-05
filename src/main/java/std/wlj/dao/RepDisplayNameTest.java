/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dao;

import java.util.List;

import org.familysearch.standards.place.dao.RepDisplayNameDAO;
import org.familysearch.standards.place.dao.dbimpl.RepDisplayNameDAOImpl;
import org.familysearch.standards.place.dao.model.DbRepDisplayName;

import std.wlj.util.DbConnectionManager;

/**
 * @author wjohnson000
 *
 */
public class RepDisplayNameTest {

    public static void main(String...args) {
        RepDisplayNameDAO dispNameDAO = new RepDisplayNameDAOImpl(DbConnectionManager.getDataSourceAwsDev());

        System.out.println("\n================================================");
        List<DbRepDisplayName> names = dispNameDAO.readByRepId(128);
        for (DbRepDisplayName name : names) {
            System.out.println("Name: " + name.getLocale() + " . " + name.getText() + " . " + name.isDeleted());
        }

        System.out.println("\n================================================");
        names = dispNameDAO.readAllByRepId(128);
        for (DbRepDisplayName name : names) {
            System.out.println("Name: " + name.getLocale() + " . " + name.getText() + " . " + name.isDeleted());
        }
    }
}
