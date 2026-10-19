package com.project.gl.repo;
import com.project.gl.model.entity.ProductRuleComponent;
import com.project.gl.model.projection.ProductRuleComponentDto;
import com.project.gl.model.projection.ProductRuleComponentOption;
import com.project.gl.model.projection.ProductRuleComponentProjection;
import com.project.gl.model.response.ProductRuleComponentDTO;
import com.project.gl.model.projection.ProductRuleComponentProjection2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductRuleComponentRepository extends JpaRepository<ProductRuleComponent, Long> {

    List<ProductRuleComponent> findByProductRuleIdAndActiveTrue(Long productRuleId);
     @Query("""
            SELECT 
                c.id AS id,
                c.productRuleId AS productRuleId,
                comp.id AS componentId,
                comp.componentCode AS componentCode,
                comp.componentName AS componentName,
                c.calculationType AS calculationType,
                c.amountSource AS amountSource,
                c.fixedAmount AS fixedAmount,
                c.active AS active,
                c.createdBy AS createdBy,
                c.createdAt AS createdAt,
                c.updatedBy AS updatedBy,
                c.updatedAt AS updatedAt
            FROM ProductRuleComponent c
            JOIN c.component comp
            WHERE c.id = :id
            """)
    Optional<ProductRuleComponentProjection2> findProjectionById(@Param("id") Long id);

   

   @Query("""
            SELECT 
                c.id AS id,
                c.productRuleId AS productRuleId,
                comp.componentCode AS componentCode,
                comp.componentName AS componentName,
                c.calculationType AS calculationType,
                c.amountSource AS amountSource,
                c.fixedAmount AS fixedAmount,
                c.active AS active,
                c.createdBy AS createdBy,
                c.createdAt AS createdAt,
                c.updatedBy AS updatedBy,
                c.updatedAt AS updatedAt
            FROM ProductRuleComponent c
            JOIN c.component comp
            WHERE
                (:productRuleId IS NULL
                    OR c.productRuleId = :productRuleId)
                AND
                (:isActive IS NULL
                    OR c.active = :isActive)
                AND
                (
                    :search IS NULL
                    OR LOWER(comp.componentCode)
                        LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(comp.componentName)
                        LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(c.calculationType)
                        LIKE LOWER(CONCAT('%', :search, '%'))
                )
            """)
    Page<ProductRuleComponentProjection> findWithFilters(
            @Param("productRuleId") Long productRuleId,
            @Param("isActive") Boolean isActive,
            @Param("search") String search,
            Pageable pageable
    );

    long countByComponentId(Long componentId);

    Optional<ProductRuleComponent> findByProductRuleIdAndComponentId(
            Long productRuleId,
            Long componentId
    );


    @Query("""
        SELECT
            prc.id AS productRuleComponentId,
            c.componentCode AS componentCode,
            c.componentName AS componentName,
            prc.calculationType AS calculationType,
            prc.amountSource AS amountSource,
            prc.componentId AS componentId
        FROM ProductRuleComponent prc
        JOIN prc.component c
        WHERE prc.productRuleId = :productRuleId
          AND prc.active = true
        ORDER BY c.componentCode
    """)
    List<ProductRuleComponentOption> findOptionsByProductRuleId(
            @Param("productRuleId") Long productRuleId
    );
}