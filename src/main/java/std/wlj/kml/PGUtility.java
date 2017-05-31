package std.wlj.kml;

import java.sql.*;
import java.util.*;

/**
 * Utility class of convenience methods to execute various "PostGIS" operations.  These
 * have been culled down from a long list of available stored procedures.  The main
 * purposes of collecting them in one place include:
 * <ul>
 *   <li><strong>1: </strong>Avoid duplication of code</li>
 *   <li><strong>2: </strong>Easier changes if we determine that other logic is more optimal</li>
 * </ul>
 * @author wjohnson000
 *
 */
public class PGUtility {

    private static final double ACCEPTABLE_COVER_PERCENT = 98.5;

    private static final String SQL_SELECT =
        "SELECT rb.*, rdn.text " +
        "  FROM rep_boundary  AS rb " +
        "  LEFT OUTER JOIN rep_display_name AS rdn ON rdn.rep_id = rb.rep_id " +
        " WHERE rb.boundary_id = ? " +
        "   AND rdn.locale='en'";

    private static final String SQL_CONTAINS =
        "SELECT ST_Contains(FN_Get_Geometry(?), FN_Get_Geometry(?)) ";

    static final String SQL_CONTAINS_POINT =
        "SELECT boundary_id " +
        "  FROM rep_boundary " +
        " WHERE ST_Contains(FN_Get_Geometry(boundary_id), ST_SetSRID(ST_MakePoint(?, ?), 4326)) " +
        " ORDER BY boundary_id";

    static final String SQL_NEARBY =
        "SELECT rb.boundary_id " +
        "  FROM rep_boundary AS rb, " +
        "       (SELECT boundary_id, rep_id " +
        "          FROM rep_boundary " +
        "         WHERE boundary_id = ?) AS rbx " +
        " WHERE FN_Reps_Related(rbx.rep_id, rb.rep_id) = FALSE " +
        "   AND ST_DWithin(FN_Get_Geometry(?), FN_Get_Geometry(rb.boundary_id), .001) " +
        " ORDER BY boundary_id";

    private static final String SQL_COVER_PERCENT =
        "SELECT ST_Area(ST_Envelope(FN_Get_Geometry(?))), " +
        "       ST_Area(ST_Intersection(ST_Envelope(FN_Get_Geometry(?)), ST_Envelope(FN_Get_Geometry(?))))";

    private static final String SQL_TOUCHES =
        "SELECT boundary_id " +
        "  FROM rep_boundary " +
        " WHERE ST_Touches(FN_Get_Geometry(boundary_id), FN_Get_Geometry(?)) ";

    private static final Map<Integer, PGModel> modelCache = new HashMap<>();


    public static PGModel readModel(Connection conn, int id) {
        PGModel model = modelCache.get(id);
        if (model == null) {
            try(PreparedStatement stmt = conn.prepareStatement(SQL_SELECT)) {
                stmt.setInt(1, id);
                try(ResultSet rset = stmt.executeQuery()) {
                    if (rset.next()) {
                        model = new PGModel();
                        model.boundaryId = rset.getInt("boundary_id");
                        model.repId      = rset.getInt("rep_id");
                        model.name       = rset.getString("text");
                        model.pointCount = rset.getInt("point_count");
                        model.fromYear   = rset.getInt("from_year");
                        model.toYear     = rset.getInt("to_year");
                        model.deleteFlag = rset.getBoolean("delete_flag");
                        modelCache.put(id, model);
                    }
                }
            } catch(SQLException ex) {
                System.out.println("ReadModel@SQL-EX: " + ex.getMessage());
            }
        }
        return model;
    }

    public static boolean isContained(Connection conn, int outerId, int innerId) {
        return inside(conn, outerId, innerId)  ||
               coverPercent(conn, outerId, innerId) > ACCEPTABLE_COVER_PERCENT;
    }

    public static boolean inside(Connection conn, int outerId, int innerId) {
        boolean inside = false;

        try(PreparedStatement stmt = conn.prepareStatement(SQL_CONTAINS)) {
            stmt.setInt(1, outerId);
            stmt.setInt(2, innerId);
            try(ResultSet rset = stmt.executeQuery()) {
                if (rset.next()) {
                    inside = rset.getBoolean(1);
                }
            }
        } catch(SQLException ex) {
            System.out.println("Inside@SQL-EX: " + ex.getMessage());
        }

        return inside;
    }

    public static double coverPercent(Connection conn, int outerId, int innerId) {
        double coverPercent = 0.0;

        try(PreparedStatement stmt = conn.prepareStatement(SQL_COVER_PERCENT)) {
            stmt.setInt(1, innerId);
            stmt.setInt(2, outerId);
            stmt.setInt(3, innerId);
            try(ResultSet rset = stmt.executeQuery()) {
                if (rset.next()) {
                    double area = rset.getDouble(1);
                    double intersect = rset.getDouble(2);
                    coverPercent = intersect * 100.0 / area;
                }
            }
        } catch(SQLException ex) {
            System.out.println("CoverPercent@SQL-EX: " + ex.getMessage());
        }

        return coverPercent;
    }

    public static List<Integer> listTouching(Connection conn, int id) {
        List<Integer> touches = new ArrayList<>();

        try(PreparedStatement stmt = conn.prepareStatement(SQL_TOUCHES)) {
            stmt.setInt(1, id);
            try(ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    touches.add(rset.getInt("boundary_id"));
                }
            }
        } catch(SQLException ex) {
            System.out.println("ListTouching@SQL-EX: " + ex.getMessage());
        }

        return touches;
    }

    public static List<Integer> listContainsPoint(Connection conn, double lattd, double longtd) {
        List<Integer> touches = new ArrayList<>();

        try(PreparedStatement stmt = conn.prepareStatement(SQL_CONTAINS_POINT)) {
            stmt.setDouble(1, longtd);
            stmt.setDouble(2, lattd);
            try(ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    touches.add(rset.getInt("boundary_id"));
                }
            }
        } catch(SQLException ex) {
            System.out.println("ListContainsPT@SQL-EX: " + ex.getMessage());
        }

        return touches;
    }

    public static List<Integer> listNearby(Connection conn, int id) {
        List<Integer> touches = new ArrayList<>();

        try(PreparedStatement stmt = conn.prepareStatement(SQL_NEARBY)) {
            stmt.setInt(1, id);
            stmt.setInt(2, id);
            try(ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    touches.add(rset.getInt("boundary_id"));
                }
            }
        } catch(SQLException ex) {
            System.out.println("ListNearby@SQL-EX: " + ex.getMessage());
        }

        return touches;
    }
}
