package std.wlj.kml.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class SimpleDataModel {

    private String  name;
    private String  value;

    // Default constructor required for JAXB serialization
    public SimpleDataModel() { }

    // Convenience constructor for all values
    public SimpleDataModel(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name="name")
    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @XmlValue()
    public String getValue() {
        return value;
    }
}
