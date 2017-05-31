package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class RepGETwithCITNHttpClient {

    /** Base URL of the application */
  private static String baseUrl = "http://localhost:8080/std-ws-place-55/places/reps";
//    private static String baseUrl = "http://place-ws-stage.dev.fsglobal.org/int-std-ws-place/places/reps";
//    private static String baseUrl = "http://familysearch.org/int-std-ws-place/places/reps";
//    private static String baseUrl = "http://place-ws-stage.dev.fsglobal.org/int-std-ws-place/places/reps";
//    private static String baseUrl = "https://place-ws-dev.dev.fsglobal.org/int-std-ws-place-55/places/reps";

    private static int[] repIds = {
//        1,
        11,
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
//    	HttpClientHelper.acceptType = "application/json";
//    	HttpClientHelper.acceptType = "application/standards-places-v2+json";
    	HttpClientHelper.acceptType = "application/xml";
//    	HttpClientHelper.acceptType = "application/standards-places-v2+xml";
//    	HttpClientHelper.acceptType = "text/html";
        for (int repId : repIds) {
            getRep(repId);
        }
    }

    private static void getRep(int repId) throws Exception {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        URL url = new URL(baseUrl + "/" + repId);
        RootModel model = HttpClientHelper.doGET(url, "metrics", "true", "noCache", "true");
        if (model != null) {
        	System.out.println(model.toXML());
        }

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        url = new URL(baseUrl + "/" + repId + "/citations");
        model = HttpClientHelper.doGET(url);
        if (model != null) {
        	System.out.println(model.toXML());
        }
    }
}
