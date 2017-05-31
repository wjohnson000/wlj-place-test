package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.ws.model.CentroidModel;
import org.familysearch.standards.place.ws.model.JurisdictionModel;
import org.familysearch.standards.place.ws.model.LocationModel;
import org.familysearch.standards.place.ws.model.NameModel;
import org.familysearch.standards.place.ws.model.PlaceModel;
import org.familysearch.standards.place.ws.model.PlaceRepresentationModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;
import org.familysearch.standards.place.ws.model.VariantModel;


public class RepCREATEandUPDATEHttpClient {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place-55/places";
    private static String baseUrl = "http://place-ws-dev.dev.fsglobal.org/int-std-ws-place-55/places/";

    /**
     * Create a place w/ associated place-rep, then try and update the display names
     * of the place-rep
     */
    public static void main(String[] args) throws Exception {
//        HttpClientHelper.acceptType = "application/json";

        RootModel outModel = createRep();
        System.out.println("PLACE: " + outModel);
        getRep(outModel.getPlace().getReps().get(0).getId());

        outModel = removeName(outModel.getPlace().getReps().get(0));
        System.out.println("PLACE-REP: " + outModel);
        getRep(outModel.getPlaceRepresentation().getId());
    }

    private static void getRep(int repId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "?noCache=true");
        RootModel model = HttpClientHelper.doGET(url);
        System.out.println("RM: " + model);
    }

    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    private static RootModel createRep() throws Exception {
        // The place stuff ...
        List<VariantModel> varNames = new ArrayList<>();
        varNames.add(makeVariant("Hubble", "en", 440, "var"));
        varNames.add(makeVariant("Bubble", "en", 440, "var"));

        PlaceModel newPlace = new PlaceModel();
        newPlace.setFromYear(1800);
        newPlace.setVariants(varNames);

        // The place-rep stuff ...
        NameModel dName01 = new NameModel();
        dName01.setLocale("en");
        dName01.setName("Hubble");
        
        NameModel dName02 = new NameModel();
        dName02.setLocale("fr");
        dName02.setName("Fubble");

        List<NameModel> dispNames = new ArrayList<>();
        dispNames.add(dName01);
        dispNames.add(dName02);

        CentroidModel centroid = new CentroidModel();
        centroid.setLatitude(44.4);
        centroid.setLongitude(-55.5);
        LocationModel location = new LocationModel();
        location.setCentroid(centroid);

        PlaceRepresentationModel newRep = new PlaceRepresentationModel();
        newRep.setFromYear(1850);
        newRep.setToYear(2100);
        newRep.setPreferredLocale("en");
        newRep.setPublished(true);
        newRep.setType(makeType(207, "A3-city"));
        newRep.setJurisdiction(new JurisdictionModel());
        newRep.setDisplayNames(dispNames);
        newRep.setLocation(location);

        // Create the 'RootModel' that will be posted ...
        RootModel prModel = new RootModel();
        List<PlaceRepresentationModel> reps = new ArrayList<>();
        reps.add(newRep);
        newPlace.setReps(reps);
        prModel.setPlace(newPlace);

        URL url = new URL(baseUrl);
        RootModel model = HttpClientHelper.doPOST(url, prModel);
        return model;
    }

    /**
     * Remove one of the display names from a Place-Rep model ...
     * 
     * @param repModel place-representation model
     * @return
     */
    private static RootModel removeName(PlaceRepresentationModel repModel) throws Exception {
        if (repModel.getDisplayNames().size() > 1) {
            repModel.getDisplayNames().remove(repModel.getDisplayNames().size()-1);

            // Create the 'RootModel' that will be posted ...
            RootModel prModel = new RootModel();
            prModel.setPlaceRepresentation(repModel);

            URL url = new URL(baseUrl + "/reps/" + repModel.getId());
            RootModel model = HttpClientHelper.doPUT(url, prModel);
            return model;
        } else {
            return null;
        }
    }

    /**
     * Create a 'VariantModel' from the text, locale, name-type-id and name-type-code
     */
    private static VariantModel makeVariant(String text, String locale, int typeId, String typeCode) {
        NameModel name01 = new NameModel();
        name01.setLocale(locale);
        name01.setName(text);

        VariantModel vName01 = new VariantModel();
        vName01.setName(name01);
        vName01.setType(makeType(typeId, typeCode));
        return vName01;
    }

    /**
     * Create a 'TypeModel' from the identifier and code
     * 
     * @param id type identifier
     * @param code type code
     * @return TypeModle
     */
    private static TypeModel makeType(int id, String code) {
        TypeModel type = new TypeModel();
        type.setId(id);
        type.setCode(code);
        return type;
    }
}
