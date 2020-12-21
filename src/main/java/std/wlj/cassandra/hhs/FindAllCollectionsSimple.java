/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import org.familysearch.homelands.lib.common.util.JsonUtility;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.fasterxml.jackson.databind.JsonNode;

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
            String details = row.getString("details");
            JsonNode node = JsonUtility.parseJson(details);
            String description = JsonUtility.getStringValue(node, "description");
            JsonNode attributionNode = JsonUtility.getJsonNode(node, "attribution");
            String attribution = JsonUtility.getStringValue(attributionNode, "en");

            StringBuilder buff = new StringBuilder();
            buff.append(row.getString("id"));
            buff.append("|").append(row.getString("name"));
            buff.append("|").append(row.getInstant("createdate"));
            buff.append("|").append(row.getSet("type", String.class));
            buff.append("|").append(description);
            buff.append("|").append(attribution);
            System.out.println(buff.toString());
        }

        cqlSession.close();
        System.exit(0);
    }
}
