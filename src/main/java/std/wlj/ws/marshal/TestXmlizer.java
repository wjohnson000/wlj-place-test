package std.wlj.ws.marshal;

import java.io.*;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.familysearch.standards.place.ws.model.*;


public class TestXmlizer {

    /** XML object mapper */
    static Map<Class<?>,Marshaller>   marshallerMap   = new HashMap<Class<?>,Marshaller>();
    static Map<Class<?>,Unmarshaller> unmarshallerMap = new HashMap<Class<?>,Unmarshaller>();


    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        long time01 = 0;
        long time02 = 0;
        long time03 = 0;
        long time04 = 0;

        for (int i=0;  i<25000;  i++) {
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
            String xml01 = toXMLSharedMarshaller(rootModel);
            time01 += System.nanoTime() - nnow;

            nnow = System.nanoTime();
            time02 += System.nanoTime() - nnow;

            nnow = System.nanoTime();
            RootModel rootModel01 = fromXMLSharedUnmarshaller(xml01, RootModel.class);
            time03 += System.nanoTime() - nnow;

            nnow = System.nanoTime();
            time04 += System.nanoTime() - nnow;

            if (i == 0) {
                System.out.println("String01:\n" + xml01);
                System.out.println("\n\n\nRoot01:\n" + rootModel01);
            }
        }

        System.out.println("Time01: " + time01 / 1000000.0);
        System.out.println("Time02: " + time02 / 1000000.0);
        System.out.println("Time03: " + time03 / 1000000.0);
        System.out.println("Time04: " + time04 / 1000000.0);
    }

    /**
     * Marshal an object to XML.
     * 
     * @param model object to marshal
     * @return XML representation of the object
     */
    public static String toXMLSharedMarshaller(Object model) {
        try {
            Marshaller marshaller = marshallerMap.get(model.getClass());
            if (marshaller == null) {
                JAXBContext context = JAXBContext.newInstance(model.getClass());
                marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshallerMap.put(model.getClass(), marshaller);
            }

            StringWriter writer = new StringWriter();
            marshaller.marshal(model, writer);
            return writer.toString();
        } catch (JAXBException e) {
            System.out.println("Unable to marshal: " + e.getMessage());
            return "";
        }
    }

    /**
     * Un-marshal an object from XML.
     * 
     * @param xmlString XML representation of the object
     * @return a model object
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromXMLSharedUnmarshaller(String xmlString, Class<T> clazz) {
        try {
            Unmarshaller unmarshaller = unmarshallerMap.get(clazz);
            if (unmarshaller == null) {
                JAXBContext context = JAXBContext.newInstance(clazz);
                unmarshaller = context.createUnmarshaller();
                unmarshallerMap.put(clazz, unmarshaller);
            }

            StringReader reader = new StringReader(xmlString);
            T model = (T)unmarshaller.unmarshal(reader);

            return model;
        } catch (JAXBException e) {
            System.out.println("Unable to marshal: " + e.getMessage());
            return null;
        }
    }
}
