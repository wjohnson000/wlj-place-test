package org.familysearch.std.wlj.access;

import java.util.*;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.*;
import org.familysearch.standards.place.data.solr.SolrDataService;
import org.familysearch.standards.place.service.DbDataService;


public class ManageCitations {
    public static void main(String[] args) throws PlaceDataException {
        SolrDataService solrService = new SolrDataService();
        DbDataService dbService = new DbDataService();

        PlaceDataServiceImpl service = new PlaceDataServiceImpl(dbService, solrService);

        // sourceId=2  --> official website
        // typeId=1053 --> name
        CitationDTO newCit01 = new CitationDTO(0, 2, 9931104, 1053, new Date(), "test-descr-xxx", "http://nowhere.com", 0);
        CitationDTO creCit01 = service.create(newCit01, "wjohnson000");
        System.out.println("CreCit: " + creCit01.getId() + " ... " + creCit01);

        CitationDTO updCit02 = new CitationDTO(creCit01.getId(), 2, 9931104, 1053, new Date(), "test-descr-xxx-upd", "http://nowhere.com", 0);
        CitationDTO creCit02 = service.update(updCit02, "wjohnson000");
        System.out.println("CreCit: " + creCit02.getId() + " ... " + creCit02.getRevision() + " ... " + creCit02);

        CitationDTO updCit03 = new CitationDTO(creCit01.getId(), 2, 9931104, 1053, new Date(), "test-descr-xxx-del", "http://nowhere.com", 0);
        CitationDTO delCit03 = service.delete(updCit03, "wjohnson000");
        System.out.println("CreCit: " + delCit03.getId() + " ... " + delCit03.getRevision() + " ... " + delCit03);

        service.shutdown();
    }
}
