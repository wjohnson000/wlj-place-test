package std.wlj.mapper;

public final class WebConstants {

    public static final String METHOD_DELETE      = "DELETE";
    public static final String METHOD_GET         = "GET";
    public static final String METHOD_POST        = "POST";
    public static final String METHOD_PUT         = "PUT";

    public static final String HEADER_ORIG_HOST   = "x-orig-host";
    public static final String HEADER_ORIG_BASE   = "x-orig-base";
    public static final String HEADER_ORIG_PROTO  = "x-orig-proto";

    public static final String ACCEPT_LANGUAGE_UC = "Accept-Language";
    public static final String ACCEPT_LANGUAGE_LC = "accept-language";

    public static final String LANGUAGE_HEADER    = "Language";
    public static final String LANGUAGE_PARAM     = "language";

    public static final String PARAM_ID           = "id";
    public static final String PARAM_ID_PATH      = "{id}";

    public static final String MSG_USER_ID        = "user";
    public static final String MSG_NOT_FOUND      = "not found";
    public static final String MSG_STATUS         = "status";
    public static final String MSG_START_REP_ID   = "startRepId";
    public static final String MSG_END_REP_ID     = "endRepId";
    public static final String MSG_SYSTEM         = "system";
    public static final String MSG_TIME           = "time";
    public static final String MSG_USER_ERROR     = "user_error";
    public static final String MSG_SYSTEM_ERROR   = "system_error";
    public static final String MSG_CHANGE_FIELD   = "changeField";
    public static final String MSG_CHANGE_TYPE    = "changeType";
    public static final String MSG_CHANGE_PLC_ID  = "changePlace";
    public static final String MSG_CHANGE_REP_ID  = "changeLocation";
    public static final String MSG_CHANGE_COUNTRY = "changeCountry";
    public static final String MSG_CHANGE_STATE   = "changeState";
    public static final String MSG_UNKNOWN        = "unknown";
    public static final String MSG_REQUEST        = "request";
    public static final String MSG_INTERP         = "interp";
    public static final String TRANSACTION_ID     = "transId";
    public static final String ERROR_MESSAGE      = "errorMsg";
    public static final String ALERT_MESSAGE      = "alert";

    public static final String FIELD_PLACE        = "place";
    public static final String FIELD_VARIANT_NAME = "variant name";
    public static final String FIELD_REP          = "rep";
    public static final String FIELD_DISPLAY_NAME = "display name";
    public static final String FIELD_ALT_REP      = "alt rep";
    public static final String FIELD_ATTRIBUTE    = "attribute";
    public static final String FIELD_BOUNDARY     = "boundary";
    public static final String FIELD_CITATION     = "citation";
    public static final String FIELD_SOURCE       = "source";
    public static final String FIELD_TYPE         = "type";
    public static final String FIELD_TYPE_GROUP   = "type group";
    public static final String FIELD_REP_GROUP    = "rep group";
    public static final String FIELD_EXT_XREF     = "xref";

    public static final String EDIT_TYPE_ADD      = "add";
    public static final String EDIT_TYPE_CHANGE   = "change";
    public static final String EDIT_TYPE_DELETE   = "delete";

    public static final String FALSE              = "false";
    public static final String TRUE               = "true";
    public static final String SELF               = "self";
    public static final String UTF8               = "UTF-8";

    public static final String KEY_ANNOTATIONS    = "annotations";
    public static final String KEY_URL            = "url";
    public static final String KEY_PARTIAL        = "partial";
    public static final String KEY_PROBLEM        = "problem";
    public static final String KEY_REP_ID         = "repId";
    public static final String KEY_CITATION_ID    = "citationId";
    public static final String KEY_PLACE_NAME     = "placeName";
    public static final String KEY_TYPE_GROUP_ID  = "typeGroupId";
    public static final String KEY_TYPE_ID        = "typeId";
    public static final String KEY_TYPE_CODE      = "typeCode";
    public static final String KEY_RESPONSE_TYPE  = "responseType";
    public static final String KEY_CACHE_EXPIRE   = "cacheExpireDate";
    public static final String KEY_FILE           = "file";
    public static final String KEY_RELEVANCE      = "relevance";
    public static final String KEY_COUNT          = "count";
    public static final String KEY_CERT_STATUS    = "certification";

    public static final String ATTR_MODEL         = "model";

    public static final String SPACE              = " ";
    public static final String PATH_SEPARATOR     = "/";
    public static final String COMMA              = ",";
    public static final String COLON              = ":";
    public static final String SEMI_COLON         = ";";
    public static final String DOUBLE_QUOTE       = "\"";

    //URL or path parameters
    public static final String URL_HEALTH_ROOT    = "healthcheck";
    public static final String URL_BOUNDARIES_ROOT = "boundaries";
    public static final String URL_ROOT           = "places";
    public static final String URL_REPS_ROOT      = "reps";
    public static final String URL_SEARCH_ROOT    = "request";
    public static final String URL_COMPARE_ROOT   = "compare";
    public static final String URL_INTERP_ROOT    = "interp";
    public static final String URL_MATCH_ROOT     = "match";
    public static final String URL_SOURCE_ROOT    = "sources";
    public static final String URL_XREF_ROOT      = "xrefs";
    public static final String URL_BORDERS_ROOT   = "borders";
    public static final String URL_FEEDBACK_ROOT  = "feedback";
    public static final String URL_RELATED_ROOT   = "related";
    public static final String URL_ATTRS_ROOT     = "attributes";
    public static final String URL_CITATIONS_ROOT = "citations";
    public static final String URL_NAMES_ROOT   = "names";
    public static final String URL_ALT_JURIS_ROOT = "alt-jurisdictions";
    public static final String URL_TYPES_ROOT     = "types";
    public static final String URL_TYPE_GROUPS_ROOT = "type-groups";
    public static final String URL_REP_GROUPS_ROOT  = "place-rep-groups";
    public static final String URL_NAME_TYPES_ROOT  = "name-types";
    public static final String URL_PLACE_TYPES_ROOT = "place-types";
    public static final String URL_ATTR_TYPES_ROOT  = "attribute-types";
    public static final String URL_CIT_TYPES_ROOT   = "citation-types";
    public static final String URL_XREF_TYPES_ROOT  = "xref-types";
    public static final String URL_RESOLUTION_TYPES_ROOT  = "resolution-types";
    public static final String URL_REP_REL_TYPES_ROOT  = "rep-relation-types";
    public static final String URL_FEEDBACK_SUBMITTER_ROOT  = "feedback-submitters";
    public static final String URL_FEEDBACK_RES_TYPES_ROOT  = "feedback-resolution-types";
    public static final String URL_FEEDBACK_STATE_TYPES_ROOT  = "feedback-states-types";

    //Query Parameter names
    public static final String PARAM_FROM_YEAR    = "from-year";
    public static final String PARAM_TO_YEAR      = "to-year";
    public static final String PARAM_COORDINATES  = "coordinates";
    public static final String PARAM_LINK_COUNT   = "link-count";
    public static final String PARAM_BOUNDARY_ID  = "boundary-id";
    public static final String PARAM_INNER_BDRY_IDS  = "inner-boundary-ids";
    public static final String PARAM_REP_IDS      = "rep-ids";
    public static final String PARAM_PLACE_NAME   = "name";
    public static final String PARAM_PLACE_TEXT   = "text";
    public static final String PARAM_DATE         = "date";
    public static final String PARAM_TYPE         = "type";
    public static final String PARAM_TYPE_GROUP   = "type-group";
    public static final String PARAM_PARENT       = "parent";
    public static final String PARAM_CENTER       = "center";
    public static final String PARAM_PUB_ONLY     = "pubonly";
    public static final String PARAM_NEW_REP_ID   = "newRepId";
    public static final String PARAM_ALT_JURIS_ID = "altJurisdictionId";
    public static final String PARAM_ATTR_ID      = "attrId";
    public static final String PARAM_CITATION_ID  = "citationId";
    public static final String PARAM_NAME_ID      = "nameId";
    public static final String PARAM_LINK_ID      = "linkId";
    public static final String PARAM_LOCALE       = "locale";
    public static final String PARAM_DISTANCE     = "distance";
    public static final String PARAM_LATITUDE     = "lat";
    public static final String PARAM_LONGITUDE    = "lng";
    public static final String PARAM_PARTIAL      = "partial";
    public static final String PARAM_LIMIT        = "limit";
    public static final String PARAM_MIN_RELEVANCE  = "minRel";
    public static final String PARAM_REQUIRED_YEARS = "reqYears";
    public static final String PARAM_OPTIONAL_YEARS = "optYears";
    public static final String PARAM_REQUIRED_TYPES = "reqTypes";
    public static final String PARAM_OPTIONAL_TYPES = "optTypes";
    public static final String PARAM_REQUIRED_PARENTS = "reqParents";
    public static final String PARAM_OPTIONAL_PARENTS = "optParents";
    public static final String PARAM_FILTER_TYPE_GROUPS = "filterTypeGroups";
    public static final String PARAM_REQUIRED_TYPE_GROUPS = "reqTypeGroups";
    public static final String PARAM_LAST_UPDATED_DATE = "lastUpdatedDate";
    public static final String PARAM_LAST_UPDATED_END_DATE = "lastUpdatedEndDate";
    public static final String PARAM_CREATED_DATE = "createdDate";
    public static final String PARAM_CREATED_END_DATE = "createdEndDate";
    public static final String PARAM_EXPERIMENTS = "experiments";
    public static final String PARAM_TYPE_CAT    = "typeCat";
    public static final String PARAM_PRIORITY_TYPES = "priorityTypes";
    public static final String PARAM_FILTER_TYPES = "filterTypes";
    public static final String PARAM_PLACE_HINT = "placeHint";
    public static final String PARAM_INCLUDE_DISPLAY = "display";
    public static final String PARAM_INCLUDE_VARIANTS = "variants";
    public static final String PARAM_INCLUDE_ATTRIBUTES = "attributes";
    public static final String PARAM_INCLUDE_CITATIONS = "citations";
    public static final String PARAM_INCLUDE_ALT_JURIS = "altJuris";
    public static final String PARAM_INCLUDE_EXT_XREFS = "extXrefs";
    public static final String PARAM_INCLUDE_CHILDREN = "children";
    public static final String PARAM_INCLUDE_SAME_PLACE = "samePlace";
    public static final String PARAM_INCLUDE_PARENT_ATTRIBUTES = "parentAttributes";
    public static final String PARAM_INCLUDE_WIKIPEDIA = "wikipediaDetails";
    public static final String PARAM_FILTER_ATTRIBUTES = "filterAttributes";
    public static final String PARAM_ATTR_FROM_YEAR_FILTER = "attrFromYear";
    public static final String PARAM_ATTR_TO_YEAR_FILTER = "attrToYear";
    public static final String PARAM_ATTR_TYPE_FILTER = "attrType";
    public static final String PARAM_ATTR_LANGUAGE_FILTER = "attrLanguage";
    public static final String PARAM_PAGE_NUMBER = "pagenum";
    public static final String PARAM_PAGE_SIZE = "pagesize";
}
