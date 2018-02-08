package std.wlj.dbload;

import java.sql.SQLException;
import java.util.Arrays;

import javax.sql.DataSource;

import org.familysearch.standards.loader.helper.PlaceRepDocCreator;
import org.familysearch.standards.place.dao.DAOFactory;
import org.familysearch.standards.place.dao.dbimpl.DAOFactoryImpl;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;

import std.wlj.datasource.DbConnectionManager;

public class RunPlaceRepDocCreator {

    static final int MAX_ROWS = 20;

    public static void main(String...args) throws SQLException {
        DataSource ds = DbConnectionManager.getDataSourceAwsDev();
        DAOFactory daoFactory = new DAOFactoryImpl(ds);
        PlaceRepDocCreator docCreator = new PlaceRepDocCreator(daoFactory);

        PlaceRepDoc doc = docCreator.exportRepToSolr(5685030);
        System.out.println("\nID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
        System.out.println("  Place:  " + doc.getPlaceId());
        System.out.println("  Par-Id: " + doc.getParentId());
        System.out.println("  Typ-Id: " + doc.getType());
        System.out.println("  Locale: " + doc.getPrefLocale());
        System.out.println("  P-Rang: " + doc.getOwnerStartYear() + " - " + doc.getOwnerEndYear());
        System.out.println("  FromTo: " + doc.getFromYear() + " - " + doc.getToYear());
        System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
        System.out.println("  Locatn: " + doc.getCentroid() + " . " + doc.getLatitude() + "," + doc.getLongitude());
        System.out.println("  Publsh: " + doc.isPublished());
        System.out.println("  Validd: " + doc.isValidated());
        System.out.println("  PrefBd: " + doc.getPreferredBoundaryId());
        System.out.println("  Creatd: " + doc.getCreateDate() + " . " + doc.getLastUpdateDate());
        System.out.println("  TGroup: " + doc.getTypeGroup());

        doc.getDisplayNames().stream().limit(MAX_ROWS).forEach(dispName -> System.out.println("  D-Name: " + dispName));
        doc.getVariantNames().stream().limit(MAX_ROWS).forEach(varName -> System.out.println("  V-Name: " + varName));
        doc.getNames().stream().limit(MAX_ROWS).forEach(nName -> System.out.println("  N-Name: " + nName));
        doc.getAttributes().stream().limit(MAX_ROWS).forEach(attr -> System.out.println("    Attr: " + attr));
        doc.getCitations().stream().limit(MAX_ROWS).forEach(citn -> System.out.println("    Citn: " + citn));
        doc.getAltJurisdictions().stream().limit(MAX_ROWS).forEach(altJuris -> System.out.println("    AltJ: " + altJuris));
        doc.getExtXrefs().stream().limit(MAX_ROWS).forEach(xref -> System.out.println("    Xref: " + xref));
        doc.getAppData().stream().limit(MAX_ROWS).forEach(appData -> System.out.println("    AppD: " + appData));

        System.exit(0);
    }
}
