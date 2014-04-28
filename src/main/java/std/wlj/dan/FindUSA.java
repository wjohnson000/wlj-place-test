package std.wlj.dan;

import java.util.List;

import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepresentationDTO;
import org.familysearch.standards.place.data.TypeCategory;
import org.familysearch.standards.place.data.solr.SolrDataService;


public class FindUSA {
    public static void main(String... args) throws PlaceDataException {
        SolrDataService solrService = new SolrDataService();

        System.out.println("Place-Type count: " + solrService.getAllTypes(TypeCategory.PLACE).size());
        System.out.println("Name-Type count: " + solrService.getAllTypes(TypeCategory.NAME).size());

        List<PlaceRepresentationDTO> placeReps = solrService.getPlaceRepresentationsByPlaceId(1, null, true);
        System.out.println("PlaceReps: " + placeReps);
        for (PlaceRepresentationDTO placeRep : placeReps) {
            System.out.println("PR: " + placeRep.getId() + " . " + placeRep.getPreferredLocale() + " . " + placeRep.getDisplayName(placeRep.getPreferredLocale()));
        }

        System.exit(0);
    }
}
