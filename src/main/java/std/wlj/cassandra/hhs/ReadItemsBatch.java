/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.BatchableStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;

/**
 * @author wjohnson000
 *
 */
public class ReadItemsBatch {

    static String readItem = "SELECT * FROM item WHERE id = '%s' AND type = '%s'";

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

        long time0 = System.nanoTime();
        List<String> statements = new ArrayList<>();
        for (Map.Entry<String, String> entry : itemData.entrySet()) {
            String sql = String.format(readItem, entry.getKey(), entry.getValue());
            statements.add(sql);
        }
        long time1 = System.nanoTime();

        List<BatchableStatement<?>> batchableStatements =
                statements.stream()
                          .map(stmt -> new SimpleStatementBuilder(stmt).build())
                          .collect(Collectors.toList());
        BatchStatement batch = BatchStatement.builder(BatchType.LOGGED)
                                             .addStatements(batchableStatements)
                                             .build();
        long time2 = System.nanoTime();
        try {
            ResultSet rset = cqlSession.execute(batch);
            System.out.println("   RSET1: " + rset.wasApplied());
            for (Row row : rset) {
                System.out.println("Item: " + row.getString("id") + " --> " + row.getString("type")); 
            }
        } catch(Exception ex) {
            try {
                ResultSet rset = cqlSession.execute(batch);
                System.out.println("   RSET2: " + rset.wasApplied());
            } catch(Exception exx) {
                System.out.println("Unable to do this silliness -- EXX: " + exx.getMessage());
                statements.forEach(System.out::println);
            }
        }
        long time3 = System.nanoTime();

        System.out.println("\nTime1: " + (time1 - time0) / 1_000_000.0);
        System.out.println("Time2: " + (time2 - time1) / 1_000_000.0);
        System.out.println("Time3: " + (time3 - time2) / 1_000_000.0);

        System.exit(0);
    }
}
