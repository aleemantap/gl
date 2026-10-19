package com.project.gl.repo;

import com.project.gl.model.entity.ProductRule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRuleRepository extends JpaRepository<ProductRule, Long> {

   
    //========================
     List<ProductRule> findAllByOrderByIdDesc();
    
    @Query("SELECT pr FROM ProductRule pr WHERE " +
           "(:productId IS NULL OR pr.product.id = :productId) AND " +
         
           "(:isActive IS NULL OR pr.isActive = :isActive)")
    Page<ProductRule> findByFilters(
            @Param("productId") Long productId,
       //      @Param("approvalStatus") String approvalStatus,
            @Param("isActive") Boolean isActive,
            Pageable pageable);
    
    @Query("SELECT pr FROM ProductRule pr WHERE " +
           "(:productId IS NULL OR pr.product.id = :productId) AND " +
           "(LOWER(pr.approvalStatus) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(pr.sourceSystem) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(pr.createdBy) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ProductRule> findByFiltersWithSearch(
            @Param("productId") Long productId,
            @Param("search") String search,
            Pageable pageable);

    //-----------

       // @Query("""
       // SELECT pr
       // FROM ProductRule pr
       // WHERE pr.productId = :productId
       // AND pr.isActive = true
       // AND pr.approvalStatus = 'APPROVED'
       // AND pr.effectiveFrom <= :transactionDate
       // AND (
       //        pr.effectiveTo IS NULL
       //        OR pr.effectiveTo >= :transactionDate
       //        )
       // ORDER BY pr.ruleVersion DESC
       // """)
       // List<ProductRule> findActiveRules(
       //        @Param("productId") Long productId,
       //        @Param("transactionDate") LocalDate transactionDate
       // );

       @Query("""
       SELECT pr
       FROM ProductRule pr
       WHERE pr.product.id = :productId
       AND pr.isActive = true
       AND pr.approvalStatus = 'APPROVED'
       AND pr.effectiveFrom <= :transactionDate
       AND (
              pr.effectiveTo IS NULL
              OR pr.effectiveTo >= :transactionDate
       )
       ORDER BY pr.ruleVersion DESC
       """)
       List<ProductRule> findActiveRules(
              @Param("productId") Long productId,
              @Param("transactionDate") LocalDate transactionDate
       );

       
}