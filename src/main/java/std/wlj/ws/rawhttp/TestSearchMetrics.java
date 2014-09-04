package std.wlj.ws.rawhttp;

import java.io.*;
import java.net.*;

import org.familysearch.standards.place.ws.model.MetricsModel;
import org.familysearch.standards.place.ws.model.PlaceSearchResultModel;
import org.familysearch.standards.place.ws.model.PlaceSearchResultsModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.ScorerModel;


public class TestSearchMetrics {

    /** Base URL of the application */
    private static String baseUrl = "http://place-ws-dev.dev.fsglobal.org/int-std-ws-place/places";
//    private static String baseUrl = "http://ec2-54-204-45-169.compute-1.amazonaws.com:8080/std-ws-place/places";

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

    private static final double ONE_MILLION = 1000000.0;


    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        PrintWriter pwOut = new PrintWriter(new FileWriter(new File("C:/temp/results-new.txt")));

        for (int loop=4;  loop<=6;  loop++) {
            for (String textx : textes) {
                long time = System.nanoTime();
                RootModel model = doSearch(textx);
                time = System.nanoTime() - time;

                if (model == null  ||  model.getSearchResults() == null) {
                    continue;
                }

                PlaceSearchResultsModel resultsModel = model.getSearchResults().get(0);
                MetricsModel metrics = resultsModel.getMetrics();

                StringBuilder buff = new StringBuilder();
                buff.append(textx);
                buff.append("|").append(loop);
                buff.append("|").append(time / ONE_MILLION);
                buff.append("|").append(metrics.getTimings().getIdentifyCandidatesLookupTime() / ONE_MILLION);
                buff.append("|").append(metrics.getTimings().getParseTime() / ONE_MILLION);
                buff.append("|").append(metrics.getTimings().getScoringTime() / ONE_MILLION);
                ScorerModel nvpScorer = null;
                for (ScorerModel scorer : metrics.getScorers().getScorers()) {
                    if (scorer.getName().equals("NameVariantPriorityScorer")) {
                        nvpScorer = scorer;
                    }
                }
                if (nvpScorer == null) {
                    buff.append("|-1");
                } else {
                    buff.append("|").append(nvpScorer.getTime() / ONE_MILLION);
                }

                buff.append("|").append(resultsModel.getCount());
                for (PlaceSearchResultModel resultModel : resultsModel.getResults()) {
                    buff.append("|").append(resultModel.getRep().getId());
                }
                pwOut.println(buff.toString());
            }
        }

        pwOut.close();
        System.exit(0);
    }

    private static RootModel doSearch(String text) throws Exception {
        URL url = new URL(baseUrl + "/request?text=" + text + "&metrics=true");
        return TestUtil.doGET(url);
    }
}
