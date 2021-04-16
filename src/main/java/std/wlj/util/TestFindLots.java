package std.wlj.util;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;
//import org.familysearch.standards.place.util.NamePriorityHelper;


public class TestFindLots {

    /** Sample data for interpretation ... */
    private static String[] textes = {
        "Provo, Ut, Ut",
        "Darlington, South Carolina",
        "la asuncion, tecamachalco, puebla, mexico",
        "ostre gausdal,opland,norway",
        "Mogulreich",
        "heinolan mlk,mikkeli,finland",
        "アルゼンチン",
        "whittlesford, cambridge",
        "Mexiko-Stadt",
        "trierweiler, germany",
        "lubusz voivodeship",
        "weymouth, dorset",
        "pennsylvania, united states",
        "davenport, scott, iowa",
        "la purisima concepcion, tula, hidalgo, mexico",
        "Nieuw-Zeeland",
        "butler county ohio",
        "cherry hill, new jersey",
        "san antonio de cortes, cortes, honduras",
        ", skinnskatteberg, vastmanland, sweden",
        "Bangladesz",
        "glamorgan, wales",
        "kanazawa, ishikawa",
        "alsace lorraine",
        ", south australia, australia",
        "sankt petri,berlin stadt,brandenburg,prussia",
        "cumberland county, pennsylvania",
        "nova scotia, canada",
        "san juan bautista, estella, navarra, spain",
        "baranya,,,hungary",
        "wayne, michigan",
        "bishopwearmouth,durham,england",
        "Bulgária",
        "holmens sogn, kobenhavn, kobenhavn, denmark",
        "Sudáfrica",
        "beverly, essex, massachusetts",
        "valkeala,viipuri,finland",
        "inmaculada concepción, graneros, tucumán, argentina",
        "amsterdam, noord-holland, netherlands",
        "chengdu",
        "katholisch, kornelimuenster, rheinland, prussia",
        "earsdon by north shields, northumberland, england",
        ", floyd, kentucky",
        "dawley magna,shropshire,england",
        "montrose, angus",
        "parramatta, new south wales",
        "bloomington, Indiana",
        "san phelipe or san felipe,linares,nuevo leon,mexico",
        "baltimore md",
        "gruenhain,zwickau,saxony",
        ", umea lands, vasterbotten, sweden",
        "st. george the martyr,southwark,london,englan",
        "vernal, uintah, utah",
        "dane county, wisconsin",
        "Joshua-Tree-Nationalpark",
        ",, indiana, usa",
        "gosford, new south wales",
        "wye, kent, england",
        "monroe county arkansas",
        "aschersleben, sachsen, preussen, germany",
        "st. andrew par. reg. and nonconf.,newcastle upon tyne,northumberland,england",
        "mono county, california",
        "ilkeston",
        "notre dame, bar-le-duc, meuse, france",
        "Grønland",
        "świętokrzyskie voivodeship",
        "zala, zala,,hungary",
        "dalton in furness, lancashire",
        "chihuahua, chihuahua",
        "billigheim",
        "saint michael,barbadoes,caribbean",
        "middlesex county, massachusetts",
        "merrimon carteret co., nc",
        "Congo-Kinshasa",
        "Falklandinseln",
        "san pedro, tacna, tacna, peru",
        "ennighüffen,westfalen,preussen,germany"
    };

    public static void main(String... args) throws PlaceDataException {
        SolrService solrService = SolrManager.localEmbeddedService();
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        System.out.println("Place-Type count: " + solrService.getTypes(TypeBridge.TYPE.PLACE).size());
        System.out.println("Place-Name count: " + solrService.getTypes(TypeBridge.TYPE.NAME).size());
//        System.out.println("Name-Priority: " + NamePriorityHelper.getInstance());

        // Do a couple of interpretations for fun ...
        for (String throwAway : new String[] { "Utah, USA", "Darlington, South Carolina" } ) {
            PlaceRequestBuilder builder = placeService.createRequestBuilder(throwAway, StdLocale.ENGLISH);
            placeService.requestPlaces(builder.getRequest());
        }

        // Do the **real** interpretations
        System.out.println("Text|TotalTime|Assembly|CndLookupTime|CndTime|ParseTime|PreScoreCnt|RawCnt|Scorer.NVPS|Scorer.EMS|Scorer.FLTHS|Results");
        for (String text : textes) {
            PlaceRequestBuilder builder = placeService.createRequestBuilder(text, StdLocale.ENGLISH);
            builder.setShouldCollectMetrics(true);

            PlaceResults results = placeService.requestPlaces(builder.getRequest());
            System.out.print("|");
            for (PlaceRepresentation placeRep : results.getPlaceRepresentations()) {
                System.out.print(placeRep + ",");
            }
            System.out.println();

            try { Thread.sleep(250); } catch(Exception ex) { }
        }

        System.exit(0);
    }
}
