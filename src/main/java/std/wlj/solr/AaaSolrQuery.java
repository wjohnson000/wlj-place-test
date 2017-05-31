package std.wlj.solr;

import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrQueryBuilder;

import java.util.Date;

import org.familysearch.standards.place.data.solr.PlaceRepDoc.SolrField;

public class AaaSolrQuery {
    @SuppressWarnings("deprecation")
    public static void main(String...args) {
        Date now = new Date();
        now.setYear(116);
        now.setMonth(11);
        now.setDate(14);
        System.out.println("DATE: " + now);
        SolrQueryBuilder sqb = new SolrQueryBuilder();
        
        sqb.andInclusive(SolrField.LAST_UPDATE_DATE, now, null);
        sqb.filterNot(PlaceRepDoc.SolrField.DELETE_ID, "[* TO * ]", false);
        sqb.setMaxResults(42);
        sqb.setIncludedFields(SolrField.REP_ID);

        System.out.println("SQB: " + sqb);
        System.out.println("  Q: " + sqb.getQuery());
    }
}
