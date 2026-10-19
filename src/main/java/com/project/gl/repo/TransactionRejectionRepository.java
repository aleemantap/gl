package com.project.gl.repo;

import com.project.gl.model.entity.TransactionRejection;
import com.project.gl.model.enums.TransactionRejectionStatus;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface TransactionRejectionRepository extends JpaRepository<TransactionRejection, Long> {

     @Query("SELECT t FROM TransactionRejection t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:fromDate IS NULL OR t.transactionTime >= :fromDate) AND " +
           "(:toDate IS NULL OR t.transactionTime <= :toDate)")
     Page<TransactionRejection> findByFilters(
            @Param("status") TransactionRejectionStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

      @Query("SELECT t FROM TransactionRejection t WHERE " +
           "(:idsEmpty = true OR t.id IN (:ids)) AND"+
           "(:status IS NULL OR t.status = :status) AND " +
           "(:fromDate IS NULL OR t.transactionTime >= :fromDate) AND " +
           "(:toDate IS NULL OR t.transactionTime <= :toDate)")
      List<TransactionRejection> findByFiltersExpor(
            @Param("ids") List<Long> ids,
            @Param("idsEmpty") boolean idsEmpty,
            @Param("status") TransactionRejectionStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
            );      

            
}