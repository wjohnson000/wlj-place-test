package std.wlj.kml.model;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="MultiGeometry")
public class MultiGeometryModel extends GeometryModel {

    private List<GeometryModel>  geometry;

    // Default constructor required for JAXB serialization
    public MultiGeometryModel() { }

    public void setGeometry(List<GeometryModel> geometry) {
        this.geometry = geometry;
    }

    @XmlElements({
        @XmlElement(name="Point", type=PointModel.class),
        @XmlElement(name="LinearRing", type=LinearRingModel.class),
        @XmlElement(name="LineString", type=LineStringModel.class),
        @XmlElement(name="Polygon", type=PolygonModel.class),
        @XmlElement(name="MultiGeometry", type=MultiGeometryModel.class)
    })
    public List<GeometryModel> getGeometry() {
        return geometry;
    }

    @Override
    public long getPointCount() {
        return geometry.stream()
            .collect(Collectors.summingLong(GeometryModel::getPointCount));
    }

    @Override
    public boolean isClosed() {
        return geometry.stream()
            .allMatch(geom -> geom.isClosed());
    }
}
