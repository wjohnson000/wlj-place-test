package std.wlj.jdbc;

import java.sql.SQLException;

import org.postgresql.util.PGtokenizer;

public class KeyValue {

    static final String type = "keyvalue";

    String key;
    String val;

    public KeyValue(String key, String val) {
        this.key = key;
        this.val = val;
    }

    public KeyValue(String str) throws SQLException {
        PGtokenizer tknizer = new PGtokenizer(PGtokenizer.removePara(str), ',');
        key = String.valueOf(tknizer.getToken(0)).toString();
        val = String.valueOf(tknizer.getToken(1)).toString();
    }

    public String getKey() {
        return key;
    }

    public String getVal() {
        return val;
    }

    public String toString() {
        return key + "=" + val;
    }

    public String toStringPG() {
        return "(" + key + "," + val + ")";
    }
}
