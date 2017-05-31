package std.wlj.analysis.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.familysearch.standards.analysis.model.InterpretationModel;
import org.familysearch.standards.analysis.model.RequestModel;
import org.familysearch.standards.analysis.model.ResultModel;
import org.familysearch.standards.analysis.model.ResultRepModel;
import org.familysearch.standards.analysis.model.ScorerModel;
import org.familysearch.standards.analysis.model.TokenMatchModel;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.logging.Logger;
import org.familysearch.standards.place.Metrics;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceResults.Annotation;
import org.familysearch.standards.place.data.PlaceNameBridge;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.scoring.Scorecard;
import org.familysearch.standards.place.scoring.Scorer;
import org.familysearch.standards.place.search.interp.Interpretation;
import org.familysearch.standards.place.search.parse.Token;
import org.familysearch.standards.place.search.parse.Token.TokenType;

public class PlaceResultsMapper {

    protected static final Logger logger = new Logger(PlaceResultsMapper.class);

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

    public InterpretationModel mapToModel(UriInfo uriInfo, HttpHeaders headers, PlaceResults results, StdLocale locale) {
        InterpretationModel interpModel = new InterpretationModel();

        setRequestData(interpModel, uriInfo, headers, locale);
        setResultData(interpModel, results);

        return interpModel;
    }

    protected void setRequestData(InterpretationModel interpModel, UriInfo uriInfo, HttpHeaders headers, StdLocale locale) {
        RequestModel requestModel = new RequestModel();

        if (uriInfo.getPath().contains("request")) {
            requestModel.setTypeCode("search");
            requestModel.setPlaceName(uriInfo.getQueryParameters().getFirst("text"));
        } else if (uriInfo.getPath().contains("interp")) {
            requestModel.setTypeCode("interpretation");
            requestModel.setPlaceName(uriInfo.getQueryParameters().getFirst("name"));
        }
        requestModel.setEventDate(new Date());
        requestModel.setAcceptLang(String.valueOf(locale));
        requestModel.setUserAgent(headers.getRequestHeaders().getFirst("user_agent"));
        requestModel.setCollectionId(null);

        addParams(requestModel, uriInfo);

        interpModel.setRequest(requestModel);
    }

    protected void addParams(RequestModel requestModel, UriInfo uriInfo) {
        MultivaluedMap<String,String> queryParams = uriInfo.getQueryParameters();

        Map<String, String> params = PARAM_NAMES.stream()
            .filter(param -> queryParams.containsKey(param))
            .collect(Collectors.toMap(param -> param , param -> queryParams.getFirst(param)));

        if (! params.isEmpty()) {
            requestModel.setParams(params);
        }
    }

    protected void setResultData(InterpretationModel interpModel, PlaceResults results) {
        List<String> annotations = new ArrayList<>();
        Iterator<Annotation> annIter = results.getAnnotations();
        annIter.forEachRemaining(ann -> annotations.add(ann.toString()));

        ResultModel resultModel = new ResultModel();
        resultModel.setTotalRepCount((int)results.getResultsFoundCount());
        resultModel.setAnnotations(annotations);
        addResultReps(resultModel, results);

        interpModel.setResult(resultModel);
    }

    protected void addResultReps(ResultModel resultModel, PlaceResults results) {
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

        resultModel.setResultReps(resultReps);
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
        for (Token token : interp.getParsedInput().getTokens()) {
            List<String> tokenTypes = token.getTypes().stream()
                    .map(TokenType::toString)
                    .collect(Collectors.toList());

            PlaceRepBridge  repB  = prBridge[ndx];
            PlaceNameBridge nameB = interp.getMatchedVariant(ndx);

            TokenMatchModel tokenModel = new TokenMatchModel();
            tokenModel.setTokenIndex(ndx);
            tokenModel.setRawToken(token.getOriginalToken());
            tokenModel.setNormalizedToken(token.getOriginalNormalizedToken());
            tokenModel.setTokenScript(token.getScript().getCode());
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
