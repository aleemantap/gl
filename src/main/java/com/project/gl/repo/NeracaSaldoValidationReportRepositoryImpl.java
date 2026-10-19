package com.project.gl.repo;
import java.util.List;
import org.springframework.stereotype.Repository;
import com.project.gl.model.response.NeracaSaldoValidationRow;
import com.project.gl.model.enums.NeracaSaldoStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class NeracaSaldoValidationReportRepositoryImpl
        implements NeracaSaldoValidationReportRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<NeracaSaldoValidationRow> findRecentValidations(int limit) {

        String sql = """
            SELECT
                nsv.id,
                nsv.submission_id,
                c.kode_cabang,
                c.nama_cabang,
                nsv.status,
                nsv.validated_by,
                nsv.validated_at,
                nsv.note
            FROM neraca_saldo_validation nsv

            JOIN neraca_saldo_submission nss
                ON nss.id = nsv.submission_id

            JOIN cabang c
                ON c.id = nss.unit_id

            ORDER BY nsv.validated_at DESC
            LIMIT :limit
            """;

        return entityManager
                .createNativeQuery(sql)
                .setParameter("limit", limit)
                .getResultList()
                .stream()
                .map(row -> {
                    Object[] r = (Object[]) row;

                    return new NeracaSaldoValidationRow(
                            ((Number) r[0]).longValue(),
                            ((Number) r[1]).longValue(),
                            (String) r[2],
                            (String) r[3],
                            NeracaSaldoStatus.valueOf((String) r[4]),
                            (String) r[5],
                            r[6] != null
                                    ? ((java.sql.Timestamp) r[6])
                                        .toLocalDateTime()
                                    : null,
                            (String) r[7]
                    );
                })
                .toList();
    }
}