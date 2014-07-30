package std.wlj.solr.handler.save;

import java.io.IOException;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.apache.solr.core.SolrCore;
import org.apache.solr.update.AddUpdateCommand;
import org.apache.solr.update.DirectUpdateHandler2;
import org.apache.solr.update.UpdateHandler;
import org.familysearch.standards.place.service.DbDataService;


/**
 * Custom SOLR handler for updating.  Intercept all update [add] requests and
 * do the following:
 * <br/> -- minor validation
 * <br/> -- call the DB service as appropriate
 * <br/> -- call "super.add(...)" to put document in SOLR
 * 
 * @author wjohnson000
 *
 */
public class SolrUpdateHandler extends DirectUpdateHandler2 {

    /**
     * Override constructor ...
     * 
     * @param core {@link SolrCore} instance
     */
    public SolrUpdateHandler(SolrCore core) {
        super(core);
    }

    /**
     * Override constructor ...
     * 
     * @param core {@link SolrCore} instance
     * @param updateHandler {@link UpdateHandler} instance
     */
    public SolrUpdateHandler(SolrCore core, UpdateHandler updateHandler) {
        super(core, updateHandler);
    }

    /* (non-Javadoc)
     * @see org.apache.solr.update.DirectUpdateHandler2#addDoc(org.apache.solr.update.AddUpdateCommand)
     */
    @Override
    public int addDoc(AddUpdateCommand cmd) throws IOException {
        // Look for any Db-Hints, which indicate what we are doing
        SolrInputDocument doc = cmd.getSolrInputDocument();
        SolrInputField    dbHints = doc.getField("dbHints");
        if (dbHints != null  &&  dbHints.getValueCount() > 0) {
            doDbOperation(cmd, dbHints);
            doc.removeField("dbHints");
        }
        return super.addDoc(cmd);
    }

    /**
     * Return the "DbDataService, or wire up a new one.
     */
    private synchronized DbDataService getDbService() {
        return null;
    }
    
    /**
     * Perform any requisite DB operations based on the given data ... it's possible
     * for there to be more than one DbHint on a request.  Many will have none ...
     * 
     * @param cmd AddUpdateCommand, containing the document fields
     * @param dbHints db-hints, indicating what operation[s] should be performed
     */
    private void doDbOperation(AddUpdateCommand cmd, SolrInputField dbHints) {
        for (Object dbHint : dbHints.getValues()) {
            switch(String.valueOf(dbHint)) {
                case "Attribute.create":
                    handleAttributeCreate(cmd); break;
                case "Attribute.delete":
                    handleAttributeDelete(cmd); break;
                case "Attribute.update":
                    handleAttributeUpdate(cmd); break;
                case "Citation.create":
                    handleCitationCreate(cmd); break;
                case "Citation.delete":
                    handleCitationDelete(cmd); break;
                case "Citation.update":
                    handleCitationUpdate(cmd); break;
                case "ExtXref.create":
                    handleExtXrefCreate(cmd); break;
                case "Group.create":
                    handleGroupCreate(cmd); break;
                case "Group.update":
                    handleGroupUpdate(cmd); break;
                case "Place.create":
                    handleTypeCreate(cmd); break;
                case "Place.update":
                    handlePlaceUpdate(cmd); break;
                case "PlaceRep.create":
                    handlePlaceRepCreate(cmd); break;
                case "PlaceRep.delete":
                    handlePlaceRepDelete(cmd); break;
                case "PlaceRep.update":
                    handlePlaceRepUpdate(cmd); break;
                case "Source.create":
                    handleSourceCreate(cmd); break;
                case "Source.update":
                    handleSourceUpdate(cmd); break;
                case "Type.create":
                    handleTypeCreate(cmd); break;
                case "Type.update":
                    handleTypeUpdate(cmd); break;
            }
        }
    }

    private void handleAttributeCreate(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handleAttributeDelete(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handleAttributeUpdate(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handleCitationCreate(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handleCitationDelete(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handleCitationUpdate(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handleExtXrefCreate(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handleGroupCreate(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handleGroupUpdate(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handlePlaceUpdate(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handlePlaceRepCreate(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handlePlaceRepDelete(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handlePlaceRepUpdate(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handleSourceCreate(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handleSourceUpdate(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handleTypeCreate(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }

    private void handleTypeUpdate(AddUpdateCommand cmd) {
        // TODO Auto-generated method stub
        
    }
}
