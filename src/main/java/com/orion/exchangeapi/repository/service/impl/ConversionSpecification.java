package com.orion.exchangeapi.repository.service.impl;

import com.orion.exchangeapi.entity.Conversion;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConversionSpecification implements Specification<Conversion> {

    private final UUID transactionId;

    private final LocalDate transactionDate;

    public ConversionSpecification(UUID transactionId, LocalDate transactionDate) {
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
    }

    @Override
    public Predicate toPredicate(Root<Conversion> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (transactionId != null) {
            predicates.add(criteriaBuilder.equal(root.get("transactionId"), transactionId));
        } else if (transactionDate != null) {
            LocalDateTime startTime = LocalDateTime.of(transactionDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(transactionDate, LocalTime.MAX);
            predicates.add(criteriaBuilder.between(root.get("createDate"), startTime, endTime));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));

    }
}
