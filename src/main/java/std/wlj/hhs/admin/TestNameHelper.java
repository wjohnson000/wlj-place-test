/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.homelands.admin.client.HomelandsCoreClient;
import org.familysearch.homelands.admin.client.WebClientWrapper;
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

        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.DEV, Site.DEV, Region.US_EAST_1);
        ServiceLocator locator = new ServiceLocator(config);

        WebClientWrapper clientWrapper = new WebClientWrapper(webClient());
        HomelandsCoreClient hscWebClient = new HomelandsCoreClient(locator, "core.homelands.service", "", clientWrapper);

        Map<String, String> nameIds = NameHelper.readNames("MMM9-XL1", names, "LAST", hscWebClient, "en", sessionId);
        nameIds.entrySet().forEach(System.out::println);
    }

    static Set<String> getEsNames() {
        Set<String> names = new TreeSet<>();
        names.add("Espinoza");
        names.add("Williams");
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
        }).retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))).build();
    }
}
