package std.wlj.kml.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={ "displayName", "value" })
public class DataModel {

    private String  name;
    private String  displayName;
    private String  value;

    // Default constructor required for JAXB serialization
    public DataModel() { }

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

    public void setValue(String value) {
        this.value = value;
    }

    @XmlElement(name="value")
    public String getValue() {
        return value;
    }
}
