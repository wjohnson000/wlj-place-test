package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class RepGET {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places/reps";
//    private static String baseUrl = "http://place-ws-stage.dev.fsglobal.org/int-std-ws-place/places/reps";
    private static String baseUrl = "http://familysearch.org/int-std-ws-place/places/reps";

    private static int[] repIds = {
        1111111,
        1337578,
        1337875,
        5314385
    };

    /**
     * Create a place w/ associated place-rep, then try and update the display names
     * of the place-rep
     */
    public static void main(String[] args) throws Exception {
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
        RootModel model = TestUtil.doGET(url, "metrics", "true", "noCache", "true");
        long nnow = System.nanoTime();
        System.out.println("TIME: " + (nnow-then)/1000000.0);
        System.out.println("MODEL: " + model);

    }
}
