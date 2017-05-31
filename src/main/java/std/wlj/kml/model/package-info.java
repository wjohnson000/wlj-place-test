@XmlSchema (
    namespace = "http://www.opengis.net/kml/2.2",
    elementFormDefault = XmlNsForm.QUALIFIED,
    xmlns = {
        @XmlNs(prefix="", namespaceURI="http://www.opengis.net/kml/2.2"),
        @XmlNs(prefix="gx", namespaceURI="http://www.google.com/kml/ext/2.2")
    }
)
package std.wlj.kml.model;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlNsForm;
