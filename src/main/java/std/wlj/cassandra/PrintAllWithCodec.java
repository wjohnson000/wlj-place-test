package std.wlj.cassandra;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.UserType;
import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.mapping.MappingManager;

public class PrintAllWithCodec {

    public static void main(String... args) {
        try (Session session = DataStaxUtil.connect()) {
            printAll(session);
        } catch(Exception ex) {
            System.out.println("Exception [" + ex.getClass().getName() + "]: " + ex.getMessage());
        }
        System.exit(0);
    }

    static void printAll(Session session) {
        Statement allInterps = QueryBuilder.select().all().from("interp", "Interpretation");
        ResultSet rset = session.execute(allInterps);
        ColumnDefinitions columns = rset.getColumnDefinitions();
        for (Definition col : columns) {
            System.out.println("Col -- Name: " + col.getName() + " --> " + col.getType());
        }

        System.out.println();
        UserType rType = null;
        for (UserType uType : session.getCluster().getMetadata().getKeyspace("interp").getUserTypes()) {
            if (uType.getTypeName().equals("InterpResult")) {
                rType = uType;
            }
        }

        System.out.println("r-type:" + rType);
        System.out.println("      :" + rType.getClass().getName());
        System.out.println("      :" + rType.getTypeName());
        System.out.println("      :" + rType.getName());
        System.out.println("      :" + rType.getFieldNames());

        new MappingManager(session).udtCodec(InterpResult.class);
//        TypeCodec<InterpResult> resultsCodec = new MappingManager(session).udtCodec(InterpResult.class);
//        session.getCluster().getConfiguration().getCodecRegistry().register(resultsCodec);

        System.out.println();
        for (Row row : rset) {
            System.out.println("ROW ... uuid: " + row.getUUID("id"));
            System.out.println("        text: " + row.getString("t_text"));
            System.out.println("        parm: " + row.getMap("params", String.class, String.class));
            System.out.println("        pars: " + row.getList("t_parse", String.class));
            System.out.println("        updt: " + row.getTimestamp("last_updated"));
            System.out.println("        reps: " + row.getList("rep_ids", Integer.class));
            System.out.println("        raws: " + row.getList("raw_scores", Integer.class));
            System.out.println("        rels: " + row.getList("rel_scores", Integer.class));
            System.out.println("        rslt: " + row.getList("results", InterpResult.class));
        }
    }
}
