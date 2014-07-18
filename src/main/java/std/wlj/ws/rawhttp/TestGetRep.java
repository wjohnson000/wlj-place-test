package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestGetRep {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places/reps";
    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places/reps";
//    private static String baseUrl = "http://54.221.37.64:8080/std-ws-place/places/reps";


    /**
     * Run two tests ... a GET of a specific place-rep, and a search
     */
    public static void main(String[] args) throws Exception {
        getRep(3631809);
        getRep(3631810);
    }

    private static void getRep(int repId) throws Exception {
        URL url = new URL(baseUrl + "/" + repId);
        RootModel model = TestUtil.doGET(url);
        System.out.println("RM: " + model);
    }
}
