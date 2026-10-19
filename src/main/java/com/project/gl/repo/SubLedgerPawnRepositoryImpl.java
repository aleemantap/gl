package com.project.gl.repo;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Repository;

@Repository
public class SubLedgerPawnRepositoryImpl implements SubLedgerPawnRepository {

    @Override
    public BigDecimal getActiveLoanBalance(
            LocalDate date) {

        return new BigDecimal("118500000");
    }

    @Override
    public BigDecimal getAdvanceBalance(
            LocalDate date) {

        return new BigDecimal("45150000");
    }

    @Override
    public BigDecimal getAccruedInterest(
            LocalDate date) {

        return new BigDecimal("8750400");
    }
}