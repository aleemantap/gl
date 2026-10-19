package com.project.gl.repo;

import com.project.gl.model.entity.Keputusan;
import com.project.gl.model.enums.KeputusanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeputusanRepository extends JpaRepository<Keputusan, Long> {

    List<Keputusan> findAllByOrderByTanggalDesc();

    List<Keputusan> findByStatusOrderByTanggalDesc(
            KeputusanStatus status
    );

    List<Keputusan> findByKategoriOrderByTanggalDesc(
            String kategori
    );
}