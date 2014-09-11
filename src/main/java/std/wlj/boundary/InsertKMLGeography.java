package std.wlj.boundary;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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

import std.wlj.util.FileUtils;


public class InsertKMLGeography {
    private static String jdbcDriver   = "org.postgresql.Driver";
    private static String jdbcURL      = "jdbc:postgresql://ec2-23-21-26-193.compute-1.amazonaws.com:5432/p124";
    private static String jdbcUser     = "postgres";
    private static String jdbcPassword = "admin";


//    public static void main(String[] args) throws Exception {
//        Connection conn = getConn();
//
//        PrintWriter pwOut = FileUtils.getWriter("C:/tools/gis-files/kml-files/kml-files-bad.txt");
//        File kmlDir = new File("C:/tools/gis-files/kml-files");
//        File[] files = kmlDir.listFiles();
//        for (File aFile : files) {
//            System.out.println("Processing file: " + aFile);
//            if (aFile.getAbsolutePath().endsWith(".kml")) {
//                int bCount = 0;
//                long nnow = System.currentTimeMillis();
//                Map<String,String> boundary = parseFile(aFile);
//                for (Map.Entry<String,String> entry : boundary.entrySet()) {
//                    if (addBoundary(conn, entry.getKey(), entry.getValue())) {
//                        bCount++;
//                    } else {
//                        System.out.println("  Unable to insert boundary for: " + entry.getKey());
//                        pwOut.println(entry.getKey() + "|" + entry.getValue());
//                    }
//                }
//                System.out.println("  Entries: " + bCount + ";  Time: " + (System.currentTimeMillis()-nnow)/1000.0);
//            }
//        }
//
//        conn.close();
//        pwOut.close();
//    }
//
//    /**
//     * Create a return a database connection ...
//     * 
//     * @return
//     */
//    private static Connection getConn() {
//        Statement stmt = null;
//        try {
//            Class.forName(jdbcDriver);
//            Connection conn = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);
//            stmt = conn.createStatement();
//            stmt.execute("SET SCHEMA 'sams_place'");
//            return conn;
//        } catch(Exception ex) {
//            System.out.println("Unable to create connection -- " + ex.getMessage());
//            return null;
//        } finally {
//            if (stmt != null) try { stmt.close(); } catch(Exception ex) { }
//        }
//    }
//
//    /**
//     * Add an entry to the "boundary" table ...
//     * 
//     * @param conn DB connection
//     * @param name boundary name
//     * @param kml KML chunk ...
//     * @return TRUE if this succeeded, false otherwise ...
//     * 
//     * @throws Exception if something bad happens
//     */
//    private static boolean addBoundary(Connection conn, String name, String kml) {
//        // Try to insert the boundary, assuming that it's 3D
//        int nDims = 0;
//        PreparedStatement stmt = null;
//        ResultSet         rset = null;
//
//        try {
//            stmt = conn.prepareStatement("INSERT INTO sams_place.bgeography(name, boundary) VALUES(?, sams_place.ST_GeomFromKML(?))");
//            stmt.setString(1, name);
//            stmt.setString(2, kml);
//            int cnt = stmt.executeUpdate();
//            return (cnt == 1);
//        } catch(SQLException ex) {
//            if (! ex.getMessage().contains("Column has Z dimension but geometry does not")) {
//                return false;
//            }
//        } finally {
//            if (stmt != null) try { stmt.close(); } catch(Exception ex) { }
//        }
//
//        // If the insert failed by reason of dimension, see if this is 2D data
//        stmt = null;
//        try {
//            stmt = conn.prepareStatement("SELECT sams_place.ST_NDims(sams_place.ST_GeomFromKML(?))");
//            stmt.setString(1, kml);
//            rset = stmt.executeQuery();
//            if (rset.next()) {
//                nDims = rset.getInt(1);
//            }
//        } catch(SQLException ex) {
//            System.out.println("Unable to calculate dimensions -- " + name + ":" + ex.getMessage());
//            return false;
//        } finally {
//            if (rset != null) try { rset.close(); } catch(Exception ex) { }
//            if (stmt != null) try { stmt.close(); } catch(Exception ex) { }
//        }
//
//        // If the data is 2D, force it to be 3D
//        stmt = null;
//        if (nDims == 2) {
//            try {
//                stmt = conn.prepareStatement("INSERT INTO sams_place.bgeography(name, boundary) VALUES(?, sams_place.ST_Force_3D(sams_place.ST_GeomFromKML(?)))");
//                stmt.setString(1, name);
//                stmt.setString(2, kml);
//                int cnt = stmt.executeUpdate();
//                return (cnt == 1);
//            } catch(SQLException ex) {
//                if (! "Column has Z dimension but geometry does not".equalsIgnoreCase(ex.getMessage())) {
//                    return false;
//                }
//            } finally {
//                if (stmt != null) try { stmt.close(); } catch(Exception ex) { }
//            }
//        }
//
//        return false;
//    }
//
//    /**
//     * Parse the input file, extracting every "Placemark" tag with it's associated
//     * geometry, which must be one of "MultiGeometry", "Polygon" or "LineString".
//     * 
//     * @param aFile input file
//     * @return Map of placemark-name to KML boundary data
//     * @throws Exception
//     */
//    private static Map<String,String> parseFile(File aFile) throws Exception {
//        String fileName = aFile.getName();
//        Map<String,String> results = new TreeMap<String,String>();
//
//        InputStream inStr = new FileInputStream(aFile);
//        Reader      reader = new InputStreamReader(inStr, "UTF-8");
//        InputSource source = new InputSource(reader);
//        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//        Document document = documentBuilder.parse(source);
//
//        NodeList places = document.getElementsByTagName("Placemark");
//        for (int i=0;  i<places.getLength();  i++) {
//            Node place = places.item(i);
//            NodeList children = place.getChildNodes();
//            String name = "child." + i;
//            String boundary = "";
//            int coordCnt = 0;
//            for (int j=0;  j<children.getLength();  j++) {
//                Node child = children.item(j);
//                if (child.getNodeName().equals("name")) {
//                    name = child.getFirstChild().getNodeValue();
//                } else if (child.getNodeName().equals("MultiGeometry")) {
//                    coordCnt = fixCoordinates(child);
//                    removePoints(child);
//                    boundary = getNodeXml(child);
//                } else if (child.getNodeName().equals("Polygon")) {
//                    coordCnt = fixCoordinates(child);
//                    boundary = getNodeXml(child);
//                } else if (child.getNodeName().equals("LineString")) {
//                    coordCnt = fixCoordinates(child);
//                    boundary = getNodeXml(child);
//                }
//            }
//            if (name.contains("<")  ||  name.contains(">")) {
//                continue;
//            }
//            String key = fileName + "." + name;
//            results.put(key, boundary);
//            if (coordCnt > 10000) {
//                System.out.println(name + " --> " + coordCnt);
//            }
//        }
//
//        reader.close();
//        return results;
//    }
//
//    /**
//     * Fix any "coordinates" tag where the the first and last entry aren't the same.
//     * 
//     * @param aNode node to fix
//     * @return total number of coordinates in the boundary data
//     */
//    private static int fixCoordinates(Node aNode) {
//        int coordCnt = 0;
//
//        if (aNode.getNodeName().equals("coordinates")) {
//            String coordData = aNode.getTextContent();
//            String[] coords = coordData.split("\\s+");
//            List<String> newCoords = new ArrayList<String>();
//            for (String coord : coords) {
//                if (coord.startsWith(",")) {
//                    int ndx = newCoords.size() - 1;
//                    newCoords.set(ndx, newCoords.get(ndx) + coord);
//                } else {
//                    newCoords.add(coord);
//                }
//            }
//            if (! newCoords.get(0).equals(newCoords.get(newCoords.size()-1))) {
//                newCoords.add(newCoords.get(0));
//            }
//            StringBuilder buff = new StringBuilder(newCoords.size()*32);
//            for (String newCoord : newCoords) {
//                buff.append(newCoord).append(" ");
//            }
//            aNode.setTextContent(buff.toString().trim());
//            coordCnt += newCoords.size();
//        } else {
//            NodeList children = aNode.getChildNodes();
//            for (int i=0;  i<children.getLength();  i++) {
//                Node cNode = children.item(i);
//                coordCnt += fixCoordinates(cNode);
//            }
//        }
//
//        return coordCnt;
//    }
//
//    /**
//     * Remove any "Point" data elements ... it'll mess up things when converting from
//     * KML to geometry when the boundary is inserted into PostGIS.
//     * 
//     * @param aNode node to fix
//     */
//    private static void removePoints(Node aNode) {
//        List<Node> points = new ArrayList<Node>();
//
//        NodeList children = aNode.getChildNodes();
//        for (int i=0;  i<children.getLength();  i++) {
//            Node cNode = children.item(i);
//            if (cNode.getNodeName().equals("Point")) {
//                points.add(cNode);
//            }
//        }
//
//        for (Node pNode : points) {
//            aNode.removeChild(pNode);
//        }
//    }
//
//    /**
//     * Get the XML format of a Node, w/out the &lt;?xml ...> header
//     * 
//     * @param aNode node
//     * @return node's XML
//     * 
//     * @throws TransformerFactoryConfigurationError
//     * @throws TransformerException
//     */
//    private static String getNodeXml(Node aNode) throws TransformerFactoryConfigurationError, TransformerException {
//        StringWriter writer = new StringWriter();
//        Transformer transformer = TransformerFactory.newInstance().newTransformer();
//        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
////        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//        transformer.transform(new DOMSource(aNode), new StreamResult(writer));
//        return writer.toString();
//    }

}
