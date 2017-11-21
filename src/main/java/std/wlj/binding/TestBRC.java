package std.wlj.binding;

import org.familysearch.paas.binding.register.api.Environment;
import org.familysearch.paas.binding.register.api.LocationResult;
import org.familysearch.paas.binding.register.api.Region;
import org.familysearch.paas.binding.register.api.ServiceLocator;
import org.familysearch.paas.binding.register.api.ServiceLocatorConfig;
import org.familysearch.paas.binding.register.api.Site;

public class TestBRC {

    public static void main(String...args) {
        System.setProperty("environment", "local");
        ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.PROD, Site.PROD, Region.US_EAST_1);
        ServiceLocator locator = new ServiceLocator(config);
        testServiceLocator(locator, "ws.place.std.cmn");
        testServiceLocator(locator, "ws.analysis.std.cmn");
        testServiceLocator(locator, "cis-public-api.cis.ident.service");
        testServiceLocator(locator, "cis-admin-api.cis.ident.service");
        testServiceLocator(locator, "cas-public-api.cas.ident.service");
        System.exit(0);
    }

    static void testServiceLocator(ServiceLocator locator, String serviceName) {
        System.out.println(">>> Locate service '" + serviceName + "'");
        LocationResult resultPri = locator.locateService(serviceName);
        System.out.println("RES: " + resultPri);
        System.out.println();
    }
}
