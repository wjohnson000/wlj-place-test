package std.wlj.dan;

import java.util.Set;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.DataMetrics;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.scoring.Filter;
import org.familysearch.standards.place.scoring.Scorer;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;

import std.wlj.util.SolrManager;


public class TestInterpretation {

    /** List of values to interpret ... */
    static String[] textToInterpret = {
        "orem, utah",
        "new york, new york",
        "paris, france",
        ",Idaho",
        "portland, or",
        "London, England",
        "Windsor, New South Wales",
        "Taiamai",
        "Saint-Martin-en-Bresse",
        "Beit Mirrah, Lebanon",
        "Le Noir, North Carolina",
        "Lynn City 2, Essex, Massachusetts",
        "Hessen Lande",
        "Holland, , IN",
        "Cork, Cork, Ireland",
        "Baltimore,Md",
        "Hidalgo co., Texas",
        "Tart l'Abbaye, France",
        "Spanish Fork Cemetery, Lot 6, Block 1, Position 7",
        "Macau, Macau, China",
        "Hung Mo, China",
        "Baretswil, Zurich, Switzerland",
        "Carthage Jail, Hancock, LI.",
        "Salt Lake, Utah",
        "Aguacalientes, Aguacalientes, Aguacalientes, Mexico",
        ",,Aguacalientes, Mexico",
        "Aguacalientes, Mexico",
        "Jasper,, Georgia, United States",
        "New Hampshire, USA",
        "UT",
        ",,UT",
        "of UT",
        "Of,Turkey",
        ",,NH",
        "Munich, Bavaria, Germany",

//        "23 Assembly District 37 Precinct San Francisco City, San Francisco, California",
//        "160 South Warren Street",
//        "3- Portsmouth, New Hampshire",
//        "16th Ward Philadelphia",
//        "Aber, Scotland",
//        "Adjala, Cardwell, Ontario, Canada",
//        "Aguascalientes, Mexico", //barry
//        "Ajuchitlán del Progreso, Guerrero, Mexico",
//        "Albardón (Departamento), San Juan, Argentina",
//        "Ålborg, Denmark",
//        "All Saints, Newton(near Manchester), Lancashire, England",
//        "Alston, Cumberland, England",
//        "Alton Township Alton City Part Of 1, Madison, Illinois",
//        "Amparo, São Paulo, Brazil",
//        "Antrim, Ireland",
//        "Århus, Denmark",
//        "Ark",
//        "ASUNCION, MEXICO, DISTRITO FEDERAL, MEXICO",
//        "Atlantic",
//        "Atlixtac, Guerrero, Mexico",
//        "Australia",
//        "Austria",
//        "Ayr, Scotland",
//        "B...Son",
//        "Baden, Germany",
//        "Badiraguato, Sinaloa, Mexico",
//        "Barbados",
//        "Baumgarten (Ag. Butzow), Mecklenburg-Schwerin, Germany",
//        "Bayern, Germany",
//        "Beat 3 Ross Mill Precinct, Monroe, Mississippi",
//        "Bedford, England",
//        "BEENHAM, BERKSHIRE, ENGLAND",
//        "Belfast",
//        "BELFORT,HAUT-RHIN,FRANCE",
//        "Bengal, India",
//        "Berwick-upon-Tweed, Northumberland, England",
//        "Best,",
//        "Birth And Marriage Index,, Misc, New Hampshire",
//        "Bladåker, Stockholm, Sweden",
//        "Blekinge, Sweden",
//        "Bohemia",
//        "Boli, Bolivia",
//        "Bolton, Lancashire, England",
//        "Bornholm, Denmark",
//        "Boston",
//
//        "Chelsea & Greenwood towns, Taylor, Wisconsin",
//        "Centro, Rio de Janeiro, Rio de Janeiro, Brazil",
//        "Morris Co New Jersey",
//        "Nuestra Señora de la Asunción, Córdoba, Córdoba, Argentina",
//        "Rhodt (BA. Landau), Bayern, Germany",
//        "3 Precinct Roxbury Boston City 16, Suffolk, Massachusetts",
//        "19th Precinct Chicago 10, Cook, Illinois",
//        "N. Wilkesboro, Wilkes, North Carolina",
//        "Santa Catarina, Rioverde, San Luis Potosi, Mexico",
//        "Santa Cruz y Soledad, Centro-Barrio la Soledad, Distrito Federal, Mexico",
//        ", ASARUM, BLEKINGE, SWEDEN",
//        "Baumgarten (Ag. Butzow), Mecklenburg-Schwerin, Germany",
//        "Benson Precinct, Cochise, Arizona Territory",
//        "Braaten tilh. Chrania Spiger & Valseværk",
//        "wyoming",
//        "BROADWATER BY WORTHING,SUSSEX,ENGLAND",
//        "Oaxaca de JuÃ¡rez, Oaxaca, Mexico",
//        "Santa MarÃ­a del Marquesado, Oaxaca de JuÃrez, Oaxaca, Mexico",
//        "Caivanos",
//        "Columbia county, Columbia, Georgia",
//        "Congress, Wayne, Ohio",
//        "Mich. U. S.",
//        "Norfolk Co., Va.",
//        "ward 3, Butte, South Dakota",
//        ", Dawson, Texas",
//        "12-Wd Scranton, Lackawanna, Pennsylvania",
//        "Burleigh & Anstruther, Ontario",
//        "Civil District 6 (east part), Dickson, Tennessee",
//        "DALTON IN FURNESS,LANCASHIRE,ENGLAND",
//        "W. Va.",
//        "Santo Stefano Quisqaina, Agrigento, Sicily, Italy",
//        "SANTA FE,GUANAJUATO,GUANAJUATO,MEXICO",
//        "SAN PABLO VILLA DE MITLA, OAXACA, MEXICO",
//        "Eden Township Mt Eden Precinct 1, Alameda, California",
//        "new york",
//        "New York City, ward 10, New York, New York",
//        "Decatur Township Decatur City 15 Precinct, Macon, Illinois",
//        "El Salvador, Valladolid, Valladolid, Spain",
//        "Fairview & Lake Townships, Monona, Iowa",
//        "West 1/2 Beat 1, Chombus, Alabama",
//        "Kansas Ward 11, Jackson, Missouri",
//        "provo",
//        "(centre Square, Montgromery, PA",
//        "stange s og pr",
//        "utah,pro",
//        "provo",
//        "ED 39 Justice Precinct 1 (all east of H.E.&W.T.R.R. excl. Nacogdoches city), Nacogdoches, Texas, United States",
//        "ED 20 Justice Precinct 1 (excl. Coleman city), Coleman, Texas, United States",
//        "*",
//        "United States,Mississippi,Calhoun",
//        "ark",
//        "Inmaculada Concepción, Villa Atamisqui, Santiago del Estero, Argentina",
//        "Nuestra Señora de la Merced, San Juan, San Juan, Argentina",
//        "Nuestra Senora de la Inmaculada Concepción, Villa Atamisqui, Santiago del Estero, Argentina",
//        "Nuestra Senora de la Inmaculada, Buenos Aires, Distrito Federal, Argentina",
//        "Nuestra Señora de los Remedios, Montecristo, Córdoba, Argentina",
//        "Nuestra Señora de la Encarnación, San Miguel de Tucumán, Tucumán, Argentina",
//        "Nuestra Señora del Carmen, Santiago del Estero, Santiago del Estero, Argentina",
//        "Santa Maria De La Asuncion,Santa Maria Del Rio,San Luis Potosi,Mexico",
//        "Nuestra Señora de la Asunción, Santa María del Rio, San Luis Potosí, Mexico",
//        "Santos Apostoles Felipe y Santiago, San Felipe del Progreso, Mexico, Mexico",
//        "Santo Antônio da Patrulha, Santo Antônio da Patrulha, Rio Grande do Sul, Brazil",
//        "Neustra Senora de la Asuncion, Santa Maria del Rio, San Luis Potosi, Mexico",
//        "an Juan Bautista,Zimapan,Hidalgo,Mexico",
//        "St. Paul's, London St. Martin Ludgate, London St. Brigide (Brides) London London St. Brides St. Sepulchre Newgate Bysshopesgate, London St. Brides St. Nicholas Coldabbey, London London St. Martin Ludgate, London St. Brides St. Martin Ludgate, London St. Se",
//        "Verschollen Auf See, mit dem Schiff \"Frauke Catharina\" auf einer Reise nach London, am 4.Feb. 1856 amtlich für Tot erklärt",
//        "Hoa Da Ap, Bao An Dong Tay Nhi Xa, Da Hoa Thuong Tong, Dien Phuoc Huyen, Dien Ban Phu, Quang Nam Tinh, Vietnam",
//        "paris,id",
//        "Hidalgo, Texas",
//        "台灣省雲林縣", //Yunlin, Taiwan
//        "бежаницкиы",
    };

    public static void main(String... args) {
        long totalTime = 0;
        long parseTime = 0;
        StdLocale en = new StdLocale("en");

        SolrService  solrService = SolrManager.localHttpService();
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        // Throw away task to get things going ...
        PlaceRequestBuilder requestBldr = new PlaceRequestBuilder();
        requestBldr.setText("denver,co", en);
        PlaceRequest request = requestBldr.getRequest();
        placeService.requestPlaces(request);

        // Do an interpretation on all of the places
        for (String text : textToInterpret) {
            long beginTime = System.nanoTime();
            requestBldr = placeService.createRequestBuilder(text, en);
            requestBldr.setFilterResults(false).setShouldCollectMetrics(true);
            PlaceResults results = placeService.requestPlaces(requestBldr.getRequest());
            long execTime = System.nanoTime() - beginTime;
            totalTime += execTime;

            PlaceRepresentation[] interps = results.getPlaceRepresentations();
//            Set<Scorer> scorers = results.getMetrics().getTimedScorers();
//            Set<Filter> filters = results.getMetrics().getTimedFilters();
//
//            parseTime += results.getMetrics().get.getParseTime();
//
//            System.out.println("|" + text + "|" + execTime);
//            for (Scorer scorer : scorers) {
//                System.out.println(scorer.getClass().getSimpleName() + " time|" + results.getMetrics().getScorerTime(scorer));
//            }
//            for (Filter filter : filters) {
//                System.out.println(filter.getClass().getSimpleName() + " time|" + results.getMetrics().getFilterTime(filter));
//            }
//            System.out.println("Initial candidate count|" + results.getMetrics().getRawCandidateCount());
//            System.out.println("Pre-scoring candidate count|" + results.getMetrics().getPreScoringCandidateCount());
//            System.out.println("Final candidate count|" + interps.length);
//            System.out.println("Parse time|" + results.getMetrics().getParseTime());
//            System.out.println("Identify Candidate time|" + results.getMetrics().getIdentifyCandidatesTime());
//            System.out.println("Identify Candidate (lookup) time|" + results.getMetrics().getIdentifyCandidateLookupTime());
//            System.out.println("Identify Candidate (tail match) time|" + results.getMetrics().getIdentifyCandidateTailMatchTime());
//            System.out.println("Identify Candidate (max hit filter) time|" + results.getMetrics().getIdentifyCandidateMaxHitFilterTime());
//            System.out.println("Scoring time|" + results.getMetrics().getScoringTime());
//            System.out.println("Assembly time|" + results.getMetrics().getAssemblyTime());
//            System.out.println("Initial ParsedInputText count|" + results.getMetrics().getInitialParsedInputTextCount());
//            System.out.println("Final ParsedInputText count|" + results.getMetrics().getFinalParsedInputTextCount());
            for (int j = 0; j < interps.length; j++) {
//                scorers = interps[j].getMetadata().getInterpretation().getScorecard().getScorersThatScored();
                System.out.println(constructIdChain(interps[j]) + "|" + interps[j].getFullPreferredDisplayName().get() + "|" + interps[j].getMetadata().getInterpretation().getParsedInput().toNormalizedString());
                System.out.println("DEBUG: " + interps[j].getMetadata().getInterpretation().toString());
                System.out.println("Raw/Relevance Score|" + interps[j].getMetadata().getScoring().getRawScore() + "|" + interps[j].getMetadata().getScoring().getRelevanceScore());
//                for (Scorer scorer : scorers) {
//                    System.out.println("     " + scorer.getClass().getSimpleName() + ": " + interps[j].getMetadata().getInterpretation().getScorecard().getScoreFromScorer(scorer));
//                }
            }
        }

        System.gc();
        System.out.println("|Total Time|" + totalTime);
        long avgTime = totalTime / textToInterpret.length;
        System.out.println("|Average Time (nano)|" + avgTime);
        System.out.println("|Used Memory|" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024));
        System.out.println("|Average parse time|" + (parseTime / textToInterpret.length));

        DataMetrics metrics = placeService.getProfile().getDataService().getMetrics();
        for (String metricName: metrics.nameIterator()) {
            System.out.println(metricName + "=" + metrics.getNamedMetric(metricName).getValue());
        }

        System.exit(0);
    }

    // Make the jurisdiction chain from the PLACE-REP
    /**
     * Make a jurisdiction chain from a PLACE-REP
     * 
     * @param place place-rep
     * @return pretty jurisdiction chain
     */
    private static String constructIdChain(PlaceRepresentation place) {
        StringBuffer buf = new StringBuffer();

        buf.append("'");
        for (int id : place.getJurisdictionChainIds()) {
            buf.append(id);
            buf.append(",");
        }
        buf.deleteCharAt(buf.length() - 1);
        buf.append("'");

        // Add the place chain
        buf.append(" (place: ");
        for (PlaceRepresentation rep : place.getJurisdictionChain()) {
            buf.append(rep.getPlaceId());
            buf.append(",");
        }
        buf.deleteCharAt(buf.length() - 1);
        buf.append(")");

        return buf.toString();
    }
}
