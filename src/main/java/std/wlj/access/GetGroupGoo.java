package std.wlj.access;

import java.util.*;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.GroupDTO;
import org.familysearch.standards.place.data.GroupType;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrDataService;
import org.familysearch.standards.place.service.DbReadableService;


public class GetGroupGoo {
    public static void main(String[] args) throws PlaceDataException {
        SolrDataService solrService = new SolrDataService();
        DbReadableService dbService = new DbReadableService();

        PlaceDataServiceImpl service = new PlaceDataServiceImpl(dbService, solrService);

        Set<GroupDTO> groups = service.getAllGroups(GroupType.PLACE_TYPE);
        System.out.println("GPS: " + groups.size());

        service.shutdown();
    }
}
