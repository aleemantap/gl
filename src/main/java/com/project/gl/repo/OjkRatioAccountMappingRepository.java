package com.project.gl.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.gl.model.entity.OjkRatioAccountMapping;
import com.project.gl.model.projection.OjkRatioBalanceProjection;

@Repository
public interface OjkRatioAccountMappingRepository  extends JpaRepository<OjkRatioAccountMapping, Long> {

    @Query("""
        SELECT m
        FROM OjkRatioAccountMapping m
        WHERE m.isActive = true
          AND m.effectiveFrom <= :periode
          AND (
              m.effectiveTo IS NULL
              OR m.effectiveTo >= :periode
          )
        ORDER BY m.componentCode
    """)
    List<OjkRatioAccountMapping> findActiveMappings(
            @Param("periode") LocalDate periode
    );

    List<OjkRatioAccountMapping> findByComponentCodeAndIsActiveTrue(
            String componentCode
    );

    boolean existsByComponentCodeAndGlAccountIdAndEffectiveFrom(
            String componentCode,
            Long glAccountId,
            LocalDate effectiveFrom
    );

    @Query(value = """
        SELECT
            m.component_code AS componentCode,
            SUM(
                (
                    CASE
                        WHEN jd.dk = 'D' THEN jd.amount
                        WHEN jd.dk = 'K' THEN -jd.amount
                        ELSE 0
                    END
                ) * m.balance_sign
            ) AS balanceAmount
        FROM ojk_ratio_account_mapping m
        JOIN journal_detail jd
            ON jd.gl_account_id = m.gl_account_id
        JOIN journal_header jh
            ON jh.id = jd.journal_header_id
        WHERE m.is_active = TRUE
        AND m.effective_from <= :periode
        AND (m.effective_to IS NULL OR m.effective_to >= :periode)
        AND jh.status = 'POSTED'
        AND jh.transaction_date < DATE_ADD(:periode, INTERVAL 1 DAY)
        GROUP BY m.component_code
        """, nativeQuery = true)
    List<OjkRatioBalanceProjection> findGlBalancesByPeriode(
            @Param("periode") LocalDate periode
    );

    @Query("""
        SELECT m
        FROM OjkRatioAccountMapping m
        WHERE m.isActive = true
          AND m.effectiveFrom <= :periode
          AND (m.effectiveTo IS NULL OR m.effectiveTo >= :periode)
        """)
    List<OjkRatioAccountMapping> findActiveMappings2(
            @Param("periode") LocalDate periode
    );
}