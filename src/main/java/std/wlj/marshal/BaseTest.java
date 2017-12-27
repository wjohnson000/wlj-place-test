package std.wlj.marshal;

import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.ws.model.*;

public class BaseTest {

    /**
     * Create and return a dummy 'CentroidModel' instance
     * @return a dummy 'CentroidModel' instance
     */
    protected CentroidModel getCentroidModel() {
        CentroidModel model = new CentroidModel();

        model.setLatitude(44.44);
        model.setLongitude(111.0);

        return model;
    }

    /**
     * Create and return a dummy 'CountsModel' instance
     * @return a dummy 'CountsModel' instance
     */
    protected CountsModel getCountsModel() {
        CountsModel model = new CountsModel();

        model.setFinalParsedInputTextCount(11);
        model.setInitialParsedInputTextCount(22);
        model.setPreScoringCandidateCount(33);
        model.setRawCandidateCount(44);

        return model;
    }

    /**
     * Create and return a dummy 'HealthCheckModel' instance
     * @return a dummy 'HealthCheckModel' instance
     */
    protected HealthCheckModel getHealthCheckModel() {
        HealthCheckModel model = new HealthCheckModel();

        List<LinkModel> links = new ArrayList<LinkModel>();
        links.add(this.getLinkModel());
        links.add(this.getLinkModel());

        model.setAPIVersion("v1.0");
        model.setWSVersion("v1.1");
        model.setCurrentRevision(11);
        model.setLinks(links);

        return model;
    }

    /**
     * Create and return a dummy 'JurisdictionModel' instance
     * @return a dummy 'JurisdictionModel' instance
     */
    protected JurisdictionModel getJurisdictionModel() {
        JurisdictionModel model = new JurisdictionModel();

        JurisdictionModel parent = new JurisdictionModel();
        parent.setId(1);
        parent.setName("Utah");
        parent.setLocale("en");
        parent.setSelfLink(this.getLinkModel("self"));

        model.setId(11);
        model.setName("Provo");
        model.setLocale("en");
        model.setParent(parent);
        model.setSelfLink(this.getLinkModel("self"));
        return model;
    }
   
    /**
     * Create and return a dummy 'LinkModel' instance
     * @return a dummy 'LinkModel' instance
     */
    protected LinkModel getLinkModel() {
        return getLinkModel("type");
    }

    /**
     * Create and return a dummy 'LinkModel' instance
     * @return a dummy 'LinkModel' instance
     */
    protected LinkModel getLinkModel(String type) {
        LinkModel model = new LinkModel();

        model.setHref("http://localhost/link");
        model.setHrefLang("http");
        model.setLength("10");
        model.setRel("rel");
        model.setTitle("title");
        model.setType(type);

        return model;
    }

    /**
     * Create and return a dummy 'LocalizedNameDescModel' instance
     * @return a dummy 'LocalizedNameDescModel' instance
     */
    protected LocalizedNameDescModel getLocalizedNameDescModel() {
        LocalizedNameDescModel model = new LocalizedNameDescModel();

        model.setDescription("Description");
        model.setLocale("en");
        model.setName("Name");

        return model;
    }

    /**
     * Create and return a dummy 'LocationModel' instance
     * @return a dummy 'LocationModel' instance
     */
    protected LocationModel getLocationModel() {
        LocationModel model = new LocationModel();

        model.setCentroid(this.getCentroidModel());

        return model;
    }

    /**
     * Create and return a dummy 'NameModel' instance
     * @return a dummy 'NameModel' instance
     */
    protected NameModel getNameModel() {
        NameModel model = new NameModel();

        model.setLocale("en");
        model.setName("Provo");

        return model;
    }

    /**
     * Create and return a dummy 'MetricsModel' instance
     * @return a dummy 'MetricsModel' instance
     */
    protected MetricsModel getMetricsModel() {
        MetricsModel model = new MetricsModel();

//        model.setCounts(this.getCountsModel());
//        model.setScorers(this.getScorersModel());
//        model.setTimings(this.getTimingsModel());

        return model;
    }

    /**
     * Create and return a dummy 'SourceModel' instance
     * @return a dummy 'SourceModel' instance
     */
    protected SourceModel getSourceModel() {
        SourceModel model = new SourceModel();

        model.setId(11);
        model.setTitle("a-title");
        model.setDescription("a-description");
        model.setIsPublished(true);

        return model;
    }

    /**
     * Create and return a dummy 'AttributeModel' instance
     * @return a dummy 'AttributeModel' instance
     */
    protected AttributeModel getAttributeModel() {
        AttributeModel model = new AttributeModel();

        List<LinkModel> links = new ArrayList<LinkModel>();
        links.add(this.getLinkModel());
        links.add(this.getLinkModel());

        model.setId(11);
        model.setRepId(22);
        model.setType(this.getTypeModel());
        model.setYear(2020);
        model.setValue("a-value");
        model.setLinks(links);

        return model;
    }

    /**
     * Create and return a dummy 'CitationModel' instance
     * @return a dummy 'CitationModel' instance
     */
    protected CitationModel getCitationModel() {
        CitationModel model = new CitationModel();

        List<LinkModel> links = new ArrayList<LinkModel>();
        links.add(this.getLinkModel());
        links.add(this.getLinkModel());

        model.setId(11);
        model.setRepId(22);
        model.setType(this.getTypeModel());
        model.setSourceId(44);
        model.setDescription("a-description");
        model.setSourceRef("a-source-ref");
        model.setCitDate(new java.util.Date(System.currentTimeMillis()));
        model.setLinks(links);

        return model;
    }

    /**
     * Create and return a dummy 'PlaceModel' instance
     * @return a dummy 'PlaceModel' instance
     */
    protected PlaceModel getPlaceModel() {
        PlaceModel model = new PlaceModel();

        List<VariantModel> variants = new ArrayList<VariantModel>();
        variants.add(this.getVariantModel());
        variants.add(this.getVariantModel());
        variants.add(this.getVariantModel());
        variants.add(this.getVariantModel());
        variants.add(this.getVariantModel());

        List<PlaceRepresentationModel> placeReps = new ArrayList<PlaceRepresentationModel>();
        placeReps.add(this.getPlaceRepresentationModel());
        placeReps.add(this.getPlaceRepresentationModel());
        placeReps.add(this.getPlaceRepresentationModel());

        model.setId(123);
        model.setFromYear(1901);
        model.setToYear(2020);
        model.setVariants(variants);
        model.setReps(placeReps);

        return model;
    }

    /**
     * Create and return a dummy 'PlaceRepresentationModel' instance with
     * three children
     * @return a dummy 'PlaceRepresentationModel' instance
     */
    protected PlaceRepresentationModel getPlaceRepresentationModel() {
        return this.getPlaceRepresentationModel(3);
    }

    /**
     * Create and return a dummy 'PlaceRepresentationModel' instance with the given
     * number of children; since we don't want to recurse forever those children won't
     * have any of their own children.
     * @return a dummy 'PlaceRepresentationModel' instance
     */
    protected PlaceRepresentationModel getPlaceRepresentationModel(int childCount) {
        PlaceRepresentationModel model = new PlaceRepresentationModel();

        List<LinkModel> links = new ArrayList<LinkModel>();
        links.add(this.getLinkModel("type01"));
        links.add(this.getLinkModel("type02"));

        List<NameModel> dispNames = new ArrayList<NameModel>();
        dispNames.add(this.getNameModel());
        dispNames.add(this.getNameModel());
        dispNames.add(this.getNameModel());
        dispNames.add(this.getNameModel());

        List<PlaceRepresentationModel> children = new ArrayList<PlaceRepresentationModel>();
        if (childCount > 0) {
            for (int i=0;  i<childCount;  i++) {
                children.add(this.getPlaceRepresentationModel(0));
            }
        }

        model.setId(111);
        model.setJurisdiction(this.getJurisdictionModel());
        model.setType(this.getTypeModel());
        model.setOwnerId(11);
        model.setGroup(this.getTypeGroupModel());
        model.setFromYear(1900);
        model.setToYear(2000);
        model.setPreferredLocale("en");
        model.setPublished(true);
        model.setValidated(false);
        model.setLocation(this.getLocationModel());
        model.setUUID("abcd-1234");
        model.setRevision(5);
        model.setLinks(links);
        model.setDisplayName(this.getNameModel());
        model.setFullDisplayName(this.getNameModel());
        model.setDisplayNames(dispNames);
        model.setChildren(children);

        return model;
    }

    /**
     * Create and return a dummy 'PlaceSearchResultModel' instance with
     * three children
     * @return a dummy 'PlaceSearchResultModel' instance
     */
    protected PlaceSearchResultModel getPlaceSearchResultModel() {
        PlaceSearchResultModel model = new PlaceSearchResultModel();

        model.setDistanceInKM(111.1);
        model.setDistanceInMiles(66.6);
        model.setRawScore(77);
        model.setRelevanceScore(88);
        model.setRep(this.getPlaceRepresentationModel());

        return model;
    }

    /**
     * Create and return a dummy 'PlaceSearchResultsModel' instance with
     * three children
     * @return a dummy 'PlaceSearchResultsModel' instance
     */
    protected PlaceSearchResultsModel getPlaceSearchResultsModel() {
        PlaceSearchResultsModel model = new PlaceSearchResultsModel();

        List<PlaceSearchResultModel> results = new ArrayList<PlaceSearchResultModel>();
        results.add(this.getPlaceSearchResultModel());
        results.add(this.getPlaceSearchResultModel());
        results.add(this.getPlaceSearchResultModel());

        model.setCount(5);
        model.setRefId(111);
        model.setMetrics(this.getMetricsModel());
        model.setResults(results);

        return model;
    }

    /**
     * Create and return a dummy 'PlaceSearchRequestModel' instance
     * @return a dummy 'PlaceSearchRequestModel' instance
     */
    protected PlaceSearchRequestModel getPlaceSearchRequestModel() {
        PlaceSearchRequestModel model = new PlaceSearchRequestModel();

        model.setId(111);
        model.setText("The text");
        model.setRequiredParents("11");
        model.setRequiredDirParents("12");
        model.setOptionalParents("13");
        model.setFilterParents("14");
        model.setRequiredTypes("15");
        model.setOptionalTypes("16");
        model.setPriorityTypes("17");
        model.setFilterTypes("18");
        model.setReqTypeGroups("19");
        model.setFilterTypeGroups("20");
        model.setRequiredYears("1900");
        model.setOptionalYears("1901");
//        model.setFilter(true);
//        model.setLimit(21);
        model.setFuzzy("22");
//        model.setThreshold(23);
        model.setProfile("24");
        model.setAcceptLanguage("25");
        model.setLanguage("26");
//        model.setPartial(true);
//        model.setLatitude(27.27);
//        model.setLongitude(28.28);
//        model.setDistance("29");
        model.setPublishedType("30");
        model.setValidatedType("31");

        return model;
    }

    /**
     * Create and return a dummy 'RootModel' instance
     * @return a dummy 'RootModel' instance
     */
    protected RootModel getRootModel() {
        RootModel model = new RootModel();

        List<TypeModel> nameTypes = new ArrayList<TypeModel>();
        nameTypes.add(this.getTypeModel());
        nameTypes.add(this.getTypeModel());

        List<PlaceTypeGroupModel> placeGroupTypes = new ArrayList<PlaceTypeGroupModel>();
        placeGroupTypes.add(this.getTypeGroupModel());
        placeGroupTypes.add(this.getTypeGroupModel());

        List<TypeModel> placeTypes = new ArrayList<TypeModel>();
        placeTypes.add(this.getTypeModel());
        placeTypes.add(this.getTypeModel());

        List<PlaceSearchRequestModel> requests = new ArrayList<PlaceSearchRequestModel>();
        requests.add(this.getPlaceSearchRequestModel());
        requests.add(this.getPlaceSearchRequestModel());
        requests.add(this.getPlaceSearchRequestModel());

        List<PlaceSearchResultsModel> results = new ArrayList<PlaceSearchResultsModel>();
        results.add(this.getPlaceSearchResultsModel());
        results.add(this.getPlaceSearchResultsModel());
        results.add(this.getPlaceSearchResultsModel());
        results.add(this.getPlaceSearchResultsModel());
        results.add(this.getPlaceSearchResultsModel());

        List<ErrorMessageModel> errors = new ArrayList<>();
        errors.add(this.getErrorMessageModel());
        errors.add(this.getErrorMessageModel());

        model.setHealthCheck(this.getHealthCheckModel());
        model.setPlace(this.getPlaceModel());
        model.setPlaceRepresentation(this.getPlaceRepresentationModel());
        model.setType(this.getTypeModel());
        model.setTypes(placeTypes);
        model.setPlaceTypeGroup(this.getTypeGroupModel());
        model.setPlaceTypeGroups(placeGroupTypes);
        model.setRequests(requests);
        model.setSearchResults(results);
        model.setErrorMessages(errors);

        return model;
    }

    /**
     * Create and return a dummy 'ScorerModel' instance
     * @return a dummy 'ScorerModel' instance
     */
    protected ScorerModel getScorerModel() {
        ScorerModel model = new ScorerModel();

        model.setName("name");
        model.setScore(100);
        model.setTime(105L);

        return model;
    }

    /**
     * Create and return a dummy 'ScorersModel' instance
     * @return a dummy 'ScorersModel' instance
     */
    protected ScorersModel getScorersModel() {
        ScorersModel model = new ScorersModel();

        List<ScorerModel> scorers = new ArrayList<ScorerModel>();
        scorers.add(this.getScorerModel());
        scorers.add(this.getScorerModel());
        scorers.add(this.getScorerModel());

        model.setScorers(scorers);

        return model;
    }

    /**
     * Create and return a dummy 'TimingsModel' instance
     * @return a dummy 'TimingsModel' instance
     */
    protected TimingsModel getTimingsModel() {
        TimingsModel model = new TimingsModel();

        model.setAssemblyTime(100L);
        model.setIdentifyCandidatesLookupTime(101L);
        model.setIdentifyCandidatesMaxHitFilterTime(102L);
        model.setIdentifyCandidatesTailMatchTime(103L);
        model.setIdentifyCandidatesTime(104L);
        model.setParseTime(105L);
        model.setScoringTime(106L);
        model.setTotalTime(721L);

        return model;
    }

    /**
     * Create and return a dummy 'TypeGroupModel' instance
     * @return a dummy 'TypeGroupModel' instance
     */
    protected PlaceTypeGroupModel getTypeGroupModel() {
        PlaceTypeGroupModel model = new PlaceTypeGroupModel();

        List<PlaceTypeGroupModel> subGroups = new ArrayList<PlaceTypeGroupModel>();
        for (int i=222;  i<225;  i++) {
            PlaceTypeGroupModel subGroup = new PlaceTypeGroupModel();
            subGroup.setId(i);
            List<LocalizedNameDescModel> nameAndDesc = new ArrayList<LocalizedNameDescModel>();
            nameAndDesc.add(this.getLocalizedNameDescModel());
            nameAndDesc.add(this.getLocalizedNameDescModel());
            nameAndDesc.add(this.getLocalizedNameDescModel());
            subGroup.setName(nameAndDesc);
            subGroup.setSelfLink(this.getLinkModel("self." + i));
            subGroups.add(subGroup);
        }

        List<TypeModel> types = new ArrayList<TypeModel>();
        types.add(this.getTypeModel());
        types.add(this.getTypeModel());

        model.setId(111);
        List<LocalizedNameDescModel> nameAndDesc = new ArrayList<LocalizedNameDescModel>();
        nameAndDesc.add(this.getLocalizedNameDescModel());
        nameAndDesc.add(this.getLocalizedNameDescModel());
        nameAndDesc.add(this.getLocalizedNameDescModel());
        model.setName(nameAndDesc);
        model.setSelfLink(this.getLinkModel("self"));
        model.setSubGroups(subGroups);
        model.setTypes(types);

        return model;
    }

    /**
     * Create and return a dummy 'TypeModel' instance
     * @return a dummy 'TypeModel' instance
     */
    protected TypeModel getTypeModel() {
        TypeModel model = new TypeModel();

        List<LocalizedNameDescModel> nameAndDesc = new ArrayList<LocalizedNameDescModel>();
        nameAndDesc.add(this.getLocalizedNameDescModel());
        nameAndDesc.add(this.getLocalizedNameDescModel());
        nameAndDesc.add(this.getLocalizedNameDescModel());

        model.setCode("code");
        model.setId(11);
        model.setPublished(true);
        model.setSelfLink(this.getLinkModel());
        model.setName(nameAndDesc);

        return model;
    }

    /**
     * Create and return a dummy 'VariantModel' instance
     * @return a dummy 'VariantModel' instance
     */
    protected VariantModel getVariantModel() {
        VariantModel model = new VariantModel();

        model.setId(111);
        model.setName(this.getNameModel());
        model.setType(this.getTypeModel());

        return model;
    }

    protected PlaceRepSummaryModel getPlaceRepSummaryModel() {
        PlaceRepSummaryModel    model = new PlaceRepSummaryModel();

        List<LinkModel> links = new ArrayList<LinkModel>();
        links.add(this.getLinkModel("type01"));
        links.add(this.getLinkModel("type02"));

        model.setId(111);
        model.setJurisdiction(this.getJurisdictionModel());
        model.setType(this.getTypeModel());
        model.setOwnerId(11);
        model.setLocation(this.getLocationModel());
        model.setLinks(links);
        model.setDisplayName(this.getNameModel());
        model.setFullDisplayName(this.getNameModel());

        return model;
    }

    protected CompareModel getCompareModel() {
        CompareModel    model = new CompareModel();

        model.setCommonHistoricParent( getPlaceRepSummaryModel() );
        model.setCommonLanguage( "en" );
        model.setDistance( 1.0 );
        model.setPlaceRep1( getPlaceRepSummaryModel() );
        model.setPlaceRep2( getPlaceRepSummaryModel() );
        model.setTypeComparison( "EQUAL" );
        return model;
    }

    protected ErrorMessageModel getErrorMessageModel() {
        ErrorMessageModel model = new ErrorMessageModel();

        model.setMessage("This is a message ...");
        model.setMsgLink(this.getLinkModel("typeAttr"));
        return model;
    }
}
