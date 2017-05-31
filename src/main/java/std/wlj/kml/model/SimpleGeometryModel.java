package std.wlj.kml.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public abstract class SimpleGeometryModel extends GeometryModel {

    private String extrude;
    private String tessellate;
    private String altitudeMode;

    // Default constructor required for JAXB serialization
    public SimpleGeometryModel() { }

    @XmlTransient
    abstract String getOuterCoordinates();

    public void setExtrude(String extrude) {
        this.extrude = extrude;
    }

    @XmlElement(name="extrude")
    public String getExtrude() {
        return extrude;
    }

    public void setTessellate(String tessellate) {
        this.tessellate = tessellate;
    }

    @XmlElement(name="tessellate")
    public String getTessellate() {
        return tessellate;
    }

    public void setAltitudeMode(String altitudeMode) {
        this.altitudeMode = altitudeMode;
    }

    @XmlElement(name="altitudeMode")
    public String getAltitudeMode() {
        return altitudeMode;
    }
    
    @Override
    public long getPointCount() {
        String coordinates = getOuterCoordinates();
        if (coordinates == null) {
            return 0;
        } else {
            String tCoord = coordinates.replaceAll("\\s+", " ");
            return tCoord.trim().chars().filter(ch -> ch == ' ').count() + 1;
        }
    }

    @Override
    public boolean isClosed() {
        boolean isClosed = false;

        String coordinates = getOuterCoordinates();
        if (coordinates != null) {
            String tCoord = coordinates.replaceAll("\\s+", " ");
            String[] coords = tCoord.trim().split(" ");
            if (coords.length >= 3) {
                String coord01 = coords[0];
                String coord99 = coords[coords.length-1];
                isClosed = coord01.trim().equals(coord99.trim());
            }
        }

        return isClosed;
    }
}
