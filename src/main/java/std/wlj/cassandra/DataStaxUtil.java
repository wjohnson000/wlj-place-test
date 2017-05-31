package std.wlj.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class DataStaxUtil {

    private static String clusterAddress = "127.0.0.1";
    private static Cluster cluster;

    public static Session connect() {
        cluster = Cluster.builder()
                    .addContactPoint(clusterAddress)
                    .build();
        return cluster.connect("interp");
    }
}
