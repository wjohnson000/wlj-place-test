/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.loader.sql.FileResultSet;

/**
 * @author wjohnson000
 *
 */
public class TestBaseValidator {

    protected static final String REGEX_HTTP = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;()*'&=$ &\\u0080-\\u9fff]+";

    protected static final List<String> URLS_TO_TEST = new ArrayList<>();
    static {
        URLS_TO_TEST.add("https://en.wikipedia.org/wiki/New_York_(state)");
    }

    public static void main(String...args) throws SQLException {
        for (String url : URLS_TO_TEST) {
            System.out.println(url.trim().matches(REGEX_HTTP) + " --> " + url);
        }

        testAllUrls();
        System.exit(0);
    }

    static void testAllUrls() throws SQLException {
        FileResultSet rset = new FileResultSet();
        rset.setSeparator("\\|");
        File attrs = new File("C:/temp/db-dump/attribute-all.txt");
        rset.openFile(attrs);
        while (rset.next()) {
            String value = rset.getString("attr_value");
            if (value != null  &&  value.toLowerCase().startsWith("http")) {
                if (! value.trim().matches(REGEX_HTTP)) {
                    System.out.println(value.trim().matches(REGEX_HTTP) + " --> " + value);
                }
            }
        }
        rset.close();
    }
}
