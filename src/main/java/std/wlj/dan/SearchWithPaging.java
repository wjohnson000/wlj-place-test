package std.wlj.dan;

import java.util.Arrays;

import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.PlaceSearchResults;
import org.familysearch.standards.place.data.SearchParameter;
import org.familysearch.standards.place.data.SearchParameters;
import org.familysearch.standards.place.data.solr.SolrQueryBuilder;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class SearchWithPaging {
    public static void main(String... args) throws PlaceDataException {
        SolrService  solrService = SolrManager.awsIntService(false);

        SearchParameters params = new SearchParameters();
        params.addParam(SearchParameter.RequiredDirectParentParam.createParam(356));
        params.addParam(SearchParameter.FilterDeleteParam.createParam(true));
        params.addParam(SearchParameter.MetricsParam.createParam(true));
        params.addParam(SearchParameter.PublishedParam.createParam(true));
//        params.addParam(SearchParameter.PageNumberParam.createParam(2));
//        params.addParam(SearchParameter.PageSizeParam.createParam(4));

        SolrQueryBuilder builder = solrService.buildQuery(params);
        System.out.println("Query: " + builder.getQuery());

        PlaceSearchResults results = solrService.search(params);
        System.out.println("PlaceReps.ret-count: " + results.getReturnedCount());
        System.out.println("PlaceReps.fnd-count: " + results.getResultsFoundCount());
        for (PlaceRepBridge repB : results.getResults()) {
            System.out.println("PR: " + repB.getRepId() + " . " + repB.getDefaultLocale() + " . " + repB.getAllDisplayNames().get(repB.getDefaultLocale()));
            System.out.println(" J: " + Arrays.toString(repB.getJurisdictionIdentifiers()));
            System.out.println(" D: " + repB.isDeleted() + " --> " + repB.getRevision());
        }

        System.exit(0);
    }
}
