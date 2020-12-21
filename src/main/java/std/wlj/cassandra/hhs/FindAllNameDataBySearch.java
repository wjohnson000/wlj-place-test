/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.databind.JsonNode;
import net.sf.ehcache.util.concurrent.ConcurrentHashMap;
import org.familysearch.homelands.lib.common.util.JsonUtility;

import std.wlj.ws.rawhttp.HttpClientX;

/**
 * @author wjohnson000
 *
 */
public class FindAllNameDataBySearch {

    static final String BASE_DIR = "C:/D-drive/homelands/names/final";
    static final String BASE_URL = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org";

    static final Set<String> nameData = ConcurrentHashMap.newKeySet();
    static final AtomicLong  counter = new AtomicLong(0);

    public static void main(String...args) throws Exception {
        System.out.println("SKIP: " + 269_000);
        List<String> nameIds = Files.readAllLines(Paths.get("C:/temp/dev-name-missing-ids.txt"), StandardCharsets.UTF_8);
        System.out.println(">> NameID.count=" + nameIds.size());
        searchNames(nameIds);
        Files.write(Paths.get("C:/temp/dev-name-details.txt"), nameData, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static byte[] bytesFromFile(String filename) throws Exception {
        return Files.readAllBytes(Paths.get(BASE_DIR, filename));
    }
    
    static void searchNames(List<String> nameIds) throws Exception {
        Map<String, String> xxHeaders = Collections.singletonMap("Accept-Language", "en,es,fr");
        ExecutorService executor = createExecutorService();

        nameIds.stream()
               .skip(260_000L)
               .limit(135_000L)
               .forEach(id -> executor.submit(() -> searchName(id, xxHeaders)));

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.MINUTES);
    }

    static void searchName(String nameId, Map<String, String> headers) {
        try {
            long count = counter.incrementAndGet();
            if (count % 1000L == 0L) System.out.println("Count=" + count);

            String json = HttpClientX.doGetJSON(BASE_URL + "/name/" + nameId, headers);
            JsonNode node = (json == null) ? null : JsonUtility.parseJson(json);

            StringBuilder buff = new StringBuilder();
            if (node == null) {
                buff.append(nameId);
            } else {
                buff.append(JsonUtility.getStringValue(node, "id"));
                buff.append("|").append(JsonUtility.getStringValue(node, "name"));
                buff.append("|").append(JsonUtility.getStringValue(node, "type"));
                buff.append("|").append(JsonUtility.getStringValue(node, "language"));
                buff.append("|").append(JsonUtility.getStringValue(node, "collectionId"));
            }
            nameData.add(buff.toString());
        } catch(Exception ex) { }
    }

    static ExecutorService createExecutorService() {
            return Executors.newScheduledThreadPool(
                        12,
                        runn -> {
                            Thread thr = Executors.defaultThreadFactory().newThread(runn);
                            thr.setDaemon(true);
                            return thr;
                    });
    }
}
