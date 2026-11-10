package com.project.gl.repo;

import com.project.gl.model.entity.Teller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TellerRepository extends JpaRepository<Teller, Long> {

    /**
     * Native query dengan filter opsional.
     * Pakai trik :param = '' agar aman di PostgreSQL & MySQL.
     */
    @Query(value = """
            SELECT
                t.id          AS id,
                t.nama        AS nama,
                t.user_gadai  AS user_gadai,
                t.created_at  AS created_at,
                t.updated_at  AS updated_at
            FROM teller t
            WHERE ( :nama = '' OR LOWER(t.nama) LIKE LOWER(CONCAT('%', :nama, '%')) )
              AND ( :userGadai = '' OR LOWER(t.user_gadai) LIKE LOWER(CONCAT('%', :userGadai, '%')) )
            ORDER BY t.id DESC
            """, nativeQuery = true)
    List<Object[]> findAllWithFilter(@Param("nama") String nama,
                                     @Param("userGadai") String userGadai);
    
    @Query("SELECT t FROM Teller t")
    List<Teller> getAll();
}