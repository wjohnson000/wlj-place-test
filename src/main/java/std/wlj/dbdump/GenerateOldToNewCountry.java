/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;

/**
 * @author wjohnson000
 *
 */
public class GenerateOldToNewCountry {

    private static SolrConnection solrConn;

    private static Set<Integer> skipMe = new HashSet<>();
    static {
        skipMe.add(15);
        skipMe.add(20);
    }

    private static List<String> results = new ArrayList<>();

    public static void main(String...args) throws Exception {
//      solrConn = SolrManager.localEmbeddedConnection("C:/D-drive/solr/standalone-7.7.1");
        solrConn = SolrManager.awsIntConnection(false);

        List<PlaceRepDoc> countries = getCountries();
        List<PlaceRepDoc> oldCountries = countries.stream()
                .filter(doc -> doc.getEndYear() != null  &&  doc.getEndYear().intValue() < 2020)
                .collect(Collectors.toList());

        oldCountries.forEach(doc -> checkThisCountry(doc));
        System.out.println("\n\n");
        results.forEach(System.out::println);

        solrConn.shutdown();
    }

    static List<PlaceRepDoc> getCountries() {
        String query = "parentId: [-1 TO 0] AND repId: [1 TO *]";

        return getDocs(query);
    }

    static void checkThisCountry(PlaceRepDoc doc) {
        System.out.println("============================================================");
        System.out.println("ID: " + doc.getId() + " .. " + doc.getDisplayNameMap().getOrDefault("en", "Unknown"));
        System.out.println("    Years: " + doc.getStartYear() + " to " + doc.getEndYear());
        if (skipMe.contains(doc.getRepId())) {
            return;
        }

        String query = "parentId: " + doc.getRepId() + " AND repId: [1 TO *]";
        List<PlaceRepDoc> kids = getDocs(query);
        System.out.println("    Kids: " + kids.size());

        boolean first = true;
        for (PlaceRepDoc kid : kids) {
            query = "ownerId: " + kid.getPlaceId() + " AND endYear: [2019 TO *]";
            List<PlaceRepDoc> reps = getDocs(query);
            for (PlaceRepDoc rep : reps) {
                if (rep.getParentId() < 1  &&  (rep.getEndYear() == null  ||  rep.getEndYear() > 2019)) {
                    System.out.println("   " + kid.getId() + " .. " + getName(kid));
                    System.out.println("       Years: " + kid.getStartYear() + " to " + kid.getEndYear());
                    System.out.println("      " + rep.getId() + " .. " + getName(rep));
                    System.out.println("          Years: " + rep.getStartYear() + " to " + rep.getEndYear());

                    StringBuilder buff = new StringBuilder();
                    if (first) {
                        results.add("");
                        first = false;
                        buff.append(doc.getRepId());
                        buff.append("|").append(getName(doc));
                        buff.append("|").append(doc.getStartYear()).append("|").append(doc.getEndYear());
                    } else {
                        buff.append("|||");
                    }

                    buff.append("|").append(rep.getRepId());
                    buff.append("|").append(getName(rep));
                    buff.append("|").append(rep.getStartYear()).append("|").append(rep.getEndYear());
                    results.add(buff.toString());
                }
            }
        }
    }

    static List<PlaceRepDoc> getDocs(String query) {
        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.setSort("repId", SolrQuery.ORDER.asc);
        solrQuery.addFilterQuery("deleteId: 0");
        solrQuery.setRows(1000);

        try {
            return solrConn.search(solrQuery);
        } catch (PlaceDataException e) {
            System.out.println(">>>> Query: " + query + " --> " + e.getMessage());
            return Collections.emptyList();
        }
    }

    static String getName(PlaceRepDoc doc) {
        String name = doc.getDisplayNameMap().get("en");

        if (name == null) {
            name = doc.getDisplayNameMap().get(doc.getPreferredLocale().toString());
        }
        if (name == null) {
            name = doc.getDisplayNameMap().values().stream().findFirst().orElse("Unknown");
        }

        return name;
    }
}
