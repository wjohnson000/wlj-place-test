/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;

/**
 * Connect to the local Cassandra (DataStax DSE) running in Docker ...
 * 
 * @author wjohnson000
 *
 */
public abstract class SessionUtilityAWS {

    private static final int      clusterPort    = 9042;
    private static final String[] clusterAddress = { "10.37.120.128", "10.37.121.133", "10.37.122.67" };

    static CqlSession connect() {
        String password = JOptionPane.showInputDialog(null, "Password:");
        if (password == null) {
            return null;
        }

        List<InetSocketAddress> contactPoints =
                Arrays.stream(clusterAddress)
                      .map(ip -> new InetSocketAddress(ip, clusterPort))
                      .collect(Collectors.toList());

        return CqlSession.builder()
                    .withKeyspace(CqlIdentifier.fromCql("hhs"))
                    .addContactPoints(contactPoints)
                    .withAuthCredentials("wjohnson000", password)
                    .withLocalDatacenter("us-east_core")
                    .build();
    }
}
