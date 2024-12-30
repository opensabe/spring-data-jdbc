package io.github.mado.jdbc.converter.extension;

import org.springframework.core.convert.converter.Converter;

import java.math.BigInteger;

/**
 * @author heng.ma
 */
public class BigIntToLongConverter implements Converter<BigInteger, Long> {

    @Override
    public Long convert(BigInteger source) {
        return source.longValue();
    }
}
