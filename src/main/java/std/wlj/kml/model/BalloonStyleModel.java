package std.wlj.kml.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class BalloonStyleModel {

    private String  id;
    private String  text;

    // Default constructor required for JAXB serialization
    public BalloonStyleModel() { }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name="id")
    public String getId() {
        return id;
    }

    public void setText(String text) {
        this.text = text;
    }

    @XmlElement(name="text")
    public String getText() {
        return text;
    }
}
