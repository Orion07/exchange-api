package com.orion.exchangeapi.service.impl;

import com.orion.exchangeapi.service.ExchangeService;
import com.orion.exchangeapi.model.ExchangeRateResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

@Service("baseExchangeService")
public class BaseExchangeService implements ExchangeService {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final List<ExchangeService> exchangeServices;

    public BaseExchangeService(List<ExchangeService> exchangeServices) {
        this.exchangeServices = exchangeServices;
    }

    @Override
    public ExchangeRateResponseModel getExchangeRate(Currency baseCurrency, Currency targetCurrency, LocalDate date) {
        for (ExchangeService exchangeService : exchangeServices) {
            try {
                ExchangeRateResponseModel serviceResponse = exchangeService.getExchangeRate(baseCurrency, targetCurrency, date);
                if (serviceResponse != null && serviceResponse.getSuccess()) {
                    return serviceResponse;
                }
            } catch (Exception e) {
                LOG.error("::getExchangeRate baseCurrency:{}, targetCurrency:{}, date:{}", baseCurrency, targetCurrency, date, e);
            }
        }
        return null;
    }
}
