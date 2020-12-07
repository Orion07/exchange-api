package com.orion.exchangeapi.service.impl;

import com.orion.exchangeapi.entity.Conversion;
import com.orion.exchangeapi.exception.ApiException;
import com.orion.exchangeapi.model.ConversionRequestModel;
import com.orion.exchangeapi.model.ConversionResponseModel;
import com.orion.exchangeapi.model.ExchangeRateResponseModel;
import com.orion.exchangeapi.repository.service.ConversionService;
import com.orion.exchangeapi.service.ExchangeService;
import com.orion.exchangeapi.constants.ErrorEnum;
import com.orion.exchangeapi.model.ConversionDetailModel;
import com.orion.exchangeapi.service.ConversionApiService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

@Service
public class ConversionApiServiceImpl implements ConversionApiService {

    private final ExchangeService exchangeService;

    private final ConversionService conversionService;

    public ConversionApiServiceImpl(@Qualifier("baseExchangeService") ExchangeService exchangeService,
                                    ConversionService conversionService) {
        this.exchangeService = exchangeService;
        this.conversionService = conversionService;
    }

    @Override
    public ConversionResponseModel conversion(ConversionRequestModel request) {
        ExchangeRateResponseModel exchangeRate = exchangeService.getExchangeRate(request.getBaseCurrency(), request.getTargetCurrency(), request.getDate());

        if (exchangeRate == null || !exchangeRate.getSuccess()) {
            throw new ApiException(ErrorEnum.RATE_NOT_FOUND);
        }

        BigDecimal rate = exchangeRate.getRate();
        BigDecimal convertedAmount = request.getAmount().multiply(rate);

        Conversion conversion = conversionService.save(request.getBaseCurrency(), request.getTargetCurrency(),
                request.getAmount(), convertedAmount, rate, request.getDate());

        if (conversion == null) {
            throw new ApiException(ErrorEnum.INTERNAL_SERVER_ERROR);
        }

        ConversionResponseModel response = new ConversionResponseModel();
        response.setTransactionId(conversion.getTransactionId());
        response.setBaseCurrency(conversion.getBaseCurrency());
        response.setTargetCurrency(conversion.getTargetCurrency());
        response.setAmount(conversion.getAmount());
        response.setDate(conversion.getRateDate());
        response.setConvertedAmount(conversion.getConvertedAmount());
        response.setRate(rate);
        response.setSuccess(true);

        return response;
    }

    @Override
    public Page<ConversionDetailModel> search(UUID transactionId, LocalDate transactionDate, Pageable pageable) {
        Page<Conversion> conversions = conversionService.search(transactionId, transactionDate, pageable);
        if (conversions != null) {
            return conversions.map(conversion -> {
                ConversionDetailModel model = new ConversionDetailModel();
                model.setTransactionId(conversion.getTransactionId());
                model.setBaseCurrency(conversion.getBaseCurrency());
                model.setTargetCurrency(conversion.getTargetCurrency());
                model.setRate(conversion.getRate());
                model.setDate(conversion.getRateDate());
                model.setAmount(conversion.getAmount());
                model.setConvertedAmount(conversion.getConvertedAmount());
                model.setTransactionTime(conversion.getCreateDate());
                return model;
            });
        }
        return new PageImpl<>(Arrays.asList());
    }
}
