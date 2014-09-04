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
public class RepGroupGET {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places/place-rep-groups";
    private static String baseUrl = "http://place-ws-test.dev.fsglobal.org/int-std-ws-place/places/place-rep-groups";

    public static void main(String[] args) throws Exception {
        RootModel responseModel;
        Set<Integer> prgIDs = new HashSet<>();

        URL url = new URL(baseUrl);

        // Read all Place-Rep GROUPS, save the IDs
        responseModel = TestUtil.doGET(url);
        System.out.println("All Place-Rep GROUPS: \n" + responseModel.toJSON());
        List<PlaceRepGroupModel> repGroups = responseModel.getPlaceRepGroups();
        for (PlaceRepGroupModel repGroup : repGroups) {
            prgIDs.add(repGroup.getId());
        }

        for (int times=1;  times<20;  times++) {
            for (int i=17;  i<=32;  i++) {
                url = new URL(baseUrl + "/" + i);
                responseModel = TestUtil.doGET(url);
                if (prgIDs.contains(Integer.valueOf(i))) {
                    if (responseModel == null) {
                        System.out.println(">>> Not found: " + i);
                    }
                } else {
                    if (responseModel != null) {
                        System.out.println(">>> Bad found: " + i);
                    }
                }
//                System.out.println("\n----------------------------------------------------------------------------------");
//                System.out.println("Group ...\n" + (responseModel == null ? "" : responseModel.toJSON()));
            }
        }
    }
}
