package std.wlj.db2solr;

import java.util.Map;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.service.DbReadableService;

import std.wlj.util.DbConnectionManager;
import std.wlj.util.SolrManager;
import std.wlj.util.DbConnectionManager.DbServices;


/**
 * Create, Update and Delete lots of stuff ...
 * 
 * @author wjohnson000
 *
 */
public class Step03DisplayNameCRUD {

    private static DbReadableService readService;
    private static PlaceDataServiceImpl dataService;
    private static String wlj = "wjohnson000";

    public static void main(String... args) {
        DbServices dbServices = DbConnectionManager.getDbServicesSams();
        SolrService solrService = SolrManager.localHttpService();
        readService = dbServices.readService;
        dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

        try {
            editPlaceRep(9, 1);
            editPlaceRep(9, 2);
            
        } catch(PlaceDataException ex) {
            System.out.println("EX: " + ex.getMessage());
            ex.printStackTrace();
        }

        dataService.shutdown();
        dbServices.shutdown();

        System.exit(0);
    }

    /**
     * Edit a place-rep
     * 
     * @param repId place-rep identifier
     * @param whatever whatever
     * @throws PlaceDataException if something bad happens
     */
    private static void editPlaceRep(int repId, int whatever) throws PlaceDataException {
        PlaceRepBridge repB = readService.getRep(repId);

        Double lattd = repB.getLatitude();
        Double longt = repB.getLongitude();
        if (lattd != null) lattd += 1;
        if (longt != null) longt -= 1;

        Map<String,String> dispNames = repB.getAllDisplayNames();
        if (whatever == 1) {
            dispNames.remove("en");
        } else {
            dispNames.put("en", "Utah County Two");
        }

        int[] jurisIds = repB.getJurisdictionIdentifiers();
        int parentId = (jurisIds == null  ||  jurisIds.length < 2) ? -1 : jurisIds[1];
        Integer groupId = (repB.getChildConstraintTypeGroup() == null) ? null : repB.getChildConstraintTypeGroup().getGroupId();

        dataService.updateRep(
            repId,
            repB.getPlaceId(),
            parentId,
            repB.getJurisdictionFromYear(),
            repB.getJurisdictionToYear(),
            repB.getPlaceType().getTypeId(),
            repB.getDefaultLocale(),
            dispNames,
            lattd,
            longt,
            repB.isPublished(),
            repB.isValidated(),
            groupId,
            null,
            wlj,
            null);
    }

}
