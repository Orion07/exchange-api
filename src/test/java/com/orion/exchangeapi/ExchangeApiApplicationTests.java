package com.orion.exchangeapi;

import com.orion.exchangeapi.service.ExchangeService;
import com.orion.exchangeapi.constants.ErrorEnum;
import com.orion.exchangeapi.entity.Conversion;
import com.orion.exchangeapi.exception.ApiException;
import com.orion.exchangeapi.model.ConversionRequestModel;
import com.orion.exchangeapi.model.ConversionResponseModel;
import com.orion.exchangeapi.model.ExchangeRateResponseModel;
import com.orion.exchangeapi.repository.service.ConversionService;
import com.orion.exchangeapi.service.ConversionApiService;
import com.orion.exchangeapi.service.impl.ConversionApiServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ExchangeApiApplicationTests {

    @Mock
    private ExchangeService mockExchangeService;

    @Mock
    private ConversionService mockConversionService;

    @Test
    void testRateNotFound() {
        ConversionApiService mockConversionApiService = new ConversionApiServiceImpl(mockExchangeService, mockConversionService);

        ConversionRequestModel request = new ConversionRequestModel();
        request.setBaseCurrency(Currency.getInstance("TRY"));
        request.setTargetCurrency(Currency.getInstance("USD"));
        request.setAmount(new BigDecimal("100"));

        Mockito.when(mockExchangeService.getExchangeRate(request.getBaseCurrency(), request.getTargetCurrency(), null)).thenReturn(null);

        ApiException apiException = Assertions.assertThrows(ApiException.class, () -> mockConversionApiService.conversion(request));
        Assertions.assertEquals(ErrorEnum.RATE_NOT_FOUND, apiException.getError());
    }

    @Test
    void testInternalServerError() {
        ConversionApiService mockConversionApiService = new ConversionApiServiceImpl(mockExchangeService, mockConversionService);

        Currency baseCurrency = Currency.getInstance("USD");
        Currency targetCurrency = Currency.getInstance("TRY");
        BigDecimal amount = new BigDecimal("100");
        BigDecimal rate = new BigDecimal("2");

        ConversionRequestModel request = new ConversionRequestModel();
        request.setBaseCurrency(baseCurrency);
        request.setTargetCurrency(targetCurrency);
        request.setAmount(amount);

        ExchangeRateResponseModel exchangeRate = new ExchangeRateResponseModel();
        exchangeRate.setBaseCurrency(baseCurrency);
        exchangeRate.setTargetCurrency(targetCurrency);
        exchangeRate.setRate(rate);
        exchangeRate.setSuccess(true);

        Mockito.when(mockExchangeService.getExchangeRate(baseCurrency, targetCurrency, null)).thenReturn(exchangeRate);
        Mockito.when(mockConversionService.save(baseCurrency, targetCurrency, amount, amount.multiply(rate), rate, null)).thenReturn(null);

        ApiException apiException = Assertions.assertThrows(ApiException.class, () -> mockConversionApiService.conversion(request));
        Assertions.assertEquals(ErrorEnum.INTERNAL_SERVER_ERROR, apiException.getError());
    }

    @Test
    void testSuccessConversion() {
        ConversionApiService mockConversionApiService = new ConversionApiServiceImpl(mockExchangeService, mockConversionService);

        Currency baseCurrency = Currency.getInstance("USD");
        Currency targetCurrency = Currency.getInstance("TRY");
        BigDecimal amount = new BigDecimal("100");
        BigDecimal rate = new BigDecimal("2");
        BigDecimal convertedAmount = amount.multiply(rate);

        ConversionRequestModel request = new ConversionRequestModel();
        request.setBaseCurrency(baseCurrency);
        request.setTargetCurrency(targetCurrency);
        request.setAmount(amount);

        ExchangeRateResponseModel exchangeRate = new ExchangeRateResponseModel();
        exchangeRate.setBaseCurrency(baseCurrency);
        exchangeRate.setTargetCurrency(targetCurrency);
        exchangeRate.setRate(rate);
        exchangeRate.setSuccess(true);
        Mockito.when(mockExchangeService.getExchangeRate(baseCurrency, targetCurrency, null)).thenReturn(exchangeRate);

        Conversion conversion = new Conversion();
        conversion.setBaseCurrency(baseCurrency);
        conversion.setTargetCurrency(targetCurrency);
        conversion.setRate(rate);
        conversion.setAmount(amount);
        conversion.setConvertedAmount(convertedAmount);
        conversion.setTransactionId(UUID.randomUUID());
        conversion.setRateDate(LocalDate.now());
        conversion.setCreateDate(LocalDateTime.now());
        Mockito.when(mockConversionService.save(baseCurrency, targetCurrency, amount, convertedAmount, rate, null)).thenReturn(conversion);

        ConversionResponseModel response = mockConversionApiService.conversion(request);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getSuccess());
        Assertions.assertEquals(convertedAmount, response.getConvertedAmount());
        Assertions.assertEquals(baseCurrency, response.getBaseCurrency());
        Assertions.assertEquals(targetCurrency, response.getTargetCurrency());
        Assertions.assertEquals(amount, response.getAmount());
        Assertions.assertEquals(rate, response.getRate());
        Assertions.assertNotNull(response.getTransactionId());
    }
}
