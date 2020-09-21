/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author wjohnson000
 *
 */
public class FormattedData {

    private List<FormattedString> formattedString;

    public FormattedData() {
        // Default constructor
    }

    public FormattedData(List<FormattedString> data) {
        setFormattedString(data);
    }

    public List<FormattedString> getFormattedString() {
        return formattedString;
    }

    @JsonProperty("formattedString")
    public void setFormattedString(List<FormattedString> formattedString) {
        this.formattedString = formattedString;
    }
}
