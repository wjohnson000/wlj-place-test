package std.wlj.ws.rawhttp;

import java.net.*;

import org.familysearch.standards.place.ws.model.RootModel;


public class DeleteTest {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
    private static String awsUrl = "http://place-ws-aws.dev.fsglobal.org/std-ws-place/places";

    /**
     * Get attributes, create an attribute, get attributes, update an attribute,
     * get the attributes again.
     */
    public static void main(String[] args) throws Exception {
        int oldRepId = 8028115;
        int newRepId = 8028116;

        readPlaceRep(oldRepId);
        readPlaceRep(newRepId);
        deletePlaceRep(oldRepId, newRepId);
        readPlaceRep(oldRepId);
        readPlaceRep(newRepId);
    }

    private static void readPlaceRep(int repId) throws Exception {
        URL url = new URL(awsUrl + "/reps/" + repId);
        RootModel model = TestUtil.doGET(url);
        System.out.println("READ place-rep: " + model);
    }

    private static void deletePlaceRep(int oldRepId, int newRepId) throws Exception {
        URL url = new URL(awsUrl + "/reps/" + oldRepId + "?newRepId=" + newRepId);
        RootModel model =TestUtil.doDELETE(url);
        System.out.println("DELETE place-rep: " + model);
    }
}
