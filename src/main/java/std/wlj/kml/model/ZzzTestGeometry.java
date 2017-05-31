package std.wlj.kml.model;

import java.nio.file.Files;
import java.nio.file.Paths;

import std.wlj.marshal.POJOMarshalUtil;

public class ZzzTestGeometry {
    public static void main(String... args) throws Exception {
        String polygonRaw = new String(Files.readAllBytes(Paths.get("C:/temp/Polygon-nons.kml")));
        if (polygonRaw.charAt(0) > 255) {
            polygonRaw = polygonRaw.substring(1);
        }

//        PolygonModel polygon = POJOMarshalUtil.fromXML(polygonRaw, PolygonModel.class);
//        System.out.println("PYG: " + polygon);
//        System.out.println("PYG: " + polygon.getExtrude());
//        System.out.println("PYG: " + polygon.getTessellate());
//        System.out.println("PYG: " + polygon.getAltitudeMode());
//        System.out.println("PYG: " + polygon.getOuterBoundaryIs());
//        System.out.println("PYG: " + polygon.getInnerBoundaryIs());
//
//        String newKml = POJOMarshalUtil.toXML(polygon);
//        System.out.println("KML:\n" + newKml);

        GeometryModel polygon = POJOMarshalUtil.fromKml(polygonRaw);
        System.out.println("PYG: " + polygon);
//        System.out.println("PYG: " + polygon.getExtrude());
//        System.out.println("PYG: " + polygon.getTessellate());
//        System.out.println("PYG: " + polygon.getAltitudeMode());
//        System.out.println("PYG: " + polygon.getOuterBoundaryIs());
//        System.out.println("PYG: " + polygon.getInnerBoundaryIs());

        String newKml = POJOMarshalUtil.toXML(polygon);
        System.out.println("KML:\n" + newKml);
    }
}
