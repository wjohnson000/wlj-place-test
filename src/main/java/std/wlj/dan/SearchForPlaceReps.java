package std.wlj.dan;

import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.PlaceSearchResults;
import org.familysearch.standards.place.data.SearchParameter;
import org.familysearch.standards.place.data.SearchParameters;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class SearchForPlaceReps {
    public static void main(String... args) throws PlaceDataException {
        SolrService  solrService = SolrManager.awsProdService(false);

        SearchParameters params = new SearchParameters();
        params.addParam( SearchParameter.PlaceParam.createParam( 1941217 ) );
        params.addParam( SearchParameter.FilterDeleteParam.createParam( true ) );

        PlaceSearchResults results = solrService.search(params);
        System.out.println("PlaceReps: " + results.getReturnedCount());
        for (PlaceRepBridge repB : results.getResults()) {
            System.out.println("PR: " + repB.getRepId() + " . " + repB.getDefaultLocale() + " . " + repB.getAllDisplayNames().get(repB.getDefaultLocale()));
            System.out.println(" D: " + repB.isDeleted() + " --> " + repB.getRevision());
        }

        System.exit(0);
    }
}
