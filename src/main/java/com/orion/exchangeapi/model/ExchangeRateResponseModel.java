package com.orion.exchangeapi.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

public class ExchangeRateResponseModel extends BaseResponseModel {

    private Currency baseCurrency;

    private Currency targetCurrency;

    private BigDecimal rate;

    private LocalDate date;

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(Currency targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
