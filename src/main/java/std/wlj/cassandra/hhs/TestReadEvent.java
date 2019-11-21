/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import org.familysearch.homelands.persistence.dao.EventDAO;
import org.familysearch.homelands.persistence.model.Event;

import com.datastax.driver.core.Session;

/**
 * @author wjohnson000
 *
 */
public class TestReadEvent {

    public static void main(String... args) {
        try (Session session = SessionUtility.connect()) {
            readEvent(session);
        } catch(Exception ex) {
            System.out.println("Exception [" + ex.getClass().getName() + "]: " + ex.getMessage());
        }

        System.exit(0);
    }

    static void readEvent(Session session) {
        EventDAO eventDAO = new EventDAO(session);
        Event event1 = eventDAO.read("MMMM-7M9");
        Event event2 = eventDAO.read("ABCD-123");

        SessionUtility.printEvent("MMMM-7M9", event1);
        SessionUtility.printEvent("ABCD-123", event2);
    }
}
