package std.wlj.jira;

import java.util.Arrays;
import java.util.Map;

import org.familysearch.standards.place.data.AltJurisdictionImpl;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class STD6236 {

    public static void main(String... args) throws PlaceDataException {
//        SolrService solrService = SolrManager.localEmbeddedService();
        SolrService solrService = SolrManager.awsDevService(true);

//        PlaceRepDoc doc01 = solrService.findPlaceRep(344513);
//        System.out.println("Doc1: " + doc01 + " --> " + doc01.isDeleted() + " --> " + doc01.getDeleteId());
//
//        PlaceRepDoc doc02 = solrService.findPlaceRepNoCache(344513);
//        if (doc02 == null) {
//            System.out.println("Doc2: " + doc02);
//        } else {
//            System.out.println("Doc2: " + doc02 + " --> " + doc02.isDeleted() + " --> " + doc02.getDeleteId());
//        }
//
//        Map<Integer, PlaceRepDoc> docs = solrService.findPlaceReps(Arrays.asList(344513));
//        System.out.println("Docs: " + docs);

        PlaceRepDoc doc = solrService.findPlaceRep(395258);
        AltJurisdictionImpl aji = new AltJurisdictionImpl(3581, 532, 344513, doc, solrService);
        System.out.println("AJI: " + aji);
        System.out.println("  rev: " + aji.getRevision());
        System.out.println("  rep: " + aji.getPlaceRep());
        System.out.println("  rel: " + aji.getRelatedPlaceRepId());
        System.out.println("  rel: " + aji.getRelatedPlaceRep());
        
        System.exit(0);
    }
}
