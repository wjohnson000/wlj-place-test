/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.familysearch.homelands.persistence.dao.EventSearchDAO;
import org.familysearch.homelands.persistence.dao.ItemDAO;
import org.familysearch.homelands.svc.cassandra.EventServiceImpl;
import org.familysearch.homelands.svc.model.Event;
import org.familysearch.homelands.svc.model.ItemKeys;
import org.familysearch.homelands.svc.util.JsonUtility;
import org.familysearch.standards.loader.sql.FileResultSet;

import com.datastax.driver.core.Session;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author wjohnson000
 *
 */
public class TestSvcEventCreateFromPlace {

    private static final String dataDir   = "C:/temp/db-dump";
    private static final String attrFile  = "attribute-all.txt";
    private static final String DELIMITER = "\\|";

    private static Map<Integer, String> attributeTypes = new TreeMap<>();
    static {
        attributeTypes.put(554, "ART");
        attributeTypes.put(556, "CLIMATE");
        attributeTypes.put(557, "CLOTHING_FASHION");
        attributeTypes.put(558, "COMMUNICATION");
        attributeTypes.put(559, "CUSTOMS");
        attributeTypes.put(537, "DENOM");
        attributeTypes.put(560, "DISASTERS");
        attributeTypes.put(561, "EDUCATION");
        attributeTypes.put(562, "ENTERTAINMENT");
        attributeTypes.put(546, "ETYMOLOGY");
        attributeTypes.put(563, "FOOD_RECIPES");
        attributeTypes.put(542, "GEOGRAPHIC_INFO");
        attributeTypes.put(541, "HISTORIC_INFO");
        attributeTypes.put(564, "HISTORICAL_MAPS");
        attributeTypes.put(538, "HISTORY");
        attributeTypes.put(565, "HOLIDAYS_CELEBRATIONS");
        attributeTypes.put(507, "INCORP_DATE");
        attributeTypes.put(566, "INVENTIONS_TECH");
        attributeTypes.put(429, "LDS_TMPLST");
        attributeTypes.put(567, "MEDICAL");
        attributeTypes.put(568, "MIGRATIONS");
        attributeTypes.put(569, "MUSIC");
        attributeTypes.put(570, "NEWSPAPERS");
        attributeTypes.put(513, "NOTE");
        attributeTypes.put(571, "OCCUPATIONS");
        attributeTypes.put(553, "PARISH_REGISTER");
        attributeTypes.put(543, "POLITIC_INFO");
        attributeTypes.put(479, "POP");
        attributeTypes.put(573, "POP_CULTURE");
        attributeTypes.put(574, "PROMINENT_PEOPLE");
        attributeTypes.put(581, "RELIG_HISTORY");
        attributeTypes.put(575, "SPORT");
        attributeTypes.put(576, "STORY");
        attributeTypes.put(577, "TRANSPORT");
        attributeTypes.put(545, "TRIVIA");
//        attributeTypes.put(474, "WIKIPEDIA_LINK");
        attributeTypes.put(579, "WORK_CHORES");
    }

    private static boolean doMore = true;
    private static int     count  = 0;

    public static void main(String... args) throws Exception {
        String collection = SessionUtility.makeJson(
            "Source", "STD Place Attributes",
            "Expires", "10/11/2020");
        JsonNode collNode = JsonUtility.parseJson(collection);

        try (Session session = SessionUtility.connect()) {
            ItemDAO itemDAO = new ItemDAO(session);
            EventSearchDAO searchDAO = new EventSearchDAO(session);
            EventServiceImpl eventSvc = new EventServiceImpl(itemDAO, searchDAO);

            addEvents(eventSvc, collNode);
        } catch(Exception ex) {
            System.out.println("Exception [" + ex.getClass().getName() + "]: " + ex.getMessage());
        }

        System.exit(0);
    }

    static void addEvents(EventServiceImpl eventSvc, JsonNode collNode) throws Exception {
        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, attrFile));

            while (rset.next()  &&  doMore) {
                int     typeId = rset.getInt("attr_type_id");
                boolean isDel  = rset.getBoolean("delete_flag");
                if (! isDel  &&  attributeTypes.containsKey(typeId)) {
                    addEvent(eventSvc, rset, collNode);
                }
            }
        }
    }

    static void addEvent(EventServiceImpl eventSvc, FileResultSet rset, JsonNode collNode) throws Exception {
        int     typeId = rset.getInt("attr_type_id");
        int     repId  = rset.getInt("rep_id");
        int     frYear = rset.getInt("year");
        int     toYear = rset.getInt("to_year");
        String  locale = rset.getString("locale");
        String  value  = rset.getString("attr_value");
        String  title  = rset.getString("title");
        String  url    = rset.getString("attr_url");
        String  urlTtl = rset.getString("attr_title");
        String  cpyNtc = rset.getString("copyright_notice");
        String  cpyUrl = rset.getString("copyright_url");

        try {
            String jsonStr = SessionUtility.makeJson(ItemKeys.KEY_REPID, String.valueOf(repId));
            JsonNode node = JsonUtility.parseJson(jsonStr);

            JsonUtility.addField(node, ItemKeys.KEY_LANG, locale);
            JsonUtility.addField(node, ItemKeys.KEY_TITLE, title);
            JsonUtility.addField(node, ItemKeys.KEY_VALUE, value);
            JsonUtility.addArray(node, ItemKeys.KEY_TAGS, attributeTypes.get(typeId), "place");
            JsonUtility.addField(node, "url", url);
            JsonUtility.addField(node, "urlTitle", urlTtl);
            JsonUtility.addField(node, "copyright", cpyNtc);
            JsonUtility.addField(node, "copyrightUrl", cpyUrl);
            if (frYear != 0  &&  frYear > -4000) {
                if (toYear != 0  &&  toYear >= frYear) {
                    JsonUtility.addField(node, ItemKeys.KEY_YEAR, frYear + "-" + toYear);
                } else {
                    JsonUtility.addField(node, ItemKeys.KEY_YEAR, frYear);
                }
            }

            if (count++ % 23 == 0) {
                Event event = new Event();
                event.setSubtype(attributeTypes.get(typeId));
                event.setVisibility("PUBLIC");
                event.setCollectionInfo(collNode);
                event.setContent("en", node);

                Event newEvent = eventSvc.createEvent(event, "wjohnson000");
                SessionUtility.printEvent("NEW EVENT", newEvent);
            }
        } catch(Exception ex) {
            System.out.println("EX: " + ex.getMessage());
            ex.printStackTrace();
            doMore = false;
        }
    }
}
