package std.wlj.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceResults.Annotation;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;
import org.familysearch.standards.place.search.interp.Interpretation;
import org.familysearch.standards.place.search.interp.ParsePath;

public class TestFindProvoUtahUsaInterpret {
    public static void main(String... args) throws PlaceDataException {
//      SolrService solrService = SolrManager.localEmbeddedService();
      SolrService solrService = SolrManager.awsBetaService(false);
      PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
      PlaceService placeService = new PlaceService(profile);

      PlaceResults results = placeService.interpretPlaceName("St George, Utah, USA", null, "Utah", "TOWN", null, null);
 
      whatData(results);
      System.exit(0);
  }

    @SuppressWarnings("unchecked")
    static void whatData(PlaceResults results) {
        System.out.println("============================================================================================");
        List<ParsePath> ppaths  = (List<ParsePath>)results.getAttr(PlaceResults.Attribute.PARSE_PATHS);
        PlaceRequest    request = (PlaceRequest)results.getAttr(PlaceResults.Attribute.REQUEST);

        // Input parameters ... interesting !?!
        System.out.println(">>> INPUT <<<");
        System.out.println(" TEXT: " + request.getText().get());
        System.out.println(" LCLE: " + request.getText().getLocale());
        System.out.println(" PARS: " + request.getOptionalParents());
        System.out.println(" DATE: " + request.getOptionalDate());
        System.out.println(" TYPS: " + request.getOptionalPlaceTypes());
        System.out.println(" GRPS: " + request.getOptionalPlaceTypeGroups());
        System.out.println(" CENT: " + request.getCentroid());

        // Parse paths ... interesting !!
        System.out.println("\n>>> PARSE PATHS <<<");
        for (ParsePath ppath : ppaths) {
            System.out.println(" PPTH: " + ppath.getTokens().stream().map(tk -> tk.serializeToStr()).collect(Collectors.joining()));
        }

        // Annotations ... interesting ??
        System.out.println("\n>>> ANNOTATIONS <<<");
        Iterator<Annotation> annIter = results.getAnnotations();
        while (annIter.hasNext()) {
            Annotation ann = annIter.next();
            System.out.println(" ANNT: " + ann.getCode() + " :: " + ann.getName());
        }

        // Results ... what do we want to include?
        System.out.println("\n>>> RESULTS <<<");
        for (PlaceRepresentation placeRep : results.getPlaceRepresentations()) {
            Interpretation interp = placeRep.getMetadata().getInterpretation();
            System.out.println("\n PREP: " + placeRep.getId());
            System.out.println("  int: " + interp.toString());
            System.out.println("  prs: " + interp.getParsedInput());
            System.out.println("  tkn: " + interp.getParsedInput().getTokens().stream().map(tk -> tk.serializeToStr()).collect(Collectors.joining()));

            interp.getParsedInput().getTokens()
                .forEach(tk -> System.out.println("   tk: [" +
                                                  tk.getOriginalToken() + "|" +
                                                  tk.getOriginalNormalizedToken() + "|" +
                                                  tk.getScript() + "|" +
                                                  (tk.getTypes().stream()
                                                      .map(ttt -> ttt.getDeclaringClass().getSimpleName() + "::" + ttt.getIndex() + "::" + ttt.toString())
                                                      .collect(Collectors.joining(",", "(", ")"))) +
                                                  "]"));

            System.out.println("  chn: " + Arrays.stream(interp.getJurisdictionIds()).mapToObj(id -> String.valueOf(id)).collect(Collectors.joining(",", "[", "]")));
            System.out.println("  tkx: " + Arrays.stream(interp.getTokenIndexMatches()).map(pr -> String.valueOf(pr.getRepId())).collect(Collectors.joining(",", "[", "]")));
            System.out.println("  vrt: " + Arrays.stream(interp.getMatchedVariants()).map(vr -> String.valueOf(vr.getNormalizedName())).collect(Collectors.joining(",", "[", "]")));
            System.out.println("  vrt: " + Arrays.stream(interp.getMatchedVariants()).map(vr -> String.valueOf(vr.getName().get())).collect(Collectors.joining(",", "[", "]")));
            System.out.println("  lvl: " + Arrays.stream(interp.getJurisdictionLevelMatches()).mapToObj(ndx -> String.valueOf(ndx)).collect(Collectors.joining(",", "[", "]")));
            System.out.println("  scr: " + interp.getScoring().getRawScore() + " :: " + interp.getScoring().getRelevanceScore());
        }
    }
}
