package std.wlj.solr;

import java.util.Arrays;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.PlaceType;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;

import std.wlj.util.SolrManager;


public class SearchByTypes {

    public static void main(String... args) throws PlaceDataException {
        SolrService  solrService = SolrManager.localEmbeddedService();
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        PlaceRepDoc prBridge = new PlaceRepDoc();
        prBridge.setId("351");
        prBridge.setRepId(351);
        PlaceRepresentation placeRep = new PlaceRepresentation(prBridge);

        int[][] typess = { { 376, 186 }, { 376 }, { 186 }, { 376, 186 }, { 376 }, { 186 } };

        StringBuilder buff = new StringBuilder();
        for (int[] types : typess) {
            PlaceRequestBuilder builder;
            builder = new PlaceRequestBuilder();
            builder.addRequiredParent(placeRep);
            for (int type : types) {
                builder.addRequiredPlaceType(new PlaceType(solrService.getTypeById(TypeBridge.TYPE.PLACE, type, false)));
            }
            PlaceResults results = placeService.requestPlaces(builder.getRequest());

            buff.append("\n\n");
            buff.append("------------------------------------------------------------------------------------------------\n");
            buff.append(">>> Search for: " + Arrays.toString(types)).append("\n");
            buff.append(">>> Result cnt: " + results.getPlaceRepresentations().length).append("\n");

            for (PlaceRepresentation resultModel : results.getPlaceRepresentations()) {
                buff.append("    -- ").append(resultModel.getFullDisplayName(StdLocale.ENGLISH).get()).append(" --> ").append(resultModel.getType().getCode()).append("\n");
            }
        }

        solrService.shutdown();
        System.out.println(buff.toString());
        System.exit(0);
    }
}
