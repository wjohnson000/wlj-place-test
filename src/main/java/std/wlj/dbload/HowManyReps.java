package std.wlj.dbload;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.datasource.DbConnectionManager;

public class HowManyReps {

    private static final String QUERY_GET_REV_PLACES        = "SELECT DISTINCT(place_id) FROM place WHERE tran_id BETWEEN %d AND %d ";
    private static final String QUERY_GET_REV_REPS_BY_PLACE = "SELECT DISTINCT(rep_id) FROM place_rep WHERE owner_id IN ";

    private static final String QUERY_GET_REV_REP           = "SELECT DISTINCT(rep_id) FROM place_rep WHERE tran_id BETWEEN %d AND %d ";

    private static final String QUERY_GET_REV_ALT_JURIS     = "SELECT DISTINCT(rep_id) FROM alt_jurisdiction WHERE tran_id BETWEEN %d AND %d ";
    private static final String QUERY_GET_REV_ATTR          = "SELECT DISTINCT(rep_id) FROM rep_attr WHERE tran_id BETWEEN %d AND %d ";
    private static final String QUERY_GET_REV_CITN          = "SELECT DISTINCT(rep_id) FROM citation WHERE tran_id BETWEEN %d AND %d ";
    private static final String QUERY_GET_REV_DISP_NAME     = "SELECT DISTINCT(rep_id) FROM rep_display_name WHERE tran_id BETWEEN %d AND %d ";
    
    public static void main(String...args) throws IOException {
        List<String> trxByDays = Files.readAllLines(Paths.get("C:/temp/transaction-by-day.txt"), Charset.forName("UTF-8"));
        System.out.println("Days: " + trxByDays.size());

        try(Connection conn = DbConnectionManager.getConnectionAws()) {
            for (String trxByDay : trxByDays) {
                String[] chunks = PlaceHelper.split(trxByDay, '\t');
                if (chunks.length > 3) {
                    String date    = chunks[0];
                    int    trxLow  = Integer.parseInt(chunks[2]);
                    int    trxHigh = Integer.parseInt(chunks[3]);
                    if (! date.startsWith("2014")) {
                        List<Integer> reps = getReps(conn, trxLow, trxHigh);
                        System.out.println("Date: " + date + " --> count: " + reps.size());
                    }
                }
            }
        } catch(SQLException ex) {
            System.out.println("OOPS: " + ex.getMessage());
        }
    }

    static List<Integer> getReps(Connection conn, int trxLow, int trxHigh) throws SQLException {
        List<Integer> results = new ArrayList<>();
        results.addAll(getRepsByPlace(conn, trxLow, trxHigh));
        results.addAll(getRepsByRep(conn, trxLow, trxHigh));
        results.addAll(getRepsByOther(conn, trxLow, trxHigh));
        return results;
    }

    static List<Integer> getRepsByPlace(Connection conn, int trxLow, int trxHigh) throws SQLException {
        List<Integer> placeIds = getIds(conn, String.format(QUERY_GET_REV_PLACES, trxLow, trxHigh));
        if (placeIds.isEmpty()) {
            return placeIds;
        }

        String placeIdIn = placeIds.stream()
            .map(id -> String.valueOf(id))
            .collect(Collectors.joining(",", "(", ")"));

        return getIds(conn, QUERY_GET_REV_REPS_BY_PLACE + placeIdIn);
    }

    static List<Integer> getRepsByRep(Connection conn, int trxLow, int trxHigh) throws SQLException {
        return getIds(conn, String.format(QUERY_GET_REV_REP, trxLow, trxHigh));
    }

    static List<Integer> getRepsByOther(Connection conn, int trxLow, int trxHigh) throws SQLException {
        List<Integer> repIds = new ArrayList<>();

        repIds.addAll(getIds(conn, String.format(QUERY_GET_REV_ATTR, trxLow, trxHigh)));
        repIds.addAll(getIds(conn, String.format(QUERY_GET_REV_CITN, trxLow, trxHigh)));
        repIds.addAll(getIds(conn, String.format(QUERY_GET_REV_DISP_NAME, trxLow, trxHigh)));
        repIds.addAll(getIds(conn, String.format(QUERY_GET_REV_ALT_JURIS, trxLow, trxHigh)));

        return repIds;
    }

    static List<Integer> getIds(Connection conn, String query) throws SQLException {
        List<Integer> ids = new ArrayList<>();

        try(Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery(query)) {
            while(rset.next()) {
                ids.add(rset.getInt(1));
            }
        }

        return ids;
    }
}
