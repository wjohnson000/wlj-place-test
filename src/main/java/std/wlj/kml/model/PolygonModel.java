package std.wlj.kml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="Polygon")
@XmlType(propOrder={ "extrude", "tessellate", "altitudeMode", "outerBoundaryIs", "innerBoundaryIs" })
public class PolygonModel extends SimpleGeometryModel {

    private OuterBoundaryIsModel        outerBoundaryIs;
    private List<InnerBoundaryIsModel>  innerBoundaryIs;

    // Default constructor required for JAXB serialization
    public PolygonModel() { }

    @Override
    String getOuterCoordinates() {
        if (outerBoundaryIs == null) {
            return null;
        } else {
            return outerBoundaryIs.getLinearRing().getCoordinates();
        }
    }

    public void setOuterBoundaryIs(OuterBoundaryIsModel outerBoundaryIs) {
        this.outerBoundaryIs = outerBoundaryIs;
    }

    @XmlElement(name="outerBoundaryIs")
    public OuterBoundaryIsModel getOuterBoundaryIs() {
        return outerBoundaryIs;
    }

    public void setInnerBoundaryIs(List<InnerBoundaryIsModel> innerBoundaryIs) {
        this.innerBoundaryIs = innerBoundaryIs;
    }

    @XmlElement(name="innerBoundaryIs")
    public List<InnerBoundaryIsModel> getInnerBoundaryIs() {
        return innerBoundaryIs;
    }

    @Override
    public boolean isClosed() {
        return outerBoundaryIs != null  &&
               outerBoundaryIs.getLinearRing().isClosed()  &&
               (innerBoundaryIs == null  ||
                innerBoundaryIs.stream()
                   .allMatch(inner -> inner.getLinearRing().isClosed()));
    }
}
