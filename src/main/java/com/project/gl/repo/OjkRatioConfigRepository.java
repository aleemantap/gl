package com.project.gl.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.project.gl.model.entity.OjkRatioConfig;
import com.project.gl.model.enums.OjkRatioRegion;


@Repository
public interface OjkRatioConfigRepository  extends JpaRepository<OjkRatioConfig, Long> {

    List<OjkRatioConfig> findByIsActiveTrueOrderByRatioCodeAsc();

    List<OjkRatioConfig> findByIsActiveTrueAndRegionScopeOrderByRatioCodeAsc(
            OjkRatioRegion regionScope
    );

    Optional<OjkRatioConfig> findByRatioCodeAndRegionScopeAndIsActiveTrue(
            String ratioCode,
            OjkRatioRegion regionScope
    );

    @Query("""
        SELECT c
        FROM OjkRatioConfig c
        WHERE c.ratioCode = :ratioCode
          AND c.isActive = true
          AND c.effectiveFrom <= :periode
          AND (c.effectiveTo IS NULL OR c.effectiveTo >= :periode)
          AND (c.regionScope = :regionScope OR c.regionScope = 'ALL')
        ORDER BY c.regionScope DESC
    """)
    List<OjkRatioConfig> findApplicableConfig(
            @Param("ratioCode") String ratioCode,
            @Param("regionScope") OjkRatioRegion regionScope,
            @Param("periode") LocalDate periode
    );

   
}