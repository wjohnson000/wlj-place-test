/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.util.Map;
import java.util.TreeMap;

import org.familysearch.homelands.core.persistence.CassandraOps;
import org.familysearch.homelands.core.persistence.mapper.ItemRowMapper;
import org.familysearch.homelands.core.persistence.model.ItemData;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;

/**
 * @author wjohnson000
 *
 */
public class ReadItemsSerial {

    static String readItem = "SELECT * FROM item WHERE id = ? AND type = ?";

    static Map<String, String> itemData = new TreeMap<>();
    static {
        itemData.put("MM98-QLZ", "ACTIVITY");
        itemData.put("MM9Z-4Y7", "EVENT");
        itemData.put("MM9Z-TZM", "ACTIVITY");
        itemData.put("MM9Z-G9Y", "EVENT");
        itemData.put("MM98-35X", "PERSON");
        itemData.put("MM98-3TR", "PERSON");
        itemData.put("MM9Z-GZP", "EVENT");
        itemData.put("MM98-QL6", "ACTIVITY");
        itemData.put("MM9Z-GZB", "EVENT");
        itemData.put("MM98-Q3C", "ACTIVITY");
        itemData.put("MM9Z-Y4C", "ACTIVITY");
        itemData.put("MM98-3VF", "PERSON");
        itemData.put("MM9Z-VGD", "FACT");
        itemData.put("MM9Z-GSY", "EVENT");
        itemData.put("MM9Z-X6G", "EVENT");
        itemData.put("MM98-9R9", "SERIES");
        itemData.put("MM9Z-VTC", "ACTIVITY");
        itemData.put("MM9Z-XSZ", "FACT");
        itemData.put("MM98-38T", "ACTIVITY");
        itemData.put("MM9Z-XMM", "FACT");
        itemData.put("MM9Z-53Z", "EVENT");
        itemData.put("MM98-3KT", "ACTIVITY");
        itemData.put("MM9Z-5S1", "EVENT");
        itemData.put("MM9Z-K6Z", "EVENT");
    }

    public static void main(String...args) throws Exception {
        CqlSession   cqlSession = SessionUtilityAWS.connect();
        CassandraOps cassandraOps = new CassandraOps(cqlSession);
        ItemRowMapper itemRowMapper = new ItemRowMapper();

        long time0 = System.nanoTime();
        PreparedStatement stmt = cassandraOps.prepare(readItem);
        for (Map.Entry<String, String> entry : itemData.entrySet()) {
            BoundStatement readStmt = stmt.bind(entry.getKey(), entry.getValue());
            ItemData itemData = cassandraOps.read(readStmt, ConsistencyLevel.LOCAL_ONE, itemRowMapper);
            if (itemData == null) {
                System.out.println("Item: " + entry.getKey() + " --> " + entry.getValue() + " --> NOT FOUND");
            } else {
                System.out.println("Item: " + itemData.getId() + " --> " + itemData.getType()); 
            }
        }

        long time1 = System.nanoTime();
        System.out.println("\nTime: " + (time1 - time0) / 1_000_000.0);

        System.exit(0);
    }
}
