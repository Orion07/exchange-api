package com.orion.exchangeapi.repository.service.impl;

import com.orion.exchangeapi.entity.Conversion;
import com.orion.exchangeapi.repository.ConversionRepository;
import com.orion.exchangeapi.repository.service.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;

@Service
public class ConversionServiceImpl implements ConversionService {

    private final ConversionRepository conversionRepository;

    public ConversionServiceImpl(ConversionRepository conversionRepository) {
        this.conversionRepository = conversionRepository;
    }

    @Override
    public Conversion saveConversion(Currency baseCurrency, Currency targetCurrency,
                                     BigDecimal amount, BigDecimal convertedAmount, BigDecimal rate,
                                     LocalDate date) {

        Conversion conversion = new Conversion();
        conversion.setTransactionId(UUID.randomUUID());
        conversion.setBaseCurrency(baseCurrency);
        conversion.setTargetCurrency(targetCurrency);
        conversion.setAmount(amount);
        conversion.setConvertedAmount(convertedAmount);
        conversion.setRate(rate);
        conversion.setRateDate(date);

        Conversion savedConversion = conversionRepository.save(conversion);
        return savedConversion;
    }

    @Override
    public Page<Conversion> searchConversions(UUID transactionId, LocalDate transactionDate, Pageable pageable) {
        return conversionRepository.findAll(new ConversionSpecification(transactionId, transactionDate), pageable);
    }
}
