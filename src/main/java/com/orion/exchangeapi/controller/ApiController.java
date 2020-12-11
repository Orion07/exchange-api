package com.orion.exchangeapi.controller;

import com.orion.exchangeapi.service.ExchangeService;
import com.orion.exchangeapi.model.ConversionDetailModel;
import com.orion.exchangeapi.model.ConversionRequestModel;
import com.orion.exchangeapi.model.ConversionResponseModel;
import com.orion.exchangeapi.model.ExchangeRateResponseModel;
import com.orion.exchangeapi.service.ConversionApiService;
import com.orion.exchangeapi.service.HttpService;
import io.swagger.annotations.ApiImplicitParam;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.Valid;
import java.net.URI;
import java.security.Key;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.time.LocalDate;
import java.util.*;

@RestController
public class ApiController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final ExchangeService exchangeService;

    private final ConversionApiService conversionApiService;

    private final HttpService httpService;

    public ApiController(@Qualifier("baseExchangeService") ExchangeService exchangeService, ConversionApiService conversionApiService, HttpService httpService) {
        this.exchangeService = exchangeService;
        this.conversionApiService = conversionApiService;
        this.httpService = httpService;
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

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    @GetMapping("/test")
    public String test() {
       //Security.addProvider(new BouncyCastleProvider());
        Map handshakeResponse = handshake();

        String authorization = (String) handshakeResponse.get("authorization");

        String aesKey = (String) handshakeResponse.get("aesKey");
        byte[] aesKeyBytes = Base64.getMimeDecoder().decode(aesKey);

        String aesIV = (String) handshakeResponse.get("aesIV");
        byte[] aesIVBytes = Base64.getMimeDecoder().decode(aesIV);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
        headers.put("X-VP-Authorization", authorization);

        String apiUrl = "https://mobilechallenge.veripark.com";
        URI stockListUri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .path("/api/stocks/list")
                .build(true)
                .toUri();

        Map<String, Object> body = new HashMap<>();
        try {
            body.put("period", aesEncrypt("all", aesKeyBytes, aesIVBytes));

            Map stockListResponse = httpService.post(stockListUri, headers, body, Map.class);

            List<Map<String,Object>> stocks = (List<Map<String, Object>>) stockListResponse.get("stocks");

            if(!CollectionUtils.isEmpty(stocks)) {
                for (Map<String, Object> stock:stocks) {
                    String symbol = (String) stock.get("symbol");

                    String decryptedSymbol = aesDecrypt(symbol, aesKeyBytes, aesIVBytes);
                    LOG.info("::symbol:{}",decryptedSymbol);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "OK";
    }

    private Map handshake() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");

        String apiUrl = "https://mobilechallenge.veripark.com";
        URI handshakeUri = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .path("/api/handshake/start")
                .build(true)
                .toUri();

        Map<String, Object> body = new HashMap<>();
        body.put("deviceId", UUID.randomUUID().toString());
        body.put("systemVersion", "12.2");
        body.put("platformName", "iOS");
        body.put("deviceModel", "iPhone XS Max");
        body.put("manifacturer", "Apple");

        Map handshakeResponse = httpService.post(handshakeUri, headers, body, Map.class);


        return handshakeResponse;
    }


    public String aesEncrypt(String data, byte[] key, byte[] ivKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        Key secretKeySpec = new SecretKeySpec(key, "AES");
        AlgorithmParameterSpec algorithmParameterSpec = new IvParameterSpec(ivKey);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, algorithmParameterSpec);
        return Base64.getMimeEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    public static String aesDecrypt(String data, byte[] key, byte[] ivKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Key secretKeySpec = new SecretKeySpec(key, "AES");
        AlgorithmParameterSpec algorithmParameterSpec = new IvParameterSpec(ivKey);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, algorithmParameterSpec);
        byte[] decodedData = cipher.doFinal(Base64.getMimeDecoder().decode(data));
        return new String(decodedData, "UTF-8");
    }

}
