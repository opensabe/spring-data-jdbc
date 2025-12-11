package io.github.opensabe.jdbc.converter.extension;

import io.github.opensabe.jdbc.converter.DefaultValueConversionContext;
import io.github.opensabe.jdbc.converter.InternalPropertyValueConverter;
import io.github.opensabe.jdbc.converter.JacksonParameterizedTypeTypeReference;
import org.springframework.data.core.TypeInformation;
import org.springframework.lang.NonNull;
import tools.jackson.databind.json.JsonMapper;

/**
 * 处理数据库中的json字段
 * @author heng.ma
 */
public class JsonPropertyValueConverter implements InternalPropertyValueConverter<Object, String> {

    private final JsonMapper objectMapper;

    public JsonPropertyValueConverter(JsonMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Object read(@NonNull String value, DefaultValueConversionContext context) {
        TypeInformation<?> typeInformation = context.getProperty().getTypeInformation();
        return objectMapper.readerFor(JacksonParameterizedTypeTypeReference.fromTypeInformation(typeInformation)).readValue(value);

    }

    @Override
    public String write(@NonNull Object value, @NonNull DefaultValueConversionContext context) {
        return objectMapper.writeValueAsString(value);
    }
}
