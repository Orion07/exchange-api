package com.orion.exchangeapi.repository;

import com.orion.exchangeapi.entity.Conversion;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ConversionRepository extends PagingAndSortingRepository<Conversion, Long>, JpaSpecificationExecutor<Conversion> {

}
