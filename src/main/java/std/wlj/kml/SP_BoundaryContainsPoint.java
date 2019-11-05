package std.wlj.kml;

import java.sql.*;
import java.util.List;

import std.wlj.util.DbConnectionManager;

public class SP_BoundaryContainsPoint {

    static final double[][] latLongList = {
        { 40.233845, -111.658531 },
    };

    public static void main(String... args) throws SQLException {
        try(Connection conn = DbConnectionManager.getConnectionSams()) {
            for (double[] latLong : latLongList) {
                System.out.println("\n==============================================================================================");
                System.out.println("Lat=" + latLong[0] + "; Long=" + latLong[1]);
                List<Integer> ids = PGUtility.listContainsPoint(conn, latLong[0], latLong[1]);
                ids.stream()
                    .map(id -> PGUtility.readModel(conn, id))
                    .forEach(System.out::println);
            }
        }
    }
}
