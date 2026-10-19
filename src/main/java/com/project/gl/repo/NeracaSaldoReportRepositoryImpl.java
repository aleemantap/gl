package com.project.gl.repo;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.project.gl.model.response.NeracaSaldoRow;
import com.project.gl.repo.NeracaSaldoReportRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class NeracaSaldoReportRepositoryImpl implements NeracaSaldoReportRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<NeracaSaldoRow> getNeracaSaldo(
            Long unitId,
            LocalDate startDate,
            LocalDate endDate) {

        String sql = """
            SELECT
                ng.id AS gl_account_id,
                ng.nomor_gl,
                ng.nama_gl_account,

                COALESCE(
                    SUM(
                        CASE
                            WHEN jd.dk = 'D'
                            THEN jd.amount
                            ELSE 0
                        END
                    ),
                    0
                ) AS debit,

                COALESCE(
                    SUM(
                        CASE
                            WHEN jd.dk = 'K'
                            THEN jd.amount
                            ELSE 0
                        END
                    ),
                    0
                ) AS credit

            FROM journal_detail jd

            JOIN journal_header jh
                ON jh.id = jd.journal_header_id

            JOIN nomor_gl_account ng
                ON ng.id = jd.gl_account_id

            WHERE jh.cabang_id = :unitId

              AND jh.transaction_date >= :startDate

              AND jh.transaction_date < :endDate

            GROUP BY
                ng.id,
                ng.nomor_gl,
                ng.nama_gl_account

            ORDER BY
                ng.nomor_gl
            """;

        return entityManager
                .createNativeQuery(sql)
                .setParameter("unitId", unitId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList()
                .stream()
                .map(row -> {

                    Object[] r = (Object[]) row;

                    return new NeracaSaldoRow(
                            ((Number) r[0]).intValue(),
                            (String) r[1],
                            (String) r[2],
                            (BigDecimal) r[3],
                            (BigDecimal) r[4]
                    );
                })
                .toList();
    }
}