package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestPlaceType {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
    private static String baseUrl = "http://place-ws-test.dev.fsglobal.org/int-std-ws-place/places";

    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        readPlaceTypes();
//        readPlaceTypeGroups();

//        System.out.println("Bad URLS ...");
//    	for (String badUrl : TestUtil.getBadUrls()) {
//    		System.out.println("  " + badUrl);
//    	}
    }

    private static void readPlaceTypes() throws Exception {
        URL url = new URL(baseUrl + "/place-types");
        RootModel model = TestUtil.doGET(url);
        System.out.println("RM: " + model);

//        for (int i=1;  i<1000;  i+=25) {
//            url = new URL(awsUrl + "/place-types/" + i);
//            model = TestUtil.doGET(url);
//            System.out.println("RM: " + model);
//    	}
    }

    private static void readPlaceTypeGroups() throws Exception {
        URL url = new URL(baseUrl + "/type-groups");
        RootModel model = TestUtil.doGET(url);
        System.out.println("RM: " + model);

        for (int i=1;  i<700;  i+=25) {
            url = new URL(baseUrl + "/type-groups/" + i);
            model = TestUtil.doGET(url);
            System.out.println("RM: " + model);
    	}
    }
}
