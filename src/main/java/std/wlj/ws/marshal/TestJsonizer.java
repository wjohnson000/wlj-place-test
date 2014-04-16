package std.wlj.ws.marshal;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;
import org.familysearch.standards.place.ws.model.CentroidModel;
import org.familysearch.standards.place.ws.model.JurisdictionModel;
import org.familysearch.standards.place.ws.model.LocationModel;
import org.familysearch.standards.place.ws.model.NameModel;
import org.familysearch.standards.place.ws.model.PlaceRepresentationModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.TypeModel;


public class TestJsonizer {

    /** JSON object mapper */
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final ObjectReader jsonReader = jsonMapper.reader(RootModel.class);
    private static final ObjectWriter jsonWriter = jsonMapper.writerWithType(RootModel.class);
    private static final ObjectWriter jsonWriterPP = jsonMapper.writerWithDefaultPrettyPrinter();


    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        long time01 = 0;
        long time02 = 0;
        long time03 = 0;
        long time04 = 0;
        long time05 = 0;

        for (int i=0;  i<250000;  i++) {
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
            
            RootModel rootModel = new RootModel();
            rootModel.setPlaceRepresentation(newRep);

            long nnow = System.nanoTime();
            String json01 = toJSONMapper(rootModel);
            time01 += System.nanoTime() - nnow;

            nnow = System.nanoTime();
            String json02 = toJSONWriter(rootModel);
            time02 += System.nanoTime() - nnow;

            nnow = System.nanoTime();
            String json03 = toJSONWriterPP(rootModel);
            time03 += System.nanoTime() - nnow;

            nnow = System.nanoTime();
            RootModel rootModel01 = fromJSONReader(json01);
            time04 += System.nanoTime() - nnow;

            nnow = System.nanoTime();
            RootModel rootModel02 = fromJSONMapper(json01);
            time05 += System.nanoTime() - nnow;

            if (i == 0) {
                System.out.println("String01:\n" + json01);
                System.out.println("\n\n\nString02:\n" + json02);
                System.out.println("\n\n\nString03:\n" + json03);
                System.out.println("\n\n\nRoot01:\n" + rootModel01);
                System.out.println("\n\n\nRoot02:\n" + rootModel02);
            }
        }

        System.out.println("Time01: " + time01 / 1000000.0);
        System.out.println("Time02: " + time02 / 1000000.0);
        System.out.println("Time03: " + time03 / 1000000.0);
        System.out.println("Time04: " + time04 / 1000000.0);
        System.out.println("Time05: " + time05 / 1000000.0);
    }

    /**
     * Un-marshal an object from JSON.
     * 
     * @param jsonString JSON representation of the object
     * @return a model object
     */
    public static RootModel fromJSONReader(String jsonString) {
        try {
            return jsonReader.readValue(jsonString);
        } catch (JsonGenerationException e) {
            System.out.println("Unable to unmarshal JSON: " + e.getLocalizedMessage());
        } catch (JsonMappingException e) {
            System.out.println("Unable to unmarshal JSON: " + e.getLocalizedMessage());
        } catch (IOException e) {
            System.out.println("Unable to unmarshal JSON: " + e.getLocalizedMessage());
        }

        return null;
    }

    /**
     * Un-marshal an object from JSON.
     * 
     * @param jsonString JSON representation of the object
     * @return a model object
     */
    public static RootModel fromJSONMapper(String jsonString) {
        try {
            return jsonMapper.readValue(jsonString, RootModel.class);
        } catch (JsonGenerationException e) {
            System.out.println("Unable to unmarshal JSON: " + e.getLocalizedMessage());
        } catch (JsonMappingException e) {
            System.out.println("Unable to unmarshal JSON: " + e.getLocalizedMessage());
        } catch (IOException e) {
            System.out.println("Unable to unmarshal JSON: " + e.getLocalizedMessage());
        }

        return null;
    }

    /**
     * Marshal an object to JSON.
     * 
     * @param model object to marshal
     * @return JSON representation of the object
     */
    public static String toJSONMapper(RootModel model) {
        try {
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(model);
        } catch (JsonGenerationException e) {
            System.out.println("Unable to marshal JSON: " + e.getLocalizedMessage());
        } catch (JsonMappingException e) {
            System.out.println("Unable to marshal JSON: " + e.getLocalizedMessage());
        } catch (IOException e) {
            System.out.println("Unable to marshal JSON: " + e.getLocalizedMessage());
        }

        return "";
    }

    /**
     * Marshal an object to JSON.
     * 
     * @param model object to marshal
     * @return JSON representation of the object
     */
    public static String toJSONWriter(RootModel model) {
        try {
            return jsonWriter.writeValueAsString(model);
        } catch (JsonGenerationException e) {
            System.out.println("Unable to marshal JSON: " + e.getLocalizedMessage());
        } catch (JsonMappingException e) {
            System.out.println("Unable to marshal JSON: " + e.getLocalizedMessage());
        } catch (IOException e) {
            System.out.println("Unable to marshal JSON: " + e.getLocalizedMessage());
        }

        return "";
    }

    /**
     * Marshal an object to JSON.
     * 
     * @param model object to marshal
     * @return JSON representation of the object
     */
    public static String toJSONWriterPP(RootModel model) {
        try {
            return jsonWriterPP.writeValueAsString(model);
        } catch (JsonGenerationException e) {
            System.out.println("Unable to marshal JSON: " + e.getLocalizedMessage());
        } catch (JsonMappingException e) {
            System.out.println("Unable to marshal JSON: " + e.getLocalizedMessage());
        } catch (IOException e) {
            System.out.println("Unable to marshal JSON: " + e.getLocalizedMessage());
        }

        return "";
    }

}
