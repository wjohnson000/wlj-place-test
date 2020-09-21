package std.wlj.hhs.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@JsonRootName(value = "nameClassification")
public class NameVariant implements Serializable {

    private static final long serialVersionUID = -6997412558616670144L;

    private String nameId;
    private String name;
    private String preHtmlName;
    private String htmlName;
    private String postHtmlName;
    private String fullHtmlName;
    private String language;

    //no args constructor required for serialization
    public NameVariant() {}

    public NameVariant(String nameId, String name, String preHtmlName, String htmlName, String postHtmlName, String fullHtmlName, String language) {
        this.nameId       = nameId;
        this.name         = name;
        this.preHtmlName  = preHtmlName;
        this.htmlName     = htmlName;
        this.postHtmlName = postHtmlName;
        this.fullHtmlName = fullHtmlName;
        this.language     = language;

        updateHtmlValues();
    }

    @JsonProperty("name")
    @JsonSerialize()
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("language")
    @JsonSerialize()
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("nameId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getNameId() {
        return nameId;
    }

    @JsonProperty("nameId")
    public void setNameId(String nameId) {
        this.nameId = nameId;
    }

    @JsonProperty("preHtmlName")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getPreHtmlName() {
        return preHtmlName;
    }

    @JsonProperty("preHtmlName")
    public void setPreHtmlName(String preHtmlName) {
        this.preHtmlName = preHtmlName;
    }

    @JsonProperty("htmlName")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getHtmlName() {
        return htmlName;
    }

    @JsonProperty("htmlName")
    public void setHtmlName(String htmlName) {
        this.htmlName = htmlName;
    }

    @JsonProperty("postHtmlName")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getPostHtmlName() {
        return postHtmlName;
    }

    @JsonProperty("postHtmlName")
    public void setPostHtmlName(String postHtmlName) {
        this.postHtmlName = postHtmlName;
    }

    @JsonProperty("fullHtmlName")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getFullHtmlName() {
        return fullHtmlName;
    }

    @JsonProperty("fullHtmlName")
    public void setFullHtmlName(String fullHtmlName) {
        this.fullHtmlName = fullHtmlName;
    }

    protected void updateHtmlValues() {
        if (StringUtils.isBlank(getHtmlName())) {
            String html = "<b>" + getName() + "</b>";
            setHtmlName(html);
        }
        if (StringUtils.isBlank(getFullHtmlName())) {
            setFullHtmlName(getHtmlName());
        }
    }
}
