package com.project.gl.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.gl.model.entity.JournalDetail;

@Repository
public interface SubLedgerGlRepository  extends JpaRepository<JournalDetail, Long> {

    //List<Object[]> getGlBalances(LocalDate date);

    //  @Query(value = """
    //     SELECT
    //         ga.nomor_gl,
    //         ga.nama_gl_account,
    //         COALESCE(SUM(jd.debit - jd.credit), 0)
    //     FROM journal_detail jd
    //     JOIN journal_header jh
    //         ON jh.id = jd.journal_header_id
    //     JOIN nomor_gl_account ga
    //         ON ga.id = jd.gl_account_id
    //     WHERE ga.account_code IN ('110101', '110102', '110103')
    //       AND jh.journal_date <= :date
    //     GROUP BY
    //         ga.account_code,
    //         ga.account_name
    //     """, nativeQuery = true)
    // List<Object[]> getGlBalances(
    //     @Param("date") LocalDate date
    // );
}
