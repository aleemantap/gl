package com.project.gl.service;

import com.project.gl.model.request.JournalRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;



@Service
@RequiredArgsConstructor
// @RequiredArgsConstructor
@Transactional
public class ExcelImportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Jika ada dependency lain, tambahkan di sini
    // private final AnotherService anotherService;
    
    // Constructor Injection (jika ada dependency)
    // public ExcelImportService(AnotherService anotherService) {
    //     this.anotherService = anotherService;
    // }

    // Jika tidak ada dependency, bisa pakai default constructor
    //public ExcelImportService() {
        // Default constructor
    //}

    /**
     * Baca file Excel dari InputStream
     */
    public List<Map<String, Object>> readExcelFile(InputStream inputStream) throws Exception {
        List<Map<String, Object>> data = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            
            if (headerRow == null) {
                throw new Exception("File tidak memiliki header");
            }
            
            // Baca header
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValueAsString(cell));
            }
            
            // Baca data
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Map<String, Object> rowData = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String value = getCellValueAsString(cell);
                    rowData.put(headers.get(j), value);
                }
                data.add(rowData);
            }
        }
        
        return data;
    }

    /**
     * Konversi data Excel ke JournalRequest
     */
    public List<JournalRequest> convertToJournalRequests(List<Map<String, Object>> excelData) {
        List<JournalRequest> requests = new ArrayList<>();
        
        for (Map<String, Object> row : excelData) {
            JournalRequest request = new JournalRequest();
            
            // ===== Transaction Information =====
            // request.setTransactionNo(getString(row, "transactionNo"));
            // request.setTransactionCode(getString(row, "transactionCode"));
            // request.setBranchCode(getString(row, "branchCode"));
            // request.setProductCode(getString(row, "productCode"));
            // request.setIdempotencyKey(getString(row, "idempotencyKey"));

            request.setTransactionNo(getString(row, "transaction_no"));
            request.setTransactionCode(getString(row, "transaction_code"));
            request.setBranchCode(getString(row, "branch_code"));
            request.setProductCode(getString(row, "product_code"));
            request.setIdempotencyKey(getString(row, "idempotency_key"));
            request.setTypeTransaction(getString(row, "transactionType"));

            String transactionType = getString(row, "transactionType");
            
            // OccurredAt
            String occurredAtStr = getString(row, "occurred_at");
            if (occurredAtStr != null && !occurredAtStr.isEmpty()) {
                try {
                    request.setOccurredAt(OffsetDateTime.parse(occurredAtStr, DATE_TIME_FORMATTER));
                } catch (Exception e) {
                    try {
                        request.setOccurredAt(OffsetDateTime.parse(occurredAtStr + " 00:00:00", DATE_TIME_FORMATTER));
                    } catch (Exception e2) {
                        request.setOccurredAt(OffsetDateTime.now(ZoneOffset.UTC));
                    }
                }
            }
            
            // ===== Counterparty Information =====
            JournalRequest.Counterparty counterparty = new JournalRequest.Counterparty();
            counterparty.setRef(getString(row, "counterparty_ref"));
            counterparty.setName(getString(row, "counterparty_name"));
            counterparty.setType(getString(row, "counterparty_type"));
            request.setCounterparty(counterparty);


           

            // ===== Actor (Teller) Information =====
            JournalRequest.Actor actor = new JournalRequest.Actor();
            String tellerUserId = getString(row, "teller_user_id");
            if (tellerUserId != null && !tellerUserId.isEmpty()) {
                try {
                    actor.setTellerUserId(Long.parseLong(tellerUserId));
                } catch (NumberFormatException e) {
                    actor.setTellerUserId(null);
                }
            }
            actor.setKasPointCode(getString(row, "kas_point_code"));
            request.setActor(actor);
            
            // ===== Amounts =====
            JournalRequest.Amounts amounts = new JournalRequest.Amounts();
            
           
            
            // if ("pencairan".equalsIgnoreCase(transactionType)) {
            //     amounts.setLoanAmount(getBigDecimal(row, "loan_amount"));
            //     amounts.setAdminCost(getBigDecimal(row, "admin_cost"));
            //     amounts.setProvision(getBigDecimal(row, "provision"));
            //     amounts.setCustomerCash(getBigDecimal(row, "customer_cash"));
            // } else if ("pembayaran".equalsIgnoreCase(transactionType)) {
            //     amounts.setRepaymentAmount(getBigDecimal(row, "repayment_amount"));
            //     amounts.setInterestAmount(getBigDecimal(row, "interest_amount"));
            //     amounts.setPaymentAmount(getBigDecimal(row, "payment_amount"));
            //     amounts.setPrincipalPayment(getBigDecimal(row, "principal_payment"));
            // } else {
            //     amounts.setLoanAmount(getBigDecimal(row, "loan_amount"));
            //     amounts.setRepaymentAmount(getBigDecimal(row, "repayment_amount"));
            //     amounts.setInterestAmount(getBigDecimal(row, "interest_amount"));
            //     amounts.setAdminCost(getBigDecimal(row, "admin_cost"));
            //     amounts.setProvision(getBigDecimal(row, "provision"));
            //     amounts.setCustomerCash(getBigDecimal(row, "customer_cash"));
            //     amounts.setPaymentAmount(getBigDecimal(row, "payment_amount"));
            //     amounts.setPrincipalPayment(getBigDecimal(row, "principal_payment"));
            // }

             if ("pencairan".equalsIgnoreCase(transactionType)) {
                amounts.setLoanAmount(parseCurrency(getString(row, "loan_amount")));
                amounts.setAdminCost(parseCurrency(getString(row, "admin_cost")));
                amounts.setProvision(parseCurrency(getString(row, "provision")));
                amounts.setCustomerCash(parseCurrency(getString(row, "customer_cash")));

                // ===== Counterparty Information =====
                JournalRequest.Collateral  collateral  = new JournalRequest.Collateral();

                collateral.setLtv(parseCurrency(getString(row, "ltv")));
                collateral.setAppraisedValue(parseCurrency(getString(row, "appraised_value")));
                collateral.setCollateralType(getString(row, "collateral_type"));
                request.setCollateral(collateral);


            } else if ("pembayaran".equalsIgnoreCase(transactionType)) {
                amounts.setRepaymentAmount(parseCurrency(getString(row, "repayment_amount")));
                amounts.setInterestAmount(parseCurrency(getString(row, "interest_amount")));
                amounts.setPaymentAmount(parseCurrency(getString(row, "payment_amount")));
                amounts.setPrincipalPayment(parseCurrency(getString(row, "principal_payment")));
            } else {
                amounts.setLoanAmount(parseCurrency(getString(row, "loan_amount")));
                amounts.setRepaymentAmount(parseCurrency(getString(row, "repayment_amount")));
                amounts.setInterestAmount(parseCurrency(getString(row, "interest_amount")));
                amounts.setAdminCost(parseCurrency(getString(row, "admin_cost")));
                amounts.setProvision(parseCurrency(getString(row, "provision")));
                amounts.setCustomerCash(parseCurrency(getString(row, "customer_cash")));
                amounts.setPaymentAmount(parseCurrency(getString(row, "payment_amount")));
                amounts.setPrincipalPayment(parseCurrency(getString(row, "principal_payment")));
            }

            System.out.println("TEEE-"+getBigDecimal(row, "loan_amount"));
            
            request.setAmounts(amounts);
            
            // ===== Flags =====
            // JournalRequest.Flags flags = new JournalRequest.Flags();
            // flags.setAdminInAdvance(getBoolean(row, "admin_in_advance"));
            // flags.setProvisionInAdvance(getBoolean(row, "provision_in_advance"));
            // request.setFlags(flags);
            
            requests.add(request);
        }
        
        return requests;
    }

    // ===== Helper Methods =====

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }

    private String getString(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) return null;
        String str = value.toString().trim();
        return str.isEmpty() ? null : str;
    }

      private BigDecimal parseCurrency(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Bersihkan string dari berbagai format mata uang
            String cleaned = value.trim()
                // Hapus "Rp" atau "IDR" (case insensitive)
                .replaceAll("(?i)^Rp\\s*", "")
                .replaceAll("(?i)^IDR\\s*", "")
                // Hapus "USD" atau mata uang lainnya
                .replaceAll("(?i)^[A-Z]{3}\\s*", "")
                // Hapus tanda mata uang lain
                .replaceAll("[$€£¥]", "")
                // Hapus spasi
                .replaceAll("\\s+", "")
                // Hapus titik (pemisah ribuan)
                .replaceAll("\\.", "")
                // Ganti koma dengan titik (untuk desimal)
                .replace(",", ".");
            
            // Jika setelah cleaning masih ada karakter non-numeric (selain titik)
            cleaned = cleaned.replaceAll("[^0-9.]", "");
            
            // Jika string kosong atau hanya berisi titik
            if (cleaned.isEmpty() || cleaned.equals(".")) {
                return null;
            }
            
            // Jika ada lebih dari satu titik, ambil hanya angka sebelum titik pertama
            // Ini untuk kasus "1.000.000,00" yang sudah diubah jadi "1000000,00"
            String[] parts = cleaned.split("\\.");
            if (parts.length > 1) {
                // Jika ada titik, kemungkinan itu adalah pemisah desimal atau ribuan
                // Jika panjang setelah titik > 2, berarti itu ribuan
                if (parts[parts.length - 1].length() > 2) {
                    // Kasus: "1000.500" -> ribuan
                    cleaned = cleaned.replace(".", "");
                } else {
                    // Kasus: "1000.50" -> desimal dengan 2 digit
                    // Format sudah benar
                }
            }
            
            // Konversi ke BigDecimal
            return new BigDecimal(cleaned);
            
        } catch (NumberFormatException e) {
            System.err.println("Gagal parse currency: " + value + " - " + e.getMessage());
            return null;
        }
    }

    private BigDecimal getBigDecimal(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) return null;
        
        try {
            String str = value.toString().trim().replace(".", "").replace(",", "");
            if (str.isEmpty()) return null;
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean getBoolean(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) return false;
        
        String str = value.toString().trim().toLowerCase();
        return "true".equals(str) || "1".equals(str) || "yes".equals(str) || "y".equals(str);
    }

}