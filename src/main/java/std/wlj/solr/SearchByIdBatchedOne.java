/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.solr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.PlaceSearchResults;
import org.familysearch.standards.place.data.ReadableDataService;
import org.familysearch.standards.place.data.SearchParameter;
import org.familysearch.standards.place.data.SearchParameters;

import std.wlj.util.SolrManager;

/**
 * Do searches two way: by ID only and then filter by jurisdiction, and by ID + jurisdiction.
 * 
 * @author wjohnson000
 *
 */
public class SearchByIdBatchedOne {

    private static final int SOLR_BATCH_SIZE  = 250;
    private static final int MAX_SOLR_QUERIES = 100;

    private static final int INDIANA_ID = 331;
    private static final int UTAH_ID = 342;
    private static final int FRANCE_ID = 477;
    private static final int GERMANY_ID = 506;
    private static final int CHINA_ID = 473;

    /** Data service for doing additional operations for place-type-groups */
    private static ReadableDataService dataService;

    public static void main(String...args) {
//        dataService = SolrManager.awsIntService(true);
        dataService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.7.1");

        Random random = new Random();
        Set<Integer> repIds = IntStream.range(1, 10_001)
                .mapToObj(i -> Integer.valueOf(random.nextInt(11_000_000) + 1))
                .collect(Collectors.toSet());

        long time0 = System.nanoTime();
        Map<Integer, PlaceRepBridge> tResults = findPlaceReps(repIds);
        long time1 = System.nanoTime();
        Set<PlaceRepBridge> indianaReps = tResults.values().stream().filter(rb -> isJurisdictionOK(INDIANA_ID, rb)).collect(Collectors.toSet());
        Set<PlaceRepBridge> utahReps    = tResults.values().stream().filter(rb -> isJurisdictionOK(UTAH_ID, rb)).collect(Collectors.toSet());
        Set<PlaceRepBridge> franceReps  = tResults.values().stream().filter(rb -> isJurisdictionOK(FRANCE_ID, rb)).collect(Collectors.toSet());
        Set<PlaceRepBridge> germanyReps = tResults.values().stream().filter(rb -> isJurisdictionOK(GERMANY_ID, rb)).collect(Collectors.toSet());
        Set<PlaceRepBridge> chinaReps   = tResults.values().stream().filter(rb -> isJurisdictionOK(CHINA_ID, rb)).collect(Collectors.toSet());
        long time2 = System.nanoTime();

        System.out.println("Vals: " + repIds.size());
        System.out.println("Reps: " + tResults.size());
        System.out.println("IN.R: " + indianaReps.size());
        indianaReps.forEach(rep -> System.out.println("  IN: " + rep + " --> " + Arrays.toString(rep.getJurisdictionIdentifiers())));
        System.out.println("UT.R: " + utahReps.size());
        utahReps.forEach(rep -> System.out.println("  UT: " + rep + " --> " + Arrays.toString(rep.getJurisdictionIdentifiers())));
        System.out.println("FR.R: " + franceReps.size());
        franceReps.forEach(rep -> System.out.println("  FR: " + rep + " --> " + Arrays.toString(rep.getJurisdictionIdentifiers())));
        System.out.println("DE.R: " + germanyReps.size());
        germanyReps.forEach(rep -> System.out.println("  DE: " + rep + " --> " + Arrays.toString(rep.getJurisdictionIdentifiers())));
        System.out.println("ZH.R: " + chinaReps.size());
        chinaReps.forEach(rep -> System.out.println("  ZH: " + rep + " --> " + Arrays.toString(rep.getJurisdictionIdentifiers())));

        System.out.println("Tim1: " + (time1 - time0) / 1_000_000.0);
        System.out.println("Tim2: " + (time2 - time1) / 1_000_000.0);

        dataService.shutdown();
        System.exit(0);
    }

    /**
     * Find a bunch of place-reps by their id.  Return a map of results, the key being the rep-id,
     * and the value being the PlaceRepBridge.  Don't propagate any exceptions -- simply return an
     * empty map if anything bad happened.
     * 
     * @param placeRepId The place-rep identifier
     * @return Returns the associated place-rep
     */
    protected static Map<Integer, PlaceRepBridge> findPlaceReps(Set<Integer> repIds) {
        Map<Integer, PlaceRepBridge> repMap = new HashMap<>();

        int queryCount = 0;
        Set<Integer> tRepIds = new HashSet<>();
        for (Integer repId : repIds) {
            tRepIds.add(repId);
            if (tRepIds.size() == SOLR_BATCH_SIZE) {
                repMap.putAll(findPlaceRepsBatched(tRepIds));
                tRepIds.clear();

                if (++queryCount > MAX_SOLR_QUERIES) {
                    break;
                }
            }
        }

        if (! tRepIds.isEmpty()) {
            repMap.putAll(findPlaceRepsBatched(tRepIds));
        }

        return repMap;
    }

    protected static Map<Integer, PlaceRepBridge> findPlaceRepsBatched(Set<Integer> repIds) {
        Map<Integer, PlaceRepBridge> repMap = new HashMap<>(); 

        try {
            SearchParameters  params = new SearchParameters();
            params.addParam(SearchParameter.FilterDeleteParam.createParam(false));
            repIds.forEach(repId -> params.addParam(SearchParameter.PlaceRepParam.createParam(repId)));
            System.out.println("... search ... " + repIds.size());

            // The key will be the original rep-id.  The value will be the associated document, or if
            // it's been deleted, the replacement rep.
            PlaceSearchResults results = dataService.search(params);
            for (PlaceRepBridge bridge : results.getResults()) {
                int id = bridge.getRepId();
                if (bridge.isDeleted()) {
                    bridge = bridge.getDeleteReplacement();
                }
                repMap.put(id, bridge);
            }
        } catch (PlaceDataException e) {
            ; // NOSONAR -- ignore this for now
        }

        return repMap;
    }

    /**
     * Retrieve the full display-name of the place-rep based on a given locale.
     * 
     * @param repBridge place-rep
     * @param locale locale in which the name is to be retrieved
     * @return
     */
    protected static String getName(PlaceRepBridge repBridge, StdLocale locale) {
        if (repBridge == null) {
            return "Not Found";
        } else {
            PlaceRepresentation placeRep = new PlaceRepresentation(repBridge);
            return placeRep.getFullDisplayName(locale).get();
        }
    }

    /**
     * Determine if a place-rep has the correct jurisdiction.
     * 
     * @param parentId required parent
     * @param repB place-rep
     * @return
     */
    protected static boolean isJurisdictionOK(int parentId, PlaceRepBridge repB) {
        return Arrays.stream(repB.getJurisdictionIdentifiers())
                .anyMatch(repId -> repId == parentId);
    }
}
