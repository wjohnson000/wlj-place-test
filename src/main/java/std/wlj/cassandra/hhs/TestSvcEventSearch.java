/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.familysearch.homelands.persistence.dao.EventSearchDAO;
import org.familysearch.homelands.persistence.dao.ItemDAO;
import org.familysearch.homelands.svc.cassandra.EventServiceImpl;
import org.familysearch.homelands.svc.exception.ItemServiceException;
import org.familysearch.homelands.svc.model.Event;
import org.familysearch.homelands.svc.model.ItemKeys;
import org.familysearch.homelands.svc.model.SearchSpec;

import com.datastax.driver.core.Session;

/**
 * @author wjohnson000
 *
 */
public class TestSvcEventSearch {

    public static void main(String... args) {
        try (Session session = SessionUtility.connect()) {
            ItemDAO itemDAO = new ItemDAO(session);
            EventSearchDAO searchDAO = new EventSearchDAO(session);
            EventServiceImpl eventSvc = new EventServiceImpl(itemDAO, searchDAO);
            doSearch(eventSvc);
        } catch(Exception ex) {
            System.out.println("Exception [" + ex.getClass().getName() + "]: " + ex.getMessage());
        }

        System.exit(0);
    }

    static void doSearch(EventServiceImpl eventSvc) throws ItemServiceException {
        int pagenum = 1;
        boolean more = true;
        while (more) {
            Map<String, String> fields = new HashMap<>();
            fields.put(ItemKeys.KEY_REPID, "123456");
            SearchSpec spec = new SearchSpec(null, pagenum++, 12, fields);

            List<Event> events = eventSvc.search(spec);
            System.out.println("\n");
            events.forEach(ev -> SessionUtility.printEvent("ev.x", ev));
            more = ! events.isEmpty();
        }
    }
}
