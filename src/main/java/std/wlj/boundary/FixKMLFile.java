package std.wlj.boundary;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
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


/**
 * Parse a KML file, looking for "&lt;coordinates&gt;" tags where the first and last
 * entry are NOT identical.  Fix these by adding the first coordinate value at the
 * end to ensure that the polygon is closed.
 * 
 * @author wjohnson000
 *
 */
public class FixKMLFile {

    static List<List<String>> allPaths = new ArrayList<List<String>>();

    public static void main(String... args) throws Exception {
        File kmlDir = new File("C:/tools/gis-files/kml-files");
        File[] files = kmlDir.listFiles();
        for (File aFile : files) {
            System.out.println("Processing file: " + aFile);
            if (aFile.getAbsolutePath().endsWith(".kml")) {
                parseFile(aFile);
            }
        }

        for (List<String> aPath : allPaths) {
            for (String tag : aPath) {
                System.out.print(tag + "  ");
            }
            System.out.println();
        }
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
                    fixCoordinates(child);
                    boundary = getNodeXml(child);
                } else if (child.getNodeName().equals("Polygon")) {
                    fixCoordinates(child);
                    boundary = getNodeXml(child);
                } else if (child.getNodeName().equals("LineString")) {
                    fixCoordinates(child);
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
     * Fix any "coordinates" tag where the the first and last entry aren't the same.
     * 
     * @param aNode node to fix
     */
    private static void fixCoordinates(Node aNode) {
        if (aNode.getNodeName().equals("coordinates")) {
            String coordData = aNode.getTextContent();
            String[] coords = coordData.split("\\s+");
            List<String> newCoords = new ArrayList<String>();
            for (String coord : coords) {
                if (coord.startsWith(",")) {
                    int ndx = newCoords.size() - 1;
                    newCoords.set(ndx, newCoords.get(ndx) + coord);
                } else {
                    newCoords.add(coord);
                }
            }
            if (! newCoords.get(0).equals(newCoords.get(newCoords.size()-1))) {
                newCoords.add(newCoords.get(0));
            }
            StringBuilder buff = new StringBuilder(newCoords.size()*32);
            for (String newCoord : newCoords) {
                buff.append(newCoord).append(" ");
            }
            aNode.setTextContent(buff.toString().trim());
        } else {
            NodeList children = aNode.getChildNodes();
            for (int i=0;  i<children.getLength();  i++) {
                Node cNode = children.item(i);
                fixCoordinates(cNode);
            }
        }
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
