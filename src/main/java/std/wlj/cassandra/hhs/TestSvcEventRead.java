/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import org.familysearch.homelands.persistence.dao.ItemDAO;
import org.familysearch.homelands.persistence.dao.EventSearchDAO;
import org.familysearch.homelands.svc.cassandra.EventServiceImpl;
import org.familysearch.homelands.svc.exception.ItemServiceException;
import org.familysearch.homelands.svc.model.Event;

import com.datastax.driver.core.Session;

/**
 * @author wjohnson000
 *
 */
public class TestSvcEventRead {

    public static void main(String... args) {
        try (Session session = SessionUtility.connect()) {
            ItemDAO itemDAO = new ItemDAO(session);
            EventSearchDAO searchDAO = new EventSearchDAO(session);
            EventServiceImpl eventSvc = new EventServiceImpl(itemDAO, searchDAO);
            addEvent(eventSvc);
        } catch(Exception ex) {
            System.out.println("Exception [" + ex.getClass().getName() + "]: " + ex.getMessage());
        }

        System.exit(0);
    }

    static void addEvent(EventServiceImpl eventSvc) throws ItemServiceException {
        Event event = eventSvc.getEvent("MMMM-3Z9");
        SessionUtility.printEvent("CURRENT EVENT", event);
    }
}
