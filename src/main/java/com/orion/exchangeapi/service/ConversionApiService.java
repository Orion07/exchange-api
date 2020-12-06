package com.orion.exchangeapi.service;

import com.orion.exchangeapi.model.ConversionRequestModel;
import com.orion.exchangeapi.model.ConversionResponseModel;
import com.orion.exchangeapi.model.ConversionDetailModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface ConversionApiService {

    ConversionResponseModel conversion(ConversionRequestModel request);

    Page<ConversionDetailModel> searchConversions(UUID transactionId, LocalDate transactionDate, Pageable pageable);
}
