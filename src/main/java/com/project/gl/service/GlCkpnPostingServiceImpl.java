
package com.project.gl.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.gl.model.entity.JournalHeader;
import com.project.gl.model.entity.JournalDetail;
import com.project.gl.model.entity.JournalLineTemplate;
import com.project.gl.model.projection.CkpnJournalMappingProjection;
import com.project.gl.model.response.GlCkpnPreviewResponse;
import com.project.gl.repo.JournalHeaderRepository;
import com.project.gl.repo.JournalDetailRepository;
import com.project.gl.repo.JournalKodeTransaksiRepository;
import com.project.gl.repo.JournalLineTemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GlCkpnPostingServiceImpl implements GlCkpnPostingService {

    private final GlCkpnService glCkpnService;
    private final JournalHeaderRepository journalHeaderRepository;
    private final JournalDetailRepository journalDetailRepository;
    private final JournalKodeTransaksiRepository journalKodeTransaksiRepository;
    private final JournalLineTemplateRepository journalLineTemplateRepository;

    @Transactional
    @Override
    public String posting(
            YearMonth periode,
            Integer cabangId,
            String kodeCabang) {

        // 1. Validasi parameter
        if (periode == null || cabangId == null
                || kodeCabang == null || kodeCabang.isBlank()) {
            throw new IllegalArgumentException(
                    "Periode dan cabang wajib diisi"
            );
        }

        // 2. Hitung ulang preview dari sumber data
        GlCkpnPreviewResponse hasil = glCkpnService.preview(
                periode, cabangId, kodeCabang
        );

        if (!hasil.isJurnalDiperlukan()) {
            throw new IllegalStateException(
                    "Tidak ada selisih CKPN yang perlu diposting"
            );
        }

        BigDecimal nominal = hasil.getNominalJurnal();

        if (nominal == null
                || nominal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException(
                    "Nominal jurnal CKPN tidak valid"
            );
        }

        // 3. Idempotency key
        String idempotencyKey = String.format(
                "CKPN-%s-%d",
                periode,
                hasil.getCabangId()
        );

        if (journalHeaderRepository
                .findByIdempotencyKey(idempotencyKey).isPresent()) {
            throw new IllegalStateException(
                    "Jurnal CKPN periode " + periode
                    + " untuk cabang " + hasil.getNamaCabang()
                    + " sudah pernah diposting"
            );
        }

        // 4. Cari mapping TX-CKPN untuk cabang
        CkpnJournalMappingProjection mapping =
                journalKodeTransaksiRepository
                        .findCkpnMapping(hasil.getCabangId())
                        .orElseThrow(() -> new IllegalStateException(
                                "Mapping TX-CKPN belum tersedia untuk cabang "
                                + hasil.getKodeCabang()
                        ));

        if (mapping.getJournalId() == null
                || mapping.getKodeTransaksiId() == null) {
            throw new IllegalStateException(
                    "Mapping jurnal TX-CKPN tidak lengkap"
            );
        }

        // 5. Ambil template jurnal aktif
        List<JournalLineTemplate> templates =
                journalLineTemplateRepository
                        .findByJournalIdAndIsActiveTrueOrderByLineOrderAsc(
                                mapping.getJournalId()
                        );

        // CKPN wajib mempunyai dua baris template
        if (templates == null || templates.size() != 2) {
            throw new IllegalStateException(
                    "Template JR-CKPN harus memiliki tepat dua baris aktif"
            );
        }

        JournalLineTemplate templateDebit = null;
        JournalLineTemplate templateKredit = null;

        for (JournalLineTemplate template : templates) {

            if (template.getGlAccountId() == null) {
                throw new IllegalStateException(
                        "Akun GL pada template JR-CKPN belum diisi"
                );
            }

            if ("D".equals(template.getDk())) {
                if (templateDebit != null) {
                    throw new IllegalStateException(
                            "Template JR-CKPN memiliki lebih dari satu "
                            + "baris debit"
                    );
                }
                templateDebit = template;

            } else if ("K".equals(template.getDk())) {
                if (templateKredit != null) {
                    throw new IllegalStateException(
                            "Template JR-CKPN memiliki lebih dari satu "
                            + "baris kredit"
                    );
                }
                templateKredit = template;

            } else {
                throw new IllegalStateException(
                        "DK template JR-CKPN tidak valid"
                );
            }
        }

        if (templateDebit == null || templateKredit == null) {
            throw new IllegalStateException(
                    "Template JR-CKPN wajib memiliki satu debit "
                    + "dan satu kredit"
            );
        }

        // 6. Tentukan apakah arah jurnal perlu dibalik
        boolean pembalikan =
                "DEBIT_CADANGAN_KREDIT_BEBAN"
                        .equals(hasil.getArahJurnal());

        if (!pembalikan
                && !"DEBIT_BEBAN_KREDIT_CADANGAN"
                        .equals(hasil.getArahJurnal())) {
            throw new IllegalStateException(
                    "Arah jurnal CKPN tidak dikenal"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        // 7. Buat header jurnal
        LocalDateTime tanggalPosting =
                hasil.getTanggalPosisi().atTime(23, 59, 59);

        JournalHeader header = new JournalHeader();
        header.setJournalId(mapping.getJournalId());
        header.setTransactionCodeId(mapping.getKodeTransaksiId());
        header.setCabangId(hasil.getCabangId().longValue());
        header.setTransactionNo(hasil.getReferensiLaporan());
        header.setTransactionDate(tanggalPosting);
        header.setStatus("POSTED");
        header.setIdempotencyKey(idempotencyKey);

        header = journalHeaderRepository.save(header);

        // 8. Generate nomor jurnal
        String journalNo = String.format(
                "%s/JV/%s/%06d",
                hasil.getKodeCabang(),
                now.format(DateTimeFormatter.ofPattern("yyyy/MM")),
                header.getId()
        );

        header.setJournalNo(journalNo);
        journalHeaderRepository.save(header);

        // 9. Generate detail berdasarkan template
        for (JournalLineTemplate template : templates) {

            String dk = template.getDk();

            // Jika selisih negatif, balik arah D/K
            if (pembalikan) {
                dk = "D".equals(dk) ? "K" : "D";
            }

            JournalDetail detail = new JournalDetail();

            detail.setJournalHeaderId(header.getId());
            detail.setGlAccountId(template.getGlAccountId());
            detail.setDk(dk);
            detail.setAmount(nominal);
            detail.setCreatedAt(now);
            detail.setDescription(
                    buildDescription(template.getDk(), dk, periode)
            );

            journalDetailRepository.save(detail);
        }

        return journalNo;
    }

    private String buildDescription(
            String templateDk,
            String actualDk,
            YearMonth periode) {

        if ("D".equals(templateDk)) {
            return "D".equals(actualDk)
                    ? "Beban CKPN periode " + periode
                    : "Pembalikan Beban CKPN periode " + periode;
        }

        return "K".equals(actualDk)
                ? "Penambahan Cadangan CKPN periode " + periode
                : "Pengurangan Cadangan CKPN periode " + periode;
    }
}
