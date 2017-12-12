package std.wlj.solr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.SolrManager;

public class SearchLocalChainOldVsNew {

    public static void main(String... args) throws PlaceDataException, IOException {
//        SolrService solrSvc = SolrManager.localEmbeddedService("D:/solr/stand-alone-6.1.0");
        SolrService solrSvc = SolrManager.localHttpService();

        long total = 0;
        List<String> lines = new ArrayList<>();

        for (int repId = 3460372;  repId<11_111_111;  repId += 11_111_111) {
            System.out.println(repId);
            PlaceRepDoc repDoc = solrSvc.findPlaceRep(repId);
            if (repDoc != null) {
                long time0 = System.nanoTime();
                int[] chain = repDoc.getJurisdictionIdentifiers();
                PlaceRepBridge[] juris = repDoc.getJurisdictions();
                long time1 = System.nanoTime();
                total += time1 - time0;

                String newPar = "";
                if (chain.length > 1  &&  repDoc.getParentId() != chain[1]) {
                    newPar = "** NEW PARENT **";
                }
                
                StringBuilder buff = new StringBuilder();
                buff.append(repDoc.getRepId());
                buff.append("|").append(repDoc.getParentId());
                buff.append("|").append(Arrays.toString(chain));
                buff.append("|").append(newPar);
                buff.append("|").append(Arrays.stream(juris).map(pr -> String.valueOf(pr.getRepId())).collect(Collectors.joining(",", "[", "]")));
                lines.add(buff.toString());
            }
        }

//        Files.write(Paths.get("C:/temp/prod-rep-chain.txt"), lines, StandardOpenOption.CREATE);

        lines.forEach(System.out::println);

        System.out.println("TIME: " + total / 1_000_000.0);
        System.exit(0);
    }
}
