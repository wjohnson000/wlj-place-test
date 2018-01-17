package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.place.ws.model.LocalizedNameDescModel;
import org.familysearch.standards.place.ws.model.PlaceRepGroupModel;
import org.familysearch.standards.place.ws.model.PlaceRepSummaryModel;
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
public class RepGroupCREATEMinimal {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places/place-rep-groups";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";
//    private static String baseUrl = "http://54.204.45.169/std-ws-place/places";


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
        System.out.println("\nREQUEST 01 -----------------------------------------------------------------------------------");
        System.out.println(requestModel.toJSON());

        responseModel = HttpHelper.doPOST(url, requestModel);
        otherPRGM = responseModel.getPlaceRepGroup();
//        System.out.println("\nRESPONSE 01 -----------------------------------------------------------------------------------");
//        System.out.println(responseModel.toJSON());

        // Create the "Mountain" group
        requestModel = new RootModel();
        prgModel = createMountain(otherPRGM);
        requestModel.setPlaceRepGroup(prgModel);
        System.out.println("\nREQUEST 02 -----------------------------------------------------------------------------------");
        System.out.println(requestModel.toJSON());

        url = new URL(baseUrl);
        responseModel = HttpHelper.doPOST(url, requestModel);
        PlaceRepGroupModel mountainPRGM = responseModel.getPlaceRepGroup();
//        System.out.println("\nRESPONSE 02 -----------------------------------------------------------------------------------");
//        System.out.println(responseModel.toJSON());

        // Create the "Pacific" group
        requestModel = new RootModel();
        prgModel = createPacific();
        requestModel.setPlaceRepGroup(prgModel);
        System.out.println("\nREQUEST 03 -----------------------------------------------------------------------------------");
        System.out.println(requestModel.toJSON());

        url = new URL(baseUrl);
        responseModel = HttpHelper.doPOST(url, requestModel);
        PlaceRepGroupModel pacificPRGM = responseModel.getPlaceRepGroup();
//        System.out.println("\nRESPONSE 03 -----------------------------------------------------------------------------------");
//        System.out.println(responseModel.toJSON());

        // Create the "Western" group
        requestModel = new RootModel();
        prgModel = createWestern(mountainPRGM, pacificPRGM);
        requestModel.setPlaceRepGroup(prgModel);
        System.out.println("\nREQUEST 04 -----------------------------------------------------------------------------------");
        System.out.println(requestModel.toJSON());

        url = new URL(baseUrl);
        responseModel = HttpHelper.doPOST(url, requestModel);
        PlaceRepGroupModel westernPRGM = responseModel.getPlaceRepGroup();
//        System.out.println("\nRESPONSE 04 -----------------------------------------------------------------------------------");
//        System.out.println(responseModel.toJSON());
    }

    /**
     * "WESTERN" -- parent group for PACIFIC and MOUNTAIN
     * @return a {@link PlaceRepGroupModel} instance
     */
    private static PlaceRepGroupModel createWestern(PlaceRepGroupModel... children) {
        List<LocalizedNameDescModel> theNameDesc = new ArrayList<>();
        theNameDesc.add(makeNameAndDesc("en", "Western", "Western states ..."));
        theNameDesc.add(makeNameAndDesc("ja", "US Nishi", "Nishi no shuu ..."));

        List<PlaceRepGroupModel> subGroups = new ArrayList<>();
        for (PlaceRepGroupModel child : children) {
            child.setName(null);
            child.setRepSummaries(null);
            child.setSelfLink(null);
            child.setSubGroups(null);
            child.setPublished(null);
            subGroups.add(child);
        }

        PlaceRepGroupModel model = new PlaceRepGroupModel();

        model.setId(0);
        model.setPublished(true);
        model.setName(theNameDesc);
        model.setSubGroups(subGroups);

        return model;
    }

    /**
     * "PACIFIC" -- California, Hawaii, Alaska, Washington, Oregon
     * @return a {@link PlaceRepGroupModel} instance
     */
    private static PlaceRepGroupModel createPacific() {
        List<LocalizedNameDescModel> theNameDesc = new ArrayList<>();
        theNameDesc.add(makeNameAndDesc("en", "Pacific", "Pacific states ..."));

        List<PlaceRepSummaryModel> summaries = new ArrayList<>();
        for (int id : Arrays.asList(4, 5)) {
            PlaceRepSummaryModel prSummary = new PlaceRepSummaryModel();
            prSummary.setId(id);
            summaries.add(prSummary);
        }
        
        PlaceRepGroupModel model = new PlaceRepGroupModel();

        model.setId(0);
        model.setPublished(true);
        model.setName(theNameDesc);
        model.setRepSummaries(summaries);

        return model;
    }

    /**
     * "MOUNTAIN" -- Idaha, Utah, Wyoming
     * @return a {@link PlaceRepGroupModel} instance
     */
    private static PlaceRepGroupModel createMountain(PlaceRepGroupModel... children) {
        List<LocalizedNameDescModel> theNameDesc = new ArrayList<>();
        theNameDesc.add(makeNameAndDesc("en", "Mountain", "Mountain states ..."));

        List<PlaceRepGroupModel> subGroups = new ArrayList<>();
        for (PlaceRepGroupModel child : children) {
            child.setName(null);
            child.setRepSummaries(null);
            child.setSelfLink(null);
            child.setSubGroups(null);
            child.setPublished(null);
            subGroups.add(child);
        }

        List<PlaceRepSummaryModel> summaries = new ArrayList<>();
        for (int id : Arrays.asList(8, 9)) {
            PlaceRepSummaryModel prSummary = new PlaceRepSummaryModel();
            prSummary.setId(id);
            summaries.add(prSummary);
        }

        PlaceRepGroupModel model = new PlaceRepGroupModel();

        model.setId(0);
        model.setPublished(true);
        model.setName(theNameDesc);
        model.setRepSummaries(summaries);
        model.setSubGroups(subGroups);

        return model;
    }

    /**
     * "OTHER" -- Arizona, Montana, Nevada
     * @return a {@link PlaceRepGroupModel} instance
     */
    private static PlaceRepGroupModel createOther() {
        List<LocalizedNameDescModel> theNameDesc = new ArrayList<>();
        theNameDesc.add(makeNameAndDesc("en", "Other", "Other states ..."));

        List<PlaceRepSummaryModel> summaries = new ArrayList<>();
        for (int id : Arrays.asList(10, 11, 12)) {
            PlaceRepSummaryModel prSummary = new PlaceRepSummaryModel();
            prSummary.setId(id);
            summaries.add(prSummary);
        }

        PlaceRepGroupModel model = new PlaceRepGroupModel();

        model.setId(0);
        model.setPublished(true);
        model.setName(theNameDesc);
        model.setRepSummaries(summaries);

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
