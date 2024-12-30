package io.github.mado.jdbc.converter;

import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.PropertyValueConversions;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.core.mapping.JdbcSimpleTypes;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.relational.core.dialect.Dialect;

import java.util.ArrayList;
import java.util.List;

/**
 * 跟原来的区别是使用自己的 PropertyValueConversions，原始的register只能根据对象来单个添加
 * @author heng.ma
 */
public class InternalConversions extends JdbcCustomConversions {

    public InternalConversions(Dialect dialect, List<?> userConverters, PropertyValueConversions propertyValueConversions) {
        super(converterConfiguration(dialect, userConverters, propertyValueConversions));
    }


    public static ConverterConfiguration converterConfiguration (Dialect dialect, List<?> userConverters, PropertyValueConversions propertyValueConversions) {
        SimpleTypeHolder simpleTypeHolder = dialect.simpleTypes().isEmpty() ? JdbcSimpleTypes.HOLDER
                : new SimpleTypeHolder(dialect.simpleTypes(), JdbcSimpleTypes.HOLDER);
        return new ConverterConfiguration(
                CustomConversions.StoreConversions.of(simpleTypeHolder, storeConverters(dialect)),
                userConverters,
                InternalConversions::excludeConversionsBetweenDateAndJsr310Types,
                propertyValueConversions);
    }

    private static List<Object> storeConverters(Dialect dialect) {

        List<Object> converters = new ArrayList<>();
        converters.addAll(dialect.getConverters());
        converters.addAll(JdbcCustomConversions.storeConverters());
        return converters;
    }

    private static boolean isDateTimeApiConversion(GenericConverter.ConvertiblePair cp) {

        if (cp.getSourceType().equals(java.util.Date.class)) {
            return cp.getTargetType().getTypeName().startsWith("java.time.");
        }

        if (cp.getTargetType().equals(java.util.Date.class)) {
            return cp.getSourceType().getTypeName().startsWith("java.time.");
        }

        return false;
    }

    private static boolean excludeConversionsBetweenDateAndJsr310Types(GenericConverter.ConvertiblePair cp) {
        return !isDateTimeApiConversion(cp);
    }
}
