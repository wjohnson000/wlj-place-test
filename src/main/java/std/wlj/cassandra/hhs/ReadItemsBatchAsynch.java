/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.familysearch.homelands.core.persistence.CassandraOps;
import org.familysearch.homelands.core.persistence.mapper.ItemRowMapper;
import org.familysearch.homelands.core.persistence.model.ItemData;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;

/**
 * @author wjohnson000
 *
 */
public class ReadItemsBatchAsynch {

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

    public static void main(String...args) {
        CqlSession cqlSession = SessionUtilityAWS.connect();
        CassandraOps cassandraOps = new CassandraOps(cqlSession);
        ItemRowMapper itemRowMapper = new ItemRowMapper();

        PreparedStatement stmt = cassandraOps.prepare(readItem);
        System.out.println("Setup is complete!");

        long time0 = System.nanoTime();
        List<CompletionStage<ItemData>> futures = new ArrayList<>();
        for (Map.Entry<String, String> entry : itemData.entrySet()) {
            CompletionStage<AsyncResultSet> stage = cqlSession.executeAsync(stmt.bind(entry.getKey(), entry.getValue()));
            futures.add(stage.thenApply(ars -> handleResult(ars, itemRowMapper)));
        }
        long time1 = System.nanoTime();

        List<ItemData> items = futures.stream()
            .map(cs -> (CompletableFuture<ItemData>)cs)
//            .map(cf -> cf.join())
            .map(cf -> {
                try {
                    return cf.get(1000L, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                   return null;
                }
            })
            .filter(item -> item != null)
            .collect(Collectors.toList());
        long time2 = System.nanoTime();

        System.out.println("Count: " + items.size());
        items.forEach(it -> System.out.println("Item: " + it.getId() + " --> " + it.getType()));

        System.out.println("\nTime1: " + (time1 - time0) / 1_000_000.0);
        System.out.println("Time2: " + (time2 - time1) / 1_000_000.0);

        System.exit(0);
    }

    /**
     * @param ars
     * @return
     * @throws Exception 
     */
    static ItemData handleResult(AsyncResultSet ars, ItemRowMapper itemRowMapper) {
        try {
            return itemRowMapper.mapRow(ars.one());
        }
        catch (Exception ex) {
            System.out.println("EEE: " + ex.getMessage());
            return null;
        }
    }
}
