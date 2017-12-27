package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class TestGetRep {

    /** Base URL of the application */
    private static String baseUrl = "http://www.familysearch.org/int-std-ws-place/places";
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places/reps";
//    private static String baseUrl =  "http://place-ws-dev.dev.fsglobal.org/int-std-ws-place-55/places";
//    private static String baseUrl = "http://place-ws-dev.dev.fsglobal.org/int-std-ws-place-55/places";


    /**
     * Run two tests ... a GET of a specific place-rep, and a search
     */
    public static void main(String[] args) throws Exception {
    	HttpHelper.overrideHTTPS = true;
//    	HttpHelper.acceptType = "application/xml";
    	HttpHelper.acceptType = "application/standards-places-v2+xml";

        getRep(10734614);
//        getRep(1);
    }

    private static void getRep(int repId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId);
        RootModel model = HttpHelper.doGET(url);
        System.out.println("RO)T: " + model);
        if (model != null) {
            System.out.println(" REP: " + model.getPlaceRepresentation());
            System.out.println(" TYP: " + model.getPlaceRepresentation().getType());
            System.out.println(" PUB: " + model.getPlaceRepresentation().getType().isPublished());
        }
    }
}
