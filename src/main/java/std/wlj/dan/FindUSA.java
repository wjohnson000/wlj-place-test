package std.wlj.dan;

import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.PlaceSearchResults;
import org.familysearch.standards.place.data.SearchParameter;
import org.familysearch.standards.place.data.SearchParameters;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.solr.SolrService;


public class FindUSA {
    public static void main(String... args) throws PlaceDataException {
        SolrService solrService = new SolrService();

        System.out.println("Place-Type count: " + solrService.getTypes(TypeBridge.TYPE.PLACE).size());
        System.out.println("Name-Type count: " + solrService.getTypes(TypeBridge.TYPE.NAME).size());

        SearchParameters params = new SearchParameters();
        params.addParam(SearchParameter.PlaceRepParam.createParam(1));
        PlaceSearchResults results = solrService.search(params);
        System.out.println("PlaceReps: " + results.getReturnedCount());
        for (PlaceRepBridge repB : results.getResults()) {
            System.out.println("PR: " + repB.getRepId() + " . " + repB.getDefaultLocale() + " . " + repB.getAllDisplayNames().get(repB.getDefaultLocale()));
        }

        System.exit(0);
    }
}
