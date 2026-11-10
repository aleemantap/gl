package com.project.gl.repo;

import java.util.List;
import com.project.gl.model.entity.OjkRatioDetail;
import com.project.gl.model.enums.OjkRatioDetailStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OjkRatioDetailRepository extends JpaRepository<OjkRatioDetail, Long> {

    List<OjkRatioDetail> findByReportIdOrderByRatioCodeAsc(Long reportId);

    List<OjkRatioDetail> findByReportIdAndRatioCode(
            Long reportId,
            String ratioCode
    );

    List<OjkRatioDetail> findByReportIdAndStatus(
            Long reportId,
            OjkRatioDetailStatus status
    );

    long countByReportId(Long reportId);

    long countByReportIdAndStatus(
            Long reportId,
            OjkRatioDetailStatus status
    );

    // void deleteByReportId(Long reportId);
    @Modifying
    @Query("DELETE FROM OjkRatioDetail d WHERE d.report.id = :reportId")
    void deleteByReportId(@Param("reportId") Long reportId);
}