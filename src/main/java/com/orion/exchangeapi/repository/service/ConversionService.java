package com.orion.exchangeapi.repository.service;

import com.orion.exchangeapi.entity.Conversion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;

public interface ConversionService {

    Conversion save(Currency baseCurrency, Currency targetCurrency,
                    BigDecimal amount, BigDecimal convertedAmount, BigDecimal rate,
                    LocalDate date);

    Page<Conversion> search(UUID transactionId, LocalDate transactionDate, Pageable pageable);

}
