/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import org.familysearch.standards.place.ws.model.CentroidModel;
import org.familysearch.standards.place.ws.util.POJOMarshalUtil;

/**
 * @author wjohnson000
 *
 */
public class TestCentroidXMLization {

    public static void main(String...args) {
        CentroidModel centroid = new CentroidModel();

        int count = 0;
        double value = -180.0;
        while (value <= 180.0) {
            if (++count % 1_000_000 == 0) {
                System.out.println(" ... " + value);
            }
            centroid.setLatitude(value);
            centroid.setLongitude(value);
            String xml = POJOMarshalUtil.toXML(centroid);

            int ndx0 = xml.indexOf("<latitude>");
            int ndx1 = xml.indexOf(".", ndx0+1);
            int ndx2 = xml.indexOf("</latitude>", ndx1+1);
            if (ndx0 > 0  &&  ndx1 > ndx0  &&  ndx2 > ndx1) {
                String decimals = xml.substring(ndx1+1, ndx2);
                if (decimals.length() > 5) {
                    System.out.println(">> " + value + " --> " + decimals);
                }
            }

            value += 0.000001;
        }
    }
}
