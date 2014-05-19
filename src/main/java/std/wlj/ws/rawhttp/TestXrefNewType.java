package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.ws.model.LocalizedNameDescModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;


public class TestXrefNewType {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places/xref-types/";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";

    /**
     * Run two tests ... all sources, and specific sources
     */
    public static void main(String[] args) throws Exception {
        URL url;
        RootModel outModel;

        url = new URL(baseUrl);
        outModel = TestUtil.doGET(url);
        System.out.println("RM01: " + outModel);

        LocalizedNameDescModel nameAndDesc = new LocalizedNameDescModel();
        nameAndDesc.setLocale("en");
        nameAndDesc.setName("US Gov't -- NSA");
        nameAndDesc.setDescription("We can run, but we can't hide!!");

        List<LocalizedNameDescModel> nameAndDescList = new ArrayList<>();
        nameAndDescList.add(nameAndDesc);

        TypeModel theType = new TypeModel();
        theType.setCode("NSA");
        theType.setIsPublished(true);
        theType.setName(nameAndDescList);

        RootModel inModel = new RootModel();
        inModel.setType(theType);

        outModel = TestUtil.doPOST(url, inModel);
        System.out.println("RM01: " + outModel);
    }
}
