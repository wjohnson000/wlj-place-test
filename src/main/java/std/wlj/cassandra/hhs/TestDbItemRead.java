/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.util.List;

import org.familysearch.homelands.persistence.dao.ItemDAO;
import org.familysearch.homelands.persistence.model.DbItem;

import com.datastax.driver.core.Session;

/**
 * @author wjohnson000
 *
 */
public class TestDbItemRead {

    public static void main(String... args) {
        try (Session session = SessionUtility.connect()) {
            readEvent(session);
        } catch(Exception ex) {
            System.out.println("Exception [" + ex.getClass().getName() + "]: " + ex.getMessage());
        }

        System.exit(0);
    }

    static void readEvent(Session session) {
        ItemDAO itemDAO = new ItemDAO(session);

        List<DbItem> items = itemDAO.read("MMMM-MM9");
        SessionUtility.printEvent("MMMM-MM9", items);
    }
}
