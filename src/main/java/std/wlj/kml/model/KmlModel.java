package std.wlj.kml.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="kml", namespace=KmlModel.NAMESPACE)
public class KmlModel {

    public static final String  APPLICATION_XML_KML = "application/vnd.google-earth.kml+xml";
    public static final String  NAMESPACE = "http://www.opengis.net/kml/2.2";

    private DocumentModel  doc;

    // Default constructor required for JAXB serialization
    public KmlModel() { }

    public void setDocument(DocumentModel doc) {
        this.doc = doc;
    }

    @XmlElement(name="Document")
    public DocumentModel getDocument() {
        return doc;
    }
}
