/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.validator;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.place.access.validator.MessageFactory;
import org.familysearch.standards.place.access.validator.PlaceLockValidator;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.WritableDataService.VariantNameDef;
import org.familysearch.standards.place.service.DbReadableService;

import std.wlj.datasource.DbConnectionManager;

/**
 * @author wjohnson000
 *
 */
public class PlaceNameValidator {

    public static void main(String...args) throws SQLException, PlaceDataException {
        MessageFactory mFact = new MessageFactory();
        DbReadableService dbRead = DbConnectionManager.getDbServicesSams().readService;
        PlaceLockValidator validator = new PlaceLockValidator(dbRead, mFact);

        System.out.println("Ready to validate .... begin");
        validator.validateUpdatePlace(2889134, null, null, getVarNames());
        System.out.println("Ready to validate .... done");
    }

    static List<VariantNameDef> getVarNames() {
        return Arrays.asList(
                makeVarName(0, 437, "my-Latn-x-nga", "Khao Wan"));
//            makeVarName(5150662, 437, "my-Latn-x-nga", "Khao Lan"),
//            makeVarName(5150663, 436, "my-Latn-x-nga", "LAN KHAO"),
//            makeVarName(15626552, 437, "th", "เขาล้าน"),
//            makeVarName(0, 437, "my-Latn-x-nga", "Khao Wan"));
    }

    static VariantNameDef makeVarName(int id, int typeId, String locale, String text) {
        VariantNameDef varName = new VariantNameDef();

        varName.id = id;
        varName.typeId = typeId;
        varName.locale = locale;
        varName.text = text;

        return varName;
    }
}
