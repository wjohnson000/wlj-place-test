/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.util.Arrays;
import java.util.List;

import org.familysearch.homelands.persistence.dao.ItemDAO;
import org.familysearch.homelands.persistence.model.DbItem;

import com.datastax.driver.core.Session;

/**
 * @author wjohnson000
 *
 */
public class TestDbItemCreate {

    public static void main(String... args) {
        try (Session session = SessionUtility.connect()) {
            addEvent(session);
        } catch(Exception ex) {
            System.out.println("Exception [" + ex.getClass().getName() + "]: " + ex.getMessage());
        }

        System.exit(0);
    }

    static void addEvent(Session session) {
        DbItem itemEn = new DbItem();
        itemEn.setLanguage("en");
        itemEn.setContent(" { a:b c:d e:f g:h }");
        itemEn.setModelVersion("1.0");
        itemEn.setType("FACT");
        itemEn.setSubtype("Population");
        itemEn.setVisibility("Public");
        itemEn.setSystemInfo(" { } ");

        DbItem itemEs = new DbItem();
        itemEs.setLanguage("es");
        itemEs.setContent(" { a:b_es }");

        DbItem itemDe = new DbItem();
        itemDe.setLanguage("de");
        itemDe.setContent(" { a:b_de }");

        DbItem itemJa = new DbItem();
        itemJa.setLanguage("ja");
        itemJa.setContent(" { a:b_ja }");

        List<DbItem> items = Arrays.asList(itemEn, itemEs, itemDe, itemJa);

        ItemDAO itemDAO = new ItemDAO(session);
        List<DbItem> itemx = itemDAO.create(items);
        System.out.println("ITEMX: " + itemx);
    }
}
