package std.wlj.access;

import java.util.*;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.AttributeDTO;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrDataService;
import org.familysearch.standards.place.service.DbDataService;


public class CreateAttribute {
    public static void main(String[] args) throws PlaceDataException {
        SolrDataService solrService = new SolrDataService();
        DbDataService dbService = new DbDataService();

        PlaceDataServiceImpl service = new PlaceDataServiceImpl(dbService, solrService);

        Set<AttributeDTO> attrs = service.getAttributesByRepId(395081, null);
        for (AttributeDTO attr : attrs) {
            System.out.println(attr.getId() + " . " + attr.getRepId() + " .  " + attr.getTypeId() + " . " + attr.getYear() + " . " + attr.getValue());
        }

        AttributeDTO aDTO = new AttributeDTO(0, 395081, 423, 1820, "3853", 0);
        AttributeDTO aaDTO = service.create(aDTO, "wjohnson000");
        System.out.println(aaDTO.getId() + " . " + aaDTO.getRepId() + " .  " + aaDTO.getTypeId() + " . " + aaDTO.getYear() + " . " + aaDTO.getValue());

        service.shutdown();
    }
}
