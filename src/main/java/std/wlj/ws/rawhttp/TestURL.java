package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestURL {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places/reps/9522056";
    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places/reps/444622";

    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        RootModel model = TestUtil.doGET(new URL(baseUrl));
        System.out.println("RM: " + model);
    }
}
