/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.jira;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import std.wlj.util.DbConnectionManager;

/**
 * @author wjohnson000
 *
 */
public class STD7175 {

    private static int[] REP_IDS = {
        780534,
        786768,
        799681,
        799680,
        780355,
        780356,
        780356,
        780379,
        780468,
        780466,
        799667,
        780470,
        780476,
        799661,
        780237,
        780230,
        799662,
        799663,
        799659,
        799652,
        780289,
        780324,
        786592,
        780159,
        780131,
        780141,
        780145,
        780120,
        780109,
        780113,
        780015,
        779981,
        780060,
        779857,
        799747,
        788741,
        779948,
        779958,
        779958,
        788678,
        779964,
        779771,
        779772,
        779762,
        797425,
        779717,
        779714,
        779740,
        779738,
        779742,
        779733,
        779731,
        779736,
        779737,
        779821,
        779815,
        779816,
        799727,
        787022,
        788820,
        764452,
        764457,
        764504,
        764485,
        764486,
        764487,
        788838,
        764405,
        764414,
        764407,
        764377,
        764365,
        781144,
        764168,
        797523,
        764170,
        764177,
        764260,
        764262,
        764069,
        799671,
        764056,
        800000,
        799999,
        764162,
        764160,
        763924,
        763897,
        763900,
        763899,
        800017,
        763909,
        763947,
        763948,
        782361,
        763963,
        763966,
        763967,
        764019,
        764017,
        780264,
        779780,
        764471,
        787096,
        764436,
        764518,
        764491,
        764495,
        782561,
        764264,
        799787,
        780667,
        780486,
        780543,
        780541,
        780593,
        779611,
        780728,
        781832,
        780592,
        780578,
        780388,
        780409,
        788725,
        780186,
        780162,
        779822,
        780172,
        780029,
        780005,
        779988,
        781853,
        780055,
        799684,
        799685,
        779843,
        779853,
        779863,
        779946,
        779947,
        779967,
        783831,
        779963,
        781846,
        779715,
        779719,
        779730,
        779819,
        779811,
        779784,
        788784,
        774939,
        774927,
        774929,
        774931,
        785990,
        785989,
        785995,
        764514,
        764508,
        764489,
        764324,
        764328,
        787706,
        764217,
        764188,
        796439,
        764135,
        799835,
        780511,
    };

    private static String REP_SQL = "SELECT * FROM place_rep WHERE rep_id = ? ORDER BY tran_id DESC";
    private static String PLC_SQL = "SELECT * FROM place WHERE place_id = ? ORDER BY tran_id DESC";

    public static void main(String...args) throws SQLException {
        try(Connection conn = DbConnectionManager.getConnectionAws();
                PreparedStatement repStmt = conn.prepareStatement(REP_SQL);
                PreparedStatement plcStmt = conn.prepareStatement(PLC_SQL)) {
            for (int repId : REP_IDS) {
                int repDelId = 0;
                int repTranId = 0;

                int placeId = 0;
                int placeDelId = 0;
                int placeTranId = 0;

                repStmt.clearParameters();
                repStmt.setInt(1, repId);
                try(ResultSet rset = repStmt.executeQuery()) {
                    if (rset.next()) {
                        placeId = rset.getInt("owner_id");
                        repDelId = rset.getInt("delete_id");
                        repTranId = rset.getInt("tran_id");
                    }
                }

                if (placeId > 0) {
                    plcStmt.clearParameters();
                    plcStmt.setInt(1, placeId);
                    try(ResultSet rset = repStmt.executeQuery()) {
                        if (rset.next()) {
                            placeDelId = rset.getInt("delete_id");
                            placeTranId = rset.getInt("tran_id");
                        }
                    }
                }

                if (repDelId == 0  ||  placeDelId == 0) {
//                    System.out.println();
//                    System.out.println("NOT DELETED: " + repId + " --> " + repDelId + " . " + repTranId);
//                    System.out.println("NOT DELETED: " + placeId + " --> " + placeDelId + " . " + placeTranId);
                } else {
                    System.out.println();
                    System.out.println("UPDATE place_rep SET delete_id = NULL WHERE rep_id=" + repId + " AND tran_id=" + repTranId + ";");
                    System.out.println("UPDATE place SET delete_id = NULL WHERE place_id=" + placeId + " AND tran_id=" + placeTranId + ";");
                }
            }
        }
    }
}
