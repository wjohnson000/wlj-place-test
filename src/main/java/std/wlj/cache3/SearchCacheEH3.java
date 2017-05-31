package std.wlj.cache3;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ehcache.UserManagedCache;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.UserManagedCacheBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.PlaceSearchResults;

public class SearchCacheEH3 {

    private String name;
    private int maxSearchSize = 0;
    private UserManagedCache<String, PlaceSearchResults> searchCache;
    private ExecutorService execService = Executors.newFixedThreadPool(2);

    public SearchCacheEH3(String name, int maxSearchCnt, long ttl) {
        this(name, maxSearchCnt, ttl, 0);
    }

    public SearchCacheEH3(String name, int maxSearchCnt, long ttl, int maxSearchSize) {
        this.name = name;
        this.maxSearchSize = maxSearchSize;

        searchCache =
            UserManagedCacheBuilder.newUserManagedCacheBuilder(String.class, PlaceSearchResults.class)
               .identifier("search-results")
               .withResourcePools(ResourcePoolsBuilder.newResourcePoolsBuilder().heap(maxSearchCnt, EntryUnit.ENTRIES))
               .withEventExecutors(execService, execService)
               .withExpiry(Expirations.timeToLiveExpiration(new Duration(ttl, java.util.concurrent.TimeUnit.SECONDS)))
               .build(true);
    }

    public void addSearchListener(CacheEventListener<String, PlaceSearchResults> listener) {
        searchCache.getRuntimeConfiguration()
            .registerCacheEventListener(
                listener,
                EventOrdering.ORDERED,
                EventFiring.ASYNCHRONOUS,
                EventType.CREATED, EventType.UPDATED, EventType.EXPIRED, EventType.EVICTED, EventType.REMOVED);
    }

    public void add(String key, PlaceSearchResults searchResults) {
        if (searchResults != null) {
            List<PlaceRepBridge> resultList = searchResults.getResults();

            // Add the entry to the cache if there is room in the inn.
            if (maxSearchSize == 0  ||  resultList.size() < maxSearchSize) {
                searchCache.put(key, searchResults);
            }
        }
    }

    public PlaceSearchResults getSearchResults(String key) {
        return searchCache.get(key);
    }

    public String getName() {
        return name;
    }

    public void doSomething() {
        searchCache.getStatus();
    }

    public void shutdown() {
        searchCache.close();
    }
}
