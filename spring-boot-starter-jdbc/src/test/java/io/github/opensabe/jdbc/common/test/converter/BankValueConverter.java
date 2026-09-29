package io.github.opensabe.jdbc.common.test.converter;

import io.github.opensabe.jdbc.converter.DefaultValueConversionContext;
import io.github.opensabe.jdbc.converter.InternalPropertyValueConverter;

public class BankValueConverter implements InternalPropertyValueConverter<BankValue, Integer> {

    @Override
    public BankValue read(Integer value, DefaultValueConversionContext<?> context) {
        return new BankValue(value);
    }

    @Override
    public Integer write(BankValue value, DefaultValueConversionContext<?> context) {
        return value.id();
    }
}
