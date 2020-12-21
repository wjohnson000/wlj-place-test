/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.ws.httpclient;

import java.util.Collections;

/**
 * @author wjohnson000
 *
 */
public class TestClients {

    static String NAME_URL = "http://core.homelands.service.integ.us-east-1.dev.fslocal.org/name?text=Masse&type=LAST&format=html";

    public static void main(String... args) throws Exception {
        WebResponse response;

        System.out.println(">>>>> RAW JAVA >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        JavaClient javaClient = JavaClient.getDefault("application/json", Collections.emptyMap());
        response = javaClient.doGet(NAME_URL);
        System.out.println(">> RC: " + response.getStatus());
        response.getHeaders().entrySet().forEach(hh -> System.out.println(">> HH: " + hh));
        System.out.println(">> BD: " + response.getBody());

        System.out.println("\n\n");
        System.out.println(">>>>> APACHE CLIENT >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        ApacheClient apacheClient = ApacheClient.getDefault("application/json", Collections.emptyMap());
        response = apacheClient.doGet(NAME_URL);
        System.out.println(">> RC: " + response.getStatus());
        response.getHeaders().entrySet().forEach(hh -> System.out.println(">> HH: " + hh));
        System.out.println(">> BD: " + response.getBody());

        System.out.println("\n\n");
        System.out.println(">>>>> APACHE CLIENT POOLED >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        ApacheClientPooled apacheClientPooled = ApacheClientPooled.getDefault("application/json", Collections.emptyMap());
        response = apacheClientPooled.doGet(NAME_URL);
        System.out.println(">> RC: " + response.getStatus());
        response.getHeaders().entrySet().forEach(hh -> System.out.println(">> HH: " + hh));
        System.out.println(">> BD: " + response.getBody());

        System.out.println("\n\n");
        System.out.println(">>>>> SPRING WEB CLIENT >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        SpringWebClient springClient = SpringWebClient.getDefault("application/json", Collections.singletonMap("Accept-Language", "en"));
        response = springClient.doGet(NAME_URL);
        System.out.println(">> RC: " + response.getStatus());
        response.getHeaders().entrySet().forEach(hh -> System.out.println(">> HH: " + hh));
        System.out.println(">> BD: " + response.getBody());
    }
}
