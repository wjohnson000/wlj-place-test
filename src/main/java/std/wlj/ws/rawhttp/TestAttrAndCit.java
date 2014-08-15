package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestAttrAndCit {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";
    private static String awsUrl = "http://place-ws-aws.dev.fsglobal.org/std-ws-place/places";

    /**
     * Run two tests ... all sources, and specific sources
     */
    public static void main(String[] args) throws Exception {
        readAttributes();
        readCitations();

        System.out.println("Bad URLS ...");
    	for (String badUrl : TestUtil.getBadUrls()) {
    		System.out.println("  " + badUrl);
    	}
    }

    private static void readAttributes() throws Exception {
        URL url = new URL(awsUrl + "/reps/1/attributes/");
        RootModel model = TestUtil.doGET(url);
        System.out.println("RM: " + model.toJSON());

        for (int i=1;  i<4;  i++) {
            url = new URL(awsUrl + "/reps/1/attributes/" + i);
            model = TestUtil.doGET(url);
            System.out.println("RM: " + (model==null ? "" : model.toJSON()));
    	}
    }

    private static void readCitations() throws Exception {
//        URL url = new URL(awsUrl + "/reps/1/citations/");
//        RootModel model = TestUtil.doGET(url);
//        System.out.println("RM: " + model);
//
//        for (int i=1;  i<4;  i++) {
//            url = new URL(awsUrl + "/reps/1/citations/" + i);
//            model = TestUtil.doGET(url);
//            System.out.println("RM: " + model);
//        }
    }
}
