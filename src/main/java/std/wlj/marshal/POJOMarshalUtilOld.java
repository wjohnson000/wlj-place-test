package std.wlj.marshal;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.familysearch.standards.core.logging.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;


/**
 * A set of utilities to marshal and un-marshal using either XML or JSON notation.
 * <p/>
 * NOTE: From a little testing it appears that creating new JSON reader and writer
 * instances each time is faster than synchronizing on pre-created instances.
 * However the reverse is true for the XML marshaller/unmarshaller instances, so
 * they are created once, saved in a map keyed by Class type, and all operations
 * are synchronized.
 * <p/>
 * NOTE 2: If we migrate to the "faster jackson" (v2.0+) the object readers and
 * writer are thread-safe so we could use single instances of them.
 * 
 * @author wjohnson000
 *
 */
public class POJOMarshalUtilOld {
    private static final Logger logger = new Logger(POJOMarshalUtilOld.class);

    /** Prefix to indicate that an error occurred in the marshaling process */
    public static final String ERROR_PREFIX = "ERROR: ";

    /** JSON object mapper */
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    /** XML object mapper */
    private static Map<Class<?>,Marshaller>   marshallerMap   = new HashMap<Class<?>,Marshaller>();
    private static Map<Class<?>,Unmarshaller> unmarshallerMap = new HashMap<Class<?>,Unmarshaller>();


    /**
     * Private constructor since this is a utility class
     */
    private POJOMarshalUtilOld() { }

    /**
     * Marshal an object to JSON, with pretty printing enabled
     * 
     * @param model object to marshal
     * @return JSON representation of the object
     */
    public static String toJSON(Object model) {
        try {
            ObjectWriter jsonWriterPP = jsonMapper.writerWithDefaultPrettyPrinter();
            return jsonWriterPP.writeValueAsString(model);
        } catch (Exception e) {
            logger.error(e, "Model", "Unable to unmarshal JSON: " + e.getLocalizedMessage());
            return ERROR_PREFIX + e.getLocalizedMessage();
        }
    }

    /**
     * Marshal an object to JSON, without the pretty printing;
     * 
     * @param model object to marshal
     * @return JSON representation of the object
     */
    public static String toJSONPlain(Object model) {
        try {
            ObjectWriter jsonWriterPP = jsonMapper.writer();
            return jsonWriterPP.writeValueAsString(model);
        } catch (Exception e) {
            logger.error(e, "Model", "Unable to unmarshal JSON: " + e.getLocalizedMessage());
            return ERROR_PREFIX + e.getLocalizedMessage();
        }
    }

    /**
     * Un-marshal an object from JSON.
     * 
     * @param jsonString JSON representation of the object
     * @return a model object
     */
    public static <T> T fromJSON(String jsonString, Class<T> clazz) {
        try {
            ObjectReader reader = jsonMapper.readerFor(clazz);
            return reader.readValue(jsonString);
        } catch (Exception e) {
            logger.error(e, "Model", "Unable to unmarshal JSON: " + e.getLocalizedMessage());
        }

        return null;
    }

    /**
     * Marshal an object to XML.
     * 
     * @param model object to marshal
     * @return XML representation of the object
     */
    public static String toXML(Object model) {
        try {
            Marshaller marshaller = marshallerMap.get(model.getClass());
            if (marshaller == null) {
                JAXBContext context = JAXBContext.newInstance(model.getClass());
                marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshallerMap.put(model.getClass(), marshaller);
            }

            synchronized(marshaller) {
                StringWriter writer = new StringWriter();
                marshaller.marshal(model, writer);

                return writer.toString();
            }
        } catch (JAXBException e) {
            logger.error(e, "Model", "Unable to unmarshal XML: " + e.getLocalizedMessage());
            return ERROR_PREFIX + e.getLocalizedMessage();
        }
    }

    /**
     * Marshal an object to XML.  NOTE: don't synchronize the marshal!!
     * 
     * @param model object to marshal
     * @return XML representation of the object
     */
    public static String toXMLNoSynch(Object model) {
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
            logger.error(e, "Model", "Unable to unmarshal XML: " + e.getLocalizedMessage());
            return ERROR_PREFIX + e.getLocalizedMessage();
        }
    }

    /**
     * Un-marshal an object from XML.
     * 
     * @param jsonString XML representation of the object
     * @return a model object
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromXML(String xmlString, Class<T> clazz) {
        try {
            Unmarshaller unmarshaller = unmarshallerMap.get(clazz);
            if (unmarshaller == null) {
                JAXBContext context = JAXBContext.newInstance(clazz);
                unmarshaller = context.createUnmarshaller();
                unmarshallerMap.put(clazz, unmarshaller);
            }

            synchronized(unmarshaller) {
                StringReader reader = new StringReader(xmlString);
                T model = (T)unmarshaller.unmarshal(reader);

                return model;
            }
        } catch (JAXBException e) {
            logger.error(e, "Model", "Unable to unmarshal XML: " + e.getLocalizedMessage());
            return null;
        }
    }
}
