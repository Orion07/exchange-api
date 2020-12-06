package com.orion.exchangeapi.service.impl;


import com.orion.exchangeapi.config.RatesApiProperties;
import com.orion.exchangeapi.constants.ErrorEnum;
import com.orion.exchangeapi.exception.ApiException;
import com.orion.exchangeapi.model.ExchangeApiRateResponseModel;
import com.orion.exchangeapi.model.ExchangeRateResponseModel;
import com.orion.exchangeapi.service.ExchangeService;
import com.orion.exchangeapi.service.HttpService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;

@Service
public class RatesApiExchangeService implements ExchangeService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final RatesApiProperties apiProperties;
    private final HttpService httpService;

    public RatesApiExchangeService(RatesApiProperties apiProperties, HttpService httpService) {
        this.apiProperties = apiProperties;
        this.httpService = httpService;
    }

    @Override
    public ExchangeRateResponseModel getExchangeRate(Currency baseCurrency, Currency targetCurrency, LocalDate date) {
        if (baseCurrency == null || targetCurrency == null) {
            throw new ApiException(ErrorEnum.CURRENCY_CAN_NOT_NULL);
        }

        ExchangeRateResponseModel response = new ExchangeRateResponseModel();
        response.setBaseCurrency(baseCurrency);
        response.setTargetCurrency(targetCurrency);
        response.setDate(date == null ? LocalDate.now() : date);

        if (baseCurrency.getCurrencyCode().equalsIgnoreCase(targetCurrency.getCurrencyCode())) {
            response.setRate(BigDecimal.ONE);
            response.setSuccess(true);
            return response;
        }

        String ratePath = date == null ? "/latest" : "/" + formatter.format(date);
        URI uri = UriComponentsBuilder
                .fromHttpUrl(apiProperties.getUrl())
                .path(ratePath)
                .queryParam("base", baseCurrency.getCurrencyCode().toUpperCase())
                .queryParam("symbols", targetCurrency.getCurrencyCode().toUpperCase())
                .build(true)
                .toUri();

        ExchangeApiRateResponseModel rateResponse = httpService.get(uri, null, ExchangeApiRateResponseModel.class);

        if (rateResponse == null || rateResponse.getRates() == null || rateResponse.getRates().get(targetCurrency) == null) {
            response.setErrorMessage(ErrorEnum.RATE_NOT_FOUND.getMessage());
            response.setSuccess(false);
            return response;
        }

        response.setRate(rateResponse.getRates().get(targetCurrency));
        response.setSuccess(true);
        return response;
    }
}
