package std.wlj.kml.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public abstract class GeometryModel {

    private String id;

    // Default constructor required for JAXB serialization
    public GeometryModel() { }

    @XmlTransient
    public abstract long getPointCount();

    @XmlTransient
    public abstract boolean isClosed();

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name="id")
    public String getId() {
        return id;
    }
}
