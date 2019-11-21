/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import org.familysearch.homelands.persistence.model.Event;
import org.familysearch.homelands.persistence.model.EventDetail;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

/**
 * Connect to the local Cassandra (DataStax DSE) running in Docker ...
 * 
 * @author wjohnson000
 *
 */
public abstract class SessionUtility {

    private static final int    clusterPort    = 9042;
    private static final String clusterAddress = "127.0.0.1";

    static Session connect() {
        Cluster cluster = Cluster.builder()
                    .addContactPoint(clusterAddress)
                    .withPort(clusterPort)
                    .build();

        return cluster.connect("hhs");
    }

    static void printEvent(String id, Event event) {
        System.out.println("\nEvent for: '" + id + "'");
        if (event == null) {
            System.out.println("NULL ...");
        } else {
            System.out.println("ID: '" + event.getId() + "' --> " + event.getType());
            for (EventDetail detail : event.getLangDetails()) {
                System.out.println("  "  + detail.getLang() + " --> " + detail.getValue());
            }
        }
    }
}
