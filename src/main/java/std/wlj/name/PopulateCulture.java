package std.wlj.name;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import std.wlj.util.DbConnectionManager;

public class PopulateCulture {

    static final String[] queries = {
        "INSERT INTO culture(culture_code) VALUES('english')",
        "INSERT INTO culture(culture_code) VALUES('japanese')",
        "INSERT INTO culture(culture_code) VALUES('danish')",

        "INSERT INTO culture_attribute(attribute_type, attribute_value) VALUES('continent', 'north america')",
        "INSERT INTO culture_attribute(attribute_type, attribute_value) VALUES('continent', 'asia')",
        "INSERT INTO culture_attribute(attribute_type, attribute_value) VALUES('continent', 'europe')",

        "INSERT INTO culture_attribute_link(culture_id, culture_attribute_id) VALUES(2, 1)",
        "INSERT INTO culture_attribute_link(culture_id, culture_attribute_id) VALUES(3, 2)",
        "INSERT INTO culture_attribute_link(culture_id, culture_attribute_id) VALUES(4, 3)",

        "INSERT INTO culture_description(culture_id, culture_locale, title, description) VALUES(2, 'en', 'en-title', 'English names')",
        "INSERT INTO culture_description(culture_id, culture_locale, title, description) VALUES(3, 'ja', 'ja-title', 'Japanese names')",
        "INSERT INTO culture_description(culture_id, culture_locale, title, description) VALUES(4, 'da', 'da-title', 'Danish names')",
    };

    public static void main(String... args) throws SQLException {
        try(Connection conn = DbConnectionManager.getConnectionPCAS()) {
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
