package std.wlj.solr;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceSearchResults;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.interp.TokenLookupCache;
import org.familysearch.standards.place.search.parser.PlaceNameToken;

import std.wlj.util.SolrManager;

public class TypeAhead {

    public static void main(String...args) throws PlaceDataException {
        PlaceNameToken token   = new PlaceNameToken("Canada", StdLocale.ENGLISH);
        PlaceRequest   request = new PlaceRequestBuilder()
                .setResultsLimit(100)
                .setPartialInput(true)
                .getRequest();
        PlaceResults   results = new PlaceResults();

        SolrService solrSvc = SolrManager.awsBetaService(true);
        TokenLookupCache cache = new TokenLookupCache(solrSvc);
        PlaceSearchResults psResults = cache.lookupTypeRestrictedToken(token, request, results);
        System.out.println("RES: " + psResults);
        psResults.getResults().forEach(System.out::println);

        solrSvc.shutdown();
    }
}
