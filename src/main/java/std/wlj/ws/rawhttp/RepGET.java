package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class RepGET {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places/reps";
//    private static String baseUrl = "http://place-ws-stage.dev.fsglobal.org/int-std-ws-place/places/reps";
//    private static String baseUrl = "http://familysearch.org/int-std-ws-place/places/reps";
//    private static String baseUrl = "http://place-ws-dev.dev.fsglobal.org/int-std-ws-place/places/reps";
    private static String baseUrl = "http://place-ws-dev.dev.fsglobal.org/int-std-ws-place-55/places/reps";

    private static int[] repIds = {
        1,
//        1111111,
//        1337578,
//        1337875,
//        5314385,
//        10625843
    };

    /**
     * Create a place w/ associated place-rep, then try and update the display names
     * of the place-rep
     */
    public static void main(String[] args) throws Exception {
    	HttpHelper.overrideHTTPS = true;
//    	HttpHelper.acceptType = "application/json";
//    	HttpHelper.acceptType = "application/standards-places-v2+json";
//    	HttpHelper.acceptType = "application/xml";
    	HttpHelper.acceptType = "application/standards-places-v2+xml";
//    	HttpHelper.acceptType = "text/html";
        for (int repId : repIds) {
            getRep(repId);
        }
    }

    private static void getRep(int repId) throws Exception {
        URL url = new URL(baseUrl + "/" + repId);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("LOOK-UP: " + repId);
        long then = System.nanoTime();
        RootModel model = HttpHelper.doGET(url, "metrics", "true", "noCache", "true");
        long nnow = System.nanoTime();
        System.out.println("TIME: " + (nnow-then)/1000000.0);
        System.out.println("MODEL: " + model);
    }
}
