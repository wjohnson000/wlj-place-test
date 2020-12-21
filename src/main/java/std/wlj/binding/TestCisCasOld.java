package std.wlj.binding;

import java.util.List;
import java.util.concurrent.Future;

import org.familysearch.identity.api.BlockingIdentityOperations;
import org.familysearch.identity.api.IdentityContext;
import org.familysearch.identity.api.IdentityService;
import org.familysearch.identity.api.PermissionSet;
import org.familysearch.identity.api.ThreadLocalIdentityContext;
import org.familysearch.identity.api.impl.IdentityContextImpl;
import org.familysearch.identity.api.impl.IdentityServiceImpl;
import org.familysearch.identity.api.impl.IdentityTimerImpl;
import org.familysearch.paas.binding.register.api.Environment;
import org.familysearch.paas.binding.register.api.LocationResult;
import org.familysearch.paas.binding.register.api.Region;
import org.familysearch.paas.binding.register.api.ServiceLocator;
import org.familysearch.paas.binding.register.api.ServiceLocatorConfig;
import org.familysearch.paas.binding.register.api.Site;
import org.familysearch.ws.identity.v4.schema.BaseUser;
import org.familysearch.ws.identity.v4.schema.Identity;

public class TestCisCasOld {

    static String cisUrl = "http://cis.app.prod.id.fsglobal.net/cis-public-api";
    static String casUrl = "http://cas.app.prod.id.fsglobal.net/cas-public-api";
    
    static ServiceLocatorConfig config = new ServiceLocatorConfig(Environment.PROD, Site.PROD, Region.US_EAST_1);
    static ServiceLocator locator = new ServiceLocator(config);

    static IdentityService identityService;

    public static void main(String...args) {
        String sessionId = "524fa43e-592f-4eec-bc6b-9ae915e91dc4-production";

        identityService = new IdentityServiceImpl(getCisUri(), getCasUri());

        final IdentityTimerImpl timer = new IdentityTimerImpl();
        Future<Identity> identityFuture = identityService.readIdentity(sessionId, true, "place");
        Future<PermissionSet> authorizedFuture = null;
        IdentityContext context = new IdentityContextImpl(sessionId, identityFuture, authorizedFuture, timer, identityService, true, "place");
        ThreadLocalIdentityContext.set(context);

        IdentityContext ictx = ThreadLocalIdentityContext.get();
        BlockingIdentityOperations bio = ictx.getBlockingOperations();
        Identity identity = bio.getActiveIdentity();
        List<BaseUser> users = identity.getUsers();
        if (! users.isEmpty()) {
            BaseUser user = users.get(0);
            System.out.println("USER: " + user + " --> " + user.getId() + " . " + user.getDisplayName());
        }
    }

    static String getCisUri() {
        LocationResult locResult = locator.locateService("cis-public-api.cis.ident.service");
        System.out.println("CIS: " + (locResult == null ? cisUrl : locResult.getUrl()));
        return (locResult == null) ? cisUrl : locResult.getUrl();
    }

    static String getCasUri() {
        LocationResult locResult = locator.locateService("cas-public-api.cas.ident.service");
        System.out.println("CAS: " + (locResult == null ? casUrl : locResult.getUrl()));
        return (locResult == null) ? casUrl : locResult.getUrl();
    }
}
