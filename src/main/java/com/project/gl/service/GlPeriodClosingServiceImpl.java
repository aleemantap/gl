package com.project.gl.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.GlPeriodClosing;
import com.project.gl.model.enums.GlPeriodClosingStatus;
import com.project.gl.model.projection.JournalPeriodSummaryProjection;
import com.project.gl.model.response.GlCkpnPreviewResponse;
import com.project.gl.model.response.GlPeriodClosingCheckResponse;
import com.project.gl.repo.GlPeriodClosingRepository;
import com.project.gl.repo.JournalHeaderRepository;
import com.project.gl.service.CabangService;
import com.project.gl.service.GlCkpnService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GlPeriodClosingServiceImpl implements GlPeriodClosingService {

    private final JournalHeaderRepository journalHeaderRepository;
    private final GlCkpnService glCkpnService;
    private final CabangService cabangService;
    private final GlPeriodClosingRepository glPeriodClosingRepository;

    @Override
    public GlPeriodClosingCheckResponse check(
            Integer tahun,
            Integer bulan,
            Integer cabangId
    ) {

        List<String> errors = new ArrayList<>();

        // 1. Validasi parameter
        if (tahun == null || bulan == null || cabangId == null) {
            throw new IllegalArgumentException(
                    "Tahun, bulan, dan cabang wajib diisi"
            );
        }

        if (bulan < 1 || bulan > 12) {
            throw new IllegalArgumentException("Bulan tidak valid");
        }

        YearMonth periode = YearMonth.of(tahun, bulan);

        // 2. Ambil data cabang
        Cabang cabang = cabangService.getById(cabangId);

        // 3. Tentukan batas periode
        LocalDateTime tanggalMulai = periode
                .atDay(1)
                .atStartOfDay();

        LocalDateTime tanggalSelesai = periode
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay();

        // 4. Hitung jurnal POSTED
        JournalPeriodSummaryProjection summary =
                journalHeaderRepository.findPeriodSummary(
                        cabangId.longValue(),
                        tanggalMulai,
                        tanggalSelesai
                );

        BigDecimal totalDebit = summary.getTotalDebit() != null
                ? summary.getTotalDebit()
                : BigDecimal.ZERO;

        BigDecimal totalKredit = summary.getTotalKredit() != null
                ? summary.getTotalKredit()
                : BigDecimal.ZERO;

        Long totalJournal = summary.getTotalJournal() != null
                ? summary.getTotalJournal()
                : 0L;

        boolean neracaSeimbang =
                totalDebit.compareTo(totalKredit) == 0;

        if (!neracaSeimbang) {
            errors.add(
                    "Neraca saldo belum seimbang. Total debit dan kredit berbeda."
            );
        }

        // 5. Pemeriksaan CKPN
        GlCkpnPreviewResponse ckpn =
                glCkpnService.preview(
                        periode,
                        cabangId,
                        cabang.getKodeCabang()
                );

        String idempotencyKey = String.format(
                "CKPN-%s-%d",
                periode,
                cabangId
        );

        boolean ckpnCreated = false;

        if (!ckpn.isJurnalDiperlukan()
                && "SEIMBANG".equalsIgnoreCase(ckpn.getStatus())) {

            // Tidak ada selisih CKPN, sehingga jurnal tidak diperlukan.
            ckpnCreated = true;

        } else if (ckpn.isJurnalDiperlukan()) {

            // Ada selisih, pastikan jurnal CKPN sudah POSTED.
            ckpnCreated =
                    journalHeaderRepository.countPostedByIdempotencyKey(
                            idempotencyKey
                    ) > 0;
        }

        if (!ckpnCreated) {
            errors.add(
                    "CKPN belum selesai. Silakan lakukan posting jurnal CKPN terlebih dahulu."
            );
        }

        // 6. Pemeriksaan antrean Gadai
        // Belum ada sumber data antrean yang dapat diakses GL.
        // Integer pendingGadaiCount = null;

        // errors.add(
        //         "Konfirmasi antrean Gadai belum tersedia. "
        //         + "Pemeriksaan ini harus dilengkapi sebelum pengajuan tutup buku."
        // );

        GlPeriodClosing closing = glPeriodClosingRepository.findByTahunAndBulanAndCabangId(
                        tahun,
                        bulan,
                        cabangId
                )
                .orElse(null);

        
       GlPeriodClosingStatus status = closing != null
            ? closing.getStatus()
            : GlPeriodClosingStatus.TERBUKA;

        String submittedBy = closing != null
            ? closing.getSubmittedBy()
            : null;

        LocalDateTime submittedAt = closing != null
            ? closing.getSubmittedAt()
            : null;

        String approvedBy = closing != null
            ? closing.getApprovedBy()
            : null;

        LocalDateTime approvedAt = closing != null
            ? closing.getApprovedAt()
            : null;

        String rejectionReason = closing != null
            ? closing.getRejectionReason()
            : null;

        String pendingGadaiNote = closing != null
            ? closing.getPendingGadaiNote()
            : null; 

        Integer pendingGadaiCount = null;

        if (closing != null
                && closing.getPendingGadaiCount() != null
                && closing.getPendingGadaiCheckedAt() != null) {

            pendingGadaiCount = closing.getPendingGadaiCount();

            if (pendingGadaiCount > 0) {
                errors.add(
                        "Masih terdapat "
                        + pendingGadaiCount
                        + " transaksi Gadai yang belum terbukukan."
                );
            }

        } else {
            errors.add(
                    "Konfirmasi antrean Gadai belum dilakukan oleh Finance."
            );
        }

        // 7. Hasil akhir pemeriksaan
        boolean canSubmit =
                neracaSeimbang
                && ckpnCreated
                && pendingGadaiCount != null
                && pendingGadaiCount == 0;

        return GlPeriodClosingCheckResponse.builder()
                .tahun(tahun)
                .bulan(bulan)
                .cabangId(cabangId)
                .kodeCabang(cabang.getKodeCabang())
                .namaCabang(cabang.getNamaCabang())
                .totalJournal(totalJournal)
                .totalDebit(totalDebit)
                .totalKredit(totalKredit)
                .neracaSeimbang(neracaSeimbang)
                .ckpnCreated(ckpnCreated)
                .statusCkpn(ckpn.getStatus())
                .totalCadanganGadai(ckpn.getTotalCadanganGadai())
                .saldoCadanganGl(ckpn.getSaldoCadanganGl())
                .pendingGadaiCount(pendingGadaiCount)

                // Informasi tutup buku
                .status(status)
                .submittedBy(submittedBy)
                .submittedAt(submittedAt)
                .approvedBy(approvedBy)
                .approvedAt(approvedAt)
                .rejectionReason(rejectionReason)
                .pendingGadaiNote(pendingGadaiNote)

                .canSubmit(canSubmit)
                .errors(errors)
                .build();
    }

    @Override
    @Transactional
    public GlPeriodClosing confirmPendingGadai(
            Integer tahun,
            Integer bulan,
            Integer cabangId,
            Integer pendingCount,
            String username,
            String note
    ) {

        // 1. Validasi input
        if (tahun == null || bulan == null || cabangId == null) {
            throw new IllegalArgumentException(
                    "Tahun, bulan, dan cabang wajib diisi"
            );
        }

        if (bulan < 1 || bulan > 12) {
            throw new IllegalArgumentException("Bulan tidak valid");
        }

        if (pendingCount == null || pendingCount < 0) {
            throw new IllegalArgumentException(
                    "Jumlah antrean Gadai tidak boleh kosong atau negatif"
            );
        }

        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException(
                    "Username yang melakukan konfirmasi wajib diisi"
            );
        }

        // 2. Pastikan cabang tersedia
        cabangService.getById(cabangId);

        // 3. Cari data tutup buku
        GlPeriodClosing closing = glPeriodClosingRepository
                .findByTahunAndBulanAndCabangId(
                        tahun,
                        bulan,
                        cabangId
                )
                .orElseGet(() -> {
                    GlPeriodClosing baru = new GlPeriodClosing();

                    baru.setTahun(tahun);
                    baru.setBulan(bulan);
                    baru.setCabangId(cabangId);
                    baru.setStatus(GlPeriodClosingStatus.TERBUKA);

                    return baru;
                });

        // 4. Periksa status periode
        if (closing.getStatus() == GlPeriodClosingStatus.DITUTUP) {
            throw new IllegalStateException(
                    "Periode sudah ditutup. Konfirmasi antrean tidak dapat diubah."
            );
        }

        if (closing.getStatus()
                == GlPeriodClosingStatus.MENUNGGU_PERSETUJUAN) {
            throw new IllegalStateException(
                    "Pengajuan tutup buku sedang menunggu persetujuan."
            );
        }

        // 5. Simpan hasil konfirmasi
        closing.setPendingGadaiCount(pendingCount);
        closing.setPendingGadaiCheckedBy(username);
        closing.setPendingGadaiCheckedAt(LocalDateTime.now());
        closing.setPendingGadaiNote(note);

        return glPeriodClosingRepository.save(closing);
    }

    @Override
    @Transactional
    public GlPeriodClosing submit(
            Integer tahun,
            Integer bulan,
            Integer cabangId,
            String username
    ) {

        // 1. Validasi username
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException(
                    "User yang mengajukan tutup buku wajib diisi"
            );
        }

        // 2. Jalankan ulang seluruh pemeriksaan
        GlPeriodClosingCheckResponse hasil = check(
                tahun,
                bulan,
                cabangId
        );

        // 3. Pastikan seluruh pemeriksaan lolos
        if (!hasil.isCanSubmit()) {
            throw new IllegalStateException(
                    "Tutup buku tidak dapat diajukan: "
                    + String.join("; ", hasil.getErrors())
            );
        }

        // 4. Ambil atau buat data closing
        GlPeriodClosing closing =
                glPeriodClosingRepository
                        .findByTahunAndBulanAndCabangId(
                                tahun,
                                bulan,
                                cabangId
                        )
                        .orElseGet(() -> {
                            GlPeriodClosing baru = new GlPeriodClosing();
                            baru.setTahun(tahun);
                            baru.setBulan(bulan);
                            baru.setCabangId(cabangId);
                            return baru;
                        });

        // 5. Validasi status
        if (closing.getStatus() == GlPeriodClosingStatus.DITUTUP) {
            throw new IllegalStateException(
                    "Periode sudah ditutup."
            );
        }

        if (closing.getStatus()
                == GlPeriodClosingStatus.MENUNGGU_PERSETUJUAN) {
            throw new IllegalStateException(
                    "Pengajuan tutup buku sudah menunggu persetujuan."
            );
        }

        // 6. Simpan snapshot hasil pemeriksaan
        closing.setTotalDebit(hasil.getTotalDebit());
        closing.setTotalKredit(hasil.getTotalKredit());

        closing.setNeracaSeimbang(
                hasil.isNeracaSeimbang()
        );

        closing.setCkpnCreated(
                hasil.isCkpnCreated()
        );

        closing.setPendingGadaiCount(
                hasil.getPendingGadaiCount()
        );

        // 7. Simpan informasi pengajuan
        closing.setStatus(
                GlPeriodClosingStatus.MENUNGGU_PERSETUJUAN
        );

        closing.setSubmittedBy(username);
        closing.setSubmittedAt(LocalDateTime.now());

        // 8. Bersihkan informasi penolakan sebelumnya
        closing.setRejectedBy(null);
        closing.setRejectedAt(null);
        closing.setRejectionReason(null);

        return glPeriodClosingRepository.save(closing);
    }

    
		
	@Override
	@Transactional
	public GlPeriodClosing approve(
			Integer tahun,
			Integer bulan,
			Integer cabangId,
			String username
	) {
		// 1. Validasi identitas penyetuju
		if (!StringUtils.hasText(username)) {
			throw new IllegalArgumentException(
					"User yang menyetujui wajib diisi."
			);
		}

		String approvedBy = username.trim();

		// 2. Ambil pengajuan tutup buku
		GlPeriodClosing closing = glPeriodClosingRepository
				.findByTahunAndBulanAndCabangId(
						tahun, bulan, cabangId
				)
				.orElseThrow(() -> new IllegalStateException(
						"Data pengajuan tutup buku tidak ditemukan."
				));

		// 3. Pastikan status masih menunggu persetujuan
		if (closing.getStatus()
				!= GlPeriodClosingStatus.MENUNGGU_PERSETUJUAN) {
			throw new IllegalStateException(
					"Pengajuan tutup buku tidak dalam status menunggu persetujuan."
			);
		}

		// 4. Validasi Maker-Checker
		String submittedBy = closing.getSubmittedBy();

		if (!StringUtils.hasText(submittedBy)) {
			throw new IllegalStateException(
					"Identitas pengaju tutup buku tidak tersedia."
			);
		}

		if (approvedBy.equalsIgnoreCase(submittedBy.trim())) {
			throw new IllegalStateException(
					"Maker tidak boleh menyetujui pengajuan tutup buku sendiri."
			);
		}

		// 5. Periksa ulang seluruh persyaratan sebelum tutup buku
		GlPeriodClosingCheckResponse hasil = check(
				tahun, bulan, cabangId
		);

		if (!hasil.isCanSubmit()) {
			throw new IllegalStateException(
					"Tutup buku tidak dapat disetujui: "
					+ String.join("; ", hasil.getErrors())
			);
		}

		// 6. Simpan hasil pemeriksaan terakhir
		closing.setTotalDebit(hasil.getTotalDebit());
		closing.setTotalKredit(hasil.getTotalKredit());
		closing.setNeracaSeimbang(hasil.isNeracaSeimbang());
		closing.setCkpnCreated(hasil.isCkpnCreated());
		closing.setPendingGadaiCount(hasil.getPendingGadaiCount());

		// 7. Tutup periode
		closing.setStatus(GlPeriodClosingStatus.DITUTUP);
		closing.setApprovedBy(approvedBy);
		closing.setApprovedAt(LocalDateTime.now());

		return glPeriodClosingRepository.save(closing);
	}



    @Override
    @Transactional
    public GlPeriodClosing reject(
            Integer tahun,
            Integer bulan,
            Integer cabangId,
            String username,
            String rejectionReason
    ) {

        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException(
                    "User yang menolak wajib diisi."
            );
        }

        if (!StringUtils.hasText(rejectionReason)) {
            throw new IllegalArgumentException(
                    "Alasan penolakan wajib diisi."
            );
        }

        GlPeriodClosing closing = glPeriodClosingRepository
                .findByTahunAndBulanAndCabangId(
                        tahun, bulan, cabangId
                )
                .orElseThrow(() -> new IllegalStateException(
                        "Data pengajuan tutup buku tidak ditemukan."
                ));

        if (closing.getStatus() != GlPeriodClosingStatus.MENUNGGU_PERSETUJUAN) {
            throw new IllegalStateException(
                    "Pengajuan tutup buku tidak dalam status menunggu persetujuan."
            );
        }

        String rejectedBy = username.trim();

		closing.setStatus(GlPeriodClosingStatus.DITOLAK);
		closing.setRejectedBy(rejectedBy);
		closing.setRejectedAt(LocalDateTime.now());
		closing.setRejectionReason(rejectionReason.trim());

        return glPeriodClosingRepository.save(closing);
    }

    @Override 
    public List<GlPeriodClosing> findAllByCabang(Integer cabangId) {
        cabangService.getById(cabangId);
        return glPeriodClosingRepository.findByCabangIdOrderByTahunDescBulanDesc(cabangId);
    }
}