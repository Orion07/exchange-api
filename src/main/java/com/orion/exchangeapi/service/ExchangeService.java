package com.orion.exchangeapi.service;

import com.orion.exchangeapi.model.ExchangeRateResponseModel;

import java.time.LocalDate;
import java.util.Currency;

public interface ExchangeService {

    ExchangeRateResponseModel getExchangeRate(Currency baseCurrency, Currency targetCurrency, LocalDate date);
}
