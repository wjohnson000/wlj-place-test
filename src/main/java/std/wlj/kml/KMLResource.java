package std.wlj.kml;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.Model;
import de.micromata.opengis.kml.v_2_2_0.MultiGeometry;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import de.micromata.opengis.kml.v_2_2_0.TimePrimitive;
import de.micromata.opengis.kml.v_2_2_0.TimeSpan;
import de.micromata.opengis.kml.v_2_2_0.gx.MultiTrack;
import de.micromata.opengis.kml.v_2_2_0.gx.Track;

/**
 * Wrapper around raw 'KML' data, with methods for extracting fields that are required
 * for processing the data.
 * 
 * @author wjohnson000
 *
 */
public class KMLResource {

    private static JAXBContext jaxbContext = null;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(Geometry.class);
        } catch (JAXBException e) {
            System.out.println("Unable to setup JAXB-Context for 'Geometry' class ...");
        }
    }

    private String    kmlData;
    private Placemark placemark;

    public KMLResource(String kmlData) {
        this.kmlData = kmlData;
    }

    public synchronized boolean isValid() {
        if (placemark == null) {
            placemark = extractPlacemarkFromKML();
        }
        return placemark != null;
    }

    public int getPointCount() {
        requireValidKmlData();

        Geometry geom = placemark.getGeometry();
        return getPointCount(geom);
    }

    public Integer getFromYear() {
        requireValidKmlData();

        Integer year = null;
        TimePrimitive time = placemark.getTimePrimitive();
        if (time instanceof TimeSpan) {
            TimeSpan timeSpan = (TimeSpan)time;
            year = getYear(timeSpan.getBegin());
        }
        return year;
    }

    public Integer getToYear() {
        requireValidKmlData();

        Integer year = null;
        TimePrimitive time = placemark.getTimePrimitive();
        if (time instanceof TimeSpan) {
            TimeSpan timeSpan = (TimeSpan)time;
            year = getYear(timeSpan.getEnd());
        }
        return year;
    }

    public String getGeometryData() {
        requireValidKmlData();

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(placemark.getGeometry(), baos);
            return baos.toString();
        } catch (JAXBException ex) {
            System.out.println("MarshallGeom@jaxb: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Check the validity of the input data; throw an exception if it's invalid.
     * 
     * @throws IllegalArgumentException if the 'kmlData' in invalid
     */
    protected void requireValidKmlData() throws IllegalArgumentException {
        if (! isValid()) {
            throw new IllegalArgumentException("The 'kmlData' input is invalid");
        }
    }

    /**
     * Find the first (and only??) 'placemark' feature from a KML file
     * 
     * @param kml
     * @return
     */
    protected Placemark extractPlacemarkFromKML() {
        Kml kml = Kml.unmarshal(kmlData);
        Document kmlDoc = (Document)kml.getFeature();
        List<Feature> folders = kmlDoc.getFeature();
        for (Feature fFeature : folders) {
            Folder folder = (Folder) fFeature;
            List<Feature> placemarks = folder.getFeature();
            for (Feature fPlacemark : placemarks) {
                return (Placemark)fPlacemark;
            }
        }
        return null;
    }

    /**
     * Return the number of points in the given geometry.  Note that this is a recursive
     * method to handle the "MultiGeometry" case.
     * 
     * @param tempGeom geometry instance 
     * @return number of points in this geometry instance
     */
    protected int getPointCount(Geometry tempGeom) {
        requireValidKmlData();

        if (tempGeom instanceof MultiTrack) {
            MultiTrack mTrack = (MultiTrack)tempGeom;
            return mTrack.getTrack().stream().mapToInt(track -> track.getCoord().size()).sum();
        } else if (tempGeom instanceof Track) {
            Track track = (Track)tempGeom;
            return track.getCoord().size();
        } else if (tempGeom instanceof LinearRing) {
            LinearRing ring = (LinearRing)tempGeom;
            return ring.getCoordinates().size();
        } else if (tempGeom instanceof Point) {
            Point point = (Point)tempGeom;
            return point.getCoordinates().size();
        } else if (tempGeom instanceof Model) {
            return 1;
        } else if (tempGeom instanceof MultiGeometry) {
            MultiGeometry mGeom = (MultiGeometry)tempGeom;
            return mGeom.getGeometry().stream().mapToInt(geomX -> getPointCount(geomX)).sum();
        } else if (tempGeom instanceof LineString) {
            LineString lString = (LineString)tempGeom;
            return lString.getCoordinates().size();
        } else if (tempGeom instanceof Polygon) {
            Polygon polygon = (Polygon)tempGeom;
            return polygon.getOuterBoundaryIs().getLinearRing().getCoordinates().size();
        }
        return 100;
    }

    /**
     * Format of the 'dateTime' parameter should 'yyyy-mm-ddThh:mm:ss.ssszzzzzz'.  We care about
     * the YEAR portion only.
     * 
     * @param dateTime date-time string
     * @return year, as an Integer
     */
    protected Integer getYear(String dateTime) {
        int ndx = dateTime.indexOf('-');
        return (ndx <= 2) ? null : Integer.parseInt(dateTime.substring(0, ndx));
    }
}
