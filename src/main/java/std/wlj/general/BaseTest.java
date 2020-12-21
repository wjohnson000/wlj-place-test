package std.wlj.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.place.ws.model.*;


/**
 * Helper methods for creating pseudo-dummy objects of various types.
 * 
 * @author wjohnson000
 *
 */
public abstract class BaseTest {

    /**
     * Create and return a dummy 'CentroidModel' instance
     * @return a dummy 'CentroidModel' instance
     */
    public static CentroidModel getCentroidModel() {
        CentroidModel model = new CentroidModel();

        model.setLatitude(44.44);
        model.setLongitude(111.0);

        return model;
    }

    /**
     * Create and return a dummy 'CountsModel' instance
     * @return a dummy 'CountsModel' instance
     */
    public static CountsModel getCountsModel() {
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
    public static HealthCheckModel getHealthCheckModel() {
        HealthCheckModel model = new HealthCheckModel();

        List<LinkModel> links = new ArrayList<LinkModel>();
        links.add(getLinkModel());
        links.add(getLinkModel());

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
    public static JurisdictionModel getJurisdictionModel() {
        JurisdictionModel model = new JurisdictionModel();

        JurisdictionModel parent = new JurisdictionModel();
        parent.setId(1);
        parent.setName("Utah");
        parent.setLocale("en");
        parent.setSelfLink(getLinkModel("self"));

        model.setId(11);
        model.setName("Provo");
        model.setLocale("en");
        model.setParent(parent);
        model.setSelfLink(getLinkModel("self"));
        return model;
    }
   
    /**
     * Create and return a dummy 'LinkModel' instance
     * @return a dummy 'LinkModel' instance
     */
    public static LinkModel getLinkModel() {
        return getLinkModel("type");
    }

    /**
     * Create and return a dummy 'LinkModel' instance
     * @return a dummy 'LinkModel' instance
     */
    public static LinkModel getLinkModel(String type) {
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
    public static LocalizedNameDescModel getLocalizedNameDescModel() {
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
    public static LocationModel getLocationModel() {
        LocationModel model = new LocationModel();

        model.setCentroid(getCentroidModel());

        return model;
    }

    /**
     * Create and return a dummy 'NameModel' instance
     * @return a dummy 'NameModel' instance
     */
    public static NameModel getNameModel() {
        NameModel model = new NameModel();

        model.setLocale("en");
        model.setName("Provo");

        return model;
    }

    /**
     * Create and return a dummy 'MetricsModel' instance
     * @return a dummy 'MetricsModel' instance
     */
    public static MetricsModel getMetricsModel() {
        MetricsModel model = new MetricsModel();

        model.setMetrics(getMetrics());

        return model;
    }

    /**
     * Create and return a dummy 'SourceModel' instance
     * @return a dummy 'SourceModel' instance
     */
    public static SourceModel getSourceModel() {
        SourceModel model = new SourceModel();

        model.setId(11);
        model.setTitle("a-title");
        model.setDescription("a-description");
        model.setPublished(true);

        return model;
    }

    /**
     * Create and return a dummy 'AttributeModel' instance
     * @return a dummy 'AttributeModel' instance
     */
    public static AttributeModel getAttributeModel() {
        AttributeModel model = new AttributeModel();

        List<LinkModel> links = new ArrayList<LinkModel>();
        links.add(getLinkModel());
        links.add(getLinkModel());

        model.setId(11);
        model.setRepId(22);
        model.setType(getTypeModel());
        model.setFromYear(2020);
        model.setToYear(2030);
        model.setValue("a-value");
        model.setUrl("http://my-home.com");
        model.setUrlTitle("my-home");
        model.setLinks(links);
        model.setCopyrightNotice("copyright-notice");
        model.setCopyrightUrl("http://copyright.com");
        model.setDetails(Arrays.asList("The attribute details ...", "The rest of the details"));

        return model;
    }

    /**
     * Create and return a dummy 'CitationModel' instance
     * @return a dummy 'CitationModel' instance
     */
    public static CitationModel getCitationModel() {
        CitationModel model = new CitationModel();

        List<LinkModel> links = new ArrayList<LinkModel>();
        links.add(getLinkModel());
        links.add(getLinkModel());

        model.setId(11);
        model.setRepId(22);
        model.setType(getTypeModel());
        model.setSourceId(44);
        model.setDescription("a-description");
        model.setSourceRef("a-source-ref");
        model.setCitDate(new java.util.Date(System.currentTimeMillis()));
        model.setLinks(links);

        return model;
    }

    /**
     * Create and return a dummy 'AltJurisdictionModel' instance
     * @return a dummy 'AltJurisdictionModel' instance
     */
    public static AltJurisdictionModel getAltJurisdictionModel() {
        AltJurisdictionModel model = new AltJurisdictionModel();

        List<LinkModel> links = new ArrayList<LinkModel>();
        links.add(getLinkModel());
        links.add(getLinkModel());

        model.setId(11);
        model.setRepId(22);
        model.setRelatedRepId(33);
        model.setType(getTypeModel());
        model.setLinks(links);

        return model;
    }

    /**
     * Create and return a dummy 'PlaceModel' instance
     * @return a dummy 'PlaceModel' instance
     */
    public static PlaceModel getPlaceModel() {
        PlaceModel model = new PlaceModel();

        List<VariantModel> variants = new ArrayList<VariantModel>();
        variants.add(getVariantModel());
        variants.add(getVariantModel());
        variants.add(getVariantModel());
        variants.add(getVariantModel());
        variants.add(getVariantModel());

        List<PlaceRepresentationModel> placeReps = new ArrayList<PlaceRepresentationModel>();
        placeReps.add(getPlaceRepresentationModel());
        placeReps.add(getPlaceRepresentationModel());
        placeReps.add(getPlaceRepresentationModel());

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
    public static PlaceRepresentationModel getPlaceRepresentationModel() {
        return getPlaceRepresentationModel(3);
    }

    /**
     * Create and return a dummy 'PlaceRepresentationModel' instance with the given
     * number of children; since we don't want to recurse forever those children won't
     * have any of their own children.
     * @return a dummy 'PlaceRepresentationModel' instance
     */
    @SuppressWarnings("deprecation")
    public static PlaceRepresentationModel getPlaceRepresentationModel(int childCount) {
        PlaceRepresentationModel model = new PlaceRepresentationModel();

        List<LinkModel> links = new ArrayList<LinkModel>();
        links.add(getLinkModel("type01"));
        links.add(getLinkModel("type02"));

        List<NameModel> dispNames = new ArrayList<NameModel>();
        dispNames.add(getNameModel());
        dispNames.add(getNameModel());
        dispNames.add(getNameModel());
        dispNames.add(getNameModel());

        List<PlaceRepresentationModel> children = new ArrayList<PlaceRepresentationModel>();
        if (childCount > 0) {
            for (int i=0;  i<childCount;  i++) {
                children.add(getPlaceRepresentationModel(0));
            }
        }

        model.setId(111);
        model.setJurisdiction(getJurisdictionModel());
        model.setType(getTypeModel());
        model.setOwnerId(11);
        model.setGroup(getTypeGroupModel());
        model.setFromYear(1900);
        model.setToYear(2000);
        model.setPreferredLocale("en");
        model.setPublished(true);
        model.setValidated(false);
        model.setLocation(getLocationModel());
        model.setUUID("abcd-1234");
        model.setRevision(5);
        model.setPreferredBoundaryId(111);
        model.setZoomLevel(23);
        model.setTypeCategory("a-type-category");
        model.setLinks(links);
        model.setDisplayName(getNameModel());
        model.setFullDisplayName(getNameModel());
        model.setDisplayNames(dispNames);
        model.setChildren(children);
        model.setCreateDate(new java.util.Date(100, 6, 24, 12, 13, 14));
        model.setLastUpdateDate(new java.util.Date(110, 6, 24, 12, 13, 14));

        return model;
    }

    /**
     * Create and return a dummy 'PlaceSearchResultModel' instance with
     * three children
     * @return a dummy 'PlaceSearchResultModel' instance
     */
    public static PlaceSearchResultModel getPlaceSearchResultModel() {
        PlaceSearchResultModel model = new PlaceSearchResultModel();

        model.setDistanceInKM(111.1);
        model.setDistanceInMiles(66.6);
        model.setRawScore(77);
        model.setRelevanceScore(88);
        model.setRep(getPlaceRepresentationModel());

        return model;
    }

    /**
     * Create and return a dummy 'PlaceSearchResultsModel' instance with
     * three children
     * @return a dummy 'PlaceSearchResultsModel' instance
     */
    public static PlaceSearchResultsModel getPlaceSearchResultsModel() {
        PlaceSearchResultsModel model = new PlaceSearchResultsModel();

        List<PlaceSearchResultModel> results = new ArrayList<PlaceSearchResultModel>();
        results.add(getPlaceSearchResultModel());
        results.add(getPlaceSearchResultModel());
        results.add(getPlaceSearchResultModel());

        model.setCount(5);
        model.setRefId(111);
        model.setMetrics(getMetricsModel());
        model.setResults(results);

        return model;
    }

    /**
     * Create and return a dummy 'PlaceSearchRequestModel' instance
     * @return a dummy 'PlaceSearchRequestModel' instance
     */
    public static PlaceSearchRequestModel getPlaceSearchRequestModel() {
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
        model.setFilter("true");
        model.setLimit("21");
        model.setFuzzy("22");
        model.setThreshold("23");
        model.setProfile("24");
        model.setAcceptLanguage("25");
        model.setLanguage("26");
        model.setPartial("true");
        model.setLatitude("27.27");
        model.setLongitude("28.28");
        model.setDistance("29");
        model.setPublishedType("30");
        model.setValidatedType("31");

        return model;
    }

    /**
     * Create and return a dummy 'RootModel' instance
     * @return a dummy 'RootModel' instance
     */
    public static RootModel getRootModel() {
        RootModel model = new RootModel();

        List<TypeModel> nameTypes = new ArrayList<TypeModel>();
        nameTypes.add(getTypeModel());
        nameTypes.add(getTypeModel());

        List<PlaceTypeGroupModel> placeGroupTypes = new ArrayList<PlaceTypeGroupModel>();
        placeGroupTypes.add(getTypeGroupModel());
        placeGroupTypes.add(getTypeGroupModel());

        List<TypeModel> placeTypes = new ArrayList<TypeModel>();
        placeTypes.add(getTypeModel());
        placeTypes.add(getTypeModel());

        List<PlaceSearchRequestModel> requests = new ArrayList<PlaceSearchRequestModel>();
        requests.add(getPlaceSearchRequestModel());
        requests.add(getPlaceSearchRequestModel());
        requests.add(getPlaceSearchRequestModel());

        List<PlaceSearchResultsModel> results = new ArrayList<PlaceSearchResultsModel>();
        results.add(getPlaceSearchResultsModel());
        results.add(getPlaceSearchResultsModel());
        results.add(getPlaceSearchResultsModel());
        results.add(getPlaceSearchResultsModel());
        results.add(getPlaceSearchResultsModel());

        List<ErrorMessageModel> errors = new ArrayList<>();
        errors.add(getErrorMessageModel());
        errors.add(getErrorMessageModel());

        model.setHealthCheck(getHealthCheckModel());
        model.setPlace(getPlaceModel());
        model.setPlaceRepresentation(getPlaceRepresentationModel());
        model.setPlaceRepresentations(Arrays.asList(new PlaceRepresentationModel[] {getPlaceRepresentationModel()}));
        model.setType(getTypeModel());
        model.setTypes(placeTypes);
        model.setPlaceTypeGroup(getTypeGroupModel());
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
    public static ScorerModel getScorerModel() {
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
    public static ScorersModel getScorersModel() {
        ScorersModel model = new ScorersModel();

        List<ScorerModel> scorers = new ArrayList<ScorerModel>();
        scorers.add(getScorerModel());
        scorers.add(getScorerModel());
        scorers.add(getScorerModel());

        model.setScorers(scorers);

        return model;
    }

    public static List<MetricModel> getMetrics() {
        List<MetricModel>   list = new ArrayList<MetricModel>();
        MetricModel         model = new MetricModel();

        model.setMetricName("metric");
        model.setMetricValue("value");
        list.add(model);

        return list;
    }

    /**
     * Create and return a dummy 'TimingsModel' instance
     * @return a dummy 'TimingsModel' instance
     */
    public static TimingsModel getTimingsModel() {
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
    public static PlaceRepGroupModel getRepGroupModel() {
        PlaceRepGroupModel model = new PlaceRepGroupModel();

        List<PlaceRepGroupModel> subGroups = new ArrayList<PlaceRepGroupModel>();
        for (int i=222;  i<225;  i++) {
            PlaceRepGroupModel subGroup = new PlaceRepGroupModel();
            subGroup.setId(i);
            List<LocalizedNameDescModel> nameAndDesc = new ArrayList<LocalizedNameDescModel>();
            nameAndDesc.add(getLocalizedNameDescModel());
            nameAndDesc.add(getLocalizedNameDescModel());
            nameAndDesc.add(getLocalizedNameDescModel());
            subGroup.setName(nameAndDesc);
            subGroup.setSelfLink(getLinkModel("self." + i));
            subGroups.add(subGroup);
        }

        List<PlaceRepSummaryModel> summaries = Arrays.asList(getPlaceRepSummaryModel(), getPlaceRepSummaryModel());

        model.setId(111);
        List<LocalizedNameDescModel> nameAndDesc = new ArrayList<LocalizedNameDescModel>();
        nameAndDesc.add(getLocalizedNameDescModel());
        nameAndDesc.add(getLocalizedNameDescModel());
        nameAndDesc.add(getLocalizedNameDescModel());
        model.setName(nameAndDesc);
        model.setSelfLink(getLinkModel("self"));
        model.setSubGroups(subGroups);
        model.setPublished(true);
        model.setRepSummaries(summaries);

        return model;
    }

    /**
     * Create and return a dummy 'TypeGroupModel' instance
     * @return a dummy 'TypeGroupModel' instance
     */
    public static PlaceTypeGroupModel getTypeGroupModel() {
        PlaceTypeGroupModel model = new PlaceTypeGroupModel();

        List<PlaceTypeGroupModel> subGroups = new ArrayList<PlaceTypeGroupModel>();
        for (int i=222;  i<225;  i++) {
            PlaceTypeGroupModel subGroup = new PlaceTypeGroupModel();
            subGroup.setId(i);
            List<LocalizedNameDescModel> nameAndDesc = new ArrayList<LocalizedNameDescModel>();
            nameAndDesc.add(getLocalizedNameDescModel());
            nameAndDesc.add(getLocalizedNameDescModel());
            nameAndDesc.add(getLocalizedNameDescModel());
            subGroup.setName(nameAndDesc);
            subGroup.setSelfLink(getLinkModel("self." + i));
            subGroups.add(subGroup);
        }

        List<TypeModel> types = new ArrayList<TypeModel>();
        types.add(getTypeModel());
        types.add(getTypeModel());

        model.setId(111);
        List<LocalizedNameDescModel> nameAndDesc = new ArrayList<LocalizedNameDescModel>();
        nameAndDesc.add(getLocalizedNameDescModel());
        nameAndDesc.add(getLocalizedNameDescModel());
        nameAndDesc.add(getLocalizedNameDescModel());
        model.setName(nameAndDesc);
        model.setSelfLink(getLinkModel("self"));
        model.setSubGroups(subGroups);
        model.setTypes(types);
        model.setPublished(true);

        return model;
    }

    /**
     * Create and return a dummy 'TypeModel' instance
     * @return a dummy 'TypeModel' instance
     */
    public static TypeModel getTypeModel() {
        TypeModel model = new TypeModel();

        List<LocalizedNameDescModel> nameAndDesc = new ArrayList<LocalizedNameDescModel>();
        nameAndDesc.add(getLocalizedNameDescModel());
        nameAndDesc.add(getLocalizedNameDescModel());
        nameAndDesc.add(getLocalizedNameDescModel());

        model.setCode("code");
        model.setId(11);
        model.setPublished(true);
        model.setSelfLink(getLinkModel());
        model.setName(nameAndDesc);

        return model;
    }

    /**
     * Create and return a dummy 'VariantModel' instance
     * @return a dummy 'VariantModel' instance
     */
    public static VariantModel getVariantModel() {
        VariantModel model = new VariantModel();

        model.setId(111);
        model.setName(getNameModel());
        model.setType(getTypeModel());

        return model;
    }

    @SuppressWarnings("deprecation")
    public static PlaceRepSummaryModel getPlaceRepSummaryModel() {
        PlaceRepSummaryModel    model = new PlaceRepSummaryModel();

        List<LinkModel> links = new ArrayList<LinkModel>();
        links.add(getLinkModel("type01"));
        links.add(getLinkModel("type02"));

        model.setId(111);
        model.setJurisdiction(getJurisdictionModel());
        model.setType(getTypeModel());
        model.setOwnerId(11);
        model.setLocation(getLocationModel());
        model.setLinks(links);
        model.setDisplayName(getNameModel());
        model.setFullDisplayName(getNameModel());
        model.setCreateDate(new java.util.Date(100, 6, 24, 12, 13, 14));
        model.setLastUpdateDate(new java.util.Date(110, 6, 24, 12, 13, 14));

        return model;
    }

    public static CompareModel getCompareModel() {
        CompareModel  model = new CompareModel();

        model.setCommonHistoricParent(getPlaceRepSummaryModel());
        model.setCommonLanguage("en");
        model.setDistance(1.0);
        model.setPlaceRep1(getPlaceRepSummaryModel());
        model.setPlaceRep2(getPlaceRepSummaryModel());
        model.setTypeComparison("EQUAL");
        return model;
    }

    public static ErrorMessageModel getErrorMessageModel() {
        ErrorMessageModel model = new ErrorMessageModel();

        model.setMessage("This is a message ...");
        model.setMsgLink(getLinkModel("typeAttr"));
        return model;
    }

    public static BoundaryModel getBoundaryModel() {
        BoundaryModel model = new BoundaryModel();

        model.setId(11);
        model.setRepId(117);
        model.setPointCount(1119);
        model.setFromYear(1800);
        model.setToYear(2020);

        return model;
    }
}
