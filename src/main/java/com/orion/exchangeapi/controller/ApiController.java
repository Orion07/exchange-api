package com.orion.exchangeapi.controller;

import com.orion.exchangeapi.service.ExchangeService;
import com.orion.exchangeapi.model.ConversionDetailModel;
import com.orion.exchangeapi.model.ConversionRequestModel;
import com.orion.exchangeapi.model.ConversionResponseModel;
import com.orion.exchangeapi.model.ExchangeRateResponseModel;
import com.orion.exchangeapi.service.ConversionApiService;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;

@RestController
public class ApiController {

    private final ExchangeService exchangeService;

    private final ConversionApiService conversionApiService;

    public ApiController(@Qualifier("baseExchangeService") ExchangeService exchangeService, ConversionApiService conversionApiService) {
        this.exchangeService = exchangeService;
        this.conversionApiService = conversionApiService;
    }

    @ApiIgnore
    @GetMapping("/")
    public RedirectView home() {
        return new RedirectView("swagger-ui/index.html");
    }

    @ApiImplicitParam(name = "date", dataType = "date", paramType = "query", value = "Date format should be like this yyyy-MM-dd")
    @GetMapping("/exchange-rate")
    public ExchangeRateResponseModel exchangeRate(@RequestParam("baseCurrency") Currency baseCurrency,
                                                  @RequestParam("targetCurrency") Currency targetCurrency,
                                                  @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return exchangeService.getExchangeRate(baseCurrency, targetCurrency, date);
    }

    @PostMapping("/conversion")
    public ConversionResponseModel conversion(@Valid @RequestBody ConversionRequestModel request) {
        return conversionApiService.conversion(request);
    }

    @ApiImplicitParam(name = "transactionDate", dataType = "date", paramType = "query", value = "Date format should be like this yyyy-MM-dd")
    @GetMapping("/conversion/list")
    public Page<ConversionDetailModel> conversion(@RequestParam(value = "transactionId", required = false) UUID transactionId,
                                                  @RequestParam(value = "transactionDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate transactionDate,
                                                  Pageable pageable) {
        return conversionApiService.search(transactionId, transactionDate, pageable);
    }
}
