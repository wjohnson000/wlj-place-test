/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import org.familysearch.homelands.persistence.dao.ItemDAO;
import org.familysearch.homelands.persistence.dao.EventSearchDAO;
import org.familysearch.homelands.svc.cassandra.EventServiceImpl;
import org.familysearch.homelands.svc.exception.ItemServiceException;
import org.familysearch.homelands.svc.model.Event;
import org.familysearch.homelands.svc.util.JsonUtility;

import com.datastax.driver.core.Session;

/**
 * @author wjohnson000
 *
 */
public class TestSvcEventCreate {

    public static void main(String... args) {
        try (Session session = SessionUtility.connect()) {
            ItemDAO itemDAO = new ItemDAO(session);
            EventSearchDAO searchDAO = new EventSearchDAO(session);
            EventServiceImpl eventSvc = new EventServiceImpl(itemDAO, searchDAO);
            addEvent(eventSvc);
        } catch(Exception ex) {
            System.out.println("Exception [" + ex.getClass().getName() + "]: " + ex.getMessage());
            ex.printStackTrace();
        }

        System.exit(0);
    }

    static void addEvent(EventServiceImpl eventSvc) throws ItemServiceException {

        String contentEn = SessionUtility.makeJson(
            "name", "1936 Olympics",
            "title", "The 1936 Olympics in Berlin",
            "repid", "123456",
            "description", "These are the Olympics where Jesse Owen dominated the ...",
            "tags", "Olympics, Jesse Owen",
            "url", "http://wikipedia.org/1936_Olympics");
        String contentFr = SessionUtility.makeJson(
            "name.fr", "1936 Olympiques",
            "title.fr", "Le 1936 Olympiques du Berlin");
        String contentEs = SessionUtility.makeJson(
            "name.es", "1936 Olimpeaks",
            "title.es", "Los 1936 Olimpeaks de Berlin");
        String collection = SessionUtility.makeJson(
            "Source", "The mind of WLJ",
            "Expires", "10/11/2020");

        Event event = new Event();
        event.setSubtype("Olympics");
        event.setVisibility("PUBLIC");
        event.setCollectionInfo(JsonUtility.parseJson(collection));
        event.setContent("en", JsonUtility.parseJson(contentEn));
        event.setContent("fr", JsonUtility.parseJson(contentFr));
        event.setContent("es", JsonUtility.parseJson(contentEs));

        Event newEvent = eventSvc.createEvent(event, "wjohnson000");
        SessionUtility.printEvent("NEW EVENT", newEvent);
    }
}
