/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.homelands.admin.client.HomelandsCoreClient;
import org.familysearch.homelands.admin.client.WebClientWrapper;
import org.familysearch.homelands.admin.client.model.WebResponse;
import org.familysearch.homelands.admin.importer.step.NameHelper;
import org.familysearch.paas.binding.register.Environment;
import org.familysearch.paas.binding.register.Region;
import org.familysearch.paas.binding.register.ServiceLocator;
import org.familysearch.paas.binding.register.ServiceLocatorConfig;
import org.familysearch.paas.binding.register.Site;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.util.retry.Retry;

/**
 * @author wjohnson000
 *
 */
public class TestNameHelper {

    public static void main(String...args) throws Exception {
        String sessionId = "7a89d83a-6557-40bb-9ebc-14a457440dff-integ";
        Set<String> names = getEsNames();
        System.out.println("Names.count=" + names.size());

        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.PROD, Site.PROD, Region.US_EAST_1);
        ServiceLocator locator = new ServiceLocator(config);

        WebClientWrapper clientWrapper = new WebClientWrapper(webClient());
        HomelandsCoreClient hscWebClient = new HomelandsCoreClient(locator, "core.homelands.service", "", clientWrapper);

        Map<String, String> nameIds = NameHelper.readNames("MMMM-98L", names, "LAST", hscWebClient, "en", sessionId, null);
        nameIds.entrySet().forEach(System.out::println);

        searchName(hscWebClient, "Espinoza");
        searchName(hscWebClient, "Williams");
        searchName(hscWebClient, "DaCosta");
        searchName(hscWebClient, "Da Costa");
        searchName(hscWebClient, "Da%20Costa");
    }

    static void searchName(HomelandsCoreClient hcsWebClient, String name) {
        WebResponse response = hcsWebClient.searchAll(name, "", "en", null);
        System.out.println("\n=============================================================");
        System.out.println("NAME: " + name);
        System.out.println("  ST: " + response.getStatus());
        System.out.println("  TX: " + response.getBody());
    }

    static Set<String> getEsNames() {
        Set<String> names = new TreeSet<>();
        names.add("Espinoza");
        names.add("Williams");
        names.add("Da Costa");
        return names;
    }

    static WebClient webClient() {
        return WebClient.builder().filter((request, next) -> next.exchange(request).doOnNext(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                Map<String, String> respHeaders = clientResponse.headers().asHttpHeaders().entrySet().stream()
                        .collect(Collectors.toMap(ee -> ee.getKey(), ee -> ee.getValue().stream().collect(Collectors.joining(", "))));

                System.out.println("5XX HTTP Status. http_status=" + clientResponse.statusCode() + "; warning_header=" + respHeaders.get(HttpHeaders.WARNING));
                throw new RuntimeException("http call failed with http_status=" + clientResponse.statusCode().value());
            }
        }).retryWhen(Retry.fixedDelay(1, Duration.ofSeconds(1)))).build();
    }
}
