/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.lib;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

import org.familysearch.homelands.lib.common.web.client.PlaceServiceClient;
import org.familysearch.homelands.lib.common.web.client.WebClientWrapper;
import org.familysearch.homelands.lib.common.web.client.WebResponse;
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
public class TestPlaceServiceClient {

    public static void main(String...args) {
//      System.setProperty("environment", "local");

        // PROD service URLs
//      ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.PROD, Site.PROD, Region.US_EAST_1);

        // BETA service URLs
        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.TEST, Site.BETA, Region.US_EAST_1);

        // INTEG service URLs
//      ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.DEV, Site.INTEG, Region.US_EAST_1);

        // DEV service URLs
//      ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.DEV, Site.DEV, Region.US_EAST_1);

        ServiceLocator locator = new ServiceLocator(config);
        WebClientWrapper wcWrapper = new WebClientWrapper(webClient());
        PlaceServiceClient psClient = new PlaceServiceClient(locator, "ws.place.standards.service", null, wcWrapper);
        WebResponse<String> webResp = psClient.findPlaceRep("221");
        System.out.println("CODE: " + webResp.getStatus());
        System.out.println("CODE: " + webResp.getBody());
    }

    static WebClient webClient() {
        return WebClient.builder().filter((request, next) -> next.exchange(request).doOnNext(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                Map<String, String> respHeaders = clientResponse.headers().asHttpHeaders().entrySet().stream()
                        .collect(Collectors.toMap(ee -> ee.getKey(), ee -> ee.getValue().stream().collect(Collectors.joining(", "))));

                System.out.println("5XX HTTP Status. http_status=" + clientResponse.statusCode() + " warning_header={}" + respHeaders.get(HttpHeaders.WARNING));
                throw new RuntimeException("http call failed with http_status=" + clientResponse.statusCode().value());
            }
        }).retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)))).build();
    }
}
