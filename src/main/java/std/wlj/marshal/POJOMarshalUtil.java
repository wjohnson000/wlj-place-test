package std.wlj.marshal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.familysearch.standards.core.logging.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import std.wlj.kml.model.*;


/**
 * A set of utilities to marshal and un-marshal using either XML or JSON notation.
 * <p/>
 * NOTE: With "faster" jackson (v2.0+) the object reader and writer are thread-safe,
 *       allowing the use a single pre-created {@link ObjectMapper} instance.
 * <p/>
 * NOTE 2: With XML, creating the {@link JAXBContext} is the expensive operation; but since
 *         there isn't a list of every class that can be marshaled/unmarshaled, a map
 *         will be used to store a per-class {@link JAXBContext}.  With that a new
 *         marshaler or unmarshaler can be created for each request, obviating the need for
 *         a 'synchronized' block.
 * 
 * @author wjohnson000
 *
 */
public class POJOMarshalUtil {
    private static final Logger logger = new Logger(POJOMarshalUtil.class);

    /** Prefix to indicate that an error occurred in the marshaling process */
    public static final String ERROR_PREFIX = "ERROR: ";

    /** JSON object mapper */
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    /** Unless we could pre-load the 'JAXBContext' with all known classes, create one per class */
    private static Map<Class<?>, JAXBContext> jaxbContextMap = new HashMap<>();

    /**
     * Private constructor since this is a utility class
     */
    private POJOMarshalUtil() { }

    /**
     * Marshal an object to JSON, with pretty printing enabled
     * 
     * @param model object to marshal
     * @return JSON representation of the object
     */
    public static String toJSON(Object model) {
        try {
            ObjectWriter jsonWriterPP = jsonMapper
                    .writerWithDefaultPrettyPrinter()
                    .withoutFeatures(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                    .without(SerializationFeature.FAIL_ON_EMPTY_BEANS);
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
            ObjectWriter jsonWriterPP = jsonMapper
                    .writerWithDefaultPrettyPrinter()
                    .withoutFeatures(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                    .without(SerializationFeature.FAIL_ON_EMPTY_BEANS);
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
            JAXBContext context = jaxbContextMap.get(model.getClass());
            if (context == null) {
                context = JAXBContext.newInstance(model.getClass());
                jaxbContextMap.put(model.getClass(), context);
            }

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

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
            JAXBContext context = jaxbContextMap.get(clazz);
            if (context == null) {
                context = JAXBContext.newInstance(clazz);
                jaxbContextMap.put(clazz, context);
            }

            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader reader = new StringReader(xmlString);
            return (T)unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            logger.error(e, "Model", "Unable to unmarshal XML: " + e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Un-marshal an object from XML.
     * 
     * @param jsonString XML representation of the object
     * @return a model object
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    public static GeometryModel fromKml(String xmlString) {
        try {
            JAXBContext context = jaxbContextMap.get(GeometryModel.class);
            if (context == null) {
                context = JAXBContext.newInstance(LinearRingModel.class, LineStringModel.class, MultiGeometryModel.class, PointModel.class, PolygonModel.class);
                jaxbContextMap.put(GeometryModel.class, context);
            }
            XMLFilter filter = new NamespaceFilter();

            // Set the parent XMLReader on the XMLFilter
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            filter.setParent(xr);

            Unmarshaller unmarshaller = context.createUnmarshaller();
            UnmarshallerHandler unmarshallerHandler = unmarshaller.getUnmarshallerHandler();

            ByteArrayInputStream bais = new ByteArrayInputStream(xmlString.getBytes());
            InputSource inSrc = new InputSource(bais);
            filter.setContentHandler(unmarshallerHandler);
            filter.parse(inSrc);
            return (GeometryModel)unmarshallerHandler.getResult();
        } catch (JAXBException | ParserConfigurationException | SAXException | IOException e) {
            logger.error(e, "Model", "Unable to unmarshal XML: " + e.getLocalizedMessage());
            return null;
        }
    }
}
