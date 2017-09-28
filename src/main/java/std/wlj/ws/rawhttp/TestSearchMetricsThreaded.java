package std.wlj.ws.rawhttp;

import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.familysearch.standards.place.ws.model.MetricModel;
import org.familysearch.standards.place.ws.model.MetricsModel;
import org.familysearch.standards.place.ws.model.PlaceSearchResultsModel;
import org.familysearch.standards.place.ws.model.RootModel;

public class TestSearchMetricsThreaded {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
//    private static String baseUrl = "https://place-ws-dev.dev.fsglobal.org/int-std-ws-place/places";
//    private static String baseUrl = "https://beta.familysearch.org/int-std-ws-place/places";
//    private static String baseUrl = "http://ws.place.std.cmn.beta.us-east-1.test.fslocal.org/places";
//    private static String baseUrl = "https://www.familysearch.org/int-std-ws-place/places";

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

    private static Map<String,Long> thrTime  = new TreeMap<>();
    private static Map<String,Long> thrOKCnt = new TreeMap<>();

    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        int numThr = 1;

        HttpHelper.acceptType = "application/xml";
        HttpHelper.userAgent  = "wlj-test";
//        HttpHelper.authId = "Bearer USYSD682A1109FC51738706632FA82A2F071_idses-refa03.a.fsglobal.net";

        Thread[] threads = new Thread[numThr];
        for (int i=0;  i<threads.length;  i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        runInterpretations();
                    } catch (Exception e) {
                        System.out.println("OOPS!! " + e.getMessage());
                    }
                }
            },
            "thr-" + i);
            threads[i].start();
        }

        // Sit here until all the threads finish
        boolean isRunning = true;
        while(isRunning) {
            try { Thread.sleep(1000); } catch(Exception ex) { }
            isRunning = false;
            for (Thread thread : threads) {
                if (thread.isAlive()) {
                    isRunning = true;
                }
            }
        }

        thrTime.entrySet().forEach(entry -> System.out.println(entry.getKey() + " --> " + (entry.getValue() / ONE_MILLION) + " --> " + thrOKCnt.getOrDefault(entry.getKey(), 0L)));

        System.exit(0);
    }

    static void runInterpretations() throws Exception{
        for (String textx : textes) {
            System.out.println("\n================================================================================");
            long time0 = System.nanoTime();
            RootModel rootModel = doSearch(textx);
            long time1 = System.nanoTime();
            System.out.println("Time: " + (time1-time0)/ONE_MILLION);

//            List<PlaceSearchResultsModel> prsmx = rootModel.getSearchResults();
//            for (PlaceSearchResultsModel prsm : prsmx) {
//                System.out.println("New PlaceSearchResult ...");
//                MetricsModel mm = prsm.getMetrics();
//                List<MetricModel> mmm = mm.getMetrics();
//                for (MetricModel mmmm : mmm) {
//                    System.out.println("  " + mmmm.getMetricName() + " --> " + mmmm.getMetricValue());
//                }
//            }
            String threadName = Thread.currentThread().getName();
            long totalTime = thrTime.getOrDefault(threadName, 0L);
            thrTime.put(threadName, totalTime + (time1 - time0));

            long count = thrOKCnt.getOrDefault(threadName, 0L);
            thrOKCnt.put(threadName, count+1);
        }
    }

    static RootModel doSearch(String text) throws Exception {
        URL url = new URL(baseUrl + "/request?text=" + text + "&metrics=true" + "&sessionId=USYSD682A1109FC51738706632FA82A2F071_idses-refa03.a.fsglobal.net");
        return HttpHelper.doGET(url);
    }
}
