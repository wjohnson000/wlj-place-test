package std.wlj.access;

import java.util.*;

import org.familysearch.standards.place.data.WritableDataService.VariantNameDef;
import org.familysearch.standards.place.service.DbWritableService;

import std.wlj.datasource.DbConnectionManager;

/**
 * This class updates the place-names ...
 * 
 * @author wjohnson000
 */
public class PlaceNameTest {

    static DbWritableService dbWService = null;

    public static void main(String[] args) throws Exception {
        List<VariantNameDef> varNames = Arrays.asList(
            makePlaceName(15451972, "Aberdeen St Nicholas - YY", "en", 437),
            makePlaceName(12345678, "Silly Name Deux", "en", 434),
//            makePlaceName(null, "St Nicholas Aberdeen", "en", 440),
            makePlaceName(15451000, "FR Nicholas Aberdeen", "fr", 440),
            makePlaceName(1111, "Aberdeen", "en", 440)
        );

        dbWService = new DbWritableService(DbConnectionManager.getDataSourceSams());
        dbWService.updatePlace(9000016, null, null, varNames, "wjohnson000", null);
        System.exit(0);
    }

    /**
     * Make a bunch of place-names from locale/text/common triplets
     * @param values locale1, text1, is-common1, locale2, text2, is-common2...
     * @return set of PlaceNameDTO instances
     */
    private static VariantNameDef makePlaceName(Integer id, String text, String locale, int typeId) {
            VariantNameDef vnDef = new VariantNameDef();
            if (id != null) vnDef.id = id;
            vnDef.typeId = typeId;
            vnDef.locale = locale;
            vnDef.text   = text;
        return vnDef;
    }
}
