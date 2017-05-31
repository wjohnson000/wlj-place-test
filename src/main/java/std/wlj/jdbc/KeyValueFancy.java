package std.wlj.jdbc;

import java.io.Serializable;
import java.sql.SQLException;

import org.postgresql.util.PGobject;
import org.postgresql.util.PGtokenizer;

public class KeyValueFancy extends PGobject implements Serializable, Cloneable {

    private static final long serialVersionUID = 8362165102840582728L;

    String key;
    String val;

    public KeyValueFancy() { 
        setType("keyvalue");
    }

    public KeyValueFancy(String key, String val) {
        this();
        this.key = key;
        this.val = val;
    }

    public void setValue(String str) throws SQLException {
        PGtokenizer t = new PGtokenizer(PGtokenizer.removePara(str), ',');
        key = String.valueOf(t.getToken(0)).toString();
        val = String.valueOf(t.getToken(1)).toString();
    }

    public String getValue() {
        return key + "=" + val;
    }

    public String getKey() {
        return key;
    }

    public String getVal() {
        return val;
    }

    public String toStringPG() {
        return "(" + key + "," + val + ")";
    }
}
