package std.wlj.hhs.model;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FormatQueryConverter implements Converter<String, FormatQuery> {
    @Override
    public FormatQuery convert(String value) {
        return FormatQuery.valueOf(value.toUpperCase());
    }
}
