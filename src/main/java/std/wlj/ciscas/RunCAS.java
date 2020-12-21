/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.ciscas;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.familysearch.paas.binding.register.Environment;
import org.familysearch.paas.binding.register.Region;
import org.familysearch.paas.binding.register.ServiceLocator;
import org.familysearch.paas.binding.register.ServiceLocatorConfig;
import org.familysearch.paas.binding.register.Site;

import std.wlj.ws.rawhttp.HttpClientX;

/**
 * @author wjohnson000
 *
 */
public class RunCAS {

    static ServiceLocatorConfig configProd = new ServiceLocatorConfig(Environment.PROD, Site.PROD, Region.US_EAST_1);
    static ServiceLocatorConfig configBeta = new ServiceLocatorConfig(Environment.TEST, Site.BETA, Region.US_EAST_1);
    static ServiceLocator locator = new ServiceLocator(configProd);

    static String mediaType = "application/cas-v2+json";

    public static void main(String...args) {
        String sessionId = JOptionPane.showInputDialog(null, "SessionID:");
        if (sessionId == null) {
            System.exit(1);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + sessionId);
        headers.put("Accept", mediaType);

        String userUrl = getCasUri() + "/authorizations/contexts/homelands-core/permissions/VISIBILITY_MEMBER;VISIBILITY_ADMIN;DUMMY?sessionId=" + sessionId;
        String json = HttpClientX.doGetJSON(userUrl, headers);
        System.out.println(json);

        System.exit(0);
    }

    static String getCisUri() {
        String locResult = locator.locateServiceUrl("cis-public-api.cis.ident.service");
        System.out.println("CIS: " + locResult);
        return locResult;
    }

    static String getCasUri() {
        String locResult = locator.locateServiceUrl("cas-public-api.cas.ident.service");
        System.out.println("CAS: " + locResult);
        return locResult;
    }
}
