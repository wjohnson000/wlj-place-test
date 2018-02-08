package std.wlj.jira;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;

/**
 * Find documents with a "placeDeleteId" but with no "deleteId".  These are places that need
 * to be fixed ...
 * 
 * @author wjohnson000
 *
 */
public class PlaceFix_GenSQL {

    private static final int MAX_ROWS = 750;

    static final String[] beginSQL = {
        "DO $$",
        "DECLARE",
        "  tranx_id INTEGER := NEXTVAL('transaction_tran_id_seq');",
        "BEGIN",
        "  INSERT INTO transaction(tran_id, create_ts, create_id) VALUES(tranx_id, now(), 'system-delete-place-reps');"
    };

    static final String[] endSQL = {
        "END $$;"    
    };

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsProdConnection(false);
        System.out.println("Write-Ready: " + solrConn.isWriteReady());

        SolrQuery query = new SolrQuery("!deleteId:[* TO *] AND placeDeleteId:[* TO *]");
        query.setRows(MAX_ROWS);
        query.setSort("repId", SolrQuery.ORDER.asc);
        System.out.println("QRY: " + query);

        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

//        for (PlaceRepDoc doc : docs) {
//            String dispName = doc.getDisplayName("en");
//            if (dispName == null) dispName = doc.getDisplayName(doc.getPrefLocale());
//
//            StringBuilder buff = new StringBuilder();
//            buff.append(doc.getRepId());
//            buff.append("|").append(doc.getPlaceId());
//            buff.append("|").append(doc.getPlaceDeleteId());
//            buff.append("|").append(Arrays.toString(doc.getJurisdictionIdentifiers()));
//            buff.append("|").append(doc.getRevision());
//            buff.append("|").append(doc.getType());
//            buff.append("|").append(doc.getPrefLocale());
//            buff.append("|").append(doc.getCentroid());
//            buff.append("|").append(doc.isPublished());
//            buff.append("|").append(doc.isValidated());
//            buff.append("|").append(doc.getLastUpdateDate());
//            buff.append("|").append(dispName);
//
//            System.out.println(buff.toString());
//        }

        // Collect the place-id (owner-id) values
        Set<Integer> placeIds = docs.stream()
            .map(doc -> doc.getPlaceId())
            .collect(Collectors.toCollection(TreeSet::new));

        Arrays.stream(beginSQL).forEach(System.out::println);

        for (PlaceRepDoc doc : docs) {
            if (placeIds.contains(doc.getPlaceId())) {
                placeIds.remove(doc.getPlaceId());

                String startYr = (doc.getOwnerStartYear() == null) ? "null" : String.valueOf(doc.getOwnerStartYear());
                if (startYr.equals(String.valueOf(Integer.MIN_VALUE))) startYr = "null";
                String endYr = (doc.getOwnerEndYear() == null) ? "null" : String.valueOf(doc.getOwnerEndYear());
                if (endYr.equals(String.valueOf(Integer.MAX_VALUE))) endYr = "null";

                StringBuilder buff = new StringBuilder();
                buff.append("  INSERT INTO place(place_id, tran_id, from_year, to_year, delete_id) ");
                buff.append("VALUES (").append(doc.getPlaceId());
                buff.append(", ").append("tranx_id");
                buff.append(", ").append(startYr);
                buff.append(", ").append(endYr);
                buff.append(", ").append("null");
                buff.append(");");
                System.out.println(buff.toString());
            }
        }
        
        Arrays.stream(endSQL).forEach(System.out::println);

        System.exit(0);
    }
}
