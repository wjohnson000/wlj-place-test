package std.wlj.kml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SchemaModel {

    private String                  id;
    private String                  name;
    private List<SimpleFieldModel>  fields;

    // Default constructor required for JAXB serialization
    public SchemaModel() { }

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

    @XmlAttribute(name="name")
    public String getName() {
        return name;
    }

    public void setSimpleFields(List<SimpleFieldModel> fields) {
        this.fields = fields;
    }

    @XmlElement(name="SimpleField")
    public List<SimpleFieldModel> getSimpleFields() {
        return fields;
    }
}
