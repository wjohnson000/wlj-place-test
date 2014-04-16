package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestAttributeNew {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";


    /**
     * Run two tests ... all sources, and specific sources
     */
    public static void main(String[] args) throws Exception {
        readAttributes();
        readCitations();
    }

    private static void readAttributes() throws Exception {
        URL url;
        RootModel model;
        long time01 = 0;
        long time02 = 0;

        for (int i=0;  i<3;  i++) {
            long nnow = System.nanoTime();
            url = new URL(baseUrl + "/attribute-types/1010");
            model = TestUtil.doGET(url);
            time01 += (System.nanoTime() - nnow);
            System.out.println("RM: " + model);

            nnow = System.nanoTime();
            url = new URL(baseUrl + "/attribute-types/1111" + i);
            model = TestUtil.doGET(url);
            time02 += (System.nanoTime() - nnow);
            System.out.println("RM: " + model);
        }

        System.out.println("Time01: " + (time01 / 1000000.0));
        System.out.println("Time02: " + (time02 / 1000000.0));
    }

    private static void readCitations() throws Exception {
//        URL url = new URL(baseUrl + "/citation-types/1055");
//        RootModel model = TestUtil.getResults(url);
//        System.out.println("RM: " + model);
//
//        for (int i=11111;  i<11114;  i++) {
//            url = new URL(baseUrl + "/citation-types/" + i);
//            model = TestUtil.getResults(url);
//            System.out.println("RM: " + model);
//        }
    }
}
