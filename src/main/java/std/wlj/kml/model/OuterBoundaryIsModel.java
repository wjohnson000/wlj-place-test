package std.wlj.kml.model;

import javax.xml.bind.annotation.XmlElement;

public class OuterBoundaryIsModel {

    private LinearRingModel linearRing;

    // Default constructor required for JAXB serialization
    public OuterBoundaryIsModel() { }

    public void setLinearRing(LinearRingModel linearRing) {
        this.linearRing = linearRing;
    }

    @XmlElement(name="LinearRing")
    public LinearRingModel getLinearRing() {
        return linearRing;
    }
}
