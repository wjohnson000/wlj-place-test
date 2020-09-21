package std.wlj.binding;

import org.familysearch.paas.binding.register.Environment;
import org.familysearch.paas.binding.register.Region;
import org.familysearch.paas.binding.register.RouteType;
import org.familysearch.paas.binding.register.ServiceLocator;
import org.familysearch.paas.binding.register.ServiceLocatorConfig;
import org.familysearch.paas.binding.register.Site;

public class TestBRC {

    public static void main(String...args) {
//        System.setProperty("environment", "local");

        // PROD service URLs
//        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.PROD, Site.PROD, Region.US_EAST_1);

        // BETA service URLs
        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.TEST, Site.BETA, Region.US_EAST_1);

        // INTEG service URLs
//        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.DEV, Site.INTEG, Region.US_EAST_1);

        // DEV service URLs
//        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.DEV, Site.DEV, Region.US_EAST_1);

        ServiceLocator locator = new ServiceLocator(config);

        testServiceLocator(locator, "ws.place.standards.service");
        testServiceLocator(locator, "ws.place-db.standards.service");
        testServiceLocator(locator, "ws.solr.standards.service");

//        testServiceLocator(locator, "ws-55.solr.standards.service");
//        testServiceLocator(locator, "ws-55.solr-repeater.standards.service");
//        testServiceLocator(locator, "ws-55.solr-repeater.standards.service");
//
//        testServiceLocator(locator, "cis-public-api.cis.ident.service");
//        testServiceLocator(locator, "cas-public-api.cas.ident.service");
//
//        testServiceLocator(locator, "ws-lts-a.place.standards.service");
        testServiceLocator(locator, "core.homelands.service");

        System.exit(0);
    }

    static void testServiceLocator(ServiceLocator locator, String serviceName) {
        String serviceUrl = null;
        String serviceUrlPublic = null;

        try {
            serviceUrl = locator.locateServiceUrl(serviceName);
        } catch(Exception ex) {
            serviceUrl = "Exception: " + ex.getMessage();
        }

        try {
            serviceUrlPublic = locator.locateServiceUrl(serviceName, RouteType.PUBLIC);
        } catch(Exception ex) {
            serviceUrlPublic = "Exception: " + ex.getMessage();
        }

        System.out.println(">>> Locate service '" + serviceName + "'");
        System.out.println("  RES: " + serviceUrl);
        System.out.println("     : " + serviceUrlPublic);
        System.out.println();
    }
}
