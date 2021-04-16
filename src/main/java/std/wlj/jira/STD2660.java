package std.wlj.jira;


import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.PlaceSearchResults;
import org.familysearch.standards.place.data.SearchParameter;
import org.familysearch.standards.place.data.SearchParameters;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class STD2660 {

    public static void main(String... args) throws PlaceDataException {
        SolrService  solrService = SolrManager.awsProdService(true);
        SearchParameters params = new SearchParameters();
        params.addParam(SearchParameter.RequiredParentParam.createParam(351));
        params.addParam(SearchParameter.RequiredTypeParam.createParam(186));
        params.addParam(SearchParameter.RequiredTypeParam.createParam(376));
        PlaceSearchResults results = solrService.search(params);

        System.out.println("PlaceReps: " + results.getReturnedCount());
        for (PlaceRepBridge repB : results.getResults()) {
            System.out.println("PR: " + repB.getRepId() + " . " + repB.getDefaultLocale() + " . " + repB.getAllDisplayNames().get(repB.getDefaultLocale()));
        }

        System.exit(0);
    }
}
