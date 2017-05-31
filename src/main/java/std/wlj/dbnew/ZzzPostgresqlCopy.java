package std.wlj.dbnew;

import java.io.File;
import java.io.PrintWriter;
import java.sql.*;

import javax.sql.DataSource;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import std.wlj.datasource.DbConnectionManager;

public class ZzzPostgresqlCopy {
	public static void main(String... args) {
	    DataSource ds;
//        String tQuery =
//            "SELECT cit.rep_id, " +
//            "       cit.citation_id, " +
//            "       cit.source_id, " +
//            "       cit.tran_id, " +
//            "       cit.type_id, " +
//            "       TO_CHAR(cit.citation_date, 'YYYY-MM-DD') AS citation_date, " +
//            "       regexp_replace(cit.description, E'[\\n\\r]+', ' ', 'g') AS description, " +
//            "       cit.source_ref, " +
//            "       cit.delete_flag " +
//            "  FROM citation AS cit " +
//            " WHERE rep_id = 3356358 " +
//            " ORDER BY cit.rep_id ASC, cit.tran_id ASC ";

        String tQuery = "SELECT * FROM transaction ORDER BY tran_id";
        File targetLocation = new File("C:/temp/transaction-all.txt");
        try {
            ds = DbConnectionManager.getDataSourceWLJ();

            try(Connection conn = ds.getConnection();
            	PrintWriter pwOut = new PrintWriter(targetLocation, "UTF-8")) {
            	BaseConnection connX = conn.unwrap(org.postgresql.core.BaseConnection.class);
                System.out.println("Conn: " + conn);
                System.out.println("Conx: " + connX);

                pwOut.println(getHeader(ds, tQuery, '|'));
                CopyManager mgr = new CopyManager(connX);
            	long rowCount = mgr.copyOut("COPY (" + tQuery + ") TO STDOUT WITH csv DELIMITER '|'", pwOut);
            	System.out.println("RowCount: " + rowCount);
            }
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
        }

        System.exit(0);
	}
    /**
     * Return the detailed header for a given query
     * 
     * @param conn  database connection
     * @param query  SQL query
     * @param delimiter  field delimiter
     * @return header line
     */
    protected static String getHeader(DataSource dataSource, String query, char delimiter) {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSetMetaData rsmd = stmt.getMetaData();
            return getHeader(rsmd, delimiter);
        } catch (SQLException ex) {
            System.out.println("Unable to execute query: " + query);
        }

        return "";
    }

    /**
     * Return a header from a query -- technically the 'ResultSetMetaData' associated with a
     * query -- with the column name, type [java.sql.Types], display size, scale precision.
     * 
     * @param rsmd  ResultSetMetaData instance
     * @param delimiter  field delimiter
     * @return header record
     * @throws SQLException
     */
    protected static String getHeader(ResultSetMetaData rsmd, char delimiter) throws SQLException {
        StringBuilder buff = new StringBuilder(1024);

        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            buff.append((i <= 1) ? "" : delimiter);
            buff.append(rsmd.getColumnName(i));
            buff.append(",T:").append(rsmd.getColumnType(i));
            buff.append(",D:").append(rsmd.getColumnDisplaySize(i));
            buff.append(",S:").append(rsmd.getScale(i));
            buff.append(",P:").append(rsmd.getPrecision(i));
        }

        return buff.toString();
    }

}
