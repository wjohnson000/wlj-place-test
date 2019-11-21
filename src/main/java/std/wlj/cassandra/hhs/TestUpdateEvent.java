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
public class TestUpdateEvent {

    public static void main(String... args) {
        try (Session session = SessionUtility.connect()) {
            updateEvent(session);
        } catch(Exception ex) {
            System.out.println("Exception [" + ex.getClass().getName() + "]: " + ex.getMessage());
        }

        System.exit(0);
    }

    static void updateEvent(Session session) {
        Event event = new Event();
        event.setId("MMMM-7M9");
        event.setType("event");

        EventDetail detail = new EventDetail();
        detail.setLang("en");
        detail.setValue("{ a:b c:d e:f g:h i:j }");
        event.addLangDetail(detail);

        detail = new EventDetail();
        detail.setLang("fr");
        detail.setValue("{ a.fr:b.fr }");
        event.addLangDetail(detail);

        detail = new EventDetail();
        detail.setLang("de");
        detail.setValue("{ a.de:b.fr c.de:d.de }");
        event.addLangDetail(detail);

        EventDAO eventDAO = new EventDAO(session);
        Event eventX = eventDAO.update(event);
        SessionUtility.printEvent("NEW EVENT", eventX);
    }
}
