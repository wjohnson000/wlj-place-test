package std.wlj.jira;

import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.PlaceSearchResults;
import org.familysearch.standards.place.data.SearchParameter;
import org.familysearch.standards.place.data.SearchParameters;
//import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.solr.SolrService;

public class STD2661 {
    public static void main(String... args) throws PlaceDataException {
//        System.setProperty("solr.master.url", "C:/tools/solr/data/tokoro");
//        System.setProperty("solr.solr.home", "C:/tools/solr/data/tokoro");
        System.setProperty("solr.master.url", "http://localhost:8983/solr/places");
        System.setProperty("solr.solr.home", "http://localhost:8983/solr/places");
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        SolrService solrService = new SolrService();

        SearchParameters params = new SearchParameters();
        params.addParam(SearchParameter.PlaceRepParam.createParam(3688));
        PlaceSearchResults results = solrService.search(params);

        System.out.println("PlaceReps: " + results.getReturnedCount());
        for (PlaceRepBridge repB : results.getResults()) {
            System.out.println("PR: " + repB.getRepId() + " . " + repB.getDefaultLocale() + " . " + repB.getAllDisplayNames().get(repB.getDefaultLocale()));
            for (PlaceRepBridge kid : repB.getChildren()) {
                System.out.println("KK: " + kid.getRepId() + " . " + kid.getDefaultLocale() + " . " + kid.getAllDisplayNames().get(repB.getDefaultLocale()));
            }
        }

        System.exit(0);
    }

}
