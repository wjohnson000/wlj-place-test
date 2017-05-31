package std.wlj.kml.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={ "begin", "end" })
public class TimeSpanModel {

    private String  id;
    private String  begin;
    private String  end;

    // Default constructor required for JAXB serialization
    public TimeSpanModel() { }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name="id")
    public String getId() {
        return id;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    @XmlElement(name="begin")
    public String getBegin() {
        return begin;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @XmlElement(name="end")
    public String getEnd() {
        return end;
    }
}
