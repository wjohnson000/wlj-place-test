package std.wlj.name;

import java.sql.*;
import java.util.Arrays;

import std.wlj.datasource.DbConnectionManager;

public class ClearDatabase {

    static final String[] queries = {
        "DELETE FROM relationship",
        "DELETE FROM name_context_link",
        "DELETE FROM name_context",
        "DELETE FROM name",
        "DELETE FROM name_type WHERE name_type_id > 2",
        "DELETE FROM culture_attribute_link",
        "DELETE FROM culture_description",
        "DELETE FROM culture_attribute",
        "DELETE FROM culture WHERE culture_id > 1",

        "ALTER SEQUENCE culture_attribute_culture_attribute_id_seq RESTART WITH 1",
        "ALTER SEQUENCE culture_culture_id_seq RESTART WITH 2",
        "ALTER SEQUENCE name_context_name_context_id_seq RESTART WITH 1",
        "ALTER SEQUENCE name_name_id_seq RESTART WITH 3",
        "ALTER SEQUENCE name_type_name_type_id_seq RESTART WITH 1",
    };

    public static void main(String... args) throws SQLException {
        try(Connection conn = DbConnectionManager.getConnectionName()) {
            Arrays.stream(queries).forEach(query -> executeQuery(conn, query));
        }
    }

    static void executeQuery(Connection conn, String query) {
        try(Statement stmt = conn.createStatement()) {
            System.out.println("\nQuery: " + query);
            int cnt = stmt.executeUpdate(query);
            System.out.println("  CNT: " + cnt);
        } catch(SQLException ex) {
            System.out.println("   EX: " + ex.getMessage());
        }
    }
}
