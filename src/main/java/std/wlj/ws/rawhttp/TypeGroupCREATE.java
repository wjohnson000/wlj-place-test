package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.place.ws.model.LocalizedNameDescModel;
import org.familysearch.standards.place.ws.model.PlaceTypeGroupModel;
import org.familysearch.standards.place.ws.model.RootModel;


/**
 * Create four 'type' groups ...
 * 
 * @author wjohnson000
 */
public class TypeGroupCREATE {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places/type-groups";
    private static String baseUrl = "http://54.204.45.169:8080/std-ws-place/places/type-groups";

    public static void main(String[] args) throws Exception {
        RootModel requestModel;
        RootModel responseModel;
        PlaceTypeGroupModel ptgModel;
        PlaceTypeGroupModel otherPRGM;

        URL url = new URL(baseUrl);

        // Create the "Other" group
        requestModel = new RootModel();
        ptgModel = createOther();
        requestModel.setPlaceTypeGroup(ptgModel);
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(requestModel.toJSON());
        System.out.println(requestModel.toXML());

        responseModel = HttpHelper.doPOST(url, requestModel);
        otherPRGM = responseModel.getPlaceTypeGroup();
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(responseModel.toJSON());
        System.out.println(responseModel.toXML());

        // Create the "Mountain" group
        requestModel = new RootModel();
        ptgModel = createMountain(otherPRGM);
        requestModel.setPlaceTypeGroup(ptgModel);
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(requestModel.toJSON());
        System.out.println(requestModel.toXML());

        url = new URL(baseUrl);
        responseModel = HttpHelper.doPOST(url, requestModel);
        PlaceTypeGroupModel mountainPRGM = responseModel.getPlaceTypeGroup();
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(responseModel.toJSON());
        System.out.println(responseModel.toXML());

        // Create the "Pacific" group
        requestModel = new RootModel();
        ptgModel = createPacific();
        requestModel.setPlaceTypeGroup(ptgModel);
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(requestModel.toJSON());
        System.out.println(requestModel.toXML());

        url = new URL(baseUrl);
        responseModel = HttpHelper.doPOST(url, requestModel);
        PlaceTypeGroupModel pacificPRGM = responseModel.getPlaceTypeGroup();
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(responseModel.toJSON());
        System.out.println(responseModel.toXML());

        // Create the "Western" group
        requestModel = new RootModel();
        ptgModel = createWestern(mountainPRGM, pacificPRGM);
        requestModel.setPlaceTypeGroup(ptgModel);
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(requestModel.toJSON());
        System.out.println(requestModel.toXML());

        url = new URL(baseUrl);
        responseModel = HttpHelper.doPOST(url, requestModel);
        PlaceTypeGroupModel westernPRGM = responseModel.getPlaceTypeGroup();
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(responseModel.toJSON());
        System.out.println(responseModel.toXML());
    }

    /**
     * "WESTERN" -- parent group for PACIFIC and MOUNTAIN
     * @return a {@link PlaceTypeGroupModel} instance
     */
    private static PlaceTypeGroupModel createWestern(PlaceTypeGroupModel... children) {
        List<LocalizedNameDescModel> theNameDesc = new ArrayList<>();
        theNameDesc.add(makeNameAndDesc("en", "Western", "Western types ..."));
        theNameDesc.add(makeNameAndDesc("ja", "US Nishi", "Nishi no mono ..."));

        PlaceTypeGroupModel model = new PlaceTypeGroupModel();

        model.setId(0);
        model.setIsPublished(true);
        model.setName(theNameDesc);
        model.setSubGroups(Arrays.asList(children));

        return model;
    }

    /**
     * "PACIFIC" -- California, Hawaii, Alaska, Washington, Oregon
     * @return a {@link PlaceTypeGroupModel} instance
     */
    private static PlaceTypeGroupModel createPacific() {
        List<LocalizedNameDescModel> theNameDesc = new ArrayList<>();
        theNameDesc.add(makeNameAndDesc("en", "Pacific", "Pacific types ..."));

        PlaceTypeGroupModel model = new PlaceTypeGroupModel();

        model.setId(0);
        model.setIsPublished(true);
        model.setName(theNameDesc);
        // TODO fix the following ...
//        model.setRepIds(Arrays.asList(327, 328, 329, 373, 375));

        return model;
    }

    /**
     * "MOUNTAIN" -- Idaha, Utah, Wyoming
     * @return a {@link PlaceTypeGroupModel} instance
     */
    private static PlaceTypeGroupModel createMountain(PlaceTypeGroupModel... children) {
        List<LocalizedNameDescModel> theNameDesc = new ArrayList<>();
        theNameDesc.add(makeNameAndDesc("en", "Mountain", "Mountain types ..."));

        PlaceTypeGroupModel model = new PlaceTypeGroupModel();

        model.setId(0);
        model.setIsPublished(true);
        model.setName(theNameDesc);
        // TODO fix the following
//        model.setRepIds(Arrays.asList(329, 342, 344));
        model.setSubGroups(Arrays.asList(children));

        return model;
    }

    /**
     * "OTHER" -- Arizona, Montana, Nevada
     * @return a {@link PlaceTypeGroupModel} instance
     */
    private static PlaceTypeGroupModel createOther() {
        List<LocalizedNameDescModel> theNameDesc = new ArrayList<>();
        theNameDesc.add(makeNameAndDesc("en", "Other", "Other types ..."));

        PlaceTypeGroupModel model = new PlaceTypeGroupModel();

        model.setId(0);
        model.setIsPublished(true);
        model.setName(theNameDesc);
        // TODO fix the following
//        model.setRepIds(Arrays.asList(326, 334, 336));

        return model;
    }

    /**
     * Create a localized name given the locale, name and description
     * 
     * @param locale locale
     * @param name name
     * @param desc description
     * @return localized name/description
     */
    private static LocalizedNameDescModel makeNameAndDesc(String locale, String name, String desc) {
        LocalizedNameDescModel nameDesc = new LocalizedNameDescModel();

        nameDesc.setLocale(locale);
        nameDesc.setName(name);
        nameDesc.setDescription(desc);

        return nameDesc;
    }

}
