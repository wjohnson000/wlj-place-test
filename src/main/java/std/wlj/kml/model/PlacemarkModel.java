package std.wlj.kml.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.familysearch.standards.place.ws.util.POJOMarshalUtil;

@XmlRootElement(name="Placemark")
@XmlType(propOrder={ "name", "styleUrl", "description", "timeSpan", "extendedData", "geometry" })
public class PlacemarkModel {

    private String              id;
    private String              name;
    private String              styleUrl;
    private String              description;
    private TimeSpanModel       timeSpan;
    private ExtendedDataModel   extendedData;
    private GeometryModel       geometry;

    // Default constructor required for JAXB serialization
    public PlacemarkModel() { }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name="id")
    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name="name")
    public String getName() {
        return name;
    }

    public void setStyleUrl(String styleUrl) {
        this.styleUrl = styleUrl;
    }

    @XmlElement(name="styleUrl")
    public String getStyleUrl() {
        return styleUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name="description")
    public String getDescription() {
        return description;
    }

    public void setTimeSpan(TimeSpanModel timeSpan) {
        this.timeSpan = timeSpan;
    }

    @XmlElement(name="TimeSpan")
    public TimeSpanModel getTimeSpan() {
        return timeSpan;
    }

    public void setExtendedData(ExtendedDataModel extendedData) {
        this.extendedData = extendedData;
    }

    @XmlElement(name="ExtendedData")
    public ExtendedDataModel getExtendedData() {
        return extendedData;
    }

    public void setGeometry(GeometryModel geometry) {
        this.geometry = geometry;
    }

    @XmlElements({
        @XmlElement(name="Point", type=PointModel.class),
        @XmlElement(name="LinearRing", type=LinearRingModel.class),
        @XmlElement(name="LineString", type=LineStringModel.class),
        @XmlElement(name="Polygon", type=PolygonModel.class),
        @XmlElement(name="MultiGeometry", type=MultiGeometryModel.class)
    })
    public GeometryModel getGeometry() {
        return geometry;
    }

    @XmlTransient
    public String getGeometryAsXML() {
        return POJOMarshalUtil.toXML(getGeometry());

    }
}
