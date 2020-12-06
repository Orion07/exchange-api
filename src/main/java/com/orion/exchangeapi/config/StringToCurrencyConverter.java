package com.orion.exchangeapi.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.util.Currency;

public class StringToCurrencyConverter implements Converter<String, Currency> {

    @Override
    public Currency convert(String source) {
        return StringUtils.isEmpty(source) ? null : Currency.getInstance(source.toUpperCase());
    }

}