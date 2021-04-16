package std.wlj.solr;

import org.familysearch.standards.place.data.GroupBridge;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;


public class SimpleTestAppData {
    public static void main(String... args) throws PlaceDataException {
        SolrService solrService = SolrService.getInstance();

        System.out.println("Place-Type count: " + solrService.getTypes(TypeBridge.TYPE.PLACE).size());
        System.out.println("Name-Type count: " + solrService.getTypes(TypeBridge.TYPE.NAME).size());
        System.out.println("Place-Type-Group count: " + solrService.getGroups(GroupBridge.TYPE.PLACE_REP).size());

        System.exit(0);
    }
}
