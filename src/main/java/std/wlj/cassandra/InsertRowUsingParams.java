package std.wlj.cassandra;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.ExecutionInfo;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.core.PreparedStatement;

public class InsertRowUsingParams {

    public static void main(String... args) {
        try (Session session = DataStaxUtil.connect()) {
            addRow(session);
        } catch(Exception ex) {
            System.out.println("Exception [" + ex.getClass().getName() + "]: " + ex.getMessage());
        }
        System.exit(0);
    }

    static void addRow(Session session) {
        new MappingManager(session).udtCodec(InterpResult.class);

        String uglyQuery =
            "INSERT INTO Interpretation " +
            "(id, t_text, params, t_parse, last_updated, results, rep_ids, raw_scores, rel_scores) " + 
            "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Map<String,String> intParams = new HashMap<>();
        intParams.put("type", "11");
        intParams.put("parent", "Utah");

        InterpResult res01 = new InterpResult(111, 90, 85);
        InterpResult res02 = new InterpResult(222, 80, 75);
        InterpResult res03 = new InterpResult(333, 70, 65);

        PreparedStatement pstmt = session.prepare(uglyQuery);
        BoundStatement    bstmt = pstmt.bind(
                UUID.fromString("34942717-49bf-474f-873b-66d15f4838AB"),
                "Provo, UT, UT",
                intParams,
                Arrays.asList("ichi", "ni", "san"),
                new java.util.Date(),
                Arrays.asList(res01, res02, res03),
                Arrays.asList(111, 222, 333),
                Arrays.asList(90, 80, 70),
                Arrays.asList(85, 75, 65));

        ResultSet rset = session.execute(bstmt);

        ExecutionInfo execInfo = rset.getExecutionInfo();
        System.out.println("Exc -- Warn: " + execInfo.getWarnings());

        ColumnDefinitions columns = rset.getColumnDefinitions();
        for (Definition col : columns) {
            System.out.println("Col -- Name: " + col.getName());
            System.out.println("Col -- Type: " + col.getType());
        }
        for (Row row : rset) {
            System.out.println("Row ...");
            for (Definition col : columns) {
                System.out.println("   " + col.getName() + " --> " + row.getString(col.getName()));
            }
        }
    }

}
