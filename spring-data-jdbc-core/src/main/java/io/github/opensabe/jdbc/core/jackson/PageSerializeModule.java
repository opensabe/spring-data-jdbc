package io.github.opensabe.jdbc.core.jackson;


import org.springframework.data.domain.Page;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;

import java.util.Objects;

/**
 * Page序列化简化字段，只返回list跟踪记录数，并且字段名跟之前mybatis保持一致
 * @author heng.ma
 */
public class PageSerializeModule extends SimpleModule {

    public PageSerializeModule() {
        addSerializer(new StdSerializer<Page>(Page.class) {

            @Override
            public void serialize(Page value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
                if (Objects.isNull(value)) {
                    gen.writeNull();
                    return;
                }
                gen.writeStartObject();
                gen.writeNumberProperty("total", value.getTotalElements());
                gen.writeArrayPropertyStart("list");
                value.getContent().forEach(item -> provider.writeValue(gen, item));
                gen.writeEndArray();
                gen.writeEndObject();
            }
        });
    }
}
