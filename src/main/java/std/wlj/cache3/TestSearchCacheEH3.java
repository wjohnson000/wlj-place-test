package std.wlj.cache3;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
//import org.ehcache.impl.events.CacheEventAdapter;
import org.familysearch.standards.place.data.PlaceSearchResults;

public class TestSearchCacheEH3 {

    SearchCacheEH3 warmCache;
    PlaceSearchResults onlyResults;
//    EH3CacheListener cacheListener;
    EH3CacheListenerX cacheListenerX;

    private class EH3CacheListenerX implements CacheEventListener<String, PlaceSearchResults> {

        int expireCnt = 0;
        int evictCnt  = 0;
        int removeCnt = 0;
        int updateCnt = 0;
        int createCnt = 0;

        public int getExpireCount() { return expireCnt; }
        public int getEvictCount()  { return evictCnt; }
        public int getRemoveCount() { return removeCnt; }
        public int getUpdateCount() { return updateCnt; }
        public int getCreateCount() { return createCnt; }

        @Override
        public void onEvent(CacheEvent<String, PlaceSearchResults> event) {
            switch (event.getType()) {
            case CREATED:
                createCnt++;  break;
            case UPDATED:
                updateCnt++;  break;
            case REMOVED:
                removeCnt++;  break;
            case EXPIRED:
                expireCnt++;  break;
            case EVICTED:
                evictCnt++;   break;
            default:
              throw new AssertionError("Unsupported event type " + event.getType());
            }
        }
    }
//
//    private class EH3CacheListener extends CacheEventAdapter<String, PlaceSearchResults> {
//
//        int expireCnt = 0;
//        int evictCnt  = 0;
//        int removeCnt = 0;
//        int updateCnt = 0;
//        int createCnt = 0;
//
//        public int getExpireCount() { return expireCnt; }
//        public int getEvictCount()  { return evictCnt; }
//        public int getRemoveCount() { return removeCnt; }
//        public int getUpdateCount() { return updateCnt; }
//        public int getCreateCount() { return createCnt; }
//
//        @Override
//        protected void onEviction(String key, PlaceSearchResults evictedValue) {
//            evictCnt++;
//            super.onEviction(key, evictedValue);
//        }
//
//        @Override
//        protected void onExpiry(String key, PlaceSearchResults expiredValue) {
//            expireCnt++;
//            super.onExpiry(key, expiredValue);
//        }
//
//        @Override
//        protected void onRemoval(String key, PlaceSearchResults removedValue) {
//            removeCnt++;
//            super.onRemoval(key, removedValue);
//        }
//
//        @Override
//        protected void onUpdate(String key, PlaceSearchResults oldValue, PlaceSearchResults newValue) {
//            updateCnt++;
//            super.onUpdate(key, oldValue, newValue);
//        }
//
//        @Override
//        protected void onCreation(String key, PlaceSearchResults newValue) {
//            createCnt++;
//            super.onCreation(key, newValue);
//        }
//    }

    public TestSearchCacheEH3() {
        onlyResults = new PlaceSearchResults(null);
//        cacheListener = new EH3CacheListener();
        cacheListenerX = new EH3CacheListenerX();

        warmCache = new SearchCacheEH3("warm", 512, 20, 1000);
//        warmCache.addSearchListener(cacheListener);
        warmCache.addSearchListener(cacheListenerX);
    }

    public void shutdown() {
        warmCache.shutdown();
    }

    public void doGets() {
        int key = 1;
        int addCnt = 0;
        for (int i=0;  i<=50_000;  i++) {
            key += i*i;
            key %= 1009;
            String sKey = String.valueOf(key);
            PlaceSearchResults result = warmCache.getSearchResults(sKey);
            if (result == null) {
                addCnt++;
                warmCache.add(sKey, onlyResults);
            }
            try { Thread.sleep(2L); } catch(Exception ex) { }
        }
        System.out.println("Add count: " + addCnt);
//        System.out.println("Expire.count: " + cacheListener.getExpireCount());
//        System.out.println(" Evict.count: " + cacheListener.getEvictCount());
//        System.out.println("Remove.count: " + cacheListener.getRemoveCount());
//        System.out.println("Update.count: " + cacheListener.getUpdateCount());
//        System.out.println("Create.count: " + cacheListener.getCreateCount());
        System.out.println("Expire.count: " + cacheListenerX.getExpireCount());
        System.out.println(" Evict.count: " + cacheListenerX.getEvictCount());
        System.out.println("Remove.count: " + cacheListenerX.getRemoveCount());
        System.out.println("Update.count: " + cacheListenerX.getUpdateCount());
        System.out.println("Create.count: " + cacheListenerX.getCreateCount());
    }

    public static void main(String...args) {
        TestSearchCacheEH3 test = new TestSearchCacheEH3();
        test.doGets();
        test.shutdown();
        System.exit(0);
    }
}
