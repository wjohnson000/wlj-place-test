package std.wlj.ws.rawhttp;

import java.net.*;


/**
 * Read a bunch of place-rep group information ...
 * 
 * @author wjohnson000
 */
public class HealthGET {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/";
//    private static String baseUrl = "http://place-ws-dev.dev.fsglobal.org/int-std-ws-place/";
//    private static String baseUrl = "http://place-ws-test.dev.fsglobal.org/int-std-ws-place/";
//    private static String baseUrl = "http://place-ws-stage.dev.fsglobal.org/int-std-ws-place/";
//    private static String baseUrl = "http://www.familysearch.org/int-std-ws-place/";
    private static String baseUrl = "https://familysearch.org/int-std-ws-place/";


    public static void main(String[] args) throws Exception {
        URL url = new URL(baseUrl + "health-check");
        TestUtil.doGET(url);

        url = new URL(baseUrl + "healthcheck/heartbeat");
        TestUtil.doGET(url);

        url = new URL(baseUrl + "healthcheck/heartbeatXXX");
        TestUtil.doGET(url);
    }
}
