package std.wlj.solr;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.access.validator.MessageFactory;
import org.familysearch.standards.place.access.validator.NameValidator;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;

public class ValidateRepNames {

    static NameValidator validator = new NameValidator(null, new MessageFactory());

    public static void main(String... args) throws PlaceDataException {

        SolrConnection solrConn = SolrManager.awsProdConnection(true);
        System.out.println("Write-Ready: " + solrConn.isWriteReady());

        SolrQuery query = new SolrQuery("repId:215");
        query.setRows(5);
        query.setSort("repId", SolrQuery.ORDER.desc);

        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        for (PlaceRepDoc doc : docs) {
            System.out.println("\nID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
            System.out.println("  Place:  " + doc.getPlaceId());
            System.out.println("  Par-Id: " + doc.getParentId());
            System.out.println("  Locale: " + doc.getPrefLocale());
            System.out.println("  Locatn: " + doc.getCentroid() + " . " + doc.getLatitude() + "," + doc.getLongitude());
            System.out.println("  Publsh: " + doc.isPublished());
            System.out.println("  Validd: " + doc.isValidated());
            System.out.println("  Creatd: " + doc.getCreateUser() + " . " + doc.getCreateDate());
            System.out.println("  Updatd: " + doc.getLastUpdateUser() + " . " + doc.getLastUpdateDate());

            doc.getDisplayNameMap().entrySet().stream().forEach(name -> validateDisplayName(name.getKey(), name.getValue()));
            doc.getAllVariantNames().stream().forEach(name -> System.out.println("  V-Name: " + name.getName().getLocale() + " . " + name.getName().get()));
        }

        System.exit(0);
    }

    static void validateDisplayName(String locale, String text) {
        try {
            System.out.println("  D-Name: " + locale + " . " + text);
            validator.validateDisplayName(text);
        } catch(Exception ex) {
            System.out.println("        : ** INVALID ** " + ex.getMessage());
        }
    }

    static void validateVariantName(StdLocale locale, String text) {
        try {
            System.out.println("  V-Name: " + locale + " . " + text);
            validator.validateVariantName(text);
        } catch(Exception ex) {
            System.out.println("        : ** INVALID ** " + ex.getMessage());
        }
    }
}

