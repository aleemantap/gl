package com.project.gl.helper;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.project.gl.model.response.JournalListDTO;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JournalExcelExporter {

    public void export(List<JournalListDTO> journals, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=jurnal.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Daftar Jurnal");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] columns = {
                    "Transaction No",
                    "Journal No",
                    "Branch Code",
                    "Branch Name",
                    "Transaction Date",
                    "Total Debit",
                    "Total Kredit",
                    "Pembalikan"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (JournalListDTO dto : journals) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(dto.getTransactionNo() != null ? dto.getTransactionNo() : "");
                row.createCell(1).setCellValue(dto.getJournalNo() != null ? dto.getJournalNo() : "");
                row.createCell(2).setCellValue(dto.getBranchCode() != null ? dto.getBranchCode() : "");
                row.createCell(3).setCellValue(dto.getNamaCabang() != null ? dto.getNamaCabang() : "");
                row.createCell(4).setCellValue(dto.getTransactionDate() != null ? dto.getTransactionDate().toString() : "");
                row.createCell(5).setCellValue(dto.getTotalDebit() != null ? dto.getTotalDebit().doubleValue() : 0.0);
                row.createCell(6).setCellValue(dto.getTotalCredit() != null ? dto.getTotalCredit().doubleValue() : 0.0);
                row.createCell(7).setCellValue(dto.getReversalStatus() != null ? dto.getReversalStatus() : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
        }
    }
}