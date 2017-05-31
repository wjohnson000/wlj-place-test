package std.wlj.datasource;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;

public class DbConnectionManager {

    private static BasicDataSource sams  = null;
    private static BasicDataSource stds  = null;
    private static BasicDataSource wlj   = null;
    private static BasicDataSource aws   = null;
    private static BasicDataSource dev55 = null;

    private static Properties jdbcProps = new Properties();
    static {
        try {
            jdbcProps.load(new FileInputStream(new File("C:/Users/wjohnson000/.std-db.props")));
        } catch (Exception e) {
            System.out.println("Unable to load DB properties ... can't proceed ...");
        }
    }

    public static class DbServices {
        public DbReadableService readService;
        public DbWritableService writeService;

        DbServices(DbReadableService readService, DbWritableService writeService) {
            this.readService = readService;
            this.writeService = writeService;
        }

        public void shutdown() {
            readService.shutdown();
            writeService.shutdown();
        }
    }

    /** =============================================================================
     *  Connection or DataSource to 'wlj' database with username='sams_user'
     *  ============================================================================= */
    public static Connection getConnectionSams() throws SQLException {
        if (sams == null) {
            sams = setupDataSource("sams");
        }
        return sams.getConnection();
    }

    public static DataSource getDataSourceSams() {
        if (sams == null) {
            sams = setupDataSource("sams");
        }
        return sams;
    }

    public static DbServices getDbServicesSams() {
        DataSource ds = getDataSourceSams();
        return new DbServices(new DbReadableService(ds), new DbWritableService(ds));
    }

    /** =============================================================================
     *  Connection or DataSource to 'standards' database with username='sams_user'
     *  ============================================================================= */
    public static Connection getConnectionStds() throws SQLException {
        if (stds == null) {
            stds = setupDataSource("stds");
        }
        return stds.getConnection();
    }

    public static DataSource getDataSourceStds() {
        if (stds == null) {
            stds = setupDataSource("stds");
        }
        return stds;
    }

    public static DbServices getDbServicesStds() {
        DataSource ds = getDataSourceStds();
        return new DbServices(new DbReadableService(ds), new DbWritableService(ds));
    }

    /** =============================================================================
     *  Connection or DataSource to 'wlj' database with username='wlj'
     *  ============================================================================= */
    public static Connection getConnectionWLJ() throws SQLException {
        if (wlj == null) {
            wlj = setupDataSource("wlj");
        }
        return wlj.getConnection();
    }

    public static DataSource getDataSourceWLJ() {
        if (wlj == null) {
            wlj = setupDataSource("wlj");
        }
        return wlj;
    }

    public static DbServices getDbServicesWLJ() {
        DataSource ds = getDataSourceWLJ();
        return new DbServices(new DbReadableService(ds), new DbWritableService(ds));
    }

    /** =============================================================================
     *  Connection or DataSource to AWS-DEV database with username='fs_schema_owner'
     *  ============================================================================= */
    public static Connection getConnectionAws() throws SQLException {
        if (aws == null) {
            aws = setupDataSource("aws-dev");
        }
        return aws.getConnection();
    }

    public static DataSource getDataSourceAwsDev() {
        if (aws == null) {
            aws = setupDataSource("aws-dev");
        }
        return aws;
    }

    public static DbServices getDbServicesAws() {
        DataSource ds = getDataSourceAwsDev();
        return new DbServices(new DbReadableService(ds), new DbWritableService(ds));
    }

    /** =============================================================================
     *  Connection or DataSource to AWS-DEV-55 database with username='fs_schema_owner'
     *  ============================================================================= */
    public static Connection getConnectionAwsDev55() throws SQLException {
        if (dev55 == null) {
            dev55 = setupDataSource("aws-dev55");
        }
        return dev55.getConnection();
    }

    public static DataSource getDataSourceDev55() {
        if (dev55 == null) {
            dev55 = setupDataSource("aws-dev55");
        }
        return dev55;
    }

    public static DbServices getDbServicesDev55() {
        DataSource ds = getDataSourceDev55();
        return new DbServices(new DbReadableService(ds), new DbWritableService(ds));
    }

    /** =============================================================================
     *  Helper methods
     *  ============================================================================= */
    static BasicDataSource setupDataSource(String key) {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(jdbcProps.getProperty(key + ".jdbc.driver"));
        dataSource.setUsername(jdbcProps.getProperty(key + ".jdbc.username"));
        dataSource.setPassword(jdbcProps.getProperty(key + ".jdbc.password"));
        dataSource.setUrl(jdbcProps.getProperty(key + ".jdbc.url"));
        dataSource.setMaxActive(2);
        dataSource.setMaxIdle(2);
        dataSource.setInitialSize(2);
        dataSource.setValidationQuery("SELECT 1");

        return dataSource;
    }
}
