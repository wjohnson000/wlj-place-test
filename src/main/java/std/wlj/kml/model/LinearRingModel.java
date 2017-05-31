package std.wlj.kml.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="LinearRing")
@XmlType(propOrder={ "extrude", "tessellate", "altitudeMode", "coordinates" })
public class LinearRingModel extends SimpleGeometryModel {

    private String coordinates;

    // Default constructor required for JAXB serialization
    public LinearRingModel() { }

    public String getOuterCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    @XmlElement(name="coordinates")
    public String getCoordinates() {
        return coordinates;
    }
}
