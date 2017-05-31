package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.ws.model.CentroidModel;
import org.familysearch.standards.place.ws.model.JurisdictionModel;
import org.familysearch.standards.place.ws.model.LocationModel;
import org.familysearch.standards.place.ws.model.NameModel;
import org.familysearch.standards.place.ws.model.PlaceRepresentationModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;


public class TestCreate {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";


    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        NameModel dName01 = new NameModel();
        dName01.setLocale("en");
        dName01.setName("Hubble");
        
        NameModel dName02 = new NameModel();
        dName02.setLocale("fr");
        dName02.setName("Fubble");

        List<NameModel> dispNames = new ArrayList<NameModel>();
        dispNames.add(dName01);
        dispNames.add(dName02);

        TypeModel type = new TypeModel();
        type.setCode("A3-city");
        type.setId(207);

        CentroidModel centroid = new CentroidModel();
        centroid.setLatitude(44.4);
        centroid.setLongitude(-55.5);
        LocationModel location = new LocationModel();
        location.setCentroid(centroid);

        JurisdictionModel j01 = new JurisdictionModel();
        j01.setId(393779);
//        JurisdictionModel j02 = new JurisdictionModel();
//        j02.setId(333);
//        JurisdictionModel j03 = new JurisdictionModel();
//        j03.setId(1);
//        j01.setParent(j02);
//        j02.setParent(j03);

        PlaceRepresentationModel newRep = new PlaceRepresentationModel();
        newRep.setFromYear(1850);
        newRep.setToYear(2100);
        newRep.setOwnerId(1337578);
        newRep.setPreferredLocale("en");
        newRep.setPublished(true);
        newRep.setType(type);
        newRep.setJurisdiction(j01);
        newRep.setDisplayNames(dispNames);
        newRep.setLocation(location);
        
        RootModel prModel = new RootModel();
        prModel.setPlaceRepresentation(newRep);

        URL url = new URL(baseUrl + "/1337578");
        RootModel model = HttpHelper.doGET(url);
        System.out.println("RM: " + model);
    }

}
