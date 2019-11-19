package std.wlj.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.querybuilder.QueryBuilder;

public class PrintAllHHS {

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
        Statement allInterps = QueryBuilder.select().all().from("hhs", "sequence");
        ResultSet rset = session.execute(allInterps);
        ColumnDefinitions columns = rset.getColumnDefinitions();

        System.out.println("\n'sequence' table columns:");
        for (Definition col : columns) {
            System.out.println("  Col -- Name: " + col.getName() + " --> " + col.getType());
        }

        System.out.println();
        for (Row row : rset) {
            System.out.println("ROW name: " + row.getString("name"));
            System.out.println("    next: " + row.getInt("nextid"));
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
