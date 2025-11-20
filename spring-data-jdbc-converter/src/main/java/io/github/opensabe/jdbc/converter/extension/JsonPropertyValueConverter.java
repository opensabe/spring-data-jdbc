package io.github.opensabe.jdbc.converter.extension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.opensabe.jdbc.converter.DefaultValueConversionContext;
import io.github.opensabe.jdbc.converter.InternalPropertyValueConverter;
import io.github.opensabe.jdbc.converter.JacksonParameterizedTypeTypeReference;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.NonNull;

/**
 * 处理数据库中的json字段
 * @author heng.ma
 */
public class JsonPropertyValueConverter implements InternalPropertyValueConverter<Object, String> {

    private final ObjectMapper objectMapper;

    public JsonPropertyValueConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Object read(@NonNull String value, DefaultValueConversionContext context) {
        TypeInformation<?> typeInformation = context.getProperty().getTypeInformation();
        try {
            return objectMapper.readValue(value, JacksonParameterizedTypeTypeReference.fromTypeInformation(typeInformation));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String write(@NonNull Object value, @NonNull DefaultValueConversionContext context) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
