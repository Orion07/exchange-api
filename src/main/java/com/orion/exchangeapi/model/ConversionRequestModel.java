package com.orion.exchangeapi.model;

import io.swagger.annotations.ApiImplicitParam;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

public class ConversionRequestModel {

    @NotNull(message = "baseCurrency cannot be null")
    private Currency baseCurrency;

    @NotNull(message = "targetCurrency cannot be null")
    private Currency targetCurrency;

    private LocalDate date;

    @NotNull(message = "amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be bigger than 0.01")
    private BigDecimal amount;

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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
