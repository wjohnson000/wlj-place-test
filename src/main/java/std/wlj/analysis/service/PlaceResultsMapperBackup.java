package std.wlj.analysis.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.familysearch.standards.analysis.model.InterpretationModel;
import org.familysearch.standards.analysis.model.ResultRepModel;
import org.familysearch.standards.analysis.model.ScorerModel;
import org.familysearch.standards.analysis.model.TokenMatchModel;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.logging.Logger;
import org.familysearch.standards.core.parse.TokenType;
import org.familysearch.standards.place.Metrics;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceResults.Annotation;
import org.familysearch.standards.place.data.PlaceNameBridge;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.scoring.Scorecard;
import org.familysearch.standards.place.scoring.Scorer;
import org.familysearch.standards.place.search.interp.Interpretation;
import org.familysearch.standards.place.search.parser.PlaceNameToken;

public class PlaceResultsMapperBackup {

    protected static final Logger logger = new Logger(PlaceResultsMapperBackup.class);

    private static final Set<String> PARAM_NAMES = new HashSet<>();
    static {
        // These parameters come from the "/search" endpoint
        PARAM_NAMES.add("reqParents");
        PARAM_NAMES.add("reqDirParents");
        PARAM_NAMES.add("reqGroupParents");
        PARAM_NAMES.add("optParents");
        PARAM_NAMES.add("optDirParents");
        PARAM_NAMES.add("filterParents");
        PARAM_NAMES.add("priorityTypes");
        PARAM_NAMES.add("reqTypes");
        PARAM_NAMES.add("optTypes");
        PARAM_NAMES.add("filterTypes");
        PARAM_NAMES.add("reqYears");
        PARAM_NAMES.add("optYears");
        PARAM_NAMES.add("filterRepGroups");
        PARAM_NAMES.add("filterTypeGroups");
        PARAM_NAMES.add("reqTypeGroups");
        PARAM_NAMES.add("filter");
        PARAM_NAMES.add("threshold");
        PARAM_NAMES.add("partial");
        PARAM_NAMES.add("pubType");
        PARAM_NAMES.add("valType");
        PARAM_NAMES.add("wildcards");

        // These parameters come from the "/interp" endpoint
        PARAM_NAMES.add("date");
        PARAM_NAMES.add("type");
        PARAM_NAMES.add("type-group");
        PARAM_NAMES.add("parent");
        PARAM_NAMES.add("center");
    }

    public InterpretationModel mapToModel(PlaceRequest request, PlaceResults results, Object notUsed, StdLocale locale) {
        InterpretationModel interpModel = new InterpretationModel();

        addRequestData(interpModel, request);
        addResultData(interpModel, results);
        addResultReps(interpModel, results);

        return interpModel;
    }

    protected void addRequestData(InterpretationModel interpModel, PlaceRequest request) {
        interpModel.setTypeCode("search");
        interpModel.setPlaceName(request.getText().get());
        interpModel.setEventDate(new Date());
        if (request.getTargetLanguage() != null) {
            interpModel.setAcceptLang(request.getTargetLanguage().toString());
        }
        interpModel.setUserAgent("wjohnson000");
        interpModel.setCollectionId(null);
        interpModel.setParameters(null);
    }

    protected void addResultData(InterpretationModel interpModel, PlaceResults results) {
        List<String> annotations = new ArrayList<>();
        Iterator<Annotation> annIter = results.getAnnotations();
        annIter.forEachRemaining(ann -> annotations.add(ann.toString()));

        interpModel.setTotalRepCount((int)results.getResultsFoundCount());
        interpModel.setAnnotations(annotations);
    }

    protected void addResultReps(InterpretationModel interpModel, PlaceResults results) {
        List<ResultRepModel> resultReps = new ArrayList<>();

        for (PlaceRepresentation placeRep : results.getPlaceRepresentations()) {
            StdLocale parseLocale = placeRep.getMetadata().getInterpretation().getParsedInput().getPathLanguage();
            StdLocale interpLocale = placeRep.getMetadata().getInterpretation().determineLanguage();

            ResultRepModel repModel = new ResultRepModel();
            repModel.setRepId(placeRep.getId());
            repModel.setRelevanceScore(placeRep.getMetadata().getInterpretation().getScorecard().getRelevanceScore());
            repModel.setParsePath(placeRep.getMetadata().getInterpretation().getParsedInput().serializeToStr());
            repModel.setParseLang(parseLocale == null ? null : String.valueOf(parseLocale));
            repModel.setInterpLang(interpLocale == null ? null : String.valueOf(interpLocale));

            addScorers(repModel, placeRep, results.getMetrics());
            addTokenMatches(repModel, placeRep);

            resultReps.add(repModel);
        }

        interpModel.setResultReps(resultReps);
    }

    protected void addScorers(ResultRepModel repModel, PlaceRepresentation placeRep, Metrics metrics) {
        List<ScorerModel> scorers = new ArrayList<>();

        Interpretation interp = placeRep.getMetadata().getInterpretation();
        Scorecard      scCard = interp.getScorecard();

        for (Scorer scorer : scCard.getScorersThatScored()) {
            ScorerModel scorerModel = new ScorerModel();
            scorerModel.setName(scorer.getClass().getSimpleName());
            scorerModel.setScore(scCard.getScoreFromScorer(scorer));
            scorerModel.setBasisScore(metrics.getMapNumberMetric(Metrics.MapNumberMetric.SCORER_BASIS_SCORE, scorer.getClass().getSimpleName()).intValue());

            scorers.add(scorerModel);
        }

        repModel.setScorers(scorers);
    }

    protected void addTokenMatches(ResultRepModel repModel, PlaceRepresentation placeRep) {
        List<TokenMatchModel> tokenMatches = new ArrayList<>();

        Interpretation interp = placeRep.getMetadata().getInterpretation();
        PlaceRepBridge[] prBridge = interp.getTokenIndexMatches();

        int ndx = 0;
        for (PlaceNameToken token : interp.getParsedInput().getTokens()) {
            List<String> tokenTypes = token.getTypes().stream()
                    .map(TokenType::toString)
                    .collect(Collectors.toList());

            PlaceRepBridge  repB  = prBridge[ndx];
            PlaceNameBridge nameB = interp.getMatchedVariant(ndx);

            TokenMatchModel tokenModel = new TokenMatchModel();
            tokenModel.setTokenIndex(ndx);
            tokenModel.setRawToken(token.getOriginalValue().toString());
            tokenModel.setNormalizedToken(token.getText().get());
            tokenModel.setTokenScript(token.getText().getLocale().getScript().getCode());
            tokenModel.setTokenTypes(tokenTypes);

            if (repB != null) {
                tokenModel.setMatchedRepId(repB.getRepId());
            }

            if (nameB != null) {
                tokenModel.setVariantText(nameB.getName().get());
                tokenModel.setVariantLocale(String.valueOf(nameB.getName().getLocale()));
                tokenModel.setVariantTypeCode(nameB.getType().getCode());
            }

            tokenMatches.add(tokenModel);
            ndx++;
        }
        repModel.setTokenMatches(tokenMatches);
    }

}
