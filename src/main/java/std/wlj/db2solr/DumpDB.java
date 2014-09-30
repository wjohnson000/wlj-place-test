package std.wlj.db2solr;

import java.sql.Connection;
import java.sql.SQLException;

import org.familysearch.engage.foundation.dbcp.SasEnabledBasicDataSource;
import org.familysearch.sas.client.ObjectRequester;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.solr.load.LoadSolrProcessor;


public class DumpDB {
    public static void main(String... args) throws Exception {
        String solrLocation = args[0];
        String tempDir      = args[1];
        boolean runIt       = args.length > 2;

        String sasName = System.getProperty("PLACE_DB_SAS_OBJECT_NAME");
        String dbAddr  = System.getProperty("PLACE_DB_ADDRESS");
        String dbPort  = System.getProperty("PLACE_DB_PORT");
        String dbName  = System.getProperty("PLACE_DB_DATABASENAME");
        String jdbcUrl = "jdbc:postgresql://" + dbAddr + ":" + dbPort + "/" + dbName;

        System.out.println("JDBC-URL: " + jdbcUrl);
        ObjectRequester sasRequestor = new ObjectRequester();
        SasEnabledBasicDataSource dsFactory = new SasEnabledBasicDataSource();
        dsFactory.setDriverClassName("org.postgresql.Driver");
        dsFactory.setObjectRequester(sasRequestor);
        dsFactory.setSasObjectName(sasName);
        dsFactory.setUrl(jdbcUrl);
        dsFactory.setInitialSize(2);
//        dsFactory.setMaxActive(2);
        dsFactory.setValidationQuery("SELECT 1");
        dsFactory.setTestOnBorrow(true);

        if (runIt) {
            SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance(solrLocation);

            Connection tmpConn = dsFactory.getConnection();
            System.out.println("TMP-Conn: " + tmpConn.getClass().getName());
            Connection useConn = getUsableConnection(tmpConn);
            System.out.println("USE-Conn: " + useConn.getClass().getName());

            LoadSolrProcessor processor = new LoadSolrProcessor(useConn, solrConn);
            processor.runLoad(tempDir, false, true, false);

            try { solrConn.shutdown(); } catch(Exception ex) { }
        }
        try { dsFactory.close();   } catch(Exception ex) { }

        System.exit(0);
    }
    
    /**
     * Unwrap the connection from the data-source looking for a PostgreSQL connection.
     * 
     * @return PostgreSQL connection
     */
    private static Connection getUsableConnection(Connection dbSource) throws SQLException {
        Connection dbConn = dbSource.getMetaData().getConnection();

        if (dbConn != null  &&  dbConn instanceof org.apache.commons.dbcp.PoolableConnection) {
            org.apache.commons.dbcp.PoolableConnection poolConn = (org.apache.commons.dbcp.PoolableConnection)dbConn;
            Connection delegateConn = poolConn.getDelegate();
            if (delegateConn != null  &&  delegateConn instanceof org.postgresql.core.BaseConnection) {
                return delegateConn;
            }

            Connection innerConn = poolConn.getInnermostDelegate();
            if (innerConn != null  &&  innerConn instanceof org.postgresql.core.BaseConnection) {
                return innerConn;
            }
        }

        return dbSource;
    }

}
