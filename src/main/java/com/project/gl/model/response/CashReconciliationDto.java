package com.project.gl.model.response;

import com.project.gl.model.entity.GlCashMovement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale; // <- Ditambahkan di sini

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CashReconciliationDto {

    private Long tellerUserId;

    private String kasPointCode;

    private LocalDate date;

    private BigDecimal openingBalance = BigDecimal.ZERO;

    private BigDecimal totalIn = BigDecimal.ZERO;

    private BigDecimal totalOut = BigDecimal.ZERO;

    private BigDecimal systemBalance = BigDecimal.ZERO;

    private BigDecimal physicalBalance;

    private BigDecimal difference = BigDecimal.ZERO;

    private String status;

    private List<GlCashMovement> movements;

    public String getOpeningBalanceFormatted() {
        return formatRupiah(openingBalance);
    }

    public String getTotalInFormatted() {
        return formatRupiah(totalIn);
    }

    public String getTotalOutFormatted() {
        return formatRupiah(totalOut);
    }

    public String getSystemBalanceFormatted() {
        return formatRupiah(systemBalance);
    }

    public String getPhysicalBalanceFormatted() {
        return formatRupiah(physicalBalance);
    }

    public String getDifferenceFormatted() {
        return formatRupiah(difference);
    }

    private String formatRupiah(BigDecimal value) {
        if (value == null) {
            return "Rp 0";
        }

        return "Rp " + String.format(
                new Locale("id", "ID"),
                "%,.0f",
                value
        ).replace(',', '.');
    }

    // public String getAmountFormatted() {

    //     if (amount == null) {
    //         return "Rp 0";
    //     }

    //     return "Rp " + String.format(
    //             new Locale("id", "ID"),
    //             "%,.0f",
    //             amount
    //     ).replace(',', '.');
    // }
    // private String formatRupiah2(BigDecimal value) {

    //     if (value == null) {
    //         return "Rp 0";
    //     }

    //     return "Rp " + String.format(
    //             new Locale("id", "ID"),
    //             "%,.0f",
    //             value
    //     ).replace(',', '.');
    // }
}