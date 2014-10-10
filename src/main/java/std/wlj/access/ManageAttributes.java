package std.wlj.access;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.*;
import org.familysearch.standards.place.data.solr.SolrDataService;
import org.familysearch.standards.place.service.DbReadableService;


public class ManageAttributes {
    public static void main(String[] args) throws PlaceDataException {
        SolrDataService solrService = new SolrDataService();
        DbReadableService dbService = new DbReadableService();

        PlaceDataServiceImpl service = new PlaceDataServiceImpl(dbService, solrService);

        // sourceId=2  --> official website
        // typeId=1053 --> name
        AttributeDTO newAttr01 = new AttributeDTO(0, 9931104, 1019, 2010, "attr-value", "en", 0);
        AttributeDTO creAttr01 = service.create(newAttr01, "wjohnson000");
        System.out.println("CreAttr: " + creAttr01.getId() + " ... " + creAttr01);

        AttributeDTO updAttr02 = new AttributeDTO(creAttr01.getId(), 9931104, 1019, 2012, "attr-value-new", "en", 0);
        AttributeDTO creAttr02 = service.update(updAttr02, "wjohnson000");
        System.out.println("CreAttr: " + creAttr02.getId() + " ... " + creAttr02.getRevision() + " ... " + creAttr02);

        AttributeDTO updAttr03 = new AttributeDTO(creAttr01.getId(), 9931104, 1019, 2013, "attr-value-del", "en", 0);
        AttributeDTO delAttr03 = service.delete(updAttr03, "wjohnson000");
        System.out.println("CreAttr: " + delAttr03.getId() + " ... " + delAttr03.getRevision() + " ... " + delAttr03);

        service.shutdown();
    }
}
