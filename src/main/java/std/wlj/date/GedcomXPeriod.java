/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import org.familysearch.standards.date.gedcomx.GedcomXGenPeriod;
import org.familysearch.standards.date.api.model.GenPeriod;

/**
 * @author wjohnson000
 *
 */
public class GedcomXPeriod {

    public static void main(String...args) throws Exception {
        String[] textes = {
            "31y",
            "p31y",
            "P31Y",
            "32y",
            "p32y",
            "P32Y",
        };

        for (String text : textes) {
            boolean isGP = GedcomXGenPeriod.isValidGedcomx(text);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("Text: " + text);
            System.out.println("  GP? " + isGP);
            if (isGP) {
                try {
                    GenPeriod gp = GedcomXGenPeriod.from(text);
                    System.out.println("   GP: " + gp.toGEDCOMX());
                } catch (Exception ex) {
                    System.out.println("   EX: " + ex.getMessage());
                }
            }
        }
    }
}
