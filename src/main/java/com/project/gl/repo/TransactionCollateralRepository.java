package com.project.gl.repo;

import com.project.gl.model.entity.TransactionCollateral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionCollateralRepository extends JpaRepository<TransactionCollateral, Long> {

    Optional<TransactionCollateral> findByTransactionNo(String transactionNo);
    boolean existsByTransactionNo(String transactionNo);
}