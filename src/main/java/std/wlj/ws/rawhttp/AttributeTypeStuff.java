package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.ws.model.LocalizedNameDescModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;


public class AttributeTypeStuff {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places/attribute-types/";
//    private static String baseUrl = "http://place-ws-test.dev.fsglobal.org/int-std-ws-place/places/name-types/";
//    private static String baseUrl = "http://ec2-54-235-166-8.compute-1.amazonaws.com:8080/std-ws-place/places/attribute-types/";

    /**
     * Run two tests ... all sources, and specific sources
     */
    public static void main(String[] args) throws Exception {
        readAttributes();
        TypeModel attrModel = createAttribute();
        updateAttribute(attrModel);
    }

    /**
     * Read all attributes; read a specific attribute; attempt to read a non-existent attribute
     * @throws Exception
     */
    private static void readAttributes() throws Exception {
        URL url;
        RootModel model;

        url = new URL(baseUrl);
        model = HttpHelper.doGET(url);
        System.out.println("RM: " + model);

        url = new URL(baseUrl + "1010");
        model = HttpHelper.doGET(url);
        System.out.println("RM: " + model);

        url = new URL(baseUrl + "2020");
        model = HttpHelper.doGET(url);
        System.out.println("RM: " + model);
    }

    /**
     * Create a new attribute type ...
     * 
     * @throws Exception
     */
    private static TypeModel createAttribute() throws Exception {
        URL url = new URL(baseUrl);

        LocalizedNameDescModel nameAndDesc = new LocalizedNameDescModel();
        nameAndDesc.setLocale("en");
        nameAndDesc.setName("WLJ-Attribute");
        nameAndDesc.setDescription("My very own attribute ... how quaint ...");

        List<LocalizedNameDescModel> nameAndDescList = new ArrayList<>();
        nameAndDescList.add(nameAndDesc);

        TypeModel theType = new TypeModel();
        theType.setCode("WLJ_ZZZ");
        theType.setIsPublished(true);
        theType.setName(nameAndDescList);

        RootModel inModel = new RootModel();
        inModel.setType(theType);

        RootModel outModel = HttpHelper.doPOST(url, inModel);
        System.out.println("RM01: " + outModel);
        return outModel.getType();
    }

    private static void updateAttribute(TypeModel attrModel) throws Exception {
        URL url = new URL(baseUrl + attrModel.getId());

        LocalizedNameDescModel nameAndDesc = new LocalizedNameDescModel();
        nameAndDesc.setLocale("ja");
        nameAndDesc.setName("WLJ-Attribute-ja");
        nameAndDesc.setDescription("My very own attribute ... how quaint ... ja");

        attrModel.getLocalizedName().add(nameAndDesc);

        RootModel inModel = new RootModel();
        inModel.setType(attrModel);

        RootModel outModel = HttpHelper.doPUT(url, inModel);
        System.out.println("RM02: " + outModel);
    }
}
