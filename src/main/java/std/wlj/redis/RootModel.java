package std.wlj.redis;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.familysearch.standards.place.ws.util.POJOMarshalUtil;


/**
 * The one -- and only -- marshal-able class for the web-service, representing both the
 * HTTP request and HTTP response body contents.  It's a wrapper for all possible input
 * or output values.
 * 
 * @author dshellman, wjohnson000
 *
 */
@XmlRootElement(name="places")
@JsonRootName(value="places")
public class RootModel implements Serializable {

    private static final long serialVersionUID = -7482016287998968563L;

    /** Body objects, only one of which should be populate at any time */
    private PlaceRepresentationModel       placeRep;

    @XmlElement(name="rep")
    @JsonProperty("rep")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public PlaceRepresentationModel getPlaceRepresentation() {
        return placeRep;
    }

    @JsonProperty("rep")
    public void setPlaceRepresentation(PlaceRepresentationModel theRep) {
        placeRep = theRep;
    }

    /**
     * Return a JSON string that represents the marshal-ed RootModel instance
     * @return JSON string
     */
    public String toJSON() {
        return POJOMarshalUtil.toJSON(this);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return POJOMarshalUtil.toXML(this);
    }
}
