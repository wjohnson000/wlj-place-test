/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.ws.httpclient;

import java.util.*;

/**
 * @author wjohnson000
 *
 */
public class TestClientsLots {

    static final String APPLICATION_JSON = "application/json";

    static final String BASE_URL = "http://core.homelands.service.integ.us-east-1.dev.fslocal.org/name";

    static List<String> nameIds = new ArrayList<>(100_000);
    static List<String> results = new ArrayList<>(100);

    public static void main(String... args) throws Exception {
        retrieveNameIds();

        for (int i=0;  i<3;  i++) {
            doItRawJava();
            doItApacheClient();
            doItApacheClientPooled();
            doItSpringWebClient();
        }

        results.forEach(System.out::println);
        System.exit(0);
    }

    static void retrieveNameIds() {
        ApacheClient client = ApacheClient.getDefault(APPLICATION_JSON, Collections.emptyMap());
        WebResponse response = client.doGet(BASE_URL + "/id/?collection=MMMM-93Z");  // "-938" for larger collection
        String idStr = response.getBody();
        int ndx0 = idStr.indexOf('[');
        int ndx1 = idStr.indexOf(']');
        idStr = idStr.substring(ndx0+1, ndx1);
        Arrays.stream(idStr.split(","))
              .map(id -> id.replace('"', ' ').trim())
              .forEach(id -> nameIds.add(id));
        results.add("IDS: " + nameIds.size() + " --> >>" + nameIds.get(0) + "<<");
        System.out.println("IDs retrieved ... count=" + nameIds.size());
    }

    static void doItRawJava() throws Exception {
        JavaClient client = JavaClient.getDefault("application/json", Collections.emptyMap());

        int found = 0;
        long time0 = System.nanoTime();
        for (String nameId : nameIds) {
            WebResponse response = client.doGet(BASE_URL + "/" + nameId);
            if (response.getStatus() == 200) {
                found++;
                if (found % 250 == 0) System.out.println("... raw ... " + found);
            }
        }
        long time1 = System.nanoTime();

        results.add("");
        results.add("");
        results.add("Raw-Java ===========================================================");
        results.add("  Found: " + found);
        results.add("   Time: " + (time1 - time0) / 1_000_000.0);
    }

    static void doItApacheClient() {
        ApacheClient client = ApacheClient.getDefault(APPLICATION_JSON, Collections.emptyMap());

        int found = 0;
        long time0 = System.nanoTime();
        for (String nameId : nameIds) {
            WebResponse response = client.doGet(BASE_URL + "/" + nameId);
            if (response.getStatus() == 200) {
                found++;
                if (found % 250 == 0) System.out.println("... apache ... " + found);
            }
        }
        long time1 = System.nanoTime();

        results.add("");
        results.add("");
        results.add("ApacheClient ===========================================================");
        results.add("  Found: " + found);
        results.add("   Time: " + (time1 - time0) / 1_000_000.0);
    }

    static void doItApacheClientPooled() {
        ApacheClientPooled client = ApacheClientPooled.getDefault(APPLICATION_JSON, Collections.emptyMap());

        int found = 0;
        long time0 = System.nanoTime();
        for (String nameId : nameIds) {
            WebResponse response = client.doGet(BASE_URL + "/" + nameId);
            if (response.getStatus() == 200) {
                found++;
                if (found % 250 == 0) System.out.println("... apache-pooled ... " + found);
            }
        }
        long time1 = System.nanoTime();

        results.add("");
        results.add("");
        results.add("ApacheClientPooled ===========================================================");
        results.add("  Found: " + found);
        results.add("   Time: " + (time1 - time0) / 1_000_000.0);
    }

    static void doItSpringWebClient() {
        SpringWebClient client = SpringWebClient.getDefault(APPLICATION_JSON, Collections.emptyMap());

        int found = 0;
        long time0 = System.nanoTime();
        for (String nameId : nameIds) {
            WebResponse response = client.doGet(BASE_URL + "/" + nameId);
            if (response.getStatus() == 200) {
                found++;
                if (found % 250 == 0) System.out.println("... spring-web ... " + found);
            }
        }
        long time1 = System.nanoTime();

        results.add("");
        results.add("");
        results.add("SpringWebClient ===========================================================");
        results.add("  Found: " + found);
        results.add("   Time: " + (time1 - time0) / 1_000_000.0);
    }
}
