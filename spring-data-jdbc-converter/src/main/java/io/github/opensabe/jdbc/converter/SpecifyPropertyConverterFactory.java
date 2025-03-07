package io.github.opensabe.jdbc.converter;

import org.springframework.context.ApplicationContext;
import org.springframework.data.convert.PropertyValueConverter;
import org.springframework.data.convert.PropertyValueConverterFactory;
import org.springframework.data.convert.ValueConversionContext;
import org.springframework.data.mapping.PersistentProperty;

import java.util.Objects;

/**
 * 转换对象时。检查是否含有{@link Converter},如果有，使用注解中指定的转换器
 * @author heng.ma
 */
public class SpecifyPropertyConverterFactory implements PropertyValueConverterFactory {

    private final ApplicationContext applicationContext;

    public SpecifyPropertyConverterFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <DV, SV, P extends ValueConversionContext<?>> PropertyValueConverter<DV, SV, P> getConverter(PersistentProperty<?> property) {
        Converter converter = property.findAnnotation(Converter.class);
        if (Objects.nonNull(converter)) {
            return this.getConverter((Class<? extends PropertyValueConverter<DV,SV,P>>) converter.value());
        }
        return null;
    }

    @Override
    public <DV, SV, C extends ValueConversionContext<?>> PropertyValueConverter<DV, SV, C> getConverter(Class<? extends PropertyValueConverter<DV, SV, C>> converterType) {
        return applicationContext.getBean(converterType);
    }
}
