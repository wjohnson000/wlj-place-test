/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.internal.core.cql.PagingIterableSpliterator;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author wjohnson000
 *
 */
public class FindItemsAllPaged {

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAWS.connect();
        System.out.println("SESS: " + cqlSession);

        List<String> results = new ArrayList<>();

        ResultSet rset = cqlSession.execute("SELECT * FROM hhs.item");
        Stream<String> stream = StreamSupport.stream(PagingIterableSpliterator.builder(rset).withChunkSize(1024).build(), true).map(row -> createFrom(row));
        stream.forEach(line -> results.add(line));

        Files.write(Paths.get("C:/temp/dev-items.txt"), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        cqlSession.close();
        System.exit(0);
    }

    static String createFrom(Row row) {
        StringBuilder buff = new StringBuilder();

        buff.append(clean(row.getString("id")));
        buff.append("|").append(clean(row.getString("type")));
        buff.append("|").append(clean(row.getString("title")));
        buff.append("|").append(clean(row.getString("details")));

        return buff.toString();
    }

    static String clean(String text) {
        return (text == null) ? "" : text.replace('\n', ' ').replace('\r', ' ').trim();
    }
}
