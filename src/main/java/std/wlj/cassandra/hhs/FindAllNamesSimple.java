/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

/**
 * @author wjohnson000
 *
 */
public class FindAllNamesSimple {

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAWS.connect();
        System.out.println("SESS: " + cqlSession);

        List<String> results = new ArrayList<>();

        ResultSet rset = cqlSession.execute("SELECT * FROM hhs.name LIMIT 100");
        for (Row row : rset) {
            StringBuilder buff = new StringBuilder();
            buff.append(clean(row.getString("id")));
            buff.append("|").append(clean(row.getString("language")));
            buff.append("|").append(clean(row.getString("name")));
            buff.append("|").append(clean(row.getString("nametype")));
            buff.append("|").append(clean(row.getString("collectionid")));
            buff.append("|").append(clean(row.getString("collectioninfo")));
            results.add(buff.toString());
        }
        Files.write(Paths.get("C:/temp/dev-all-names.text"), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        cqlSession.close();
        System.exit(0);
    }

    static String clean(String text) {
        return (text == null) ? "" : text.replace('\n', ' ').replace('\r', ' ').trim();
    }
}
