package com.project.gl.repo;


import com.project.gl.model.entity.CashReconciliation;

import java.time.LocalDate;
import java.util.Optional;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface CashReconciliationRepository
        extends JpaRepository<CashReconciliation, Long> {

    Optional<CashReconciliation> findByTellerUserIdAndKasPointCodeAndReconciliationDate(
            Long tellerUserId,
            String kasPointCode,
            LocalDate reconciliationDate
    );
}