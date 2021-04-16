package std.wlj.solr.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.core.CoreContainer;

import org.familysearch.standards.core.logging.Logger;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.PlaceRepDocFactory;
import org.familysearch.standards.place.exceptions.PlaceDataException;

/**
 * Instances of this class wrap connections to Solr.
 * 
 * @author dshellman
 */
public class SolrConnectionX {
    private static final Logger  logger = new Logger(SolrConnectionX.class);

    public static final String   MODULE_NAME = "solr";

    private SolrClient  solrClient;
    private String      connectLocation;


    public SolrConnectionX(SolrClient conn) {
        solrClient = conn;
    }

    public SolrConnectionX(SolrClient conn, String theLocation) {
        solrClient = conn;
        connectLocation = theLocation;
    }

    /**
     * Returns true if this instance is connected to Solr.
     * 
     * @return Returns true if connected.
     */
    public boolean isConnected() {
        return solrClient != null;
    }

    /**
     * Retrieve the number of documents managed by SOLR, or zero (0) if unable to
     * complete the request.
     * 
     * @return number of documents
     */
    public int getDocCount() {
        try {
            SolrQuery q = new SolrQuery("*:*").setRows(0);
            return (int) solrClient.query(q).getResults().getNumFound();
        } catch (Exception ex) {
            logger.debug("Unable to get doc count: " + ex.getMessage());
            return 0;
        }
    }

    public List<PlaceRepDoc> search(SolrQuery query) throws PlaceDataException {
        QueryResponse      response = null;
        List<PlaceRepDoc>  docs;

        try {
            response = solrClient.query(query);
        } catch (Exception e) {
            logger.error(null, MODULE_NAME, "Error performing solr search.", "query", query.toString());
            return new ArrayList<>();
        }

        docs = response.getBeans(PlaceRepDoc.class);
        if (docs == null) {
            docs = new ArrayList<PlaceRepDoc>();
        }

        return docs;
    }

    public List<int[]> searchLite(SolrQuery query) throws PlaceDataException {
        QueryResponse response = null;

        query.setFields("repId", "revision");
        try {
            response = solrClient.query(query);
        } catch (Exception e) {
            logger.error(null, MODULE_NAME, "Error performing solr search.", "query", query.toString());
            return new ArrayList<>();
        }

        return response.getResults().stream()
             .map(doc -> new int[] {
                 (Integer)doc.getFieldValue("repId"),
                 (Integer)doc.getFieldValue("revision")
             })
             .collect(Collectors.toList());
    }

    /**
     * Retrieve a document by its identifier ... it's a little faster to do a straight
     * "GET" rather than a "SELECT" if you know the identifier.
     * 
     * @param docId document identifier
     * @return associated document
     */
    public PlaceRepDoc getById(String docId) {
        // Set up a blank query, use the "get" handler, and remove the "q" parameter
        // before setting the document id parameter.
        SolrQuery query = new SolrQuery();
        query.set(CommonParams.QT, "/get");
        query.remove("q");
        query.add("id", docId);

        try {
            QueryResponse response = solrClient.query(query);
            SolrDocument sDoc = (SolrDocument)response.getResponse().get("doc");
            if (sDoc != null) {
                return PlaceRepDocFactory.createFrom(sDoc);
            }
        } catch (SolrServerException | IOException e) {
            logger.warn(e, MODULE_NAME, "Unable to get item", "docId", docId);
        }

        return null;
    }

    public List<Integer> getDeletedRepIds(Date lastUpdateDate) throws PlaceDataException {
        List<Integer> repIds = null;
        DateFormat solrDateFmt = new SimpleDateFormat("yyyy-MM-dd'T00:00:00Z'"); 

        String queryStr = "repId:[1 TO *]";
        if (lastUpdateDate != null) {
            queryStr += " AND lastUpdateDate:[" + solrDateFmt.format(lastUpdateDate) + " TO *]";
        }

        SolrQuery solrQuery = new SolrQuery(queryStr);
        solrQuery.addFilterQuery("deleteId:[* TO *]");
        solrQuery.setFields("repId");
        solrQuery.setRows(1_000_000);

        try {
            QueryResponse response = solrClient.query(solrQuery);
            SolrDocumentList docList = response.getResults();
            repIds = docList.stream()
                    .map(doc -> (Integer)doc.getFieldValue("repId"))
                    .collect(Collectors.toList());
        } catch (SolrServerException | IOException e) {
            logger.error(e, MODULE_NAME, "Error retrieving deleted rep-ids.");
            throw new PlaceDataException("Error performing deleted rep retrieval", e);
        }

        return repIds;
    }

    /**
     * Shuts down the connection.  This should be called to release the connection/resources.
     */
    public void shutdown() {
        try {
            solrClient.close();
        } catch (IOException e) {
            logger.error(e, MODULE_NAME, "Unable to close Solr client.");
        }
        solrClient = null;
    }

    @Override
    public String toString() {
        if (solrClient != null) {
            return connectLocation;
        }

        return "Solr Connection: not connected.";
    }
    
    public boolean isConnectionActive() {
        if (solrClient != null) {
            try {
                search(new SolrQuery("*:*").setRows(0));
                return true;
            }
            catch (Exception e) {
                logger.error(e, MODULE_NAME, "Active connection failed attempt to query Solr.  Connection may be invalid or Solr may be down.", "con", toString());
            }
        }
        
        return false;
    }

    public static SolrConnectionX connectToEmbeddedInstance(String location) throws PlaceDataException {
        // Support the new location [down at the actual directory we want] and the old
        // location [.../solr-places/solr/<data>]
        File  newStyleDir = new File(location);
        File  oldStyleDir = new File(location + File.separator + "solr-places" + File.separator + "solr");

        CoreContainer container = null;
        if (oldStyleDir.exists()) {
//            logger.info(null, MODULE_NAME, "Solr:  Using container location of " + oldStyleDir.getAbsolutePath());
//            container = CoreContainer.createAndLoad(Paths.get(oldStyleDir.getAbsolutePath()));
            Path oldDir = Paths.get(oldStyleDir.getAbsolutePath());
            logger.info( null, MODULE_NAME, "Solr:  Using container location of " + oldDir);
            container = CoreContainer.createAndLoad(oldDir);
        } else if (newStyleDir.exists()) {
//            logger.info(null, MODULE_NAME, "Solr:  Using container location of " + newStyleDir.getAbsolutePath());
//            container = CoreContainer.createAndLoad(Paths.get(newStyleDir.getAbsolutePath()));
            Path newDir = Paths.get(newStyleDir.getAbsolutePath());
            logger.info(null, MODULE_NAME, "Solr:  Using container location of " + newDir);
            container = CoreContainer.createAndLoad(newDir);
        } else {
            throw new PlaceDataException("Unable to find SOLR at the given location: " + location);
        }

        // Load the container and create the connection if we have a good location
        container.load();
        SolrConnectionX conn = new SolrConnectionX(new EmbeddedSolrServer(container, "places"), location);
        return (conn.isConnectionActive()) ? conn : null;
    }

    public static SolrConnectionX connectToRemoteInstance(String location) throws PlaceDataException {
        SolrConnectionX conn = new SolrConnectionX(new HttpSolrClient.Builder(location).build(), location);
        return (conn.isConnectionActive()) ? conn : null;
    }
}