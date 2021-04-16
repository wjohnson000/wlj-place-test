/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.jira;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.familysearch.standards.place.data.PlaceBridge;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.SearchParameter;
import org.familysearch.standards.place.data.SearchParameters;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;

/**
 * The input 'Lamar, Texas' results in 'Lamar, Republic of Texas'.  It does NOT return the
 * desired result of 'Lamar, Texas, USA'.
 * 
 * The cause is the new "FullParsePathSearch" which finds a perfect match in the former, so
 * never looks for the latter.
 * 
 * This little application will look for cases where a top-level place and a second-level
 * place share the same variant name.  Sigh ...
 * 
 * @author wjohnson000
 *
 */
public class STD6009_Fix {

    static SolrService service;
    static final Set<String> topLevelNames = new HashSet<>();

    public static void main(String...args) throws SQLException {
        service = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");

        initializeTopLevelNames();
        System.out.println("Name-count=" + topLevelNames.size());
        service.shutdown();
        System.exit(0);
    }

    /**
     * Search for all top-level place-reps, i.e., those with no parent.  Return the intersection
     * of their normalized variant names.
     */
    protected static void initializeTopLevelNames() {
        List<PlaceRepBridge> allTopLevel = getAllTopLevelPlaces();
        System.out.println("Top-Level places.count=" + String.valueOf(allTopLevel.size()));

//        List<PlaceRepBridge> uniqueTopLevel = filterPlacesWithNonTopLevelReps(allTopLevel);
//        System.out.println("Unique top-Level places.count=" + String.valueOf(uniqueTopLevel.size()));
        List<PlaceRepBridge> uniqueTopLevel = allTopLevel;

        for (PlaceRepBridge prBridge : uniqueTopLevel) {
            PlaceBridge owner = prBridge.getAssociatedPlace();
            topLevelNames.addAll(owner.getAllNormalizedVariantNames());
        }
        System.out.println("Loaded top-level names.count=" + String.valueOf(topLevelNames.size()));
        System.out.println("Has 'texas'?? " + String.valueOf(topLevelNames.contains("texas")));
        System.out.println(">>>>>> USA? " + topLevelNames.contains("unitedstates"));
        System.out.println(">>>>>> USA? " + topLevelNames.contains("usa"));
        System.out.println(">>>>>> USA? " + topLevelNames.contains("us"));

        removeSecondLevelNames(uniqueTopLevel);
        System.out.println("Final top-level names.count=" + String.valueOf(topLevelNames.size()));
        System.out.println("Has 'texas'!! " + String.valueOf(topLevelNames.contains("texas")));
        System.out.println(">>>>>> USA? " + topLevelNames.contains("unitedstates"));
        System.out.println(">>>>>> USA? " + topLevelNames.contains("usa"));
        System.out.println(">>>>>> USA? " + topLevelNames.contains("us"));
    }
    
    protected static List<PlaceRepBridge> getAllTopLevelPlaces() {
        SearchParameters params = new SearchParameters();
        params.addParam(SearchParameter.RequiredDirectParentParam.createParam(-1));
        params.addParam(SearchParameter.FilterDeleteParam.createParam(true));
        params.addParam(SearchParameter.PublishedParam.createParam(true));
        params.addParam(SearchParameter.ResultLimitParam.createParam(1_000));
        
        try {
            return service.search(params).getResults();
        } catch (PlaceDataException e) {
            System.out.println("Error loading top-level names --> " + e.getMessage());
            return new ArrayList<>();
        }
    }

    protected static List<PlaceRepBridge> filterPlacesWithNonTopLevelReps(List<PlaceRepBridge> allTopLevelPlaces) {
        List<Integer> ownerIds = allTopLevelPlaces.stream()
            .map(repB -> repB.getPlaceId())
            .collect(Collectors.toList());

        Set<Integer> ownersWithUniqueReps = new HashSet<>();

        for (int ndx=0;  ndx<allTopLevelPlaces.size();  ndx+=50) {
            int last = Math.min(ndx+49, allTopLevelPlaces.size());
            List<Integer> subList = ownerIds.subList(ndx, last);
            List<PlaceRepBridge> repsByOwner = getRepsByOwner(subList);

            Map<Integer, Long> ownersByCount = repsByOwner.stream()
                .collect(Collectors.groupingBy(rep -> new Integer(rep.getPlaceId()), Collectors.counting()));
            ownersByCount.values().removeIf(count -> count > 1);
            ownersWithUniqueReps.addAll(ownersByCount.keySet());
        }

        return allTopLevelPlaces.stream()
            .filter(rep -> ownersWithUniqueReps.contains(rep.getPlaceId()))
            .collect(Collectors.toList());
    }

    protected static void removeSecondLevelNames(List<PlaceRepBridge> uniqueTopLevel) {
        List<Integer> repIds = uniqueTopLevel.stream()
             .map(repB -> repB.getRepId())
             .collect(Collectors.toList());

        for (int ndx=0;  ndx<uniqueTopLevel.size();  ndx+=5) {
            int last = Math.min(ndx+4, uniqueTopLevel.size());
            List<Integer> subList = repIds.subList(ndx, last);
            List<PlaceRepBridge> children = getChildPlaces(subList);
            for (PlaceRepBridge child : children) {
                PlaceBridge owner = child.getAssociatedPlace();
boolean usa01 = topLevelNames.contains("unitedstates");
                topLevelNames.removeAll(owner.getAllNormalizedVariantNames());
boolean usa02 = topLevelNames.contains("unitedstates");
if (usa01  &&  ! usa02) {
    System.out.println("REP: " + child + " --> PLACE: " + owner);
}
            }
        }
    }
    
    protected static List<PlaceRepBridge> getRepsByOwner(List<Integer> subList) {
        SearchParameters params = new SearchParameters();
        subList.forEach(id -> params.addParam(SearchParameter.PlaceParam.createParam(id)));
        params.addParam(SearchParameter.FilterDeleteParam.createParam(true));
        params.addParam(SearchParameter.ResultLimitParam.createParam(1_000));
        
        try {
            return service.search(params).getResults();
        } catch (PlaceDataException e) {
            System.out.println("Error loading top-level names --> " + e.getMessage());
            return new ArrayList<>();
        }
    }

    protected static List<PlaceRepBridge> getChildPlaces(List<Integer> parents) {
        SearchParameters params = new SearchParameters();
        parents.forEach(parent -> params.addParam(SearchParameter.RequiredDirectParentParam.createParam(parent)));
        params.addParam(SearchParameter.FilterDeleteParam.createParam(true));
        params.addParam(SearchParameter.ResultLimitParam.createParam(10_000));

        try {
            return service.search(params).getResults();
        } catch (PlaceDataException e) {
            System.out.println("Error loading top-level names --> " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
