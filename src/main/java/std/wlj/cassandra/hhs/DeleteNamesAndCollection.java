/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;

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
public class DeleteNamesAndCollection {

    static String[] collIds = {
        "MMM9-XL1",
    };

    static String readCollectionCQL = "SELECT * FROM hhs.collectionData WHERE id = ";
    static String delCollectionCQL  = "DELETE FROM hhs.collectionData WHERE id = ";
    static String delNameCQL        = "DELETE FROM hhs.name WHERE id = ";
    static String delNameSearchCQL  = "DELETE FROM hhs.name_search WHERE nameId = ";

    public static void main(String...args) throws Exception {
        Map<String, List<String>> collToName = getNameIds();

        CqlSession cqlSession = SessionUtilityAWS.connect();
        System.out.println("SESS: " + cqlSession);

        int count = 0;
        List<String> statements = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : collToName.entrySet()) {
            System.out.println("Coll-ID: " + entry.getKey());
            ResultSet rset = cqlSession.execute(readCollectionCQL + "'" + entry.getKey() + "'");
            Row row = rset.one();
            if (row != null) {
                String name = row.getString("name");
                System.out.println("    nam: " + name);
                if (name.startsWith("Oxford Collection of Surnames")  &&  entry.getValue().size() == 70420) {
                    for (String nameId : entry.getValue()) {
                        count++;
                        statements.add(delNameCQL + "'" + nameId + "'");
                        statements.add(delNameSearchCQL + "'" + nameId + "'");
                        if (statements.size() > 100) {
                            System.out.println("COUNT: " + count);
                            executeBatch(cqlSession, statements);
                            statements.clear();
                        }
                    }
                    ResultSet rs3 = cqlSession.execute(delCollectionCQL + "'" + entry.getKey() + "'");
                    System.out.println("    app? " + rs3.wasApplied());
                }
            }
        }
        executeBatch(cqlSession, statements);

        cqlSession.close();
        System.exit(0);
    }

    static Map<String, List<String>> getNameIds() throws Exception {
        Map<String, List<String>> collToName = new TreeMap<>();
        Arrays.stream(collIds)
              .forEach(id -> collToName.put(id, new ArrayList<>()));

        List<String> names = Files.readAllLines(Paths.get("C:/temp/dev-name-details-all.txt"), StandardCharsets.UTF_8);
        names.stream()
             .map(row -> PlaceHelper.split(row, '|'))
             .filter(rr -> rr.length == 5)
             .filter(rr -> collToName.containsKey(rr[4]))
             .forEach(rr -> collToName.get(rr[4]).add(rr[0]));

        return collToName;
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
