package std.wlj.solr;

import org.familysearch.standards.place.data.GroupBridge;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.solr.SolrService;


public class SimpleTestAppData {
    public static void main(String... args) throws PlaceDataException {
        SolrService solrService = SolrService.getInstance();

        System.out.println("Place-Type count: " + solrService.getTypes(TypeBridge.TYPE.PLACE, true).size());
        System.out.println("Name-Type count: " + solrService.getTypes(TypeBridge.TYPE.NAME, true).size());
        System.out.println("Place-Type-Group count: " + solrService.getGroups(GroupBridge.TYPE.PLACE_REP, true).size());

        System.exit(0);
    }
}
