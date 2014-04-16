package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestSources {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";


    /**
     * Run two tests ... all sources, and specific sources
     */
    public static void main(String[] args) throws Exception {
        readSources();

        System.out.println("Bad URLS ...");
    	for (String badUrl : TestUtil.getBadUrls()) {
    		System.out.println("  " + badUrl);
    	}
    }

    private static void readSources() throws Exception {
        URL url = new URL(baseUrl + "/sources?this=that&what=whatever");
        RootModel model = TestUtil.doGET(url);
        System.out.println("RM: " + model);

//        url = new URL(baseUrl + "/sources/");
//        model = getResults(url);
//        System.out.println("RM: " + model);
//
        for (int i=1;  i<250;  i+=250) {
            url = new URL(baseUrl + "/sources/" + i);
            model = TestUtil.doGET(url);
            System.out.println("RM: " + model);
    	}
    }
}
