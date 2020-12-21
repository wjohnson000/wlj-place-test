/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author wjohnson000
 *
 */
public class FormattedString {
    private String type;
    private List<StyledText> content;

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

    @JsonProperty("content")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public List<StyledText> getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(List<StyledText> content) {
        this.content = content;
    }
}
