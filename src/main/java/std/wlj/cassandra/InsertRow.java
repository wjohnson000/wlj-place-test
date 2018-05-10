package std.wlj.cassandra;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.ColumnDefinitions.Definition;

public class InsertRow {

    public static void main(String... args) {
        try (Session session = DataStaxUtil.connect()) {
            addRow(session);
        } catch(Exception ex) {
            System.out.println("Exception [" + ex.getClass().getName() + "]: " + ex.getMessage());
        }
        System.exit(0);
    }

    static void addRow(Session session) {
        String uglyQuery =
            "INSERT INTO Interpretation " +
            "(id, t_text, params, t_parse, last_updated, results, rep_ids, raw_scores, rel_scores) " + 
            "VALUES ( " +
            "  34942717-49bf-474f-873b-66d15f483868, " + 
            "  'Provo, UT, UT',  " +
            "  { 'type': '11', 'parent': 'Utah' }, " + 
            "  [ 'one', 'two', 'three' ],  " +
            " '2017-01-10', " + 
            "  [ { 'rep_id': 111, 'raw_score': 90, 'rel_score': 85 }, { 'rep_id': 222, 'raw_score': 80, 'rel_score': 75 }, { 'rep_id': 333, 'raw_score': 70, 'rel_score': 65 } ], " + 
            "  [ 111, 222, 333 ], " +
            "  [ 90, 80, 70 ], " +
            "  [ 85, 75, 65 ] )";

        ResultSet rset = session.execute(uglyQuery);
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
