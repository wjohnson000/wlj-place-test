/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.familysearch.homelands.lib.common.util.JsonUtility;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.BatchableStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;
import com.datastax.oss.driver.internal.core.cql.PagingIterableSpliterator;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Delete all items that are NOT associated with any collection.  This uses the "item" table only.
 * @author wjohnson000
 *
 */
public class DeleteItemsNoCollectionMore {

    final static String  selectCollection = "SELECT * FROM hhs.collectiondata";
    final static String  selectItemAll    = "SELECT * FROM hhs.item";
    final static String  deleteItem1      = "DELETE FROM hhs.item WHERE id = '%s' AND type = '%s'";

    final static Set<String> collectionIds = new TreeSet<>();

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAWS.connect();
        System.out.println("SESS: " + cqlSession);

        collectionIds.addAll(getAllCollections(cqlSession));
        collectionIds.remove("MMM3-G4V");
        System.out.println("Collections: " + collectionIds);

        List<String> deleteStmts = getDeleteStmts(cqlSession);
        System.out.println("StmtCount: " + deleteStmts.size());

        for (int i=0;  i<deleteStmts.size();  i+=25) {
            int start = i;
            int end   = Math.min(deleteStmts.size(), i+24);
            List<String> deleteChunk = deleteStmts.subList(start, end);
            System.out.println("... delete from " + start + " to " + end + " --> " + deleteChunk.size());
            executeBatch(cqlSession, deleteChunk);
        }

        cqlSession.close();
        System.exit(0);
    }

    static Set<String> getAllCollections(CqlSession cqlSession) throws Exception {
        Set<String> ids = new HashSet<>();
        ResultSet rset = cqlSession.execute(selectCollection);
        for (Row row : rset) {
            ids.add(row.getString("id"));
        }

        return ids;
    }

    static List<String> getDeleteStmts(CqlSession cqlSession) throws Exception {
        List<String> deleteStmts = new ArrayList<>();

        ResultSet rset = cqlSession.execute(selectItemAll);
        StreamSupport.stream(PagingIterableSpliterator.builder(rset).withChunkSize(512).build(), true)
                                             .forEach(row -> addDelStatements(deleteStmts, row));

        return deleteStmts;
    }

    static void addDelStatements(List<String> deleteStmts, Row row) {
        String id     = row.getString("id");
        String type   = row.getString("type");
        String collId = null;

        try {
            String details    = row.getString("details");
            JsonNode json     = JsonUtility.parseJson(details);
            if (json != null) {
                collId = JsonUtility.getStringValue(json, "collectionId");
            }
        } catch(Exception ex) {
            System.out.println(id + "|" + type + "|" + ex.getMessage());
        }

        if (collId != null  &&  ! collectionIds.contains(collId)) {
            deleteStmts.add(String.format(deleteItem1, id, type));
        } else {
            System.out.println(id + "|" + type + "|" + collId);
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
