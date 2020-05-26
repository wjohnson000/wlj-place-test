/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wjohnson000
 *
 */
public interface NameDefParser {

    // MALE and FEMALE characters, indicating gender of name
    static final String MALE_CHAR = "♂";
    static final String FEMALE_CHAR = "♀";

    // HTML tags that can be kept in text fields
    static final Set<String> OK_TEXT_TAGS = new HashSet<>(Arrays.asList("i", "b", "p", "span"));

    NameDef parseXml(String xml);

    default String extractTypeFromDefinition(String nameDefn) {
        if (nameDefn != null) {
            if (nameDefn.toLowerCase().contains(" pet ")  ||  nameDefn.toLowerCase().contains(">pet ")  ||  nameDefn.toLowerCase().startsWith("pet ")) {
                return "PET";
            } else if (nameDefn.toLowerCase().contains(" short ")  ||  nameDefn.toLowerCase().contains(">short ")  ||  nameDefn.toLowerCase().startsWith("short ")) {
                return "SHORT";
            }
        }
        return "REGULAR";
    }

    default String extractTypeFromDefinitionVariant(String nameDefn) {
        if (nameDefn != null) {
            if (nameDefn.toLowerCase().contains("pet")) {
                return "PET";
            } else if (nameDefn.toLowerCase().contains("short")) {
                return "SHORT";
            }
        }
        return "COGNATE";
    }
}
