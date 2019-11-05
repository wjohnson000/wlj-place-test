package std.wlj.kml;

import java.sql.*;
import java.util.List;
import java.util.stream.IntStream;

import std.wlj.util.DbConnectionManager;

public class SP_BoundaryNearby {

    public static void main(String... args) throws SQLException {
        try(Connection conn = DbConnectionManager.getConnectionSams()) {
            IntStream.rangeClosed(1, 224).forEach(
                id -> {
                    List<Integer> ids = PGUtility.listNearby(conn, id);
                    System.out.println("\n==============================================================================================");
                    System.out.println(PGUtility.readModel(conn, id));
                    System.out.println();
                    ids.stream()
                        .map(idx -> PGUtility.readModel(conn, idx))
                        .forEach(System.out::println);
                }
            );
        }
    }
}
