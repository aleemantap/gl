package com.project.gl.repo;

import com.project.gl.model.entity.GlPeriodClosing;
import com.project.gl.model.entity.GlPeriodClosingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GlPeriodClosingRepository  extends JpaRepository<GlPeriodClosing, Long> {

    Optional<GlPeriodClosing> findByTahunAndBulanAndCabangId(
            Integer tahun,
            Integer bulan,
            Integer cabangId
    );

    List<GlPeriodClosing> findByTahunAndBulanOrderByCabangIdAsc(
            Integer tahun,
            Integer bulan
    );

    boolean existsByTahunAndBulanAndCabangIdAndStatus(
            Integer tahun,
            Integer bulan,
            Integer cabangId,
            GlPeriodClosingStatus status
    );


    boolean existsByTahunAndBulanAndCabangId(
            Integer tahun,
            Integer bulan,
            Integer cabangId
    );

    List<GlPeriodClosing> findByCabangIdOrderByTahunDescBulanDesc(Integer cabangId);
    
}