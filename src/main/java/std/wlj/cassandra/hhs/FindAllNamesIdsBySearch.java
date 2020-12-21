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
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.sf.ehcache.util.concurrent.ConcurrentHashMap;
import org.familysearch.homelands.admin.parser.name.*;
import org.familysearch.homelands.lib.common.util.JsonUtility;
import org.familysearch.homelands.lib.common.util.TextUtility;

import std.wlj.ws.rawhttp.HttpClientX;

/**
 * @author wjohnson000
 *
 */
public class FindAllNamesIdsBySearch {

    static final String BASE_DIR = "C:/D-drive/homelands/names/final";
    static final String BASE_URL = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org";

    static final Set<String> nameIds = ConcurrentHashMap.newKeySet();
    static final AtomicLong  counter = new AtomicLong(0);

    public static void main(String...args) throws Exception {
        Set<String> names = getAllNames();
        System.out.println(">> Name.count=" + names.size());
        searchNames(names);
        Files.write(Paths.get("C:/temp/dev-name-ids.txt"), nameIds, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

    }

    static Set<String> getAllNames() throws Exception {
        Set<String> names = new TreeSet<>();

        names.addAll((new OxfordFirstNameEnParser()).parse(bytesFromFile("first_acref_9780198610601.xml")).keySet());
        names.addAll((new OxfordLastNameEnParser()).parse(bytesFromFile("last_acref_9780195081374.xml")).keySet());
        names.addAll((new OxfordLastNameEsParser()).parse(bytesFromFile("DAFN_Hispanic_981_translated.csv")).keySet());
        names.addAll((new ROCNameParser()).parse(bytesFromFile("roc-names-from-missionaries.xlsx")).keySet());
        names.addAll((new GeneanetFirstNameParser()).parse(bytesFromFile("signification_geneanet.csv")).keySet());
        names.addAll((new GeneanetLastNameParser()).parse(bytesFromFile("noms_fr.csv")).keySet());

        return names;
    }

    static byte[] bytesFromFile(String filename) throws Exception {
        return Files.readAllBytes(Paths.get(BASE_DIR, filename));
    }
    
    static void searchNames(Set<String> names) throws Exception {
        Map<String, String> enHeaders = Collections.singletonMap("Accept-Language", "en");
        Map<String, String> frHeaders = Collections.singletonMap("Accept-Language", "fr");
        ExecutorService executor = createExecutorService();

        for (String name : names) {
            executor.submit(() -> searchName(name, enHeaders));
            executor.submit(() -> searchName(name, frHeaders));
        }
        executor.shutdown();
        executor.awaitTermination(150, TimeUnit.MINUTES);
    }

    static void searchName(String name, Map<String, String> headers) {
        try {
            if (counter.incrementAndGet() % 1000 == 0) System.out.println("Count=" + counter.get());

            String nName = TextUtility.normalize(name).replaceAll(" ", "%20s");
            String json = HttpClientX.doGetJSON(BASE_URL + "/names?text=" + nName, headers);
            JsonNode node = JsonUtility.parseJson(json);
            if (node instanceof ArrayNode) {
                ArrayNode aNode = (ArrayNode)node;
                for (JsonNode child : aNode) {
                    String nameId = JsonUtility.getStringValue(child, "id");
                    if (nameId != null) {
                        nameIds.add(nameId);
                    }
                }
            }
        } catch(Exception ex) { }
    }

    static ExecutorService createExecutorService() {
            return Executors.newScheduledThreadPool(
                        15,
                        runn -> {
                            Thread thr = Executors.defaultThreadFactory().newThread(runn);
                            thr.setDaemon(true);
                            return thr;
                    });
    }

}
