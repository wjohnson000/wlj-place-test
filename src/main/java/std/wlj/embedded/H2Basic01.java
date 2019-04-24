/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.embedded;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.dao.DAOFactory;
import org.familysearch.standards.place.dao.dbimpl.DAOFactoryImpl;
import org.h2.tools.RunScript;

/**
 * @author wjohnson000
 *
 */
public class H2Basic01 {

    public static void main(String... args) {
        DAOFactory factory = getDAOFactory();
        System.out.println("FF: " + factory);
        System.out.println("pd: " + factory.getPlaceDAO());
    }

    static DAOFactory getDAOFactory() {
        return new DAOFactoryImpl(setupDataSource());
    }

    static BasicDataSource setupDataSource() {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        dataSource.setUrl("jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1");
        dataSource.setMaxActive(2);

        return dataSource;
    }

    static void loadSchema(BasicDataSource bds) throws SQLException, FileNotFoundException {
        Connection conn = bds.getConnection();
        String schemaPath = H2Basic01.class.getResource("/db/schema.sql").getPath();
        Reader schema = new FileReader(schemaPath);
        RunScript.execute(conn, schema);
    }

    static void cleanSchema(BasicDataSource bds) throws SQLException {
        Connection conn = bds.getConnection();
        Reader clean = new StringReader("drop all objects");
        RunScript.execute(conn, clean);
    }
}
