/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


/**
 * @author wjohnson000
 *
 */
@XmlRootElement
public class Item {

    private String  id;
    private String  type;
    private String  name;
    private String  subtype;
    private String  createDate;  // String?  Calendar?  LocalDate?
    private String  createUser;
    private String  modifyDate;  // String?  Calendar?  LocalDate?
    private String  modifyUser;
    private Map<String, String> properties;
//    private Map<String, List<String>> mvProperties;


    @XmlAttribute(name="id")
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

    @XmlAttribute(name="type")
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

    @XmlElement(name="name")
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

    @XmlElement(name="subtype")
    @JsonProperty("subtype")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getSubtype() {
        return subtype;
    }

    @JsonProperty("subtype")
    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    @XmlElement(name="createUser")
    @JsonProperty("createUser")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getCreateUser() {
        return createUser;
    }

    @JsonProperty("createUser")
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @XmlElement(name="createDate")
    @JsonProperty("createDate")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getCreateDate() {
        return createDate;
    }

    @JsonProperty("createDate")
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @XmlElement(name="modifyUser")
    @JsonProperty("modifyUser")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getModifyUser() {
        return modifyUser;
    }

    @JsonProperty("modifyUser")
    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    @XmlElement(name="modifyDate")
    @JsonProperty("modifyDate")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getModifyDate() {
        return modifyDate;
    }

    @JsonProperty("modifyDate")
    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    @XmlElementWrapper(name="properties")
    @XmlElement(name="property")
    @JsonProperty("properties")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public Map<String, String> getProperties() {
        return properties;
    }

    @JsonProperty("properties")
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;;
    }

}
