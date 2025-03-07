package io.github.opensabe.jdbc.core.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.Objects;

/**
 * Page序列化简化字段，只返回list跟踪记录数，并且字段名跟之前mybatis保持一致
 * @author heng.ma
 */
public class PageSerializeModule extends SimpleModule {

    public PageSerializeModule() {
        addSerializer(Page.class, new JsonSerializer<>() {
            @Override
            public void serialize(Page value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (Objects.isNull(value)) {
                    gen.writeNull();
                    return;
                }
                gen.writeStartObject();
                gen.writeNumberField("total", value.getTotalElements());
                gen.writeObjectField("list", value.getContent());
                gen.writeEndObject();
            }
        });
    }
}
