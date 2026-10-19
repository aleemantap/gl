package com.project.gl.service;

import com.project.gl.repo.NomorGlAccountRepository;
import com.project.gl.repo.SubLedgerGlRepository;
import com.project.gl.repo.SubLedgerPawnRepository;
import com.project.gl.repo.SubLedgerRepository;
import com.project.gl.service.SubLedgerReconciliationService;
import java.sql.Timestamp;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.gl.model.entity.NomorGlAccount;
import com.project.gl.model.response.SubLedgerDetailDto;
import com.project.gl.model.response.SubLedgerReconciliationDto;


@Service
@RequiredArgsConstructor
public class SubLedgerReconciliationService {
 

    @Autowired
    private  SubLedgerRepository subLedgerRepository;
    
    @Autowired
    private  NomorGlAccountRepository glAccountRepository;

    public SubLedgerReconciliationDto reconcile(
            Integer glAccountId,
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Periode wajib diisi"
            );
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "Tanggal akhir tidak boleh lebih kecil dari tanggal mulai"
            );
        }

        NomorGlAccount account =
                glAccountRepository.findById(glAccountId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "GL Account tidak ditemukan"
                                )
                        );

        LocalDateTime start =
                startDate.atStartOfDay();

        LocalDateTime endExclusive =
                endDate.plusDays(1).atStartOfDay();

        BigDecimal openingBalance =
                defaultZero(
                        subLedgerRepository.findOpeningBalance(
                                glAccountId,
                                start
                        )
                );

        BigDecimal saldoGl =
                defaultZero(
                        subLedgerRepository.findGlBalance(
                                glAccountId,
                                endExclusive
                        )
                );

        List<Object[]> rows =
                subLedgerRepository.findDetails(
                        glAccountId,
                        start,
                        endExclusive
                );

                // System.out.println("======================================");
                // System.out.println("SUB LEDGER DEBUG");
                // System.out.println("GL ACCOUNT ID : " + glAccountId);
                // System.out.println("START         : " + start);
                // System.out.println("END           : " + endExclusive);
                // System.out.println("ROWS          : " + rows.size());
        


        BigDecimal runningBalance = openingBalance;

        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        List<SubLedgerDetailDto> details =  new ArrayList<>();
               

        for (Object[] row : rows) {

//                    System.out.println(
//         "ROW => journalNo=" + row[1]
//         + ", transactionNo=" + row[2]
//         + ", dk=" + row[3]
//         + ", amount=" + row[4]
//     );

            LocalDateTime transactionDate = ((Timestamp) row[0]).toLocalDateTime();
            String journalNo = String.valueOf(row[1]); 
            String transactionNo =  String.valueOf(row[2]); 
            String dk =  String.valueOf(row[3]);   
            BigDecimal amount =defaultZero((BigDecimal) row[4]);
            String description =  String.valueOf(row[5]);   
              BigDecimal debit = BigDecimal.ZERO;
              BigDecimal credit = BigDecimal.ZERO;
           // BigDecimal mutation;

               if ("D".equalsIgnoreCase(dk)) {
                        debit = amount;
                        totalDebit = totalDebit.add(amount);

                } else {
                        credit = amount;
                        totalCredit = totalCredit.add(amount);
                }

                BigDecimal mutation;

                if ("D".equalsIgnoreCase(account.getSaldoNormal())) {
                         mutation = debit.subtract(credit);
                } else {
                         mutation = credit.subtract(debit);
                }
                runningBalance = runningBalance.add(mutation);

            details.add(
                    new SubLedgerDetailDto(
                            transactionDate,
                            journalNo,
                            transactionNo,
                            debit,
                            credit,
                            mutation,
                            runningBalance,
                            description
                    )
            );
        }

        BigDecimal difference =
                saldoGl.subtract(runningBalance);

        String status =
                difference.compareTo(BigDecimal.ZERO) == 0
                        ? "SESUAI"
                        : "SELISIH";

        SubLedgerReconciliationDto result =
                new SubLedgerReconciliationDto();

        result.setGlAccountId(account.getId());
        result.setNomorGl(account.getNomorGl());
        result.setNamaGlAccount(account.getNamaGlAccount());
        result.setSaldoNormal(account.getSaldoNormal());

        result.setStartDate(startDate);
        result.setEndDate(endDate);

        result.setOpeningBalance(openingBalance);

        result.setTotalDebit(totalDebit);
        result.setTotalCredit(totalCredit);

        result.setSaldoSubLedger(runningBalance);
        result.setSaldoGl(saldoGl);
        result.setDifference(difference);
        result.setStatus(status);
        result.setDetails(details);

//         System.out.println("======================================");
// System.out.println("SUB LEDGER RESULT");
// System.out.println("Opening Balance : " + openingBalance);
// System.out.println("Total Debit     : " + totalDebit);
// System.out.println("Total Credit    : " + totalCredit);
// System.out.println("Saldo GL        : " + saldoGl);
// System.out.println("Saldo SubLedger : " + runningBalance);
// System.out.println("Difference      : " + difference);
// System.out.println("Status          : " + status);
// System.out.println("Details         : " + details.size());
// System.out.println("======================================");


        return result;
    }

    private BigDecimal defaultZero(BigDecimal value) {

        return value != null
                ? value
                : BigDecimal.ZERO;
    }
}