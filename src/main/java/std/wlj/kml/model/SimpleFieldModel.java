package std.wlj.kml.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class SimpleFieldModel {

    private String  type;
    private String  name;
    private String  displayName;

    // Default constructor required for JAXB serialization
    public SimpleFieldModel() { }

    // Convenience constructor for all values
    public SimpleFieldModel(String name, String type, String displayName) {
        this.name = name;
        this.type = type;
        this.displayName = displayName;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute(name="type")
    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name="name")
    public String getName() {
        return name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @XmlElement(name="displayName")
    public String getDisplayName() {
        return displayName;
    }
}
