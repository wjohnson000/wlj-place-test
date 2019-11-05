package std.wlj.analysis.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.familysearch.standards.analysis.dal.dao.DAOFactory;
import org.familysearch.standards.analysis.dal.impl.DAOFactoryImpl;
import org.familysearch.standards.analysis.exception.AnalysisSystemException;
import org.familysearch.standards.analysis.model.*;
import org.familysearch.standards.analysis.service.AnalysisService;
import org.familysearch.standards.analysis.service.impl.AnalysisServiceImpl;

import std.wlj.util.DbConnectionManager;

public class CreateInterpretation {

    public static void main(String...args) throws AnalysisSystemException {
        DAOFactory daoFactory = new DAOFactoryImpl(DbConnectionManager.getDataSourcePCAS());

        AnalysisService aService = new AnalysisServiceImpl(daoFactory);
        InterpretationModel interpModel = new InterpretationModel();
        populateInterpretation(interpModel);
        InterpretationModel interpModelNew = aService.create(interpModel);
        System.out.println("IM.id: " + interpModelNew.getId());
    }

    static void populateInterpretation(InterpretationModel model) {
        model.setAcceptLang("en");
        model.setCollectionId("the-collection-id");
        model.setEventDate(new java.util.Date());
        model.setParameters(makeParams("parent", "Utah", "type", "TOWN"));
        model.setPlaceName("Provo, ZT, USA");
        model.setUserAgent("wjohnson000");

        model.setMd5Hash("md5-hash-result-" + System.nanoTime());
        model.setAnnotations(Arrays.asList("Annotation-01", "Annotation-02", "Annotation-03"));
        model.setTotalRepCount(111);
        model.setResultReps(Arrays.asList(makeResultRep1(), makeResultRep2(), makeResultRep3()));
    }

    static ResultRepModel makeResultRep1() {
        ResultRepModel model = new ResultRepModel();

        model.setParsePath("[token-01][token-02][token-03]");
        model.setRelevanceScore(93);
        model.setRepId(5314385);
        model.setScorers(Arrays.asList(makeScorer("scorer-01", 15, 25), makeScorer("scorer-02", 20, 25), makeScorer("scorer-03", 11, 19)));
        model.setTokenMatches(Arrays.asList(makeToken(5314385, "Provo (provo)", "Provo"), makeToken(393437, "UT (ut) [type=IS_TWO_CHAR]", "UT"), makeToken(1, "USA (usa)", "USA")));

        return model;
    }

    static ResultRepModel makeResultRep2() {
        ResultRepModel model = new ResultRepModel();

        model.setParsePath("[token-02][token-03][token-04]");
        model.setRelevanceScore(89);
        model.setRepId(5314384);
        model.setScorers(Arrays.asList(makeScorer("scorer-03", 15, 25), makeScorer("scorer-05", 20, 25), makeScorer("scorer-07", 11, 19)));
        model.setTokenMatches(Arrays.asList(makeToken(5314384, "Provo (provo)", "Provo"), makeToken(393436, "UT (ut) [type=IS_TWO_CHAR]", "UT"), makeToken(1, "USA (usa)", "USA")));

        return model;
    }

    static ResultRepModel makeResultRep3() {
        ResultRepModel model = new ResultRepModel();

        model.setParsePath("[token-01][token-02][token-04]");
        model.setRelevanceScore(88);
        model.setRepId(5314383);
        model.setScorers(Arrays.asList(makeScorer("scorer-02", 15, 25), makeScorer("scorer-05", 20, 25), makeScorer("scorer-09", 11, 19)));
        model.setTokenMatches(Arrays.asList(makeToken(5314383, "Provo (provo)", "Provo"), makeToken(393436, "UT (ut) [type=IS_TWO_CHAR]", "UT"), makeToken(1, "USA (usa)", "USA")));
        return model;
    }

    static ScorerModel makeScorer(String name, int score, int basisScore) {
        ScorerModel model = new ScorerModel();

        model.setName(name);
        model.setScore(score);
        model.setBasisScore(basisScore);

        return model;
    }

    static TokenMatchModel makeToken(int repId, String rawToken, String variantText) {
        TokenMatchModel model = new TokenMatchModel();

        model.setMatchedRepId(repId);
        model.setRawToken(rawToken);
        model.setVariantText(variantText);
        model.setNormalizedToken(variantText.toLowerCase());
        model.setTokenScript("tkns");
        model.setTokenIndex(repId % 4 + 1);
        model.setVariantTypeCode("type-" + repId);
        model.setVariantLocale("en");
        model.setTokenTypes(Arrays.asList("code-0" + (repId % 3 + 1), "code-0" + (repId % 7 + 1)));

        return model;
    }
    static Map<String, String> makeParams(String... values) {
        Map<String, String> aMap = new HashMap<>();
        for (int i=0;  i<values.length;  i+=2) {
            aMap.put(values[i], values[i+1]);
        }
        return aMap;
    }
}
