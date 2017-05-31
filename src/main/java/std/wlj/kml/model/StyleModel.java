package std.wlj.kml.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StyleModel {

    private String             id;
    private BalloonStyleModel  balloonStyle;

    // Default constructor required for JAXB serialization
    public StyleModel() { }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name="id")
    public String getId() {
        return id;
    }

    public void setBalloonStyle(BalloonStyleModel model) {
        balloonStyle = model;
    }

    @XmlElement(name="BalloonStyle")
    public BalloonStyleModel getBalloonStyle() {
        return balloonStyle;
    }
}
