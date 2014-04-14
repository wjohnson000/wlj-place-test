package org.familysearch.std.wlj.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.Map;

import org.familysearch.standards.place.database.util.DbUtility;


public class TestGenerateChains {
    public static void main(String... args) throws Exception {
        String driver = "org.postgresql.Driver";
        String url    = "jdbc:postgresql://fh2-std-place-db-team.cqbtyzjgnvqo.us-east-1.rds.amazonaws.com:5432/p124";
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, "sams_place", "sams_place");
        DbUtility.setConnection(conn);
        DbUtility.seedPlaceChain();
        PrintWriter pwOut = new PrintWriter(new FileOutputStream(new File("C:/temp/chain-01.txt")));
        Iterator<Map.Entry<Integer,String>> iter = DbUtility.getChainIterator();
        while (iter.hasNext()) {
            Map.Entry<Integer,String> entry = iter.next();
            pwOut.println(entry.getKey() + " | " + entry.getValue());
        }
        pwOut.close();
        conn.close();
    }

}
