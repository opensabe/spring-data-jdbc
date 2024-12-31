package io.github.mado.jdbc.core.converter;

import org.springframework.core.convert.converter.Converter;

import java.math.BigInteger;

/**
 * @author heng.ma
 */
public class BigIntegerToLongConverter implements Converter<BigInteger, Long> {

    @Override
    public Long convert(BigInteger source) {
        return source.longValue();
    }
}
