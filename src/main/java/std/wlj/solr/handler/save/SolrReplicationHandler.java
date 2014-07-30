package std.wlj.solr.handler.save;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.UpdateParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.ReplicationHandler;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.CommitUpdateCommand;
import org.familysearch.sas.client.ObjectRequester;
import org.familysearch.sas.schema.Attribute;
import org.familysearch.sas.schema.Sas;
//import org.familysearch.standards.core.logging.Logger;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.solr.load.AppDataReader;
import org.familysearch.standards.place.solr.load.PlaceRepFileGenerator;
import org.familysearch.standards.place.solr.load.PlaceRepReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Custom SOLR handler for replication.  Intercept all replication requests and
 * determine if the current system is in a state to handle replication.  If so,
 * pass all requests to the parent system.  If not, populate the SOLR repository
 * from the associated database before allowing any replication to happen.
 * 
 * @author wjohnson000
 *
 */
@SuppressWarnings("rawtypes")
public class SolrReplicationHandler extends ReplicationHandler {

    /** Logger ... use SOLR's logger, not fs-standards one */
    private static final Logger logger = LoggerFactory.getLogger(SolrReplicationHandler.class);
    private static final String JDBC_DRIVER = "org.postgresql.Driver";

    /** Default 'action' for adding documents ... */
    static ModifiableSolrParams params = new ModifiableSolrParams();

    /** Flag that's set when the repository is loading ... */
    private boolean   isLoading = false;

    private String    isMaster = null;
    private String    isSlave  = null;
    private SolrCore  core     = null;


    /* (non-Javadoc)
     * @see org.apache.solr.handler.RequestHandlerBase#init(org.apache.solr.common.util.NamedList)
     */
    @Override
    public void init(NamedList args) {
        logger.info("In 'init' method ...");
        super.init(args);

        // Start the load if we have the "master" and "core", and it's NOT a slave
        isMaster = System.getProperty("solr.master", "false");
        isSlave  = System.getProperty("solr.slave", "false");
        if ("true".equalsIgnoreCase(isMaster)  &&  "false".equalsIgnoreCase(isSlave)  &&  core != null) {
            loadRepositoryFromDb();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.solr.handler.ReplicationHandler#inform(org.apache.solr.core.SolrCore)
     */
    @Override
    public void inform(SolrCore core) {
        logger.info("In 'inform' method ...");
        super.inform(core);

        // Start the load if we have the "master" and "core", and it's NOT a slave
        this.core = core;
        logger.info("IsMaster: " + isMaster);
        logger.info("IsSlave: " + isSlave);

        if ("true".equalsIgnoreCase(isMaster)  &&  "false".equalsIgnoreCase(isSlave)  &&  core != null) {
            loadRepositoryFromDb();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.solr.handler.ReplicationHandler#handleRequestBody(org.apache.solr.request.SolrQueryRequest, org.apache.solr.response.SolrQueryResponse)
     */
    @Override
    public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp)
            throws Exception {

        if (isLoading) {
            logger.info("Replication Request -- denied ... " + req.getParams());
            rsp.add(STATUS, ERR_STATUS);
            rsp.add("message", "Master repository is being loaded ...");
        } else {
            super.handleRequestBody(req, rsp);
        }
    }

    /**
     * Load the SOLR repository from the database.  The database connection parameters
     * will come from ... ???
     */
    private void loadRepositoryFromDb() {
        // Check to see if the repository is empty
        logger.info("Data-dir: " + core.getDataDir());
        logger.info("Index-dir: " + core.getIndexDir());
        File indexDir = new File(core.getIndexDir());
        if (indexDir.exists()  &&  indexDir.isDirectory()) {
            String[] files = indexDir.list();
            if (files != null  &&  files.length > 5) {
                logger.info("Documents already exist ... no population from DB will be done ...");
                return;
            }
        }
        logger.info("No documents ... time to load them from the DB");

        // Define the temporary directory where files are to be stored, and the
        // database connectivity parameter defaults
        String dbURL    = System.getProperty("JDBC_CONNECT_STRING", System.getProperty("db.url", ""));
        String username = System.getProperty("db.username", "sams_place");
        String password = System.getProperty("db.password", "sams_place");
        String tempDir  = System.getProperty("java.io.tmpdir");

        try {
            String sasId  = System.getProperty("SAS_ID");
            ObjectRequester requester = new ObjectRequester();
            Sas sas = requester.getSecureObject(sasId);
            if (sas == null) {
                logger.error("Unable to get SAS object by ID ...");
            } else {
                Map<String,Attribute> map = sas.getAttributes();
                username = map.get("fchUser").getSingleStringValue();
                password = map.get("fchPassword").getSingleStringValue();
                dbURL    = map.get("fchConnectUrl").getSingleStringValue();
            }
        } catch (IOException ex) {
            logger.error("Unable to get SAS object ...: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unable to get SAS object ...: " + ex.getMessage());
        }

        try {
            String sasKey = System.getProperty("SAS_KEY");
            ObjectRequester requester = new ObjectRequester();
            Sas sas = requester.getSecureObject(sasKey);
            if (sas == null) {
                logger.error("Unable to get SAS object by KEY ...");
            } else {
                Map<String,Attribute> map = sas.getAttributes();
                username = map.get("fchUser").getSingleStringValue();
                password = map.get("fchPassword").getSingleStringValue();
                dbURL    = map.get("fchConnectUrl").getSingleStringValue();
            }
        } catch (IOException ex) {
            logger.error("Unable to get SAS object ...: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unable to get SAS object ...: " + ex.getMessage());
        }

        logger.info("URL: " + dbURL);
        logger.info("User: " + username);
        logger.info("Password: " + "********"); // + password);

        // Ensure that the directory exists or can be created
        File file = new File(tempDir);
        if (file.exists()  &&  ! file.isDirectory()) {
            logger.error("Temporary directory exists but isn't a directory: " + tempDir);
            return;
        } else  if (! file.exists()) {
            if (! file.mkdirs()) {
                logger.error("Unable to create the temporary directory: " + tempDir);
                return;
            }
        }

        // Start loading the SOLR repository -- create "final" variables for the thread
        final String fDbURL = dbURL;
        final String fUsername = username;
        final String fPassword = password;
        final String fTempDir  = tempDir;

        Runnable populateTask = new Runnable() {
            @Override
            public void run() {
                logger.info("Sleep for a few seconds ...");
                try {
                    Thread.sleep(2500L);
                } catch(Exception ex) {
                    logger.info("Sorry, I just can't seem to get to sleep ...");
                }

                logger.info("Start the load process ...");
                isLoading = true;
                File tempDirFile = new File(fTempDir);
                Connection conn = getConnection(JDBC_DRIVER, fDbURL, fUsername, fPassword);
                if (conn != null) {
                    PlaceRepFileGenerator.generateFiles(conn, tempDirFile);
                    List<PlaceRepDoc> appDocs = new AppDataReader().getAppDocs(conn, "|");
                    for (PlaceRepDoc appDoc : appDocs) {
                        addDoc(appDoc, false);
                        logger.info("Loaded app-doc: " + appDoc.getId());
                    }

                    int cnt = 0;
                    PlaceRepReader repReader = new PlaceRepReader(tempDirFile);
                    for (Iterator<PlaceRepDoc> iter=repReader.iterator();  iter.hasNext(); ) {
                        PlaceRepDoc repDoc = iter.next();
                        cnt++;
                        boolean doCommit = (cnt % 10000 == 0);
                        addDoc(repDoc, doCommit);
                        if (doCommit) {
                            logger.info("Loaded " + cnt + " documents ...");
                        }
                    }

                    commitSolr();
                }

                isLoading = false;
            }
            
        };

        new Thread(populateTask).start();
    }

    /**
     * Create a database connection from explicit JDBC parameters
     * 
     * @param driver JDBC driver class name
     * @param url JDBC url
     * @param username username
     * @param password password
     * @return
     */
    private Connection getConnection(String driver, String url, String username, String password) {
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, username, password);
        } catch(ClassNotFoundException | SQLException ex) {
            logger.error("Unable to make database connection: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Add a PlaceRepDoc to the repository.  That will entail converting into something
     * that the default "updater" can understand, then calling the updater.
     * 
     * @param aDoc
     * @param doCommit perform a "commit" if TRUE
     */
    private void addDoc(PlaceRepDoc aDoc, boolean doCommit) {
        params.clear();
        if (doCommit) {
            params.set(UpdateParams.COMMIT, true);
            params.set(UpdateParams.SOFT_COMMIT, true);
        }

        SolrQueryRequest updateQueryReq = new LocalSolrQueryRequest(core, params);

        try {
            AddUpdateCommand addUpdateCmd = new AddUpdateCommand(updateQueryReq);
            addUpdateCmd.overwrite = false;
            addUpdateCmd.solrDoc = PlaceRepDocConverter.makeFromPlaceRepDoc(aDoc);  //new SolrInputDocument();
            core.getUpdateHandler().addDoc(addUpdateCmd);
        } catch (IOException ex) {
            logger.error("Unable to save place-rep doc '" + aDoc.getId() + "' -- " + ex.getMessage());
        } finally {
            updateQueryReq.close();
        }
    }

    /**
     * Perform a final commit to SOLR ... optimize and use a "soft" commit
     */
    private void commitSolr() {
        logger.info("Doing final commit ...");

        params.clear();
        params.set(UpdateParams.COMMIT, true);
        params.set(UpdateParams.OPEN_SEARCHER, true);
        params.set(UpdateParams.WAIT_SEARCHER, false);
        params.set(UpdateParams.SOFT_COMMIT, false);
        params.set(UpdateParams.OPTIMIZE, true);

        SolrQueryRequest commitQueryReq = new LocalSolrQueryRequest(core, params);

        try {
            CommitUpdateCommand commitUpdateCmd = new CommitUpdateCommand(commitQueryReq, false);
            commitUpdateCmd.openSearcher = true;
            commitUpdateCmd.waitSearcher = false;
            commitUpdateCmd.softCommit = false;
            commitUpdateCmd.optimize = true;
            core.getUpdateHandler().commit(commitUpdateCmd);
            logger.info("Commit done: " + commitUpdateCmd);
        } catch (IOException ex) {
            logger.error("Unable to commit changes -- " + ex.getMessage());
        } finally {
            commitQueryReq.close();
        }

        logger.info("Commit complete ...");
    }
}

