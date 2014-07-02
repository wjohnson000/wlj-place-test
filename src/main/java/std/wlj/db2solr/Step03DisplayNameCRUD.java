package std.wlj.db2solr;

import java.util.Map;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepresentationDTO;
import org.familysearch.standards.place.data.solr.SolrDataService;
import org.familysearch.standards.place.service.DbDataService;

import std.wlj.util.DbManager;
import std.wlj.util.SolrManager;


/**
 * Create, Update and Delete lots of stuff ...
 * 
 * @author wjohnson000
 *
 */
public class Step03DisplayNameCRUD {

    private static PlaceDataServiceImpl dataService;
    private static String wlj = "wjohnson000";

    public static void main(String... args) {
        DbDataService dbService = DbManager.getLocal();
        SolrDataService solrService = SolrManager.getLocalHttp();
        dataService = new PlaceDataServiceImpl(dbService, solrService);

        try {
            editPlaceRep(9, 1);
            editPlaceRep(9, 2);
            
        } catch(PlaceDataException ex) {
            System.out.println("EX: " + ex.getMessage());
            ex.printStackTrace();
        }

        dataService.shutdown();
        DbManager.closeAppContext();

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
        PlaceRepresentationDTO placeRep = dataService.getPlaceRepresentationById(repId, null);

        Double lattd = placeRep.getLatitude();
        Double longt = placeRep.getLongitude();
        if (lattd != null) lattd += 1;
        if (longt != null) longt -= 1;

        Map<String,String> names = placeRep.getDisplayNames();
        if (whatever == 1) {
            names.remove("en");
        } else {
            names.put("en", "Utah County Two");
        }

        PlaceRepresentationDTO updPlaceRep = new PlaceRepresentationDTO(
            placeRep.getJurisdictionChain(),
            placeRep.getOwnerId(),
            placeRep.getFromYear(),
            placeRep.getToYear(),
            placeRep.getType(),
            placeRep.getPreferredLocale(),
            names,
            lattd,
            longt,
            placeRep.isPublished(),
            placeRep.isValidated(),
            placeRep.getRevision(),
            placeRep.getUUID(),
            placeRep.getTypeGroup(),
            placeRep.getCreatedUsingVersion());

        dataService.update(updPlaceRep, wlj);
    }

}
