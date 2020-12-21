package std.wlj.access;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import std.wlj.util.DbConnectionManager;

public class CitationBatchTest {

    public static void main(String... args) {
        DataSource dataSource = DbConnectionManager.getDataSourceSams();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        final String query =
            "INSERT INTO citation(citation_id, tran_id, source_id, rep_id, type_id, citation_date, description, source_ref, delete_flag) VALUES(?,?,?,?,?,?,?,?,?)";

        BatchPreparedStatementSetter batchSetter = createBatchSetter();
        int[] count = jdbcTemplate.batchUpdate(query, batchSetter);
        Arrays.stream(count).forEach(System.out::println);

        System.exit(0);
    }

    protected static BatchPreparedStatementSetter createBatchSetter() {
        return new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement pstmt, int ndx) throws SQLException {
                pstmt.setNull(1, Types.INTEGER);
                pstmt.setNull(2, Types.INTEGER);
                pstmt.setInt(3, 5);
                pstmt.setInt(4, 3333);
                pstmt.setInt(5, 460);
                pstmt.setDate(6, new Date(System.currentTimeMillis()));
                pstmt.setNull(7, Types.VARCHAR);
                pstmt.setString(8, "source-ref-bulk-" + ndx);
                pstmt.setBoolean(9, false);
            }

            @Override
            public int getBatchSize() {
                return 10;
            }
        };
    }
}
