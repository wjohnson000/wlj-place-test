/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.BatchableStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder;


/**
 * @author wjohnson000
 *
 */
public class DeleteFromFile {

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAWS.connect();
        System.out.println("SESS: " + cqlSession);

        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-G4B.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-P2V.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-PLQ.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-G4V.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-PGS.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-RMZ.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-P26.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-PK5.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-P2N.cql");
        executeFromFile(cqlSession, "C:/temp/dev-delete-MMM3-PL7.cql");

        cqlSession.close();
        System.exit(0);
    }

    static void executeFromFile(CqlSession cqlSession, String filepath) throws Exception {
        // Read in the list of delete statements
        List<String> deleteStmts = Files.readAllLines(Paths.get(filepath), StandardCharsets.UTF_8);
        System.out.println("StmtCount: " + deleteStmts.size());
        
        for (int i=0;  i<deleteStmts.size();  i+=50) {
            int start = i;
            int end   = Math.min(deleteStmts.size(), i+49);
            List<String> deleteChunk = deleteStmts.subList(start, end);
            
            System.out.println("... delete from " + start + " to " + end + " --> " + deleteChunk.size());
            executeBatch(cqlSession, deleteChunk);
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
