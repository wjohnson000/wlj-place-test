package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestSearch {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";
    private static String baseUrl = "http://place-solr.dev.fsglobal.org/solr/places";


    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
//        readUSA();
        searchProvo();
        healthCheck();
        searchKnighton();
    }

    private static void readUSA() throws Exception {
        URL url = new URL(baseUrl + "/1");
        RootModel model = TestUtil.doGET(url);
        System.out.println("RM: " + model);
    }

    private static void searchProvo() throws Exception {
        URL url = new URL(baseUrl + "/request?id:*");
        RootModel model = TestUtil.doGET(url);
        System.out.println("RM: " + model);
    }

    private static void searchKnighton() throws Exception {
        URL url = new URL(baseUrl + "/request?text=Knighton-on-Teme");
        RootModel model = TestUtil.doGET(url);
        System.out.println("RM: " + model);
    }

    private static void healthCheck() throws Exception {
        URL url = new URL(baseUrl);
        RootModel model = TestUtil.doGET(url);
        System.out.println("RM: " + model);
    }
}
