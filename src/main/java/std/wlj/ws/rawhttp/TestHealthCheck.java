package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestHealthCheck {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";

    /**
     * Run two tests ... all sources, and specific sources
     */
    public static void main(String[] args) throws Exception {
        URL url;
        RootModel model;

        url = new URL(baseUrl);
        model = TestUtil.doGET(url);
        System.out.println("RM: " + model);

        model = TestUtil.doHEAD(url);
        System.out.println("RM: " + model);

        model = TestUtil.doOPTIONS(url);
        System.out.println("RM: " + model);

        model = TestUtil.doDELETE(url);
        System.out.println("RM: " + model);
    }
}
