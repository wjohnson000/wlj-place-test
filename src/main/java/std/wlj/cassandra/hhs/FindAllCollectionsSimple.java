/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

/**
 * @author wjohnson000
 *
 */
public class FindAllCollectionsSimple {

    public static void main(String...args) throws Exception {
        CqlSession cqlSession = SessionUtilityAWS.connect();
        System.out.println("SESS: " + cqlSession);

        ResultSet rset = cqlSession.execute("SELECT * FROM hhs.collectiondata");
        for (Row row : rset) {
            StringBuilder buff = new StringBuilder();
            buff.append(row.getString("id"));
            buff.append("|").append(row.getString("name"));
            System.out.println(buff.toString());
        }

        cqlSession.close();
        System.exit(0);
    }
}
