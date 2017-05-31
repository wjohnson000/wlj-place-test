package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestAppEndpoint {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place";

    /**
     * Run two tests ... all sources, and specific sources
     */
    public static void main(String[] args) throws Exception {
        URL url = new URL(baseUrl);
        RootModel model = HttpHelper.doGET(url);
        System.out.println("RM: " + model);

        model = HttpHelper.doPOST(url, null);
        System.out.println("RM: " + model);
}
}
