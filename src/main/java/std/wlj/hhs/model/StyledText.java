/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author wjohnson000
 *
 */
public class StyledText {
    private String style;
    private String text;

    @JsonProperty("style")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getStyle() {
        return style;
    }

    @JsonProperty("style")
    public void setStyle(String style) {
        this.style = style;
    }

    @JsonProperty("text")
    @JsonSerialize()
    @JsonInclude(Include.NON_NULL)
    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }
}
