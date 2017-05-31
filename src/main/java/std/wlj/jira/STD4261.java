package std.wlj.jira;


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

public class STD4261 {

    static int[] repIds = {
        994800,
        1729788,
        2219115,
        6723879,
        7722563,
        7726450,
        8045920,
        8045921,
        8193107,
        8754921,
        8779618,
        8779619,
        8815907,
        8815908,
        8815909,
        8962242,
        8962243,
        8962246,
        8962247,
        9178400,
        9288732,
        9288733,
        9301697,
        9303173,
        9305892,
        9393429,
        9403055,
        9403056,
        9414919,
        9418746,
        9418747,
        9430164,
        9430165,
        9556046,
        9567488,
        9567489,
        9600132,
        9600133,
        9600134,
        9600135,
        9600136,
        9600137,
        9659820,
        9659821,
        9659822,
        9659823,
        9659824,
        9659825,
        9659826,
        9659827,
        9778818,
        9778828,
        9778829,
        9778836,
        9778837,
        9778838,
        9778839,
        9778840,
        9873798,
        9873799,
        9873800,
        9961224,
        9973469,
        9973470,
        9973471,
        9973472,
        9984343,
        9984344,
        9984345,
        9984346,
        10015729,
        10015730,
        10015731,
        10034746,
        10034747,
        10034748,
        10034749,
        10068597,
        10068598,
        10068599,
        10068600,
        10068601,
        10148382,
        10148383,
        10148384,
        10148394,
        10148395,
        10148396,
        10250008,
        10250009,
        10250010,
        10257434,
        10257435,
    };

    public static void main(String... args) throws PlaceDataException, IOException {
//      SolrService solrService = SolrManager.localEmbeddedService();
        SolrService solrService = SolrManager.awsProdService(false);

        int count = 0;
        List<String> lines = new ArrayList<>();
        long then = System.nanoTime();
//        for (int repId=1;  repId<=11_111_111;  repId+=1119) {
        for (int repId : repIds) {
            if (++count % 250 == 0) System.out.println("CNT: " + count + " --> repId: " + repId);
            PlaceRepDoc prDoc = solrService.findPlaceRep(repId);
            if (prDoc != null) {
                System.out.println(prDoc);
                lines.add(toString(prDoc));
            }
        }
        long nnow = System.nanoTime();
        System.out.println("Time: " + (nnow - then) / 1_000_000.0);
        Files.write(Paths.get("C:/temp/pr-juris-new.txt"), lines, StandardOpenOption.CREATE);

        System.exit(0);
    }

    static String toString(PlaceRepBridge prBridge) {
        StringBuilder buff = new StringBuilder();

        if (prBridge == null) {
            buff.append("null");
        } else {
            String chainIds = Arrays.toString(prBridge.getJurisdictionIdentifiers());
            String jurisIds = Arrays.stream(((PlaceRepDoc)prBridge).getResolvedJurisdictions())
                .map(prDoc -> prDoc.getRepId())
                .map(id -> String.valueOf(id))
                .collect(Collectors.joining(", ", "[", "]"));
            buff.append("repId=").append(prBridge.getRepId());
            buff.append("; placeId=").append(prBridge.getPlaceId());
            buff.append("; deleteId=").append(prBridge.getDeleteId());
            buff.append("; chain=").append(chainIds);
            buff.append("; juris=").append(jurisIds);
            if (! chainIds.equals(jurisIds)) {
                buff.append("; DIFFERENT DIFFERENT");
            }
        }

        return buff.toString();
    }
}
