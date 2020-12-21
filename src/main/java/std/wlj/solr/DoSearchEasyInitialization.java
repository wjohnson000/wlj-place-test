package std.wlj.solr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.familysearch.standards.place.data.PlaceBridge;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.SearchParameter;
import org.familysearch.standards.place.data.SearchParameters;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.SolrManager;

public class DoSearchEasyInitialization {

    static final Set<String> topLevelNames    = new HashSet<>();
    static final Set<String> topLevelNamesAll = new HashSet<>();

    public static void main(String... args) throws Exception {
        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");

        List<PlaceRepBridge> allTopLevel = getAllTopLevelPlaces(solrService);
        List<PlaceRepBridge> uniqueTopLevel = filterOutXxx(solrService, allTopLevel);
        System.out.println("ALL: " + allTopLevel.size());
        System.out.println("UNQ: " + uniqueTopLevel.size());

        solrService.shutdown();

        for (PlaceRepBridge prBridge : allTopLevel) {
            PlaceBridge owner = prBridge.getAssociatedPlace();
            topLevelNamesAll.addAll(owner.getAllNormalizedVariantNames());
        }

        for (PlaceRepBridge prBridge : uniqueTopLevel) {
            PlaceBridge owner = prBridge.getAssociatedPlace();
            topLevelNames.addAll(owner.getAllNormalizedVariantNames());
        }

        System.out.println("ALL-names-count: " + topLevelNamesAll.size());
        System.out.println("UNQ-names-count: " + topLevelNames.size());
        topLevelNamesAll.removeAll(topLevelNames);
        new TreeSet<>(topLevelNamesAll).forEach(System.out::println);

        System.exit(0);
    }

    static List<PlaceRepBridge> getAllTopLevelPlaces(SolrService solrService) {
        SearchParameters params = new SearchParameters();
        params.addParam(SearchParameter.RequiredDirectParentParam.createParam(-1));
        params.addParam(SearchParameter.FilterDeleteParam.createParam(true));
        params.addParam(SearchParameter.PublishedParam.createParam(true));
        params.addParam(SearchParameter.ResultLimitParam.createParam(1_000));

        try {
            return solrService.search(params).getResults();
        } catch (PlaceDataException e) {
            System.out.println("Oops: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private static List<PlaceRepBridge> getRepsByOwner(SolrService solrService, List<Integer> subList) {
        SearchParameters params = new SearchParameters();
        for (Integer id : subList) {
            params.addParam(SearchParameter.PlaceParam.createParam(id));
        }
        params.addParam(SearchParameter.FilterDeleteParam.createParam(true));
        params.addParam(SearchParameter.PublishedParam.createParam(true));
        params.addParam(SearchParameter.ResultLimitParam.createParam(1_000));

        try {
            return solrService.search(params).getResults();
        } catch (PlaceDataException e) {
            System.out.println("Oops: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    static List<PlaceRepBridge> filterOutXxx(SolrService solrService, List<PlaceRepBridge> allTopLevelPlaces) {
        List<Integer> ownerIds = allTopLevelPlaces.stream()
            .map(repB -> repB.getPlaceId())
            .collect(Collectors.toList());

        Set<Integer> ownersWithUniqueReps = new HashSet<>();

        for (int ndx=0;  ndx<allTopLevelPlaces.size();  ndx+=50) {
            int last = Math.min(ndx+49, allTopLevelPlaces.size());
            List<Integer> subList = ownerIds.subList(ndx, last);
            List<PlaceRepBridge> repsByOwner = getRepsByOwner(solrService, subList);

            Map<Integer, Long> ownersByCount = repsByOwner.stream()
                .collect(Collectors.groupingBy(rep -> new Integer(rep.getPlaceId()), Collectors.counting()));
            ownersByCount.values().removeIf(count -> count > 1);
            ownersWithUniqueReps.addAll(ownersByCount.keySet());
        }

        return allTopLevelPlaces.stream()
            .filter(rep -> ownersWithUniqueReps.contains(rep.getPlaceId()))
            .collect(Collectors.toList());
    }
}
