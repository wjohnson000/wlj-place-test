package std.wlj.interpretation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.scoring.Scorecard;
import org.familysearch.standards.place.scoring.Scorer;
import org.familysearch.standards.place.search.ConfigurablePlaceRequestProfile;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;

import std.wlj.util.SolrManager;

/**
 * Run an interpretation through the Place 2.0 engine.
 * 
 * @author wjohnson000
 *
 */
public class RunInterpretationCJKJira7429 {

    private static String[][] names = {
        { "ja" , "イギリスイギリススタフォードシュアイアウエスト ブロムウィチ" },
        { "ja" , "イギリス, イギリス, スタフォードシュア, イアウエスト, ブロムウィチ" },
//        { "ja" , "アイルランドコウントイ ワテルフォードミルフォード" },
//        { "ja" , "イギリスイギリスケントカンテルブリーカンテルブリー スト マーガレット" },
//        { "ja" , "イギリスイギリスウエスト ヨークシュアイアブラムハム" },
//        { "ja" , "イギリスイギリスウエスト スセックスボクスグローブ" },
//        { "ja" , "イギリスイギリスウオルセステルシュアイアオンバースリー" },
//        { "ja" , "イギリスイギリスバークシュアイアウエスト イルスリー" },

        { "ko" , "미국 펜실베이니아 주 앨레게이니 피츠버그 앨레게이니 시티" },
        { "ko" , "나이테드 킨그돔 웨일즈 글램르건 니앳 홀이 트리니티 후르치" },
//        { "ko" , "브리티시 콜론이앨 어머릭아 뉴 함프실르 록잉함 함프턴" },
//        { "ko" , "나이테드 킨그돔 잉글랜드 워스스터실르 드들리 세인트 톰아스스 후르치" },
//        { "ko" , "미국 유타 주 살트 레이크 이스트 미크리크 와사트치 로 모리앨 박" },
//        { "ko" , "미국 유타 주 살트 레이크 살트 레이크 시티 살트 레이크 템일" },
//        { "ko" , "나이테드 킨그돔 잉글랜드 레이스스터실르 애스비 데 라 즈우치" },
//        { "ko" , "나이테드 킨그돔 잉글랜드 랭카실르 오그턴 비 랭커스터" },
//        { "ko" , "미국 유타 주 보스 엘더 보스 엘더 엘렉티언 프레신크트" },
//        { "ko" , "미국 아이다호 주 프랭클린 프랭클린 엘렉티언 프레신크트" },
//        { "ko" , "나이테드 킨그돔 잉글랜드 노르폴크 노위치 노위치 스트 앤드루" },
//        { "ko" , "나이테드 킨그돔 웨일즈 글램르건 니앳h 홀이 트리니티 c후르치" },
//        { "ko" , "나이테드 킨그돔 잉글랜드 스태퍼드실르 킨그스윈퍼드" },
//        { "ko" , "브리티시 콜론이앨 어머릭아 뉴 함프실르 록잉함 함프턴 팰스" },
//        { "ko" , "미국 캘리포니아 주 산 조퀸 '닐 주딕이앨 타운시프" },
//        { "ko" , "브리티시 콜론이앨 어머릭아 뉴 함프실르 록잉함 시브룩" },
//        { "ko" , "나이테드 킨그돔 잉글랜드 랭카실르 오그h턴 비 랭커스터" },
//        { "ko" , "나이테드 킨그돔 잉글랜드 워스스터실르 드들리 세인트 톰아스'스 c후르치" },
    };

    public static void main(String... args) throws PlaceDataException, IOException {
        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.7.1");
//        SolrService  solrService = SolrManager.awsBetaService(true);

        PlaceService placeService = PlaceService.getInstance( new DefaultPlaceRequestProfile( null, solrService, null ) );
        PlaceService placeInterpService = PlaceService.getInstance( new ConfigurablePlaceRequestProfile( ConfigurablePlaceRequestProfile.URL_INTERP_PROPS, solrService ) );

        for (String[] name : names) {
            doIt(placeService, name[0], name[1], false);
            doIt(placeInterpService, name[0], name[1], false);
        }
 
        solrService.shutdown();
        System.exit(0);
    }

    static void doIt(PlaceService placeService, String locale, String name, boolean filterRequests) {
        try {
            PlaceRequestBuilder builder = placeService.createRequestBuilder(name, new StdLocale(locale));
            builder.setShouldCollectMetrics(true);
            builder.setFilterResults(filterRequests);

            long timeAA = System.nanoTime();
            PlaceRequest request = builder.getRequest();
            PlaceResults results = placeService.requestPlaces(request);
            long timeBB = System.nanoTime();

            System.out.println("\n---> " + name);
            for (PlaceRepresentation rep : results.getPlaceRepresentations()) {
                System.out.println("    rep." + rep.getFullDisplayName(StdLocale.ENGLISH).get() +
                        " | " + Arrays.toString(rep.getJurisdictionChainIds()) +
                        " + " + getRawScore(rep) +
                        " + " + getRelevanceScore(rep));
                getScorers(rep).forEach((scorer, val) -> System.out.println("       scr: " + scorer + " --> " + val));
            }

            if (results.getAlternatePlaceRepresentations() != null) {
                for (PlaceRepresentation rep : results.getAlternatePlaceRepresentations()) {
                    System.out.println("    alt." + rep.getFullDisplayName(StdLocale.ENGLISH).get() +
                        " | " + Arrays.toString(rep.getJurisdictionChainIds()) +
                        " + " + getRawScore(rep) +
                        " + " + getRelevanceScore(rep));
                    getScorers(rep).forEach((scorer, val) -> System.out.println("       scr: " + scorer + " --> " + val));
                }
            }

            System.out.println("    Time=" + (timeBB - timeAA) / 1_000_000.0);
        } catch(Exception ex) {
            System.out.println("Exception for " + name + " --> " + ex.getMessage());
        }
    }

    static String getText(String line) {
        int ndx0 = line.indexOf('"');
        int ndx1 = line.indexOf('"', ndx0+1);
        if (ndx0 == 0  &&  ndx1 > ndx0) {
            return line.substring(ndx0+1, ndx1);
        } else {
            return null;
        }
    }

    static int getRawScore(PlaceRepresentation rep) {
        if (rep.getMetadata() != null  &&  rep.getMetadata().getScoring() != null) {
            return rep.getMetadata().getScoring().getRawScore();
        } else {
            return 0;
        }
    }

    static int getRelevanceScore(PlaceRepresentation rep) {
        if (rep.getMetadata() != null  &&  rep.getMetadata().getScoring() != null) {
            return rep.getMetadata().getScoring().getRelevanceScore();
        } else {
            return 0;
        }
    }

    static Map<String, Integer> getScorers(PlaceRepresentation rep) {
        if (rep.getMetadata() != null  &&  rep.getMetadata().getScoring() != null) {
            Scorecard scorecard = rep.getMetadata().getInterpretation().getScorecard();
            Set<Scorer> scorers = scorecard.getScorersThatScored();
            return scorers.stream()
                    .collect(Collectors.toMap(
                            sc -> sc.getClass().getSimpleName(),
                            sc -> scorecard.getScoreFromScorer(sc)));
        }

        return Collections.emptyMap();
    }
}
