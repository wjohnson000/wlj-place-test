package std.wlj.solr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;

public class SearchBetaByDeleteFromDate {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T00:00:00Z'"); 

    public static void main(String...args) throws PlaceDataException {
        mainNew();
        System.exit(0);
    }

    public static void mainNew() throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsIntConnection(false);
        List<Integer> delRepIds = solrConn.getDeletedRepIds(makeDate(2017, 1, 01));
//        List<Integer> delRepIds = solrConn.getDeletedRepIds(null);
        delRepIds.forEach(System.out::println);
        System.out.println("\nTotal: " + delRepIds.size());
    }

    public static void mainOld() throws PlaceDataException {
        String ddd = DATE_FORMAT.format(makeDate(2016, 12, 01));
        System.out.println("NOW: " + ddd);
        SolrConnection solrConn = SolrManager.awsIntConnection(false);

        // Do a look-up by documents ...
        SolrQuery query = new SolrQuery("lastUpdateDate:[" + ddd + " TO *]");
        query.setRows(10);
        query.addFilterQuery("deleteId: [* TO *]");
        query.setFields("repId", "deleteId", "lastUpdateDate");

        System.out.println("Q: " + query);
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        for (PlaceRepDoc doc : docs) {
            System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
            System.out.println("  Rep-Id: " + doc.getRepId());
            System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
            System.out.println("   Dates: " + doc.getLastUpdateDate());
        }
    }

    @SuppressWarnings("deprecation")
    static Date makeDate(int year, int month, int day) {
        Date aDate = new Date();
        aDate.setYear(year - 1900);
        aDate.setMonth(month - 1);
        aDate.setDate(day);
        return aDate;
    }
}
