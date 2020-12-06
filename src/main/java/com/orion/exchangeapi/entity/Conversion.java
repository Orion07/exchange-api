package com.orion.exchangeapi.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;

@Entity
@Table(name = "conversion")
public class Conversion extends BaseEntity {

    @Column
    private UUID transactionId;

    @Column
    private Currency baseCurrency;

    @Column
    private Currency targetCurrency;

    @Column(precision = 19, scale = 10)
    private BigDecimal amount;

    @Column(precision = 19, scale = 10)
    private BigDecimal convertedAmount;

    @Column(precision = 19, scale = 10)
    private BigDecimal rate;

    @Column
    private LocalDate rateDate;

    @PrePersist
    public void prePersistyRateDate() {
        if (this.rateDate == null) {
            this.rateDate = LocalDate.now();
        }
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public LocalDate getRateDate() {
        return rateDate;
    }

    public void setRateDate(LocalDate rateDate) {
        this.rateDate = rateDate;
    }
}
