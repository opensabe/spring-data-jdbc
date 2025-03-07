package io.github.opensabe.jdbc.core.converter;

import org.springframework.core.convert.converter.Converter;

import java.math.BigInteger;

/**
 * @author heng.ma
 */
public class BigIntegerToIntegerConverter implements Converter<BigInteger, Integer> {

    @Override
    public Integer convert(BigInteger source) {
        return source.intValue();
    }
}
