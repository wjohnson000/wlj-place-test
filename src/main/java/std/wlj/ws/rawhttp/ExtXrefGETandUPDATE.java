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
public class ExtXrefGETandUPDATE {

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

        url = new URL("http://localhost:8080/std-ws-place/places/reps/7/xrefs");
        outModel = HttpHelper.doGET(url);

        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("GET ...");
        System.out.println(outModel.toJSON());
        System.out.println("---------------------------------------------------------------------------------");

        if (url != null) System.exit(0);

        inModel = new RootModel();
        inModel.setRepIds(Arrays.asList(5, 7, 9));

        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("PUT-B ...");
        System.out.println(inModel.toJSON());
        System.out.println("---------------------------------------------------------------------------------");

        url = new URL(baseUrl + "XREF_NEW.abc123");
        outModel = HttpHelper.doPUT(url, inModel);
        System.out.println("RM01: " + outModel);

        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("PUT-A ...");
        System.out.println(outModel.toJSON());
        System.out.println("---------------------------------------------------------------------------------");

        url = new URL(baseUrl + "XREF_NEW.abc123");
        outModel = HttpHelper.doGET(url);
        System.out.println("RM02: " + outModel);

        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("GET ...");
        System.out.println(outModel.toJSON());
        System.out.println("---------------------------------------------------------------------------------");

        inModel = new RootModel();
        inModel.setRepIds(Arrays.asList(7, 9, 11));

        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("PUT-B ...");
        System.out.println(inModel.toJSON());
        System.out.println("---------------------------------------------------------------------------------");

        url = new URL(baseUrl + "XREF_NEW.abc123");
        outModel = HttpHelper.doPUT(url, inModel);
        System.out.println("RM01: " + outModel);

        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("PUT-A ...");
        System.out.println(outModel.toJSON());
        System.out.println("---------------------------------------------------------------------------------");

        url = new URL(baseUrl + "XREF_NEW.abc123");
        outModel = HttpHelper.doGET(url);
        System.out.println("RM02: " + outModel);

        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("GET ...");
        System.out.println(outModel.toJSON());
        System.out.println("---------------------------------------------------------------------------------");
    }
}
