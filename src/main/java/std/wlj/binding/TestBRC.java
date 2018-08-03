package std.wlj.binding;

import org.familysearch.paas.binding.register.Environment;
import org.familysearch.paas.binding.register.Region;
import org.familysearch.paas.binding.register.ServiceLocator;
import org.familysearch.paas.binding.register.ServiceLocatorConfig;
import org.familysearch.paas.binding.register.Site;

public class TestBRC {

    public static void main(String...args) {
        System.setProperty("environment", "local");
        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.PROD, Site.PROD, Region.US_EAST_1);
        ServiceLocator locator = new ServiceLocator(config);

//        testServiceLocator(locator, "ws-55.solr-repeater.std");
//        testServiceLocator(locator, "ws-55.solr-repeater.std.cmn");
//        testServiceLocator(locator, "ws.place.std.cmn");
//        testServiceLocator(locator, "ws-55-dbload.place.std.cmn");
//        testServiceLocator(locator, "ws.analysis.std.cmn");
        testServiceLocator(locator, "cis-public-api.cis.ident.service");
        testServiceLocator(locator, "cis-admin-api.cis.ident.service");
        testServiceLocator(locator, "cas-public-api.cas.ident.service");
        testServiceLocator(locator, "ws.analysis.std.cmn");
        System.exit(0);
    }

    static void testServiceLocator(ServiceLocator locator, String serviceName) {
        String serviceUrl = null;
        try {
            serviceUrl = locator.locateServiceUrl(serviceName);
        } catch(Exception ex) {
            serviceUrl = "Exception: " + ex.getMessage();
        }

        System.out.println(">>> Locate service '" + serviceName + "'");
        System.out.println("  RES: " + serviceUrl);
        System.out.println();
    }
}
