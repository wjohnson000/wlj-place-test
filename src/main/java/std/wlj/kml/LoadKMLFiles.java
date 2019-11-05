package std.wlj.kml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.micromata.opengis.kml.v_2_2_0.*;
import de.micromata.opengis.kml.v_2_2_0.gx.*;
import std.wlj.util.DbConnectionManager;

public class LoadKMLFiles {

    static final String kmlFileDir = "D:/postgis/files";
    static final String boundarySQL =
        "INSERT INTO rep_boundary(rep_id, point_count, from_year, to_year, boundary_data, delete_flag) " +
        "VALUES(?, ?, ?, ?, geography(ST_Transform(ST_GeomFromKML(?), 4326)), false)";

    public static void main(String... args) throws IOException {
        Map<Integer,List<String>> repToFile = MatchKMLToPlaceRep.createMatchFileMap();
        DataSource bds = DbConnectionManager.getDataSourceWLJ();

        try(Connection conn = bds.getConnection()) {
            cleanBoundaryTable(conn);
            repToFile.entrySet().forEach(
                entry ->
                    entry.getValue().forEach(
                        file -> loadFile(conn, entry.getKey(), file)));
        } catch(SQLException ex) {
            System.out.println("Main@SQL-EX: " + ex.getMessage());
        }
    }

    private static void cleanBoundaryTable(Connection conn) {
        try(Statement stmt = conn.createStatement()) {
            int cnt = stmt.executeUpdate("DELETE FROM rep_boundary");
            System.out.println("'rep_boundary' delete count=" + cnt);
        } catch(SQLException ex) {
            System.out.println("CleanTable@SQL-EX: " + ex.getMessage());
        }
    }
    
    private static void loadFile(Connection conn, Integer key, String file) {
        System.out.println(key + " --> " + file);
        Kml kml = readKmlData(file);
        Geometry geom = (kml == null) ? null : extractGeometryFromKML(kml);
        if (geom == null) {
            System.out.println("Unable to get KML geometry from file: " + file);
        } else {
            try(PreparedStatement stmt = conn.prepareStatement(boundarySQL)) {
                stmt.setInt(1, key.intValue());
                stmt.setInt(2, getPointCount(geom));
                stmt.setNull(3, Types.INTEGER);
                stmt.setNull(4, Types.INTEGER);
                stmt.setString(5, marshalGeometry(geom));
                int cnt = stmt.executeUpdate();
                System.out.println("  insert count=" + cnt);
            } catch(SQLException ex) {
                System.out.println("LoadFile@SQL-EX: " + ex.getMessage());
            }
        }
    }

    private static Kml readKmlData(String file) {
        Path path = Paths.get(kmlFileDir, file);
        try {
            String kmlData = new String(Files.readAllBytes(path));
            return Kml.unmarshal(kmlData);
        } catch (IOException ex) {
            System.out.println("ReadKMLData@IO-EX: " + ex.getMessage());
            return null;
        }
    }

    private static Geometry extractGeometryFromKML(Kml kml) {
        Document kmlDoc = (Document)kml.getFeature();
        List<Feature> folders = kmlDoc.getFeature();
        for (Feature fFeature : folders) {
            Folder folder = (Folder) fFeature;
            List<Feature> placemarks = folder.getFeature();
            for (Feature fPlacemark : placemarks) {
                Placemark placemark = (Placemark)fPlacemark;
                return placemark.getGeometry();
            }
        }
        return null;
    }

    private static int getPointCount(Geometry geom) {
        if (geom instanceof MultiTrack) {
            MultiTrack mTrack = (MultiTrack)geom;
            return mTrack.getTrack().stream().mapToInt(track -> track.getCoord().size()).sum();
        } else if (geom instanceof Track) {
            Track track = (Track)geom;
            return track.getCoord().size();
        } else if (geom instanceof LinearRing) {
            LinearRing ring = (LinearRing)geom;
            return ring.getCoordinates().size();
        } else if (geom instanceof Point) {
            Point point = (Point)geom;
            return point.getCoordinates().size();
        } else if (geom instanceof Model) {
            return 1;
        } else if (geom instanceof MultiGeometry) {
            MultiGeometry mGeom = (MultiGeometry)geom;
            return mGeom.getGeometry().stream().mapToInt(geomX -> getPointCount(geomX)).sum();
        } else if (geom instanceof LineString) {
            LineString lString = (LineString)geom;
            return lString.getCoordinates().size();
        } else if (geom instanceof Polygon) {
            Polygon polygon = (Polygon)geom;
            return polygon.getOuterBoundaryIs().getLinearRing().getCoordinates().size();
        }
        return 100;
    }

    private static String marshalGeometry(Geometry geom) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JAXBContext jaxbContext = JAXBContext.newInstance(geom.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(geom, baos);
            return baos.toString();
        } catch (JAXBException ex) {
            System.out.println("MarshallGeom@jaxb: " + ex.getMessage());
            return null;
        }
    }
}
