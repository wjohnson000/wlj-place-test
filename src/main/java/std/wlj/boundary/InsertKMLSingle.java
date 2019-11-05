package std.wlj.boundary;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import std.wlj.util.DbConnectionManager;


public class InsertKMLSingle {
    public static void main(String[] args) throws Exception {
        Connection conn = DbConnectionManager.getConnectionStds();

        File kmlDir = new File("C:/tools/gis-files/kml-files/World countries boundaries inc South Sudan.kml");
        System.out.println("Processing file: " + kmlDir);
        if (kmlDir.getAbsolutePath().endsWith(".kml")) {
            Map<String,String> boundaries = parseFile(kmlDir);
            for (Map.Entry<String,String> entry : boundaries.entrySet()) {
                if (! addBoundary(conn, entry.getKey(), entry.getValue())) {
                    System.out.println("  Unable to insert boundary for: " + entry.getKey());
                }
            }
        }

        conn.close();
    }

    /**
     * Add an entry to the "boundary" table ...
     * 
     * @param conn DB connection
     * @param name boundary name
     * @param kml KML chunk ...
     * @return TRUE if this succeeded, false otherwise ...
     * 
     * @throws Exception if something bad happens
     */
    private static boolean addBoundary(Connection conn, String name, String kml) {
        // Determine if the KML is 2D or 3D
        int nDims = 0;
        PreparedStatement stmt = null;
        ResultSet         rset = null;

        try {
            stmt = conn.prepareStatement("INSERT INTO sams_place.boundaries(name, boundary) VALUES(?, sams_place.ST_GeomFromKML(?))");
            stmt.setString(1, name);
            stmt.setString(2, kml);
            int cnt = stmt.executeUpdate();
            return (cnt == 1);
        } catch(SQLException ex) {
            if (! "Column has Z dimension but geometry does not".equalsIgnoreCase(ex.getMessage())) {
                return false;
            }
        } finally {
            if (stmt != null) try { stmt.close(); } catch(Exception ex) { }
        }

        stmt = null;
        try {
            stmt = conn.prepareStatement("SELECT sams_place.ST_NDims(sams_place.ST_GeomFromKML(?))");
            stmt.setString(1, kml);
            rset = stmt.executeQuery();
            if (rset.next()) {
                nDims = rset.getInt(1);
            }
        } catch(SQLException ex) {
            System.out.println("Unable to calculate dimensions -- " + name + ":" + ex.getMessage());
            return false;
        } finally {
            if (rset != null) try { rset.close(); } catch(Exception ex) { }
            if (stmt != null) try { stmt.close(); } catch(Exception ex) { }
        }

        stmt = null;
        if (nDims == 2) {
            try {
                stmt = conn.prepareStatement("INSERT INTO sams_place.boundaries(name, boundary) VALUES(?, sams_place.ST_Force_3D(sams_place.ST_GeomFromKML(?)))");
                stmt.setString(1, name);
                stmt.setString(2, kml);
                int cnt = stmt.executeUpdate();
                return (cnt == 1);
            } catch(SQLException ex) {
                if (! "Column has Z dimension but geometry does not".equalsIgnoreCase(ex.getMessage())) {
                    return false;
                }
            } finally {
                if (stmt != null) try { stmt.close(); } catch(Exception ex) { }
            }
        }

        return false;
    }

    private static Map<String,String> parseFile(File aFile) throws Exception {
        String fileName = aFile.getName();
        Map<String,String> results = new TreeMap<String,String>();

        InputStream inStr = new FileInputStream(aFile);
        Reader      reader = new InputStreamReader(inStr, "UTF-8");
        InputSource source = new InputSource(reader);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(source);

        NodeList places = document.getElementsByTagName("Placemark");
        for (int i=0;  i<places.getLength();  i++) {
            Node place = places.item(i);
            NodeList children = place.getChildNodes();
            String name = "child." + i;
            String boundary = "";
            for (int j=0;  j<children.getLength();  j++) {
                Node child = children.item(j);
                if (child.getNodeName().equals("name")) {
                    name = child.getFirstChild().getNodeValue();
                } else if (child.getNodeName().equals("MultiGeometry")) {
                    boundary = getNodeXml(child);
                } else if (child.getNodeName().equals("Polygon")) {
                    boundary = getNodeXml(child);
                } else if (child.getNodeName().equals("LineString")) {
                    boundary = getNodeXml(child);
                }
            }
            if (name.contains("<")  ||  name.contains(">")) {
                continue;
            }

            String key = fileName + "." + name;
            results.put(key, boundary);
        }
        
        reader.close();
        return results;
    }

    /**
     * Get the XML format of a Node, w/out the &lt;?xml ...> header
     * 
     * @param aNode node
     * @return node's XML
     * 
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     */
    private static String getNodeXml(Node aNode) throws TransformerFactoryConfigurationError, TransformerException {
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(aNode), new StreamResult(writer));
        return writer.toString();
    }
}
