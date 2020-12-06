package com.orion.exchangeapi.service.impl;

import com.orion.exchangeapi.config.FixerApiProperties;
import com.orion.exchangeapi.exception.ApiException;
import com.orion.exchangeapi.model.ExchangeApiRateResponseModel;
import com.orion.exchangeapi.model.ExchangeRateResponseModel;
import com.orion.exchangeapi.service.ExchangeService;
import com.orion.exchangeapi.constants.ErrorEnum;
import com.orion.exchangeapi.service.HttpService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Map;

@Service
public class FixerApiExchangeService implements ExchangeService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final FixerApiProperties apiProperties;
    private final HttpService httpService;

    public FixerApiExchangeService(FixerApiProperties apiProperties, HttpService httpService) {
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
                .queryParam("access_key", apiProperties.getAuthToken())
                //.queryParam("base", baseCurrency.getCurrencyCode().toUpperCase()) // Fixer API's free token doesn't support base parameter :/
                .queryParam("symbols", buildSymbols(baseCurrency, targetCurrency))
                .build(true)
                .toUri();

        ExchangeApiRateResponseModel rateResponse = httpService.get(uri, null, ExchangeApiRateResponseModel.class);

        if (rateResponse == null || rateResponse.getRates() == null || rateResponse.getRates().get(targetCurrency) == null) {
            response.setErrorMessage(ErrorEnum.RATE_NOT_FOUND.getMessage());
            response.setSuccess(false);
            return response;
        }

        Map<Currency, BigDecimal> rates = rateResponse.getRates();

        BigDecimal baseRate = rates.get(baseCurrency);
        BigDecimal targetRate = rates.get(targetCurrency);
        BigDecimal rate = targetRate.divide(baseRate, 10, RoundingMode.HALF_UP);

        response.setRate(rate);
        response.setSuccess(true);
        return response;
    }

    private String buildSymbols(Currency... currencies) {
        StringBuilder builder = new StringBuilder();
        if (currencies != null && currencies.length > 0) {
            for (int i = 0; i < currencies.length; i++) {
                builder.append(currencies[i].getCurrencyCode().toUpperCase());
                builder.append(",");
            }
        }
        return builder.toString();
    }
}
