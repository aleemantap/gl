package com.project.gl.service;

import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.response.NeracaSaldoDTO;
import com.project.gl.helper.Util;
import com.project.gl.model.entity.KodeTransaksi;
import com.project.gl.model.projection.NeracaSaldoProjection;
import com.project.gl.repo.NeracaSaldoRepository;
import com.project.gl.service.KodeTransaksiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NeracaSaldoService {

    private final NeracaSaldoRepository neracaSaldoRepository;
    private final Util util;


     public PageResponse<NeracaSaldoDTO> getNeracaSaldo(
            int page,
            int size,
        //     String listIds,
            LocalDate tanggalDari,
            LocalDate tanggalSampai,
            String branchCode
    ) {

     
        List<Long> listIds = null;
        LocalDateTime dari = tanggalDari.atStartOfDay();

        LocalDateTime sampai = tanggalSampai
                        .plusDays(1)
                        .atStartOfDay();

        boolean idsEmpty = (listIds == null || listIds.isEmpty());
        List<Long> safeIds = idsEmpty ? List.of(-1L) : listIds; 

        List<NeracaSaldoProjection> projections =  neracaSaldoRepository.findNeracaSaldo(
                        safeIds, 
                        idsEmpty, 
                        dari,
                        sampai,
                        branchCode
                );
               

        List<NeracaSaldoDTO> allData =   projections.stream()
                        .map(this::toDTO)
                        .toList();
              

        // =========================================
        // PAGINATION
        // =========================================

        int totalElements = allData.size();
        int fromIndex = page * size;
        int toIndex = Math.min(
                fromIndex + size,
                totalElements
        );

        List<NeracaSaldoDTO> content;

        if (fromIndex >= totalElements) {
            content = Collections.emptyList();
        } else {
            content = allData.subList(
                    fromIndex,
                    toIndex
            );
        }

        int totalPages =
                size == 0
                        ? 0
                        : (int) Math.ceil(
                                (double) totalElements / size
                        );

        boolean last =
                totalPages == 0
                        || page >= totalPages - 1;

        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages,
                last
        );
    }


    // =====================================================
    // CONVERT PROJECTION -> DTO
    // =====================================================

    private NeracaSaldoDTO toDTO(
            NeracaSaldoProjection p
    ) {

        BigDecimal debitSebelum =
                nvl(p.getDebitSebelum());

        BigDecimal kreditSebelum =
                nvl(p.getKreditSebelum());

        BigDecimal totalDebit =
                nvl(p.getTotalDebit());

        BigDecimal totalKredit =
                nvl(p.getTotalKredit());


        BigDecimal saldoAwal;
        BigDecimal saldoAkhir;


        // =================================================
        // NORMAL BALANCE DEBIT
        // =================================================

        if ("D".equalsIgnoreCase(
                p.getNormalBalance())) {

            saldoAwal =
                    debitSebelum
                            .subtract(kreditSebelum);

            saldoAkhir =
                    saldoAwal
                            .add(totalDebit)
                            .subtract(totalKredit);


        // =================================================
        // NORMAL BALANCE KREDIT
        // =================================================

        } else if ("K".equalsIgnoreCase(
                p.getNormalBalance())) {

            saldoAwal =
                    kreditSebelum
                            .subtract(debitSebelum);

            saldoAkhir =
                    saldoAwal
                            .add(totalKredit)
                            .subtract(totalDebit);

        } else {

            throw new IllegalStateException(
                    "Normal balance tidak valid untuk GL Account "
                            + p.getAccountCode()
                            + ": "
                            + p.getNormalBalance()
            );
        }


        // =================================================
        // SALDO AKHIR DEBIT / KREDIT
        // =================================================

        BigDecimal saldoAkhirDebit =
                BigDecimal.ZERO;

        BigDecimal saldoAkhirKredit =
                BigDecimal.ZERO;


        if (saldoAkhir.compareTo(
                BigDecimal.ZERO) >= 0) {

            if ("D".equalsIgnoreCase(
                    p.getNormalBalance())) {

                saldoAkhirDebit =
                        saldoAkhir;

            } else {

                saldoAkhirKredit =
                        saldoAkhir;
            }

        } else {

            /*
             * Saldo negatif berarti posisi akun
             * berlawanan dengan normal balance.
             */

            if ("D".equalsIgnoreCase(
                    p.getNormalBalance())) {

                saldoAkhirKredit =
                        saldoAkhir.abs();

            } else {

                saldoAkhirDebit =
                        saldoAkhir.abs();
            }
        }


        return new NeracaSaldoDTO(
                p.getGlAccountId(),
                p.getAccountCode(),
                p.getAccountName(),
                p.getNormalBalance(),
                saldoAwal,
                totalDebit,
                totalKredit,
                saldoAkhirDebit,
                saldoAkhirKredit
        );
    }


    // =====================================================
    // NULL -> ZERO
    // =====================================================

    private BigDecimal nvl(BigDecimal value) {

        return value != null
                ? value
                : BigDecimal.ZERO;
    }



        public List<NeracaSaldoDTO> getAllNeracaSaldo(
                String ids,
                LocalDate tanggalDari,
                LocalDate tanggalSampai,
                String branchCode
        ) {
                LocalDateTime dari = tanggalDari.atStartOfDay();
                LocalDateTime sampai = tanggalSampai.plusDays(1).atStartOfDay();
                
                List<Long> listIds = util.parseIdsJson(ids);    
              
                boolean idsEmpty = (listIds == null || listIds.isEmpty());
                List<Long> safeIds = idsEmpty ? List.of(-1L) : listIds; 
                List<NeracaSaldoProjection> projections = neracaSaldoRepository.findNeracaSaldo(
                        safeIds, 
                        idsEmpty,
                        dari,
                        sampai,
                        branchCode
                );

                return projections.stream().map(this::toDTO).toList();
        }




//     public PageResponse<NeracaSaldoDTO> getNeracaSaldox(
//             int page,
//             int size,
//             LocalDate tanggalDari,
//             LocalDate tanggalSampai,
//             String branchCode
//     ) {

//         LocalDateTime dari =
//                 tanggalDari.atStartOfDay();

//         LocalDateTime sampai =
//                 tanggalSampai
//                         .plusDays(1)
//                         .atStartOfDay();


//         List<NeracaSaldoProjection> projections =
//                 neracaSaldoRepository.findNeracaSaldo(
//                         dari,
//                         sampai,
//                         branchCode
//                 );


//         List<NeracaSaldoDTO> allData =
//                 projections.stream()
//                         .map(this::toDTO)
//                         .toList();


//         /*
//          * =====================================
//          * PAGINATION
//          * =====================================
//          */

//         int totalElements = allData.size();

//         int fromIndex = page * size;

//         int toIndex =
//                 Math.min(
//                         fromIndex + size,
//                         totalElements
//                 );


//         List<NeracaSaldoDTO> content;

//         if (fromIndex >= totalElements) {

//             content = Collections.emptyList();

//         } else {

//             content =
//                     allData.subList(
//                             fromIndex,
//                             toIndex
//                     );
//         }


//         int totalPages =
//                 size == 0
//                         ? 0
//                         : (int) Math.ceil(
//                                 (double) totalElements / size
//                         );


//         boolean last =
//                 totalPages == 0
//                         || page >= totalPages - 1;


//         return new PageResponse<>(
//                 content,
//                 page,
//                 size,
//                 totalElements,
//                 totalPages,
//                 last
//         );
//     }


//    private NeracaSaldoDTO toDTOX(NeracaSaldoProjection p) {

//         BigDecimal debitSebelum =
//                 p.getDebitSebelum() != null
//                         ? p.getDebitSebelum()
//                         : BigDecimal.ZERO;

//         BigDecimal kreditSebelum =
//                 p.getKreditSebelum() != null
//                         ? p.getKreditSebelum()
//                         : BigDecimal.ZERO;

//         BigDecimal totalDebit =
//                 p.getTotalDebit() != null
//                         ? p.getTotalDebit()
//                         : BigDecimal.ZERO;

//         BigDecimal totalKredit =
//                 p.getTotalKredit() != null
//                         ? p.getTotalKredit()
//                         : BigDecimal.ZERO;


//         BigDecimal saldoAwal;

//         BigDecimal saldoAkhir;


//         /*
//         * ================================
//         * NORMAL BALANCE DEBIT
//         * ================================
//         */
//         if ("D".equalsIgnoreCase(p.getNormalBalance())) {

//             saldoAwal = debitSebelum.subtract(kreditSebelum);
//             saldoAkhir = saldoAwal.add(totalDebit).subtract(totalKredit);

//         /*
//         * ================================
//         * NORMAL BALANCE KREDIT
//         * ================================
//         */
//         } else if ("K".equalsIgnoreCase(p.getNormalBalance())) {

//             saldoAwal = kreditSebelum.subtract(debitSebelum);
//             saldoAkhir = saldoAwal.add(totalKredit).subtract(totalDebit);

//         } else {

//             throw new IllegalStateException(
//                     "Normal balance tidak valid untuk GL Account "
//                             + p.getAccountCode()
//                             + ": "
//                             + p.getNormalBalance()
//             );
//         }


//         /*
//         * ================================
//         * SALDO AKHIR DEBIT / KREDIT
//         * ================================
//         */

//         BigDecimal saldoAkhirDebit = BigDecimal.ZERO;
//         BigDecimal saldoAkhirKredit = BigDecimal.ZERO;

//         if (saldoAkhir.compareTo(BigDecimal.ZERO) >= 0) {

//             if ("D".equalsIgnoreCase(p.getNormalBalance())) {
//                 saldoAkhirDebit = saldoAkhir;
//             } else {
//                 saldoAkhirKredit = saldoAkhir;
//             }

//         } else {

//             /*
//             * Kalau saldo negatif,
//             * berarti posisinya berlawanan
//             * dengan normal balance.
//             */

//             if ("D".equalsIgnoreCase(p.getNormalBalance())) {
//                 saldoAkhirKredit = saldoAkhir.abs();
//             } else {
//                 saldoAkhirDebit = saldoAkhir.abs();
//             }
//         }


//         return new NeracaSaldoDTO(
//                 p.getGlAccountId(),
//                 p.getAccountCode(),
//                 p.getAccountName(),
//                 p.getNormalBalance(),
//                 saldoAwal,
//                 totalDebit,
//                 totalKredit,
//                 saldoAkhirDebit,
//                 saldoAkhirKredit
//         );
//     }

//     public void testNeracaSaldo() {

//         LocalDateTime tanggalDari =
//                 LocalDate.of(2026, 8, 1)
//                         .atStartOfDay();

//         LocalDateTime tanggalSampai =
//                 LocalDate.of(2026, 8, 21)
//                         .atStartOfDay();

//         String branchCode = "002";

//         List<NeracaSaldoProjection> result =
//                 neracaSaldoRepository.findNeracaSaldo(
//                         tanggalDari,
//                         tanggalSampai,
//                         branchCode
//                 );

//         for (NeracaSaldoProjection p : result) {

//             System.out.println("==============================");

//             System.out.println(
//                     "GL ID       : " + p.getGlAccountId()
//             );

//             System.out.println(
//                     "Account Code: " + p.getAccountCode()
//             );

//             System.out.println(
//                     "Account Name: " + p.getAccountName()
//             );

//             System.out.println(
//                     "Normal      : " + p.getNormalBalance()
//             );

//             System.out.println(
//                     "Debit Sebelum : " + p.getDebitSebelum()
//             );

//             System.out.println(
//                     "Kredit Sebelum: " + p.getKreditSebelum()
//             );

//             System.out.println(
//                     "Debit Periode : " + p.getTotalDebit()
//             );

//             System.out.println(
//                     "Kredit Periode: " + p.getTotalKredit()
//             );
//         }
//     }


}