package com.project.gl.service;

import com.project.gl.exception.JournalValidationException;
import com.project.gl.model.entity.TransactionCollateral;
import com.project.gl.model.request.JournalRequest;
import com.project.gl.repo.TransactionCollateralRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionCollateralService {

    @Autowired
    private TransactionCollateralRepository transactionCollateralRepository;

    // =========================================================
    // VALIDASI COLLATERAL
    // =========================================================

    public void validateCollateral(JournalRequest request) {

        List<String> errors = new ArrayList<>();

        JournalRequest.Collateral collateral =
                request.getCollateral();

        if (collateral == null) {
            errors.add(
                    "collateral wajib diisi untuk transaksi pencairan."
            );

            throw new JournalValidationException(errors);
        }

        if (collateral.getCollateralType() == null
                || collateral.getCollateralType().isBlank()) {

            errors.add("collateral_type wajib diisi.");
        }

        if (collateral.getAppraisedValue() == null
                || collateral.getAppraisedValue()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            errors.add(
                    "appraised_value harus lebih besar dari 0."
            );
        }

        if (collateral.getLtv() == null
                || collateral.getLtv()
                        .compareTo(BigDecimal.ZERO) < 0
                || collateral.getLtv()
                        .compareTo(new BigDecimal("100")) > 0) {

            errors.add(
                    "ltv harus berada antara 0 sampai 100."
            );
        }

        if (!errors.isEmpty()) {
            throw new JournalValidationException(errors);
        }
    }

    // =========================================================
    // SIMPAN COLLATERAL
    // =========================================================

    public void saveCollateral(JournalRequest request) {

        JournalRequest.Collateral collateral =
                request.getCollateral();

        TransactionCollateral entity =
                new TransactionCollateral();

        entity.setTransactionNo(
                request.getTransactionNo()
        );

        entity.setCollateralType(
                collateral.getCollateralType()
        );

        // entity.setAppraisedValue(
        //         collateral.getAppraisedValue()
        // );

        // entity.setLtv(
        //         collateral.getLtv()
        // );

        transactionCollateralRepository.save(entity);
    }
}