package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestGetChildren {

    /** Base URL of the application */
//    private static String theUrl = "http://localhost:8080/std-ws-place/places/reps/9522056";
    private static String theUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places/reps/387831?children=true&pubonly=false";


    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        RootModel model = TestUtil.doGET(new URL(theUrl));
        System.out.println("RM: " + model);
    }
}
