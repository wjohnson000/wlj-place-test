/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.model;

import org.familysearch.homelands.core.persistence.model.ElementType;

/**
 * Take the contents of a "FormattedData" object and format it into HTML.
 * 
 * @author wjohnson000
 *
 */
public final class HtmlFormatter {

    private HtmlFormatter() {
        // private constructor hides the implicit one
    }

    public static String format(FormattedData formattedData) {
        if (formattedData == null  ||  formattedData.getFormattedString() == null  || formattedData.getFormattedString().isEmpty()) {
            return "";
        }

        StringBuilder buff = new StringBuilder();

        formattedData.getFormattedString().forEach(fs -> {
            fs.getContent().forEach(st -> {
                buff.append(ElementType.fromString(fs.getType()).getStartTag());
                buff.append(st.getText());
                buff.append(ElementType.fromString(fs.getType()).getEndTag());
            });
        });

        return buff.toString();
    }
}
