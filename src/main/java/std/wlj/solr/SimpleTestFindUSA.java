package std.wlj.solr;

import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.PlaceSearchResults;
import org.familysearch.standards.place.data.SearchParameter;
import org.familysearch.standards.place.data.SearchParameters;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.solr.SolrService;


public class SimpleTestFindUSA {
    public static void main(String... args) throws PlaceDataException {
        SolrService solrService = SolrService.getInstance();
        SearchParameters params = new SearchParameters();
        PlaceSearchResults results;

        System.out.println("Place-Type count: " + solrService.getTypes(TypeBridge.TYPE.PLACE).size());
        System.out.println("Name-Type count: " + solrService.getTypes(TypeBridge.TYPE.NAME).size());

        params.addParam( SearchParameter.PlaceParam.createParam( 1 ) );
        results = solrService.search( params );
        for ( PlaceRepBridge placeRep : results.getResults() ) {
            System.out.println("PR: " + placeRep.getRepId() + " . " + placeRep.getDefaultLocale() + " . " + placeRep.getAllDisplayNames().get( placeRep.getDefaultLocale() ) );
        }

        System.exit(0);
    }
}
