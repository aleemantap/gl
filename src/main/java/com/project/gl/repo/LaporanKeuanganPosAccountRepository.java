package com.project.gl.repo;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.gl.model.entity.LaporanKeuanganPosAccount;

import java.util.List;

public interface LaporanKeuanganPosAccountRepository extends JpaRepository<LaporanKeuanganPosAccount, Long> {

    List<LaporanKeuanganPosAccount> findByLaporanPosId(Long laporanPosId);

    List<LaporanKeuanganPosAccount> findByGlAccountId(Integer glAccountId);

    boolean existsByLaporanPosIdAndGlAccountId(
        Long laporanPosId,
        Integer glAccountId
    );

    void deleteByLaporanPosId(Long laporanPosId);
}