package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.ws.model.AttributeModel;
import org.familysearch.standards.place.ws.model.CentroidModel;
import org.familysearch.standards.place.ws.model.CitationModel;
import org.familysearch.standards.place.ws.model.JurisdictionModel;
import org.familysearch.standards.place.ws.model.LocationModel;
import org.familysearch.standards.place.ws.model.NameModel;
import org.familysearch.standards.place.ws.model.PlaceModel;
import org.familysearch.standards.place.ws.model.PlaceRepresentationModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;
import org.familysearch.standards.place.ws.model.VariantModel;


public class RepCREATEwithATTRandCITN {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
    private static String baseUrl = "https://place-ws-dev.dev.fsglobal.org/int-std-ws-place-55/places";


    /**
     * Create a place w/ associated place-rep, then try and update the display names
     * of the place-rep
     */
    public static void main(String[] args) throws Exception {
    	HttpHelper.overrideHTTPS = true;
        RootModel outModel = createRep();
        int repId = outModel.getPlace().getReps().get(0).getId();
        System.out.println("PLACE00: " + outModel);
        getRep(repId);

        outModel = addAttributes(repId);
        System.out.println("PLACE01: " + outModel);
        getRep(repId);

        outModel = addCitations(repId);
        System.out.println("PLACE02: " + outModel);
        getRep(repId);
    }

    private static void getRep(int repId) throws Exception {
        System.out.println("REP-ID: " + repId);
        URL url = new URL(baseUrl + "/reps/" + repId + "?noCache=true");
        RootModel model = HttpHelper.doGET(url);
        System.out.println("RM: " + model);
    }

    /**
     * Create a Place + Rep + Attributes + Citations
     */
    private static RootModel createRep() throws Exception {
        // The place stuff ...
        List<VariantModel> varNames = new ArrayList<>();
        varNames.add(makeVariant("HubbleX", "en", 440, "var"));
        varNames.add(makeVariant("BubbleX", "en", 440, "var"));

        PlaceModel newPlace = new PlaceModel();
        newPlace.setFromYear(1800);
        newPlace.setVariants(varNames);

        // The place-rep stuff ...
        NameModel dName01 = new NameModel();
        dName01.setLocale("en");
        dName01.setName("HubbleX");
        
        NameModel dName02 = new NameModel();
        dName02.setLocale("fr");
        dName02.setName("FubbleX");

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

        // Create some attributes
        List<AttributeModel> attrs = new ArrayList<>();
        TypeModel attrType = makeType(433, "FS_REG");
        AttributeModel attrModel = new AttributeModel();
        attrModel.setType(attrType);
        attrModel.setValue("WLJ - TEST - 433");
        attrModel.setYear(1930);
        attrModel.setLocale("en");
        attrs.add(attrModel);

        attrType = makeType(418, "PRI_REG");
        attrModel = new AttributeModel();
        attrModel.setType(attrType);
        attrModel.setValue("WLJ - TEST - 418");
        attrModel.setYear(1930);
        attrModel.setLocale("en");
        attrs.add(attrModel);

        // Create some citations
        List<CitationModel> citns = new ArrayList<>();
        TypeModel citnType = makeType(460, "NAME");
        CitationModel citnModel = new CitationModel();
        citnModel.setType(citnType);
        citnModel.setDescription("WLJ - DESCR - 460");
        citnModel.setSourceId(11);
        citnModel.setSourceRef("WLJ - SRC_REF eleven");
        citns.add(citnModel);

        citnType = makeType(460, "NAME");
        citnModel = new CitationModel();
        citnModel.setType(citnType);
        citnModel.setDescription("WLJ - DESCR - 460");
        citnModel.setSourceId(12);
        citnModel.setSourceRef("WLJ - SRC_REF twelve");
        citns.add(citnModel);

        citnType = makeType(1459, "PLACE");
        citnModel = new CitationModel();
        citnModel.setType(citnType);
        citnModel.setDescription("WLJ - DESCR - 459");
        citnModel.setSourceId(13);
        citnModel.setSourceRef("WLJ - SRC_REF thirteen");
        citns.add(citnModel);

        // Create the 'RootModel' that will be posted ...
        RootModel prModel = new RootModel();
        List<PlaceRepresentationModel> reps = new ArrayList<>();
        reps.add(newRep);
        newPlace.setReps(reps);
        prModel.setPlace(newPlace);
        prModel.setAttributes(attrs);
        prModel.setCitations(citns);

        URL url = new URL(baseUrl);
        RootModel model = HttpHelper.doPOST(url, prModel);
        return model;
    }

    private static RootModel addAttributes(int repId) throws Exception {
        System.out.println("REP-ID: " + repId);
        URL url = new URL(baseUrl + "/reps/" + repId + "/attributes");

        // Create some attributes
        List<AttributeModel> attrs = new ArrayList<>();
        TypeModel attrType = makeType(1424, "NGA_US");
        AttributeModel attrModel = new AttributeModel();
        attrModel.setType(attrType);
        attrModel.setValue("WLJ - TEST - 424");
        attrModel.setYear(1940);
        attrModel.setLocale("en");
        attrs.add(attrModel);

        attrType = makeType(425, "NGA_ST_CD");
        attrModel = new AttributeModel();
        attrModel.setType(attrType);
        attrModel.setValue("WLJ - TEST - 425");
        attrModel.setYear(1950);
        attrModel.setLocale("en");
        attrs.add(attrModel);

        attrType = makeType(426, "ISOCNTRY");
        attrModel = new AttributeModel();
        attrModel.setType(attrType);
        attrModel.setValue("WLJ - TEST - 426");
        attrModel.setYear(1960);
        attrModel.setLocale("en");
        attrs.add(attrModel);

        // Create the 'RootModel' that will be posted ...
        RootModel prModel = new RootModel();
        prModel.setAttributes(attrs);

        RootModel model = HttpHelper.doPOST(url, prModel);
        return model;
    }

    private static RootModel addCitations(int repId) throws Exception {
        System.out.println("REP-ID: " + repId);
        URL url = new URL(baseUrl + "/reps/" + repId + "/citations");

        // Create some citations
        List<CitationModel> citns = new ArrayList<>();
        TypeModel citnType = makeType(1461, "TYPE");
        CitationModel citnModel = new CitationModel();
        citnModel.setType(citnType);
        citnModel.setDescription("WLJ - DESCR - 461");
        citnModel.setSourceId(111);
        citnModel.setSourceRef("WLJ - SRC_REF one-one-one");
        citns.add(citnModel);

        citnType = makeType(462, "JD");
        citnModel = new CitationModel();
        citnModel.setType(citnType);
        citnModel.setDescription("462");
        citnModel.setSourceId(112);
        citnModel.setSourceRef("WLJ - SRC_REF one-one-two");
        citns.add(citnModel);

        citnType = makeType(463, "BNDRY");
        citnModel = new CitationModel();
        citnModel.setType(citnType);
        citnModel.setDescription("WLJ - DESCR - 463");
        citnModel.setSourceId(113);
        citnModel.setSourceRef("WLJ - SRC_REF one-one-three");
        citns.add(citnModel);

        citnType = makeType(463, "BNDRY");
        citnModel = new CitationModel();
        citnModel.setType(citnType);
        citnModel.setDescription("WLJ - DESCR - 463");
        citnModel.setSourceId(114);
        citnModel.setSourceRef("WLJ - SRC_REF one-one-four");
        citns.add(citnModel);

        // Create the 'RootModel' that will be posted ...
        RootModel prModel = new RootModel();
        prModel.setCitations(citns);

        RootModel model = HttpHelper.doPOST(url, prModel);
        return model;
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
