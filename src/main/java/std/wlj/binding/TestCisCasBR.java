/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.binding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.sun.jersey.api.client.Client;

import org.familysearch.identity.api.IdentityService;
import org.familysearch.identity.api.impl.IdentityServiceImpl;
import org.familysearch.identity.api.impl.IdentityServiceWithBindingRegisterImpl;
import org.familysearch.identity.util.JerseyClientHelper;
import org.familysearch.paas.binding.register.DefaultServiceLocationResolver;
import org.familysearch.paas.binding.register.ServiceLocator;
import org.familysearch.paas.binding.register.ServiceLocatorConfig;
import org.familysearch.ws.identity.v4.schema.Identity;

import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * @author wjohnson000
 *
 */
@SuppressWarnings("deprecation")
public class TestCisCasBR {

    static final String CIS_SERVICE = "cis-public-api.cis.ident.service";
    static final String CAS_SERVICE = "cas-public-api.cas.ident.service";
    static final String HCS_SERVICE = "core.homelands.service";

    private static final ExecutorService DEFAULT_EXECUTER = null;
    private static final String          CASC_SERVER_NOT_NEEDED = null;

    private static final String          SESSION_ID = "f321b287-4a20-4f8e-97c8-3a987863eb3f-integ";

    public static void main(String... args) throws Exception {
        System.setProperty("FS_SITE", "integ");
        System.setProperty("FS_REGION", "us-east-1");
        System.setProperty("REGION", "us-east-1");
 
        testOld();
        testNew();
        testNewServiceLocator();

        System.exit(0);
    }

    static void testOld() {
        String cisUri = "http://cis-public-api.cis.ident.service.integ.us-east-1.dev.fslocal.org";
        String casUri = "http://cas-public-api.cas.ident.service.integ.us-east-1.dev.fslocal.org";

        System.out.println("OLD STUFF >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        IdentityService identityService = new IdentityServiceImpl(cisUri, casUri);
        try {
            Future<Identity> identFF = identityService.readIdentity(SESSION_ID);
            System.out.println("IDFF: " + identFF);
            Identity ident = identFF.get();
            System.out.println("ID: " + ident);
        } catch(Exception ex) {
            System.out.println("Oops: " + ex.getMessage());
        }
    }

    static void testNew() {
        System.out.println("\n\nNEW STUFF >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        String cisUri = "http://cis-public-api.cis.ident.service.integ.us-east-1.dev.fslocal.org";
        String casUri = "http://cas-public-api.cas.ident.service.integ.us-east-1.dev.fslocal.org";

        ServiceLocator locator = new ServiceLocator(new ServiceLocatorConfig());
        DefaultServiceLocationResolver serviceResolver = new DefaultServiceLocationResolver(locator);
        DefaultClientConfig jerseyConfig = JerseyClientHelper.createDefaultClientConfig();
        Client jerseyClient = Client.create(jerseyConfig);

        IdentityService identityService = new IdentityServiceWithBindingRegisterImpl(serviceResolver, cisUri, casUri,
                                CASC_SERVER_NOT_NEEDED, DEFAULT_EXECUTER, jerseyClient);  //new com.sun.jersey.api.client.Client());
        try {
            Future<Identity> identFF = identityService.readIdentity(SESSION_ID);
            System.out.println("IDFF: " + identFF);
            Identity ident = identFF.get();
            System.out.println("ID: " + ident);
        } catch(Exception ex) {
            System.out.println("Oops: " + ex.getMessage());
        }
    }

    static void testNewServiceLocator() {
        System.out.println("\n\nNEW STUFF II >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        ServiceLocator locator = new ServiceLocator(new ServiceLocatorConfig());
        DefaultServiceLocationResolver serviceResolver = new DefaultServiceLocationResolver(locator);
        try {
            String cisUrl = serviceResolver.resolve("locate://" + CIS_SERVICE);
            String casUrl = serviceResolver.resolve("locate://" + CAS_SERVICE);
            System.out.println(String.format("ClientConfig -- resolve CIS='%s' to '%s'", CIS_SERVICE, cisUrl));
            System.out.println(String.format("ClientConfig -- resolve CAS='%s' to '%s'", CAS_SERVICE, casUrl));
        } catch(Exception ex) {
            System.out.println(String.format("ClientConfig -- unable to resolve CIS='%' or CAS='%', error=%", CIS_SERVICE, CAS_SERVICE, ex.getMessage()));
        }

        DefaultClientConfig jerseyConfig = JerseyClientHelper.createDefaultClientConfig();
        Client jerseyClient = Client.create(jerseyConfig);

        IdentityService identityService = new IdentityServiceWithBindingRegisterImpl(serviceResolver, CIS_SERVICE, CAS_SERVICE,
                                CASC_SERVER_NOT_NEEDED, DEFAULT_EXECUTER, jerseyClient); //new com.sun.jersey.api.client.Client());
        try {
            Future<Identity> identFF = identityService.readIdentity(SESSION_ID);
            System.out.println("IDFF: " + identFF);
            Identity ident = identFF.get();
            System.out.println("ID: " + ident);
        } catch(Exception ex) {
            System.out.println("Oops: " + ex.getMessage());
        }
    }
}
