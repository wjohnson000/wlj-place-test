package std.wlj.jira;

import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 *   (1) Create a Place and Place-Rep
 *   (2) Create a second Place and Place-Rep
 *   (3) Create a child Place and Place-Rep, hang it off (1)
 *   (4) Create a child Place and Place-Rep, hang it off (2)
 *   (5) Add a citation to (3)
 *   (6) Add a citation to (4)
 *   (7) Move (4) as a child of (1)
 *   (8) Delete citation on (3)
 *   (9) Delete citation on (4)
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
        PlaceModel place01 = createRep(0);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("PLACE01: " + place01.toJSON());

        PlaceModel place02 = createRep(0);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("PLACE02: " + place02.toJSON());

        PlaceModel place03 = createRep(place01.getReps().get(0).getId());
        System.out.println("----------------------------------------------------------------------");
        System.out.println("PLACE03: " + place03.toJSON());

        PlaceModel place04 = createRep(place02.getReps().get(0).getId());
        System.out.println("----------------------------------------------------------------------");
        System.out.println("PLACE04: " + place04.toJSON());


        CitationModel citn03 = addCitation(place03.getReps().get(0).getId(), 613);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("CITN03: " + citn03.getId() + " . " + citn03.getRepId() + " . " + citn03.getSourceId() + " . " + citn03.getSourceRef());

        CitationModel citn04 = addCitation(place04.getReps().get(0).getId(), 613);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("CITN04: " + citn04.getId() + " . " + citn04.getRepId() + " . " + citn04.getSourceId() + " . " + citn04.getSourceRef());

        List<CitationModel> citn03S = readCitations(place03.getReps().get(0).getId());
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Rep03-citations: " + citn03S.size());
        for (CitationModel citnModel : citn03S) {
            System.out.println("CITN03S: " + citnModel.getId() + " . " + citnModel.getRepId() + " . " + citnModel.getSourceId() + " . " + citnModel.getSourceRef());
        }

        List<CitationModel> citn04S = readCitations(place04.getReps().get(0).getId());
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Rep04-citations: " + citn04S.size());
        for (CitationModel citnModel : citn04S) {
            System.out.println("CITN04S: " + citnModel.getId() + " . " + citnModel.getRepId() + " . " + citnModel.getSourceId() + " . " + citnModel.getSourceRef());
        }

        PlaceRepresentationModel rep04x = movePlaceRep(place04.getReps().get(0), place02.getReps().get(0).getId());
        System.out.println("----------------------------------------------------------------------");
        System.out.println("REP04X: " + rep04x.toJSON());

        PlaceRepresentationModel rep04xx = movePlaceRep(place04.getReps().get(0), place01.getReps().get(0).getId());
        System.out.println("----------------------------------------------------------------------");
        System.out.println("REP04XX: " + rep04xx.toJSON());

        citn03S = readCitations(place03.getReps().get(0).getId());
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Rep03-citations: " + citn03S.size());
        for (CitationModel citnModel : citn03S) {
            System.out.println("CITN03S: " + citnModel.getId() + " . " + citnModel.getRepId() + " . " + citnModel.getSourceId() + " . " + citnModel.getSourceRef());
        }

        citn04S = readCitations(place04.getReps().get(0).getId());
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Rep04-citations: " + citn04S.size());
        for (CitationModel citnModel : citn04S) {
            System.out.println("CITN04S: " + citnModel.getId() + " . " + citnModel.getRepId() + " . " + citnModel.getSourceId() + " . " + citnModel.getSourceRef());
        }

        removeCitation(place03.getReps().get(0).getId(), citn03S.get(0).getId());
        citn03S = readCitations(place03.getReps().get(0).getId());
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Rep03-citations: " + citn03S.size());
        for (CitationModel citnModel : citn03S) {
            System.out.println("CITN03S: " + citnModel.getId() + " . " + citnModel.getRepId() + " . " + citnModel.getSourceId() + " . " + citnModel.getSourceRef());
        }

        removeCitation(place04.getReps().get(0).getId(), citn04S.get(0).getId());
        citn04S = readCitations(place04.getReps().get(0).getId());
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Rep04-citations: " + citn04S.size());
        for (CitationModel citnModel : citn04S) {
            System.out.println("CITN04S: " + citnModel.getId() + " . " + citnModel.getRepId() + " . " + citnModel.getSourceId() + " . " + citnModel.getSourceRef());
        }
    }

//    private static void getRep(int repId) throws Exception {
//        URL url = new URL(baseUrl + "/reps/" + repId + "?noCache=true");
//        RootModel model = TestUtil.doGET(url);
//        System.out.println("RM: " + model);
//    }

    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    private static PlaceModel createRep(int parentId) throws Exception {
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
        RootModel model = TestUtil.doPOST(url, prModel);
        return (model == null) ? null : model.getPlace();
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
     * Modify the jurisdiction of a place-rep
     * @param placeRepresentationModel place-rep to move
     * @param parentId new parent id
     * @return
     */
    private static PlaceRepresentationModel movePlaceRep(PlaceRepresentationModel placeRepModel, Integer parentId) throws Exception {
        URL url = new URL(baseUrl + "/reps/" + placeRepModel.getId());

        JurisdictionModel jurisModel = new JurisdictionModel();
        jurisModel.setId(parentId);

        // Create the 'RootModel' that will be posted ...
        placeRepModel.setJurisdiction(jurisModel);
        RootModel prModel = new RootModel();
        prModel.setPlaceRepresentation(placeRepModel);

        RootModel model = TestUtil.doPUT(url, prModel);
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
