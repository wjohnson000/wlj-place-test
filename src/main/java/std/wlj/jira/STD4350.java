package std.wlj.jira;

import org.familysearch.standards.core.Localized;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.PlaceSearchResults;
import org.familysearch.standards.place.data.SearchParameter;
import org.familysearch.standards.place.data.SearchParameters;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;

public class STD4350 {
    public static void main(String... args) throws PlaceDataException {
        SolrService  solrService = SolrManager.awsBetaService(false);

        SearchParameters params = new SearchParameters();
        params.addParam(SearchParameter.PlaceRepParam.createParam(7344509));
        PlaceSearchResults results = solrService.search(params);

        System.out.println("PlaceReps: " + results.getReturnedCount());
        for (PlaceRepBridge repB : results.getResults()) {
            System.out.println("PR: " + repB.getRepId() + " . " + repB.getDefaultLocale() + " . " + repB.getAllDisplayNames().get(repB.getDefaultLocale()));
            PlaceRepresentation placeRep = new PlaceRepresentation(repB);
            Localized<String> enName = placeRep.getFullDisplayName(new StdLocale("en"));
            Localized<String> afName = placeRep.getFullDisplayName(new StdLocale("af"));
            System.out.println("  en FN: " + enName.get() + " --> " + enName.getLocale());
            System.out.println("  af FN: " + afName.get() + " --> " + afName.getLocale());
        }

        System.exit(0);
    }

}
