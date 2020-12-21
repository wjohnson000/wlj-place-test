/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

/**
 * @author wjohnson000
 *
 */
public class FindAllCollectionsPaged {

    private static final CompletableFuture<Object> resultFuture = new CompletableFuture<>();

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAWS.connect();
        System.out.println("SESS: " + cqlSession);

        queryCollection(cqlSession, null);

        cqlSession.close();
        System.exit(0);
    }

    static void queryCollection(CqlSession session, ByteBuffer pagingState) {
        System.out.println("HERE ... " + pagingState);
        session
            .executeAsync(SimpleStatement.builder("SELECT * FROM hhs.collectiondata")
                                         .setPageSize(100)
                                         .setPagingState(pagingState)
                                         .build())
            .whenComplete(
               (rs, error) -> {
                   if (error != null) {
                       System.out.println("Oops: " + error.getMessage());
                       resultFuture.completeExceptionally(error);
                   } else {
                       System.out.println("OK: " + rs);
                       for (Row row : rs.currentPage()) {
                           System.out.println(createFrom(row));
                       }

                       ByteBuffer nextPagingState = rs.getExecutionInfo().getPagingState();
                       if (nextPagingState == null) {
                           resultFuture.complete("HI");
                       } else {
                           queryCollection(session, nextPagingState);
                       }
                   }
               });
    }

    static String createFrom(Row row) {
        StringBuilder buff = new StringBuilder();
        buff.append(row.getString("id"));
        buff.append("|").append(row.getString("name"));
        return buff.toString();
    }
}
