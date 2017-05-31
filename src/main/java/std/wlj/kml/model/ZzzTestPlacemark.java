package std.wlj.kml.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import std.wlj.marshal.POJOMarshalUtil;

public class ZzzTestPlacemark {
    public static void main(String... args) throws IOException {
        String placemarkRaw = new String(Files.readAllBytes(Paths.get("C:/temp/Placemark.kml")));
        if (placemarkRaw.charAt(0) > 255) {
            placemarkRaw = placemarkRaw.substring(1);
        }

        PlacemarkModel placemark = POJOMarshalUtil.fromXML(placemarkRaw, PlacemarkModel.class);
        System.out.println("PYG: " + placemark);
        System.out.println("PYG: " + placemark.getName());
        System.out.println("PYG: " + placemark.getDescription());
        System.out.println("PYG: " + placemark.getTimeSpan());
        System.out.println("PYG: " + placemark.getGeometry());

        String newKml = POJOMarshalUtil.toXML(placemark);
        System.out.println("KML:\n" + newKml);
    }
}
