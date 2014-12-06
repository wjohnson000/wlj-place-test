package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.ws.model.LocalizedNameDescModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;


public class NameTypeGETandUPDATE {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places/name-types/";
//    private static String baseUrl = "http://place-ws-test.dev.fsglobal.org/int-std-ws-place/places/name-types/";
    private static String baseUrl = "http://ec2-54-235-166-8.compute-1.amazonaws.com:8080/std-ws-place/places/name-types/";


    /**
     * Run two tests ... all sources, and specific sources
     */
    public static void main(String[] args) throws Exception {
        readNames();
//        TypeModel nameModel = createName();
//        updateName(nameModel);
    }

    /**
     * Read all Names; read a specific Name; attempt to read a non-existent Name
     * @throws Exception
     */
    private static void readNames() throws Exception {
        URL url;
        RootModel model;

        url = new URL(baseUrl);
        model = TestUtil.doGET(url);
        System.out.println("RM: " + model);

        url = new URL(baseUrl + "445");
        model = TestUtil.doGET(url);
        System.out.println("RM: " + model);

        url = new URL(baseUrl + "2020");
        model = TestUtil.doGET(url);
        System.out.println("RM: " + model);
    }

    /**
     * Create a new Name type ...
     * 
     * @throws Exception
     */
    private static TypeModel createName() throws Exception {
        URL url = new URL(baseUrl);

        LocalizedNameDescModel nameAndDesc = new LocalizedNameDescModel();
        nameAndDesc.setLocale("en");
        nameAndDesc.setName("WLJ-Name");
        nameAndDesc.setDescription("My very own Name ... how quaint ...");

        List<LocalizedNameDescModel> nameAndDescList = new ArrayList<>();
        nameAndDescList.add(nameAndDesc);

        TypeModel theType = new TypeModel();
        theType.setCode("WLJ-NAME-BBB");
        theType.setIsPublished(true);
        theType.setName(nameAndDescList);

        RootModel inModel = new RootModel();
        inModel.setType(theType);

        RootModel outModel = TestUtil.doPOST(url, inModel);
        System.out.println("RM01: " + outModel.toJSON());
        return outModel.getType();
    }

    private static void updateName(TypeModel nameModel) throws Exception {
        URL url = new URL(baseUrl + nameModel.getId());

//        LocalizedNameDescModel nameAndDesc = new LocalizedNameDescModel();
//        nameAndDesc.setLocale("ja");
//        nameAndDesc.setName("WLJ-Name-ja");
//        nameAndDesc.setDescription("My very own <JA> Name ... how quaint ... ja");
//
//        nameModel.getLocalizedName().add(nameAndDesc);

        RootModel inModel = new RootModel();
        nameModel.setIsPublished(false);
        inModel.setType(nameModel);

        System.out.println("UPDATE: " + inModel.toJSON());
        RootModel outModel = TestUtil.doPUT(url, inModel);
        System.out.println("RM02: " + outModel.toJSON());
    }
}
