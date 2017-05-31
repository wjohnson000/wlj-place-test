package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestTypeAll {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";


    /**
     * Run two tests ... a GET of a all types, and a GET of a specific type
     */
    public static void main(String[] args) throws Exception {
        readTypes("/place-types", 1, 10);  // ID range = 1 - 993
        readTypes("/name-types", 450, 455);  // ID range = 1025 - 1049
        readTypes("/attribute-types", 410, 415);  // ID range = 994 - 1024
        readTypes("/citation-types", 1, 0);  // ID range = 1050 - 1055 ... these may be dummy values

        System.out.println("Bad URLS ...");
    	for (String badUrl : HttpHelper.getBadUrls()) {
    		System.out.println("  " + badUrl);
    	}
    }

    private static void readTypes(String subUrl, int firstId, int lastId) throws Exception {
        URL url = new URL(baseUrl + subUrl);
        RootModel model = HttpHelper.doGET(url);
//        System.out.println("RM: " + model);

        for (int i=firstId;  i<=lastId; i++) {
            url = new URL(baseUrl + subUrl + "/" + i);
            model = HttpHelper.doGET(url);
            System.out.println("RM: " + model);
    	}
    }
}
