package std.wlj.name;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.datasource.DbConnectionManager;

public class PopulateName {

    static final String[] queries = {
        "INSERT INTO name_context(is_male, is_female) VALUES(TRUE, FALSE)",
        "INSERT INTO name_context(is_male, is_female) VALUES(FALSE, TRUE)",
        "INSERT INTO name_context(is_male, is_female) VALUES(TRUE, TRUE)",

        "DELETE FROM name_context_link",
        "DELETE FROM name",
        "ALTER SEQUENCE name_context_name_context_id_seq RESTART WITH 1", 
        "ALTER SEQUENCE name_type_name_type_id_seq RESTART WITH 1"
    };

    public static void main(String... args) throws Exception {
        Set<String> male    = getMaleNames();
        Set<String> female  = getFemaleNames();
        Set<String> surname = getLastNames();

        try(Connection conn = DbConnectionManager.getConnectionPCAS()) {
            Arrays.stream(queries).forEach(query -> executeQuery(conn, query));

            male.forEach(name -> processMaleName(conn, female, name));
            female.forEach(name -> processFemaleName(conn, male, name));
            surname.forEach(name -> processSurname(conn, name));
        }
    }

    static Set<String> getMaleNames() throws IOException {
        List<String> names = Files.readAllLines(Paths.get("C:/temp/names/male-all.txt"), Charset.forName("UTF-8"));

        return names.stream()
            .map(name -> name.trim())
            .map(name -> PlaceHelper.split(name, ' '))
            .map(nameA -> nameA[0])
            .filter(name -> name.length() > 1)
            .map(name -> PlaceHelper.split(name, '\t'))
            .map(nameA -> nameA[0])
            .filter(name -> name.length() > 1)
            .map(name -> name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase())
            .collect(Collectors.toCollection(TreeSet::new));
    }

    static Set<String> getFemaleNames() throws IOException {
        List<String> names = Files.readAllLines(Paths.get("C:/temp/names/female-all.txt"), Charset.forName("UTF-8"));

        return names.stream()
           .map(name -> name.trim())
           .map(name -> PlaceHelper.split(name, ' '))
           .map(nameA -> nameA[0])
           .filter(name -> name.length() > 1)
           .map(name -> PlaceHelper.split(name, '\t'))
           .map(nameA -> nameA[0])
           .filter(name -> name.length() > 1)
           .map(name -> name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase())
            .collect(Collectors.toCollection(TreeSet::new));
    }

    static Set<String> getLastNames() throws IOException {
        List<String> names = Files.readAllLines(Paths.get("C:/temp/names/full.txt"), Charset.forName("UTF-8"));

        return names.stream()
            .map(name -> PlaceHelper.split(name, ' '))
            .filter(nameA -> nameA.length > 1)
            .map(nameA -> nameA[nameA.length-1])
            .map(name -> name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase())
            .collect(Collectors.toCollection(TreeSet::new));
    }

    static void processMaleName(Connection conn, Set<String> femaleAll, String name) {
        StringBuilder queryB = new StringBuilder();
        queryB.append("INSERT into name(text, name_type_id, culture_id, normalized_text, script, published)");
        queryB.append(" VALUES(");
        queryB.append("'").append(name).append("', ");
        queryB.append("1, ");
        queryB.append("2, ");
        queryB.append("'").append(name.toLowerCase()).append("', ");
        queryB.append("'en', ");
        queryB.append("TRUE)");
//        System.out.println(queryB.toString());
        executeQuery(conn, queryB.toString());

        int ctxId = (femaleAll.contains(name)) ? 3 : 1;
        queryB = new StringBuilder();
        queryB.append("INSERT INTO name_context_link(name_id, name_context_id, frequency, weight)");
        queryB.append(" VALUES(");
        queryB.append(getNameId(conn)).append(", ");
        queryB.append(ctxId).append(", ");
        queryB.append("50, ");
        queryB.append("50)");
//        System.out.println(queryB.toString());
        executeQuery(conn, queryB.toString());
    }

    static void processFemaleName(Connection conn, Set<String> maleAll, String name) {
        int ctxId = (maleAll.contains(name)) ? 3 : 2;
        if (ctxId == 2) {
            StringBuilder queryB = new StringBuilder();
            queryB.append("INSERT into name(text, name_type_id, culture_id, normalized_text, script, published)");
            queryB.append(" VALUES(");
            queryB.append("'").append(name).append("', ");
            queryB.append("1, ");
            queryB.append("2, ");
            queryB.append("'").append(name.toLowerCase()).append("', ");
            queryB.append("'en', ");
            queryB.append("TRUE)");
//            System.out.println(queryB.toString());
            executeQuery(conn, queryB.toString());
            
            queryB = new StringBuilder();
            queryB.append("INSERT INTO name_context_link(name_id, name_context_id, frequency, weight)");
            queryB.append(" VALUES(");
            queryB.append(getNameId(conn)).append(", ");
            queryB.append(ctxId).append(", ");
            queryB.append("50, ");
            queryB.append("50)");
//            System.out.println(queryB.toString());
            executeQuery(conn, queryB.toString());
        }
    }

    static void processSurname(Connection conn, String name) {
        StringBuilder queryB = new StringBuilder();
        queryB.append("INSERT into name(text, name_type_id, culture_id, normalized_text, script, published)");
        queryB.append(" VALUES(");
        queryB.append("'").append(name).append("', ");
        queryB.append("2, ");
        queryB.append("2, ");
        queryB.append("'").append(name.toLowerCase()).append("', ");
        queryB.append("'en', ");
        queryB.append("TRUE)");
//        System.out.println(queryB.toString());
        executeQuery(conn, queryB.toString());

        queryB = new StringBuilder();
        queryB.append("INSERT INTO name_context_link(name_id, name_context_id, frequency, weight)");
        queryB.append(" VALUES(");
        queryB.append(getNameId(conn)).append(", ");
        queryB.append("3, ");
        queryB.append("50, ");
        queryB.append("50)");
//        System.out.println(queryB.toString());
        executeQuery(conn, queryB.toString());
    }

    static int getNameId(Connection conn) {
        int nameId = 0;
        try(Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery("SELECT MAX(name_id) FROM name")) {
            if (rset.next()) {
                nameId = rset.getInt(1);
            }
        } catch(SQLException ex) {
            System.out.println("   EX: " + ex.getMessage());
        }
        return nameId;
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
