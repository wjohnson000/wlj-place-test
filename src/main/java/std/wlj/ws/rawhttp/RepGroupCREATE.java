package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.place.ws.model.LocalizedNameDescModel;
import org.familysearch.standards.place.ws.model.PlaceRepGroupModel;
import org.familysearch.standards.place.ws.model.RootModel;


/**
 * Create four groups of states ...
 *   -- WESTERN US
 *   --  PACIFIC   (CA/HI/AK/WA/OR)
 *   --  MOUNTAIN  (ID/UT/WY)
 *   --    OTHER   (AZ/MT/NV)
 * 
 * @author wjohnson000
 */
public class RepGroupCREATE {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places/place-rep-groups";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";


    public static void main(String[] args) throws Exception {
        RootModel requestModel;
        RootModel responseModel;
        PlaceRepGroupModel prgModel;
        PlaceRepGroupModel otherPRGM;

        URL url = new URL(baseUrl);

        // Create the "Other" group
        requestModel = new RootModel();
        prgModel = createOther();
        requestModel.setPlaceRepGroup(prgModel);
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(requestModel.toJSON());
        System.out.println(requestModel.toXML());

        responseModel = TestUtil.doPOST(url, requestModel);
        otherPRGM = responseModel.getPlaceRepGroup();
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(responseModel.toJSON());
        System.out.println(responseModel.toXML());

        // Create the "Mountain" group
        requestModel = new RootModel();
        prgModel = createMountain(otherPRGM);
        requestModel.setPlaceRepGroup(prgModel);
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(requestModel.toJSON());
        System.out.println(requestModel.toXML());

        url = new URL(baseUrl);
        responseModel = TestUtil.doPOST(url, requestModel);
        PlaceRepGroupModel mountainPRGM = responseModel.getPlaceRepGroup();
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(responseModel.toJSON());
        System.out.println(responseModel.toXML());

        // Create the "Pacific" group
        requestModel = new RootModel();
        prgModel = createPacific();
        requestModel.setPlaceRepGroup(prgModel);
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(requestModel.toJSON());
        System.out.println(requestModel.toXML());

        url = new URL(baseUrl);
        responseModel = TestUtil.doPOST(url, requestModel);
        PlaceRepGroupModel pacificPRGM = responseModel.getPlaceRepGroup();
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(responseModel.toJSON());
        System.out.println(responseModel.toXML());

        // Create the "Western" group
        requestModel = new RootModel();
        prgModel = createWestern(mountainPRGM, pacificPRGM);
        requestModel.setPlaceRepGroup(prgModel);
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(requestModel.toJSON());
        System.out.println(requestModel.toXML());

        url = new URL(baseUrl);
        responseModel = TestUtil.doPOST(url, requestModel);
        PlaceRepGroupModel westernPRGM = responseModel.getPlaceRepGroup();
        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.println(responseModel.toJSON());
        System.out.println(responseModel.toXML());
    }

    /**
     * "WESTERN" -- parent group for PACIFIC and MOUNTAIN
     * @return a {@link PlaceRepGroupModel} instance
     */
    private static PlaceRepGroupModel createWestern(PlaceRepGroupModel... children) {
        List<LocalizedNameDescModel> theNameDesc = new ArrayList<>();
        theNameDesc.add(makeNameAndDesc("en", "Western", "Western states ..."));
        theNameDesc.add(makeNameAndDesc("ja", "US Nishi", "Nishi no shuu ..."));

        PlaceRepGroupModel model = new PlaceRepGroupModel();

        model.setId(0);
        model.setIsPublished(true);
        model.setName(theNameDesc);
        model.setSubGroups(Arrays.asList(children));

        return model;
    }

    /**
     * "PACIFIC" -- California, Hawaii, Alaska, Washington, Oregon
     * @return a {@link PlaceRepGroupModel} instance
     */
    private static PlaceRepGroupModel createPacific() {
        List<LocalizedNameDescModel> theNameDesc = new ArrayList<>();
        theNameDesc.add(makeNameAndDesc("en", "Pacific", "Pacific states ..."));

        PlaceRepGroupModel model = new PlaceRepGroupModel();

        model.setId(0);
        model.setIsPublished(true);
        model.setName(theNameDesc);
        // TODO fix the following ...
//        model.setRepIds(Arrays.asList(327, 328, 329, 373, 375));

        return model;
    }

    /**
     * "MOUNTAIN" -- Idaha, Utah, Wyoming
     * @return a {@link PlaceRepGroupModel} instance
     */
    private static PlaceRepGroupModel createMountain(PlaceRepGroupModel... children) {
        List<LocalizedNameDescModel> theNameDesc = new ArrayList<>();
        theNameDesc.add(makeNameAndDesc("en", "Mountain", "Mountain states ..."));

        PlaceRepGroupModel model = new PlaceRepGroupModel();

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
     * @return a {@link PlaceRepGroupModel} instance
     */
    private static PlaceRepGroupModel createOther() {
        List<LocalizedNameDescModel> theNameDesc = new ArrayList<>();
        theNameDesc.add(makeNameAndDesc("en", "Other", "Other states ..."));

        PlaceRepGroupModel model = new PlaceRepGroupModel();

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
