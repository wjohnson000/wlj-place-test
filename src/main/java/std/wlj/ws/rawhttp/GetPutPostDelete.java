package std.wlj.ws.rawhttp;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.ws.model.CentroidModel;
import org.familysearch.standards.place.ws.model.JurisdictionModel;
import org.familysearch.standards.place.ws.model.LocalizedNameDescModel;
import org.familysearch.standards.place.ws.model.LocationModel;
import org.familysearch.standards.place.ws.model.NameModel;
import org.familysearch.standards.place.ws.model.PlaceModel;
import org.familysearch.standards.place.ws.model.PlaceRepresentationModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;
import org.familysearch.standards.place.ws.model.VariantModel;


public class GetPutPostDelete {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
  private static String baseUrl = "http://place-ws-dev.dev.fsglobal.org/int-std-ws-place/places";

    /** Sample data for interpretation ... */
    private static String[] textes = {
        "Mogulreich",
        "heinolan mlk,mikkeli,finland",
        "アルゼンチン",
        "cherry hill, new jersey",
        "baranya,,,hungary",
        "valkeala,viipuri,finland",
        "dawley magna,shropshire,england",
        "st. george the martyr,southwark,london,englan",
        "Grønland",
        "ennighüffen,westfalen,preussen,germany"
    };

    /** URLs for three different types */
    private static String[] typeURLs = {
        "place-types",
        "name-types",
        "attribute-types"
    };

    /** Keep track of everything being logged */
    private static List<String> logStuff = new ArrayList<>();


    /**
     * Create a place w/ associated place-rep, then try and update the display names
     * of the place-rep
     */
    public static void main(String[] args) throws IOException {
        for (int i=0;  i<1;  i++) {
            try {
                searchTen();
                addAttributeType();
                createPlaceAndRep();
                getTypeThree();
                Thread.sleep(500);
            } catch (Exception e) {
                logIt("Bad thing happened: " + e.getMessage(), null, null);
            }
        }

        Path logPath = Paths.get("C:", "temp", "place-2.0-get-put-post-delete-x.txt");
        Files.write(logPath, logStuff, StandardCharsets.UTF_8);
    }

    /**
     * Do ten searches for different text strings
     * @throws Exception
     */
    private static void searchTen() throws Exception {
        URL url = new URL(baseUrl + "/request");
        for (String text : textes) {
            RootModel model = HttpHelper.doGET(url, "text", text, "limit", "2", "pubType", "pub_only");
            logIt("Search '" + text + "'", model, (model==null ? null : model.getSearchResults()));
        }
    }

    /**
     * Create a PLACE and associated PLACE-REP
     */
    private static void createPlaceAndRep() throws Exception {
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

        // Create the 'RootModel' that will be POST-ed ...
        RootModel prModel = new RootModel();
        List<PlaceRepresentationModel> reps = new ArrayList<>();
        reps.add(newRep);
        newPlace.setReps(reps);
        prModel.setPlace(newPlace);

        URL url = new URL(baseUrl);
        RootModel model = HttpHelper.doPOST(url, prModel);
        logIt("Make Place+Rep", model, (model==null ? null : model.getPlace()));
    }


    /**
     * Create a "Name" type
     */
    private static void addAttributeType() throws Exception {
        LocalizedNameDescModel nameDesc01 = new LocalizedNameDescModel();
        nameDesc01.setLocale("en");
        nameDesc01.setName("wlj-name-en");
        nameDesc01.setDescription("wlj-desc-en");

        LocalizedNameDescModel nameDesc02 = new LocalizedNameDescModel();
        nameDesc02.setLocale("fr");
        nameDesc02.setName("wlj-name-fr");
        nameDesc02.setDescription("wlj-desc-fr");

        List<LocalizedNameDescModel> names = new ArrayList<LocalizedNameDescModel>();
        names.add(nameDesc01);
        names.add(nameDesc02);

        TypeModel typeModel = new TypeModel();
        typeModel.setId(0);
        typeModel.setCode("WLJ-ATTR-" + System.currentTimeMillis());
        typeModel.setPublished(false);
        typeModel.setName(names);
        
        RootModel prModel = new RootModel();
        prModel.setType(typeModel);

        URL url = new URL(baseUrl + "/attribute-types?noCache=true");
        RootModel model = HttpHelper.doPOST(url, prModel);
        logIt("Make Name-Type", model, (model==null ? null : model.getType()));
    }

    /**
     * Retrieve the list of PLACE types, NAME types and ATTRIBUTE types
     * @throws Exception
     */
    public static void getTypeThree() throws Exception {
        for (String typeURL : typeURLs) {
            URL url = new URL(baseUrl + "/" + typeURL);
            RootModel model = HttpHelper.doGET(url);
            logIt("Get type '" + typeURL + "'", model, (model==null ? null : model.getTypes()));
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

    private static void logIt(String message, RootModel model, Object detail) {
        StringBuilder buff = new StringBuilder();
        if (model == null) {
            buff.append(message).append(": >>root-model is NULL<<");
        } else if (detail == null) {
            buff.append(message).append(": >>detail is NULL<<");
        } else {
            buff.append(message).append(": all is normal");
        }

        System.out.println(buff.toString());
        logStuff.add(buff.toString());
    }
}
