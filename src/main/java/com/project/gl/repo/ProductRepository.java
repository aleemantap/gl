package com.project.gl.repo;

import com.project.gl.model.entity.Product;
import com.project.gl.model.projection.ProductRuleOption;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Page<Product> findProductsWithFilter(
    // @Param("search") String search,
    // @Param("active") Boolean active,
    // Pageable pageable);

     @Query("""
        SELECT p
        FROM Product p
        WHERE
            (
                :search IS NULL
                OR :search = ''
                OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            AND (
                :active IS NULL
                OR p.isActive = :active
            )
    """)
    Page<Product> findProductsWithFilter(
            @Param("search") String search,
            @Param("active") Boolean active,
            Pageable pageable
    );

   Optional<Product> findByProductCode(String productCode);
   Optional<Product> findByProductCodeAndIsActiveTrue(String productCode);

   boolean existsByProductCode(String code);

   @Query("""
        SELECT
            pr.id AS id,
            pr.productId AS productId,
            p.productCode AS productCode,
            p.productName AS productName,
            pr.ruleVersion AS ruleVersion,
            pr.effectiveFrom AS effectiveFrom
        FROM ProductRule pr
        JOIN Product p ON p.id = pr.productId
        WHERE pr.isActive = true
        AND p.isActive = true
        ORDER BY
            p.productName,
            pr.effectiveFrom DESC,
            pr.ruleVersion DESC
    """)
    List<ProductRuleOption> findProductRuleOptions();
   
}