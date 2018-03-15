package std.wlj.redis;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.redisson.Redisson;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class Test01_Simple {

    static Random random = new Random();
    static RMapCache<String, String> wikiChunks;

    public static void main(String...args) {
        Config config = new Config();
//        config.useSingleServer().setAddress("127.0.0.1:6379");
//        config.useSingleServer().setAddress("redis://localhost:6379");
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");

        System.out.println("Made it here!!"); 
        RedissonClient client = Redisson.create(config);
        System.out.println("Made it here here here !!");

        wikiChunks = client.getMapCache("wikipedia");
        for (int i=1;  i<=10000;  i++) {
            int stupid = random.nextInt(123);
            String stupidURL = "http://www.wikipedia.org/" + stupid;
            String wikiChunk = getWikiChunk(stupidURL);
            System.out.println(stupidURL + " --> " + wikiChunk);
        }

        client.shutdown();
        System.exit(0);
    }

    static String getWikiChunk(String url) {
        String chunk = wikiChunks.get(url);
        if (chunk == null) {
            try {
                System.out.println("Waiting .... " + url);
                chunk = "This is the data for URL=" + url;
                Thread.sleep(1000L);
            } catch(Exception ex) { }
            wikiChunks.put(url, chunk, 3, TimeUnit.MINUTES);
        }
        return chunk;
    }
    
}
