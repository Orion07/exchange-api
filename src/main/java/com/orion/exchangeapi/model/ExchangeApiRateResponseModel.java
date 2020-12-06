package com.orion.exchangeapi.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Map;

public class ExchangeApiRateResponseModel {

    private Currency base;

    private Map<Currency, BigDecimal> rates;

    private LocalDate date;

    public Currency getBase() {
        return base;
    }

    public void setBase(Currency base) {
        this.base = base;
    }

    public Map<Currency, BigDecimal> getRates() {
        return rates;
    }

    public void setRates(Map<Currency, BigDecimal> rates) {
        this.rates = rates;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
