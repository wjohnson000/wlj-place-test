package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.familysearch.standards.place.ws.model.PlaceRepGroupModel;
import org.familysearch.standards.place.ws.model.RootModel;


/**
 * Read a bunch of place-rep group information ...
 * 
 * @author wjohnson000
 */
public class TypeGroupGET {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places/type-groups";
    private static String baseUrl = "http://54.204.45.169:8080/std-ws-place/places/type-groups";


    public static void main(String[] args) throws Exception {
        RootModel responseModel;
        URL url = new URL(baseUrl);

        // Read all Place-Rep GROUPS, save the IDs
        responseModel = HttpHelper.doGET(url);
        System.out.println("All Place-Rep GROUPS: \n" + responseModel.toJSON());
    }
}
