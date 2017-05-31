package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestGetRep {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places/reps";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places/reps";
//    private static String baseUrl = "http://54.221.37.64:8080/std-ws-place/places/reps";
    private static String baseUrl =  "http://place-ws-dev.dev.fsglobal.org/int-std-ws-place-55/places";
//    private static String baseUrl = "http://place-ws-dev.dev.fsglobal.org/int-std-ws-place-55/places/reps";


    /**
     * Run two tests ... a GET of a specific place-rep, and a search
     */
    public static void main(String[] args) throws Exception {
    	HttpHelper.overrideHTTPS = true;

        getRep(393288);
        getRep(1);
    }

    private static void getRep(int repId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId);
        RootModel model = HttpHelper.doGET(url);
        System.out.println("RM: " + model);
    }
}
