/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.BatchableStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.datastax.oss.driver.internal.core.cql.PagingIterableSpliterator;

/**
 * @author wjohnson000
 *
 */
public class DeleteItemsTwo {

    final static String      collection    = "MMMS-8LV";
    final static String      selectItemAll = "SELECT * FROM hhs.item_search";
    final static String      deleteItem1   = "DELETE FROM hhs.item WHERE id = '%s' AND type = '%s'";
    final static String      deleteItem2   = "DELETE FROM hhs.item_search WHERE itemId = '%s'";

    final static Set<String> ignoreIds   = new TreeSet<>();
    static int count = 0;

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAWS.connect();
        System.out.println("SESS: " + cqlSession);

        List<String> deleteStmts = getDeleteStmts(cqlSession);
        System.out.println("ItemCount: " + count);
        System.out.println("StmtCount: " + deleteStmts.size());

        for (int i=0;  i<deleteStmts.size();  i+=50) {
            int start = i;
            int end   = Math.min(deleteStmts.size(), i+49);
            List<String> deleteChunk = deleteStmts.subList(start, end);
            System.out.println("... delete from " + start + " to " + end + " --> " + deleteChunk.size());
            executeBatch(cqlSession, deleteChunk);
        }

        cqlSession.close();
        System.exit(0);
    }

    static List<String> getDeleteStmts(CqlSession cqlSession) throws Exception {
        List<String> deleteStmts = new ArrayList<>();

        ResultSet rset = cqlSession.execute(selectItemAll);
        StreamSupport.stream(PagingIterableSpliterator.builder(rset).withChunkSize(1024).build(), true)
                                             .forEach(row -> addDelStatements(deleteStmts, row));

        return deleteStmts;
    }

    static void addDelStatements(List<String> deleteStmts, Row row) {
        count++;
        String id         = row.getString("itemId");
        String type       = row.getString("type");
        String collId     = row.getString("collectionId");
        List<String> tags = row.getList("tags", String.class);

        if (! "QUIZ".equals(type)  &&  collection.equals(collId)) {
            deleteStmts.add(String.format(deleteItem1, id, type));
            deleteStmts.add(String.format(deleteItem2, id));
        } else {
            System.out.println(id + "|" + type + "|" + collId + "|" + tags);
        }
    }

    static void executeBatch(CqlSession cqlSession, List<String> statements) {
        if (! statements.isEmpty()) {
            List<BatchableStatement<?>> batchableStatements =
                statements.stream()
                          .map(stmt -> new SimpleStatementBuilder(stmt).build())
                          .collect(Collectors.toList());

            BatchStatement batch =
                  BatchStatement.builder(BatchType.LOGGED)
                                .addStatements(batchableStatements)
                                .build();

            try {
                ResultSet rset = cqlSession.execute(batch);
                System.out.println("   RSET1: " + rset.wasApplied());
            } catch(Exception ex) {
                try {
                    ResultSet rset = cqlSession.execute(batch);
                    System.out.println("   RSET2: " + rset.wasApplied());
                } catch(Exception exx) {
                    System.out.println("Unable to do this silliness -- EXX: " + exx.getMessage());
                    statements.forEach(System.out::println);
                }
            }
        }
    }
}
