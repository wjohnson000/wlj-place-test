package std.wlj.redis;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.familysearch.standards.place.ws.util.POJOMarshalUtil;

@XmlRootElement
public class PlaceRepresentationModel implements Serializable {

    private static final long serialVersionUID = -2458013547298021874L;

    // Constants used by JSON marshaller to convert date/time to String and vice versa
    private static final String US_MOUNTAIN = "US/Mountain";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";

	private Integer				id;
	private Integer				ownerId;
	private Integer				fromYear;
	private Integer				toYear;
	private String				preferredLocale;
	private boolean				published;
	private boolean				validated;
	private String				uuid;
	private Integer				revision;
	private Date                createDate;
	private Date                lastUpdateDate;
	private Integer             preferredBoundaryId;
	private Integer             zoomLevel;
	private String              typeCategory;


	public PlaceRepresentationModel() {}

	@XmlAttribute(name="id")
	@JsonProperty("id")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
	public Integer getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId( Integer theId ) {
		id = theId;
	}

	@XmlAttribute(name="place")
	@JsonProperty("place")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
	public Integer getOwnerId() {
		return ownerId;
	}

	@JsonProperty("place")
	public void setOwnerId( Integer ownerId ) {
		this.ownerId = ownerId;
	}

	@XmlElement(name="from-year")
	@JsonProperty("fromYear")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
	public Integer getFromYear() {
		return fromYear;
	}

	@JsonProperty("fromYear")
	public void setFromYear( Integer theFromYear ) {
		fromYear = theFromYear;
	}

	@XmlElement(name="to-year")
	@JsonProperty("toYear")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
	public Integer getToYear() {
		return toYear;
	}

	@JsonProperty("toYear")
	public void setToYear( Integer theToYear ) {
		toYear = theToYear;
	}

	@XmlElement(name="uuid")
	@JsonProperty("uuid")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
	public String getUUID() {
		return uuid;
	}

	@JsonProperty("uuid")
	public void setUUID( String theUUID ) {
		uuid = theUUID;
	}

	@XmlElement(name="revision")
	@JsonProperty("revision")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
	public Integer getRevision() {
		return revision;
	}

	@JsonProperty("revision")
	public void setRevision( Integer theRevision ) {
		revision = theRevision;
	}

	@XmlElement(name="preferred-locale")
	@JsonProperty("preferredLocale")
	public String getPreferredLocale() {
		return preferredLocale;
	}

	@JsonProperty("preferredLocale")
	public void setPreferredLocale( String thePreferredLocale ) {
		preferredLocale = thePreferredLocale;
	}

	@XmlElement(name="published")
	@JsonProperty("published")
	public boolean isPublished() {
		return published;
	}

	@JsonProperty("published")
	public void setPublished( boolean isPublished ) {
		published = isPublished;
	}

	@XmlElement(name="validated")
	@JsonProperty("validated")
	public boolean isValidated() {
		return validated;
	}

	@JsonProperty("validated")
	public void setValidated( boolean isValidated ) {
		validated = isValidated;
	}

	@XmlTransient
	@JsonIgnore
	public boolean isProvisional() {
	    return ! isPublished()  &&  ! isValidated();
	}

    @XmlTransient
    @JsonIgnore
    public boolean isConfirmed() {
        return isPublished()  &&  ! isValidated();
    }

    @XmlTransient
    @JsonIgnore
    public boolean isCertified() {
        return isPublished()  &&  isValidated();
    }

    @XmlElement(name="create-date")
    @JsonProperty("createDate")
    @JsonSerialize()
    @JsonFormat(pattern=DATE_TIME_PATTERN, timezone=US_MOUNTAIN)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonProperty("createDate")
    public void setCreateDate( Date theCreateDate ) {
        createDate = theCreateDate;
    }

    @XmlElement(name="last-update-date")
    @JsonProperty("lastUpdateDate")
    @JsonSerialize()
    @JsonFormat(pattern=DATE_TIME_PATTERN, timezone=US_MOUNTAIN)
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    @JsonProperty("lastUpdateDate")
    public void setLastUpdateDate( Date theLastUpdateDate ) {
        lastUpdateDate = theLastUpdateDate;
    }

    @XmlElement(name="preferred-boundary-id")
    @JsonProperty("preferredBoundaryId")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public Integer getPreferredBoundaryId() {
        return preferredBoundaryId;
    }

    @JsonProperty("preferredBoundaryId")
    public void setPreferredBoundaryId( Integer preferredBoundaryId ) {
        this.preferredBoundaryId = preferredBoundaryId;
    }

    @XmlElement(name="zoom-level")
    @JsonProperty("zoomLevel")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public Integer getZoomLevel() {
        return zoomLevel;
    }

    @JsonProperty("zoomLevel")
    public void setZoomLevel( Integer zoomLevel ) {
        this.zoomLevel = zoomLevel;
    }

    @XmlElement(name="type-category")
    @JsonProperty("typeCategory")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getTypeCategory() {
        return typeCategory;
    }

    @JsonProperty("typeCategory")
    public void setTypeCategory( String typeCategory ) {
        this.typeCategory = typeCategory;
    }

	public String toXML() {
		return toString();
	}

    public String toJSON() {
        return POJOMarshalUtil.toJSON(this);
    }

    @Override
    public String toString() {
        return POJOMarshalUtil.toXML(this);
    }

}
