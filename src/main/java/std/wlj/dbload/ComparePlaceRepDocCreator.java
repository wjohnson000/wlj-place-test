package std.wlj.dbload;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.loader.helper.PlaceRepDocCreator;
import org.familysearch.standards.place.dao.DAOFactory;
import org.familysearch.standards.place.dao.dbimpl.DAOFactoryImpl;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.DbConnectionManager;
import std.wlj.util.SolrManager;

public class ComparePlaceRepDocCreator {

    private static final double   DBL_CLOSE  = 0.00001;

    private static SolrConnection solrConn   = SolrManager.awsProdConnection(true);
    private static DataSource     dataSource = DbConnectionManager.getDataSourceSams();  //.getDataSourceAwsDev();
    private static DAOFactory     daoFactory = new DAOFactoryImpl(dataSource);

    public static void main(String...args) throws PlaceDataException {
        long time0 = System.nanoTime();
        PlaceRepDoc solrDoc = readDocFromSolr(234);
        long time1 = System.nanoTime();
        PlaceRepDoc dbDoc   = readDocFromDB(234);
        long time2 = System.nanoTime();
        System.out.println("Solr time: " + (time1-time0) / 1_000_000.0 + " --> " + solrDoc);
        System.out.println("  Db time: " + (time2-time1) / 1_000_000.0 + " --> " + dbDoc);

        compare(solrDoc, dbDoc);

        solrConn.shutdown();
    }

    static PlaceRepDoc readDocFromSolr(int repId) throws PlaceDataException {
        SolrQuery query = new SolrQuery("repId: " + repId);
        query.setRows(2);
        query.setSort("repId", SolrQuery.ORDER.desc);

        List<PlaceRepDoc> docs = solrConn.search(query);
        return (docs.isEmpty()) ? null : docs.get(0);
    }

    static PlaceRepDoc readDocFromDB(int repId) {
        PlaceRepDocCreator docCreator = new PlaceRepDocCreator(daoFactory);
        return docCreator.exportRepToSolr(repId);
    }

    static void compare(PlaceRepDoc solrDoc, PlaceRepDoc dbDoc) {
        System.out.println("\n=========================================================================");
        if (solrDoc == null  &&  dbDoc == null) {
            System.out.println("Docs not found in either 'SOLR' or 'DB'");
        } else if (solrDoc == null) {
            System.out.println("No 'SOLR' doc found for " + dbDoc.getId());
        } else if (dbDoc == null) {
            System.out.println("No 'DB' doc found for " + solrDoc.getId());
        }

        System.out.println("Comparing docs: '" + solrDoc.getId() + "'  vs.  '" + dbDoc.getId() + "'");
        compare("ID", solrDoc.getId(), dbDoc.getId());
        compare("REP-ID", solrDoc.getRepId(), dbDoc.getRepId());
        compare("PARENT-ID", solrDoc.getParentId(), dbDoc.getParentId());
        compare("OWNER-ID", solrDoc.getOwnerId(), dbDoc.getOwnerId());
        compare("REVISION", solrDoc.getRevision(), dbDoc.getRevision());
        compare("CENTROID", solrDoc.getCentroid(), dbDoc.getCentroid());
        compare("LATITUDE", solrDoc.getLatitude(), dbDoc.getLatitude());
        compare("LONGITUDE", solrDoc.getLongitude(), dbDoc.getLongitude());
        compare("START-YR", solrDoc.getStartYear(), dbDoc.getStartYear());
        compare("END-YR", solrDoc.getEndYear(), dbDoc.getEndYear());
        compare("OWNER-START-YR", solrDoc.getOwnerStartYear(), dbDoc.getOwnerStartYear());
        compare("OWNER-END-YR", solrDoc.getOwnerEndYear(), dbDoc.getOwnerEndYear());
        compare("DELETE-ID", solrDoc.getDeleteId(), dbDoc.getDeleteId());
        compare("PLACE-DELETE-ID", solrDoc.getPlaceDeleteId(), dbDoc.getPlaceDeleteId());
        compare("TYPE", solrDoc.getType(), dbDoc.getType());
        compare("PUBLISHED", solrDoc.getPublished(), dbDoc.getPublished());
        compare("VALIDATED", solrDoc.getValidated(), dbDoc.getValidated());
        compare("PREF-LOCALE", solrDoc.getPrefLocale(), dbDoc.getPrefLocale());
        compare("UUID", solrDoc.getUUID(), dbDoc.getUUID());
        compare("TYPE-GROUP", solrDoc.getTypeGroup(), dbDoc.getTypeGroup());
        compare("PREF-BOUNDARY", solrDoc.getPreferredBoundaryId(), dbDoc.getPreferredBoundaryId());
        compare("CREATE-DATE", solrDoc.getCreateDate(), dbDoc.getCreateDate());
        compare("UPDATE_DATE", solrDoc.getLastUpdateDate(), dbDoc.getLastUpdateDate());
        compare("REP-ID-CHAIN", solrDoc.getRepIdChain(), dbDoc.getRepIdChain());
        compare("VARIANT-NAMES", solrDoc.getVariantNames(), dbDoc.getVariantNames());
        compare("DISPLAY-NAMES", solrDoc.getDisplayNames(), dbDoc.getDisplayNames());
        compare("ATTRIBUTES", solrDoc.getAttributes(), dbDoc.getAttributes());
        compare("CITATIONS", solrDoc.getCitations(), dbDoc.getCitations());
        compare("XREF-DATA", solrDoc.getExtXrefs(), dbDoc.getExtXrefs());
        compare("ALT-JURIS", solrDoc.getAltJurisdictions(), dbDoc.getAltJurisdictions());
        compare("NAMES", solrDoc.getNames(), dbDoc.getNames());
        compare("APP-DATA", solrDoc.getValidated(), dbDoc.getValidated());
    }

    static void compare(String field, int solrVal, int dbVal) {
        if (solrVal == dbVal) {
            System.out.println("SAME -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        }
    }

    static void compare(String field, Integer solrVal, Integer dbVal) {
        if (solrVal == null  &&  dbVal == null) {
            System.out.println("SAME -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (solrVal == null) {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (dbVal == null) {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (solrVal.intValue() == dbVal.intValue()) {
            System.out.println("SAME -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        }
    }

    static void compare(String field, Double solrVal, Double dbVal) {
        if (solrVal == null  &&  dbVal == null) {
            System.out.println("SAME -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (solrVal == null) {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (dbVal == null) {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (solrVal.doubleValue() == dbVal.doubleValue()) {
            System.out.println("SAME -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (Math.abs(solrVal.doubleValue() - dbVal.doubleValue()) < DBL_CLOSE) {
            System.out.println("SAME -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        }
    }

    static void compare(String field, String solrVal, String dbVal) {
        if (solrVal == null  &&  dbVal == null) {
            System.out.println("SAME -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (solrVal == null) {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (dbVal == null) {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (solrVal.equals(dbVal)) {
            System.out.println("SAME -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        }
    }

    static void compare(String field, Date solrVal, Date dbVal) {
        if (solrVal == null  &&  dbVal == null) {
            System.out.println("SAME -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (solrVal == null) {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (dbVal == null) {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (solrVal.equals(dbVal)) {
            System.out.println("SAME -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        }
    }

    static void compare(String field, Integer[] solrVal, Integer[] dbVal) {
        if (solrVal == null  &&  dbVal == null) {
            System.out.println("SAME -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (solrVal == null) {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + Arrays.toString(dbVal));
        } else if (dbVal == null) {
            System.out.println("DIFF -- " + field + " --> " + Arrays.toString(solrVal) + " vs. " + dbVal);
        } else if (solrVal.length != dbVal.length) {
            System.out.println("DIFF -- " + field + " --> " + Arrays.toString(solrVal) + " vs. " + Arrays.toString(dbVal));
        } else {
            boolean same = true;
            for (int ndx=0;  ndx<solrVal.length;  ndx++) {
                same = same & solrVal[ndx].intValue() == dbVal[ndx].intValue();
            }
            if (same) {
                System.out.println("SAME -- " + field + " --> " + Arrays.toString(solrVal) + " vs. " + Arrays.toString(dbVal));
            } else {
                System.out.println("DIFF -- " + field + " --> " + Arrays.toString(solrVal) + " vs. " + Arrays.toString(dbVal));
            }
        }
    }

    static void compare(String field, List<String> solrVal, List<String> dbVal) {
        if (solrVal == null  &&  dbVal == null) {
            System.out.println("SAME -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (solrVal == null) {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (dbVal == null) {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else if (solrVal.size() != dbVal.size()) {
            System.out.println("DIFF -- " + field + " --> " + solrVal + " vs. " + dbVal);
        } else {
            Set<String> solrSet = new TreeSet<>(solrVal);
            Set<String> dbSet   = new TreeSet<>(dbVal);
            solrSet.removeAll(dbVal);
            dbSet.removeAll(solrVal);

            if (solrSet.isEmpty()  &&  dbSet.isEmpty()) {
                System.out.println("SAME -- " + field + " --> " + solrVal + " vs. " + dbVal);
            } else {
                System.out.println("DIFF -- " + field + " --> SOLR-only: " + solrVal);
                System.out.println("DIFF -- " + field + " --> DB-only: " + dbVal);
            }
        }
    }
}
