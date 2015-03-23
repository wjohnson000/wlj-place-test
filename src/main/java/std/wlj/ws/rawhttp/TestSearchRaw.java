package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestSearchRaw {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
    private static String baseUrl = "http://place-ws-stage.dev.fsglobal.org/int-std-ws-place/places";
//    private static String baseUrl = "http://familysearch.org/int-std-ws-place/places";

    private static String[] interps = {
        "England, United Kingdom",
//        "徳島県徳島市北常三島町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町町"
    };


    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        for (String interp : interps) {
            doSearch(interp);
        }
    }

    private static void doSearch(String interp) throws Exception {
        URL url = new URL(baseUrl + "/request");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("TEST: " + interp);
        long then = System.nanoTime();
        RootModel model = TestUtil.doGET(url, "text", interp, "metrics", "true");
        long nnow = System.nanoTime();
        System.out.println("TIME: " + (nnow-then)/1000000.0);
        System.out.println("MODEL: " + model);
    }
}
