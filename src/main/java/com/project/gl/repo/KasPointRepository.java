package com.project.gl.repo;
import com.project.gl.model.entity.KasPoint;
import com.project.gl.model.projection.KasPointTellerProjection;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KasPointRepository extends JpaRepository<KasPoint, Long> {

    @Query(value = """
        SELECT
            kp.id          AS id,
            kp.kas_point_code        AS kasPointCode,
            kp.nama        AS nama,
            kp.jenis       AS jenis,
            kp.cabang_id   AS cabangId,
            c.kode_cabang  AS kodeCabang,
            c.nama_cabang  AS namaCabang,
            kp.created_at  AS createdAt,
            kp.updated_at  AS updatedAt
        FROM kas_point kp
        LEFT JOIN cabang c ON c.id = kp.cabang_id
        WHERE ( COALESCE(:nama, '') = '' OR LOWER(kp.nama) LIKE LOWER(CONCAT('%', :nama, '%')) )
          AND ( COALESCE(:jenis, '') = '' OR LOWER(kp.jenis) = LOWER(:jenis) )
          AND ( :cabangId IS NULL OR kp.cabang_id = :cabangId )
          AND (  COALESCE(:code, '') = '' OR LOWER(kp.kas_point_code) LIKE LOWER(CONCAT('%', :code, '%')) )
        ORDER BY kp.id DESC
        """, nativeQuery = true)
    List<Object[]> findAllWithCabangRaw(@Param("code") String code,
                                        @Param("nama") String nama,
                                        @Param("jenis") String jenis,
                                        @Param("cabangId") Integer cabangId);


    @Query("""
         SELECT 
            kp.id AS id,
            kp.kasPointCode AS kasPointCode,
            kp.nama AS namaKas,
            c.namaCabang AS namaCabang
        FROM KasPoint kp
        LEFT JOIN Cabang c ON c.id = kp.cabangId
        """)
    List<KasPointTellerProjection> getAllKasPointWithTeller();
}