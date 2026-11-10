package com.project.gl.service;

import java.util.List;

import com.project.gl.model.entity.GlPeriodClosing;
import com.project.gl.model.response.GlPeriodClosingCheckResponse;

public interface GlPeriodClosingService {

    /**
     * Melakukan pemeriksaan kelayakan tutup buku periode.
     *
     * @param tahun    tahun periode
     * @param bulan    bulan periode (1-12)
     * @param cabangId ID cabang
     * @return hasil pemeriksaan tutup buku
     */
    GlPeriodClosingCheckResponse check(
            Integer tahun,
            Integer bulan,
            Integer cabangId
    );

    /**
     * Menyimpan konfirmasi jumlah antrean transaksi Gadai yang belum terbukukan
     * untuk periode tertentu.
     *
     * @param tahun        tahun periode
     * @param bulan        bulan periode (1-12)
     * @param cabangId     ID cabang
     * @param pendingCount jumlah antrean Gadai (>= 0)
     * @param username     user yang melakukan konfirmasi
     * @param note         catatan konfirmasi
     * @return data tutup buku yang telah diperbarui
     */
    GlPeriodClosing confirmPendingGadai(
            Integer tahun,
            Integer bulan,
            Integer cabangId,
            Integer pendingCount,
            String username,
            String note
    );

    /**
     * Mengajukan tutup buku periode setelah seluruh pemeriksaan lolos.
     *
     * @param tahun    tahun periode
     * @param bulan    bulan periode (1-12)
     * @param cabangId ID cabang
     * @param username user yang mengajukan
     * @return data tutup buku dengan status MENUNGGU_PERSETUJUAN
     */
    GlPeriodClosing submit(
            Integer tahun,
            Integer bulan,
            Integer cabangId,
            String username
    );

    /**
     * Menyetujui pengajuan tutup buku periode (maker-checker).
     *
     * @param tahun    tahun periode
     * @param bulan    bulan periode (1-12)
     * @param cabangId ID cabang
     * @param username user yang menyetujui
     * @return data tutup buku dengan status DITUTUP
     */
    GlPeriodClosing approve(
            Integer tahun,
            Integer bulan,
            Integer cabangId,
            String username
    );

    /**
     * Menolak pengajuan tutup buku periode.
     *
     * @param tahun           tahun periode
     * @param bulan           bulan periode (1-12)
     * @param cabangId        ID cabang
     * @param username        user yang menolak
     * @param rejectionReason alasan penolakan
     * @return data tutup buku dengan status DITOLAK
     */
    GlPeriodClosing reject(
            Integer tahun,
            Integer bulan,
            Integer cabangId,
            String username,
            String rejectionReason
    );

    List<GlPeriodClosing> findAllByCabang(Integer cabangId);
}