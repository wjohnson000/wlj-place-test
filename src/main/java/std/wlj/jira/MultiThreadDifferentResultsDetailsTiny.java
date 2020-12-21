/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.jira;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.Metrics;
import org.familysearch.standards.place.Metrics.MapNumberMetric;
import org.familysearch.standards.place.Metrics.MapObjectMetric;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;
import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.util.SolrManager;

/**
 * NOTE: the two texts are interpreted differently on consecutive calls.  Details of each interpretation
 * is displayed.  If lines 169-178 in "CombineTypedTokensSegmenter" (std-lib-place, not std-lib-core)
 * are comment-ed out, the results for multiple interpretations are the same.  Red flag !!!
 * 
 * @author wjohnson000
 *
 */
public class MultiThreadDifferentResultsDetailsTiny {

    static final int condenseBy = 10;
    static final Object object = new Object();
    static final Random random = new Random();

    public static void main(String...args) throws IOException {
        List<String> placeNames = Arrays.asList(
            "Prince George, Amelia, Virginia, British Colonial America",
            "Open Hall, Bonavista Bay, Newfoundland and Labrador, Canada");

        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        for (String placeName : placeNames) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(placeName);

            interp(placeService, placeName);
            interp(placeService, placeName);
        }

        solrService.shutdown();
        System.exit(0);
    }

    static void interp(PlaceService service, String textAndLang) {
        String[] chunks = PlaceHelper.split(textAndLang, '|');
        String text = chunks[0];
        StdLocale locale = (chunks.length == 1) ? StdLocale.ENGLISH : new StdLocale(chunks[1]);
        PlaceRequestBuilder builder = service.createRequestBuilder(text, locale);
        builder.setShouldCollectMetrics(true);
        builder.setFilterResults(false);
        
        PlaceRequest request = builder.getRequest();
        PlaceResults results = service.requestPlaces(request);
        addResults(textAndLang, results);
    }

    static void addResults(String textAndLang, PlaceResults results) {
        PlaceRepresentation[] reps = results.getPlaceRepresentations();
        String value = Arrays.stream(reps)
            .map(rep -> String.valueOf(rep.getId()))
            .collect(Collectors.joining("|", String.valueOf(reps.length), ""));

        System.out.println(value);
        System.out.println(formatResults(textAndLang, results));
    }

    static String formatResults(String textAndLang, PlaceResults results) {
        StringBuilder buff = new StringBuilder(16 * 1024);
        buff.append(textAndLang);

        buff.append("\n    f-cnt: ").append(results.getFoundCount());
        buff.append("\n    r-cnt: ").append(results.getReturnedCount());
        Arrays.stream(results.getPlaceRepresentations()).forEach(rep -> buff.append("\n      rep: ").append(rep.getId()));
        buff.append("\n    annot: ");
        results.getAnnotations().forEachRemaining(ann -> buff.append(ann).append("  "));
        buff.append("\n");

        Metrics metrics = results.getMetrics();
        metrics.getSimpleNumberMetricSet().forEach(mm -> buff.append("\n  Number: ").append(mm.getName()).append(" --> ").append(metrics.getSimpleNumberMetric(mm)));
        metrics.getSimpleStringMetricSet().forEach(mm -> buff.append("\n  String: ").append(mm.getName()).append(" --> ").append(metrics.getSimpleStringMetric(mm)));
        for (MapNumberMetric mnm : metrics.getMapNumberMetricSet()) {
            buff.append("\n  NumMap: ").append(mnm.getName());
            for (String met : metrics.getMapNumberMetricSet(mnm)) {
                buff.append("\n     Met: ").append(met).append(" --> ").append(metrics.getMapNumberMetric(mnm, met));
            }
        }
//        for (MapObjectMetric mom : metrics.getMapObjectMetricSet()) {
//            buff.append("\n  ObjMap: ").append(mom.getName());
//            for (Object obj : metrics.getMapObjectMetricSet(mom)) {
//                buff.append("\n     Met: ").append(obj).append(" --> ").append(metrics.getMapObjectMetric(mom, obj));
//            }
//        }
        buff.append("\n");
        return buff.toString();
    }
}
