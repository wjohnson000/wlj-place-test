package std.wlj.jira;

import java.net.*;
import java.util.ArrayList;
import java.util.Date;
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

import std.wlj.ws.rawhttp.TestUtil;


/**
 * Can't delete a CITATION after a PLACE-REP is moved to a different parent ...
 * 
 * Steps:
 *   (1) Create a Place and Place-Rep (parent #1)
 *   (2) Create a second Place and Place-Rep (parent #2)
 *   (3) Create a child Place and Place-Rep, with parent (1)
 *   (4) Add a couple of attributes and citations to (3)
 *   (5) Clone (3), with parent (2)
 *   (6) Delete (3)
 *   (7) Add attributes and citations to (5)
 *   (8) Delete a citation from (3)
 *   (9) Delete a citation from (5)
 * @author wjohnson000
 *
 */
public class STD2663 {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places";


    /**
     * Create a place w/ associated place-rep, then try and update the display names
     * of the place-rep
     */
    public static void main(String[] args) throws Exception {
    	// ========================================================================================
    	// [1] create the first parent PLACE + REP
    	// ========================================================================================
        System.out.println("----------------------------------------------------------------------");
        PlaceModel place01 = createPlaceAndRep(0);
        System.out.println("PLACE01: " + place01.toJSON());

    	// ========================================================================================
    	// [2] create the second parent PLACE + REP
    	// ========================================================================================
        System.out.println("----------------------------------------------------------------------");
        PlaceModel place02 = createPlaceAndRep(0);
        System.out.println("PLACE02: " + place02.toJSON());

    	// ========================================================================================
    	// [3] create the third PLACE + REP, child of [1]
    	// ========================================================================================
        System.out.println("----------------------------------------------------------------------");
        PlaceModel place03 = createPlaceAndRep(place01.getReps().get(0).getId());
        System.out.println("PLACE03: " + place03.toJSON());

    	// ========================================================================================
    	// [4] add some citations and attributes to [3]
    	// ========================================================================================
        System.out.println("----------------------------------------------------------------------");
        CitationModel citn03A = addCitation(place03.getReps().get(0).getId(), 460);
        CitationModel citn03B = addCitation(place03.getReps().get(0).getId(), 461);
        System.out.println("CITN03A: " + citn03A.getId() + " . " + citn03A.getRepId() + " . " + citn03A.getSourceId() + " . " + citn03A.getSourceRef());
        System.out.println("CITN03B: " + citn03B.getId() + " . " + citn03B.getRepId() + " . " + citn03B.getSourceId() + " . " + citn03B.getSourceRef());

        System.out.println("----------------------------------------------------------------------");
        AttributeModel attr03A = addAttribute(place03.getReps().get(0).getId(), 410);
        AttributeModel attr03B = addAttribute(place03.getReps().get(0).getId(), 411);
        System.out.println("attr03A: " + attr03A.getId() + " . " + attr03A.getRepId() + " . " + attr03A.getLocale() + " . " + attr03A.getValue());
        System.out.println("attr03B: " + attr03B.getId() + " . " + attr03B.getRepId() + " . " + attr03B.getLocale() + " . " + attr03B.getValue());

    	// ========================================================================================
    	// [5] create a place-rep
    	// ========================================================================================
        System.out.println("----------------------------------------------------------------------");
        PlaceRepresentationModel rep05 = createRep(place02.getId(), place02.getReps().get(0).getId());
        System.out.println("REP05: " + rep05.toJSON());

    	// ========================================================================================
    	// [6] delete [3]
    	// ========================================================================================
        System.out.println("----------------------------------------------------------------------");
        PlaceRepresentationModel rep05x = deletePlaceRep(place03.getReps().get(0).getId(), rep05.getId());
        System.out.println("REP05X: " + ((rep05x==null) ? null : rep05x.toJSON()));

    	// ========================================================================================
    	// [7] add some citations and attributes to [5]
    	// ========================================================================================
        System.out.println("----------------------------------------------------------------------");
        CitationModel citn05A = addCitation(rep05.getId(), 460);
        CitationModel citn05B = addCitation(rep05.getId(), 461);
        System.out.println("CITN05A: " + citn05A.getId() + " . " + citn05A.getRepId() + " . " + citn05A.getSourceId() + " . " + citn05A.getSourceRef());
        System.out.println("CITN05B: " + citn05B.getId() + " . " + citn05B.getRepId() + " . " + citn05B.getSourceId() + " . " + citn05B.getSourceRef());

        System.out.println("----------------------------------------------------------------------");
        AttributeModel attr05A = addAttribute(rep05.getId(), 410);
        AttributeModel attr05B = addAttribute(rep05.getId(), 411);
        System.out.println("attr05A: " + attr05A.getId() + " . " + attr05A.getRepId() + " . " + attr05A.getLocale() + " . " + attr05A.getValue());
        System.out.println("attr05B: " + attr05B.getId() + " . " + attr05B.getRepId() + " . " + attr05B.getLocale() + " . " + attr05B.getValue());

    	// ========================================================================================
    	// [7-x] read citations and attributes
    	// ========================================================================================
        System.out.println("----------------------------------------------------------------------");
        List<CitationModel> citn03S = readCitations(place03.getReps().get(0).getId());
        System.out.println("Rep03-citations: " + citn03S.size());
        for (CitationModel citnModel : citn03S) {
            System.out.println("CITN03S: " + citnModel.getId() + " . " + citnModel.getRepId() + " . " + citnModel.getSourceId() + " . " + citnModel.getSourceRef());
        }

        System.out.println("----------------------------------------------------------------------");
        List<AttributeModel> attr03S = readAttributes(place03.getReps().get(0).getId());
        System.out.println("Rep03-Attributes: " + attr03S.size());
        for (AttributeModel attrModel : attr03S) {
            System.out.println("attr03S: " + attrModel.getId() + " . " + attrModel.getRepId() + " . " + attrModel.getLocale() + " . " + attrModel.getValue());
        }

        System.out.println("----------------------------------------------------------------------");
        List<CitationModel> citn05S = readCitations(rep05.getId());
        System.out.println("Rep05-citations: " + citn05S.size());
        for (CitationModel citnModel : citn05S) {
            System.out.println("CITN05S: " + citnModel.getId() + " . " + citnModel.getRepId() + " . " + citnModel.getSourceId() + " . " + citnModel.getSourceRef());
        }

        System.out.println("----------------------------------------------------------------------");
        List<AttributeModel> attr05S = readAttributes(rep05.getId());
        System.out.println("Rep05-Attributes: " + attr05S.size());
        for (AttributeModel attrModel : attr05S) {
            System.out.println("attr05S: " + attrModel.getId() + " . " + attrModel.getRepId() + " . " + attrModel.getLocale() + " . " + attrModel.getValue());
        }

    	// ========================================================================================
    	// [8] remove a citation from [5]
    	// ========================================================================================
//        System.out.println("----------------------------------------------------------------------");
//        removeCitation(rep05.getId(), citn05S.get(0).getId());
//
//        System.out.println("----------------------------------------------------------------------");
//        citn05S = readCitations(rep05.getId());
//        System.out.println("Rep05-citations: " + citn05S.size());
//        for (CitationModel citnModel : citn05S) {
//            System.out.println("CITN05S: " + citnModel.getId() + " . " + citnModel.getRepId() + " . " + citnModel.getSourceId() + " . " + citnModel.getSourceRef());
//        }
//
//        System.out.println("----------------------------------------------------------------------");
//        attr05S = readAttributes(rep05.getId());
//        System.out.println("Rep05-Attributes: " + attr05S.size());
//        for (AttributeModel attrModel : attr05S) {
//            System.out.println("attr05S: " + attrModel.getId() + " . " + attrModel.getRepId() + " . " + attrModel.getLocale() + " . " + attrModel.getValue());
//        }
    }

    /**
     * Create a place and a place-rep, and return the place model
     * 
     * @param parentId place-rep parent identifier, or 0 for "no parent"
     */
    private static PlaceModel createPlaceAndRep(int parentId) throws Exception {
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

        JurisdictionModel jurisModel = null;
        if (parentId > 0) {
            jurisModel = new JurisdictionModel();
            jurisModel.setId(parentId);
        }

        PlaceRepresentationModel newRep = new PlaceRepresentationModel();
        newRep.setJurisdiction(jurisModel);
        newRep.setFromYear(1850);
        newRep.setToYear(2100);
        newRep.setPreferredLocale("en");
        newRep.setPublished(true);
        newRep.setType(makeType(207, "A3-city"));
        newRep.setDisplayNames(dispNames);
        newRep.setLocation(location);

        // Create the 'RootModel' that will be posted ...
        RootModel prModel = new RootModel();
        List<PlaceRepresentationModel> reps = new ArrayList<>();
        reps.add(newRep);
        newPlace.setReps(reps);
        prModel.setPlace(newPlace);

        URL url = new URL(baseUrl);
        RootModel model = TestUtil.doPOST(url, prModel);
        return (model == null) ? null : model.getPlace();
    }

    /**
     * Create a place-rep, and return the place-rep model
     * 
     * @param placeId the place (owner) identifier
     * @param parentId place-rep parent identifier, or 0 for "no parent"
     */
    private static PlaceRepresentationModel createRep(int placeId, int parentId) throws Exception {
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

        JurisdictionModel jurisModel = null;
        if (parentId > 0) {
            jurisModel = new JurisdictionModel();
            jurisModel.setId(parentId);
        }

        PlaceRepresentationModel newRep = new PlaceRepresentationModel();
        newRep.setOwnerId(placeId);
        newRep.setJurisdiction(jurisModel);
        newRep.setFromYear(1850);
        newRep.setToYear(2100);
        newRep.setPreferredLocale("en");
        newRep.setPublished(true);
        newRep.setType(makeType(207, "A3-city"));
        newRep.setDisplayNames(dispNames);
        newRep.setLocation(location);

        // Create the 'RootModel' that will be posted ...
        RootModel prModel = new RootModel();
        prModel.setPlaceRepresentation(newRep);

        URL url = new URL(baseUrl + "/" + placeId);
        RootModel model = TestUtil.doPOST(url, prModel);
        return (model == null) ? null : model.getPlaceRepresentation();
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

    /**
     * Create a citation for a place-rep
     * @param repId place-rep identifier
     * @param citnTypeId citation-type identifier
     * @return
     * @throws Exception
     */
    private static CitationModel addCitation(int repId, int citnTypeId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/citations?noCache=true");

        TypeModel citnType = new TypeModel();
        citnType.setId(citnTypeId);
        citnType.setCode("ATTR");

        CitationModel citnModel = new CitationModel();
        citnModel.setRepId(repId);
        citnModel.setType(citnType);
        citnModel.setSourceId(1);
        citnModel.setSourceRef("test - abc - " + citnTypeId);
        citnModel.setDescription("This is a description." + citnTypeId);
        citnModel.setCitDate(new Date());

        RootModel model = new RootModel();
        model.setCitation(citnModel);

        RootModel modelX = TestUtil.doPOST(url, model);
        return (modelX == null) ? null : modelX.getCitation();
    }

    /**
     * Create a Attribute for a place-rep
     * @param repId place-rep identifier
     * @param attrTypeId Attribute-type identifier
     * @return
     * @throws Exception
     */
    private static AttributeModel addAttribute(int repId, int attrTypeId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/attributes?noCache=true");

        TypeModel attrType = new TypeModel();
        attrType.setId(attrTypeId);
        attrType.setCode("ATTR");

        AttributeModel attrModel = new AttributeModel();
        attrModel.setRepId(repId);
        attrModel.setType(attrType);
        attrModel.setLocale("en");
        attrModel.setValue("test - abc - " + attrTypeId);
        attrModel.setYear(1901);

        RootModel model = new RootModel();
        model.setAttribute(attrModel);

        RootModel modelX = TestUtil.doPOST(url, model);
        return (modelX == null) ? null : modelX.getAttribute();
    }

    /**
     * Delete a place-rep, replacing it with a new one
     * @param repId place-rep identifier
     * @param replaceRepId replacement place-rep identifier
     * @return
     */
    private static PlaceRepresentationModel deletePlaceRep(int repId, int replaceRepId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "?newRepId=" + replaceRepId);

        RootModel model = TestUtil.doDELETE(url);
        return (model == null) ? null : model.getPlaceRepresentation();
    }

    /**
     * Read the citations of a place-rep
     * @param repId rep-identifier
     * @return List of citations
     * @throws Exception
     */
    private static List<CitationModel> readCitations(int repId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/citations?noCache=true");
        RootModel model = TestUtil.doGET(url);
        return model.getCitations();
    }

    /**
     * Read the attributes of a place-rep
     * @param repId rep-identifier
     * @return List of attributes
     * @throws Exception
     */
    private static List<AttributeModel> readAttributes(int repId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/attributes?noCache=true");
        RootModel model = TestUtil.doGET(url);
        return model.getAttributes();
    }

    /**
     * Remove a citation from a place-rep
     * @param repId place-rep identifier
     * @param citnId citation identifier
     * @return
     * @throws Exception
     */
    private static void removeCitation(int repId, int citnId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + repId + "/citations/" + citnId + "?noCache=true");

        TestUtil.doDELETE(url);
    }

}
