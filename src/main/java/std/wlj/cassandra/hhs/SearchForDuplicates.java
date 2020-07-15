/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

/**
 * @author wjohnson000
 *
 */
public class SearchForDuplicates {

    public static void main(String...args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("C:/temp/oxford-duplicate-names.txt"), StandardCharsets.UTF_8);
        List<String> names = lines.stream()
                                  .map(line -> getName(line))
                                  .filter(line -> line != null)
                                  .collect(Collectors.toList());

        CqlSession cqlSession = SessionUtilityAWS.connect();
        System.out.println("SESS: " + cqlSession);

        for (String name : names) {
            ResultSet rset = cqlSession.execute("SELECT * FROM hhs.name WHERE name = '" + name + "'");
            System.out.println("\n" + name);
            for (Row row : rset) {
                System.out.println("ID: " + row.getString("id") + " --> '" + 
                                        row.getString("name") + "' [" + row.getString("nameType") + "]");
            }
        }
    }

    static String getName(String line) {
        int ndx = line.indexOf("-->");
        if (ndx < 0) {
            return null;
        } else {
            return line.substring(0, ndx).trim().toLowerCase();
        }
    }
}
