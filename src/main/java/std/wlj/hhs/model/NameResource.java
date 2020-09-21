package std.wlj.hhs.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.familysearch.homelands.core.persistence.model.NameData;
import org.familysearch.homelands.core.persistence.util.JsonUtility;
import org.familysearch.homelands.core.svc.ServiceHelper;

import static org.familysearch.homelands.core.svc.ServiceHelper.DATE_TIME_PATTERN;

@JsonRootName(value = "name")
public class NameResource implements Serializable {

    private static final long serialVersionUID = -6887412558616670188L;

    private String                         id;
    private String                         name;
    private String                         collectionId;
    private String                         collectionVisibility;
    private String                         nameVisibility;
    private String                         type;
    private String                         gender;
    private String                         language;
    private String                         originLanguage;
    private FormattedData                  definition;
    private String                         htmlDefinition;
    private Map<String, List<NameVariant>> variants;
    private String                         attribution;
    private String                         attributionURL;
    private String                         createUserId;
    private LocalDateTime                  createDate;
    private String                         modifyUserId;
    private LocalDateTime                  modifyDate;

    public NameResource() {
        // Must have a default constructor
    }

    /**
     * Constructor that takes an {@link NameData} instance
     *
     * @param nameData persistence model of an Item
     */
    public NameResource(NameData nameData) {
        this(nameData, null);
    }

    public NameResource(NameData nameData, FormatQuery format) {
        if (nameData != null) {
            JsonNode details = nameData.getDetails();
            JsonNode collectionInfo = nameData.getCollectionInfo();
            JsonNode collectionDetails = JsonUtility.getJsonNode(nameData.getCollectionInfo(), ServiceHelper.COLLECTION_DETAILS_KEY);
            JsonNode systemInfo = nameData.getSystemInfo();
            this.setId(nameData.getId());
            String origLanguage = systemInfo != null ? JsonUtility.getStringValue(systemInfo, ServiceHelper.ORIG_LANGUAGE_KEY) : null;
            if (origLanguage != null) {
                this.setOriginLanguage(origLanguage);
            }
            this.setCollectionId(nameData.getCollectionId());
            
            //Temp code to fall back to old way of storing collectionId - Remove after all data is reloaded
            if (getCollectionId() == null) {
                this.setCollectionId(JsonUtility.getStringValue(collectionInfo, ServiceHelper.COLLECTION_ID_KEY));        
            }
            
            if (collectionDetails != null) {
                JsonNode attributionNode = JsonUtility.getJsonNode(collectionDetails, ServiceHelper.ATTRIBUTION_KEY);
                this.setAttribution(JsonUtility.getStringValue(attributionNode, nameData.getLanguage()));
                this.setAttributionURL(JsonUtility.getStringValue(collectionDetails, ServiceHelper.ATTRIBUTION_URL_KEY));
            }
            this.setName(JsonUtility.getStringValue(details, ServiceHelper.NAME_KEY));
            this.setType(String.valueOf(nameData.getNameType()));
            this.setGender(JsonUtility.getStringValue(details, ServiceHelper.GENDER_KEY));
            this.setLanguage(nameData.getLanguage());
            this.setCollectionVisibility(nameData.getCollectionVisibility() != null ? nameData.getCollectionVisibility().name() : null);
            this.setNameVisibility(nameData.getNameVisibility() != null ? nameData.getNameVisibility().name() : null);
            this.setCreateUserId(nameData.getCreateUserId());
            this.setCreateDate(nameData.getCreateDate());
            this.setModifyUserId(nameData.getModifyUserId());
            this.setModifyDate(nameData.getModifyDate());

            JsonNode definitionNode = JsonUtility.getJsonNode(details, ServiceHelper.NAME_DEFINITION_KEY);

            //If format isn't set then generate all types of formats, otherwise only set definition matching format type
            // Raw [FormattedData] is stored in JSON node
            FormattedData formattedData = JsonUtility.createObject(definitionNode, FormattedData.class);
            if (format == null || format == FormatQuery.JSON) {
                this.setDefinition(formattedData);
            }
            if (format == null || format == FormatQuery.HTML) {
                this.setHtmlDefinition(HtmlFormatter.format(formattedData));
            }

            // Extract the variant name data
            JsonNode nameVariantMapNode = JsonUtility.getJsonNode(details, ServiceHelper.NAME_VARIANTS_KEY);
            Iterator<Map.Entry<String, JsonNode>> nameVariantIter = nameVariantMapNode.fields();
            Map<String, List<NameVariant>> nameVariantsMap = new HashMap<>();
            while(nameVariantIter.hasNext()) {
                Map.Entry<String, JsonNode> entry = nameVariantIter.next();

                List<NameVariant> nameVariants = new ArrayList<>();
                JsonNode nameVariantsNode = entry.getValue();
                if (nameVariantsNode != null) {
                    nameVariantsNode.forEach(nameVariantNode -> nameVariants.add(
                          new NameVariant(
                                JsonUtility.getStringValue(nameVariantNode, ServiceHelper.NAME_VARIANT_NAME_ID_KEY),
                                JsonUtility.getStringValue(nameVariantNode, ServiceHelper.NAME_VARIANT_NAME_KEY),
                                JsonUtility.getStringValue(nameVariantNode, ServiceHelper.NAME_VARIANT_PRE_HTML_NAME_KEY),
                                JsonUtility.getStringValue(nameVariantNode, ServiceHelper.NAME_VARIANT_HTML_NAME_KEY),
                                JsonUtility.getStringValue(nameVariantNode, ServiceHelper.NAME_VARIANT_POST_HTML_NAME_KEY),
                                JsonUtility.getStringValue(nameVariantNode, ServiceHelper.NAME_VARIANT_FULL_HTML_NAME_KEY),
                                JsonUtility.getStringValue(nameVariantNode, ServiceHelper.NAME_VARIANT_LANGUAGE_KEY)
                          )));
                }
                nameVariantsMap.put(entry.getKey(), nameVariants);
            }
            this.setVariants(nameVariantsMap);

        }
    }

    @JsonProperty("id")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("collectionId")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getCollectionId() {
        return collectionId;
    }

    @JsonProperty("collectionId")
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    @JsonProperty("collectionVisibility")
    @JsonInclude(Include.NON_NULL)
    public String getCollectionVisibility() {
        return collectionVisibility;
    }

    public void setCollectionVisibility(String collectionVisibility) {
        this.collectionVisibility = collectionVisibility;
    }

    @JsonProperty("nameVisibility")
    @JsonInclude(Include.NON_NULL)
    public String getNameVisibility() {
        return nameVisibility;
    }

    public void setNameVisibility(String nameVisibility) {
        this.nameVisibility = nameVisibility;
    }

    @JsonProperty("type")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("gender")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getGender() {
        return gender;
    }

    @JsonProperty("gender")
    public void setGender(String gender) {
        this.gender = gender;
    }

    @JsonProperty("language")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("originLanguage")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getOriginLanguage() {
        return originLanguage;
    }

    @JsonProperty("originLanguage")
    public void setOriginLanguage(String originLanguage) {
        this.originLanguage = originLanguage;
    }

    @JsonProperty("htmlDefinition")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getHtmlDefinition() {
        return htmlDefinition;
    }

    @JsonProperty("htmlDefinition")
    public void setHtmlDefinition(String htmlDefinition) {
        this.htmlDefinition = htmlDefinition;
    }

    @JsonProperty("definition")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public FormattedData getDefinition() {
        return definition;
    }

    @JsonProperty("definition")
    public void setDefinition(FormattedData definition) {
        this.definition = definition;
    }

    @JsonProperty("variants")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public Map<String, List<NameVariant>> getVariants() {
        return variants;
    }

    @JsonProperty("variants")
    public void setVariants(Map<String, List<NameVariant>> variants) {
        this.variants = variants;
    }

    @JsonProperty("attribution")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getAttribution() {
        return attribution;
    }

    @JsonProperty("attribution")
    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    @JsonProperty("attributionURL")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getAttributionURL() {
        return attributionURL;
    }

    @JsonProperty("attributionURL")
    public void setAttributionURL(String attributionURL) {
        this.attributionURL = attributionURL;
    }

    @JsonProperty("createUserId")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getCreateUserId() {
        return createUserId;
    }

    @JsonProperty("createUserId")
    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    @JsonProperty("createDate")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    @JsonProperty("createDate")
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    @JsonProperty("modifyUserId")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getModifyUserId() {
        return modifyUserId;
    }

    @JsonProperty("modifyUserId")
    public void setModifyUserId(String modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    @JsonProperty("modifyDate")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    public LocalDateTime getModifyDate() {
        return modifyDate;
    }

    @JsonProperty("modifyDate")
    public void setModifyDate(LocalDateTime modifyDate) {
        this.modifyDate = modifyDate;
    }
}