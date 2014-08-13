package std.wlj.db2solr;

import java.util.List;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.PlaceDTO;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceNameDTO;
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
public class Step99CreateDuplicateName {

    private static PlaceDataServiceImpl dataService;
    private static String wlj = "wjohnson000";

    public static void main(String... args) {
        DbDataService dbService = DbManager.getLocal();
        SolrDataService solrService = SolrManager.getLocalEmbedded("C:/tools/solr/data/tokoro");
        dataService = new PlaceDataServiceImpl(dbService, solrService);

        try {
            editProvoPlace();
        } catch(PlaceDataException ex) {
            System.out.println("EX: " + ex.getMessage());
            ex.printStackTrace();
        }

        dataService.shutdown();
        DbManager.closeAppContext();

        System.exit(0);
    }

    /**
     * Modify "Provo" place: change the start-year just a tad, and add a duplicate name
     * 
     * @throws PlaceDataException
     */
    private static void editProvoPlace() throws PlaceDataException {
     // Get the current place, modify the start-data, save it
        PlaceDTO aPlace = dataService.getPlaceById(8, null);
        List<PlaceNameDTO> names = aPlace.getVariants();
        PlaceNameDTO dupName01 = new PlaceNameDTO(0, "provo", "en", 445);
        PlaceNameDTO dupName02 = new PlaceNameDTO(0, "Provo", "en", 445);
        names.add(dupName01);
        names.add(dupName02);
        PlaceDTO updPlace = new PlaceDTO(aPlace.getId(), names, 1850, aPlace.getEndYear(), 0, null);
        dataService.update(updPlace, wlj);
    }
}
