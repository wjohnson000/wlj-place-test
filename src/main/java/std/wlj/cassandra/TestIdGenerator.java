package std.wlj.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class TestIdGenerator {

    private static final int    clusterPort    = 9042;
    private static final String clusterAddress = "127.0.0.1";

    public static void main(String... args) {
        try (Session session = connect()) {
            printAll(session);
        } catch(Exception ex) {
            System.out.println("Exception [" + ex.getClass().getName() + "]: " + ex.getMessage());
        }

        System.exit(0);
    }

    static void printAll(Session session) {
        CassandraIdGenerator cid = new CassandraIdGenerator(session);
        for (int i=1;  i<1000;  i++) {
            System.out.println(cid.getNext());
        }
    }

    static Session connect() {
        Cluster cluster = Cluster.builder()
                    .addContactPoint(clusterAddress)
                    .withPort(clusterPort)
                    .build();

        return cluster.connect("hhs");
    }
}
