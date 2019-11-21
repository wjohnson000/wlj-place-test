/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import org.familysearch.homelands.persistence.dao.EventDAO;
import org.familysearch.homelands.persistence.model.Event;
import org.familysearch.homelands.persistence.model.EventDetail;

import com.datastax.driver.core.Session;

/**
 * @author wjohnson000
 *
 */
public class TestCreateEvent {

    public static void main(String... args) {
        try (Session session = SessionUtility.connect()) {
            addEvent(session);
        } catch(Exception ex) {
            System.out.println("Exception [" + ex.getClass().getName() + "]: " + ex.getMessage());
        }

        System.exit(0);
    }

    static void addEvent(Session session) {
        Event event = new Event();
        event.setType("event");

        EventDetail detail = new EventDetail();
        detail.setLang("en");
        detail.setValue("{ a:b c:d e:f g:h }");
        event.addLangDetail(detail);

        detail = new EventDetail();
        detail.setLang("fr");
        detail.setValue("{ a.fr:b.fr g.fr:h.fr }");
        event.addLangDetail(detail);

        EventDAO eventDAO = new EventDAO(session);
        Event eventX = eventDAO.create(event);
        SessionUtility.printEvent("NEW EVENT", eventX);
    }
}
