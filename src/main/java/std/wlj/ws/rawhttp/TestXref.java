package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

import org.familysearch.standards.place.ws.model.RootModel;


/**
 * Create a new "EXT_XREF" type.
 * 
 * @author wjohnson000
 *
 */
public class TestXref {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places/xrefs/";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";

    /**
     * Run two tests ... all sources, and specific sources
     */
    public static void main(String[] args) throws Exception {
        URL url;
        RootModel inModel;
        RootModel outModel;

        inModel = new RootModel();
//        inModel.setRepIds(Arrays.asList(5, 7, 9));
        inModel.setRepIds(new ArrayList<Integer>());

        url = new URL(baseUrl + "GOOGLE.blah");
        outModel = TestUtil.doPUT(url, inModel);
        System.out.println("RM01: " + outModel);

        url = new URL(baseUrl + "GOOGLE.blah");
        outModel = TestUtil.doGET(url);
        System.out.println("RM02: " + outModel);
    }
}
