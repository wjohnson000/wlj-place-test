package std.wlj.ws.rawhttp;

import java.net.*;
import org.familysearch.standards.place.ws.model.RootModel;


/**
 * Read a bunch of place-rep group information ...
 * 
 * @author wjohnson000
 */
public class RepGroupGET {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places/place-rep-groups";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";


    public static void main(String[] args) throws Exception {
        RootModel responseModel;

        URL url = new URL(baseUrl);

        // Read all Place-Rep GROUPS
        responseModel = TestUtil.doGET(url);
        System.out.println("All Place-Rep GROUPS: \n" + responseModel.toJSON());

        for (int i=16;  i<=19;  i++) {
            url = new URL(baseUrl + "/" + i);
            responseModel = TestUtil.doGET(url);
            System.out.println("\n----------------------------------------------------------------------------------");
            System.out.println("Group ...\n" + responseModel.toJSON());
            System.out.println("Group ...\n" + responseModel.toJSON());
        }
    }
}
