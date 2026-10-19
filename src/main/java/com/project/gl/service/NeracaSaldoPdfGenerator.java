package com.project.gl.service;
import com.project.gl.model.response.NeracaSaldoDTO;

import lombok.RequiredArgsConstructor;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service 
// @RequiredArgsConstructor
// @Transactional
public class NeracaSaldoPdfGenerator {

    public ByteArrayInputStream generatePdf(List<NeracaSaldoDTO> dataList, LocalDate dari, LocalDate sampai, String branchCode) {
        Document document = new Document(PageSize.A4.rotate()); // Landscape agar muat banyak kolom
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Font Styles
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font fontSub = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            Font fontBody = FontFactory.getFont(FontFactory.HELVETICA, 8);

            // Title Header
            Paragraph title = new Paragraph("LAPORAN NERACA SALDO", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            String periodeText = String.format("Periode: %s s/d %s | Branch: %s", 
                    dari, sampai, (branchCode != null && !branchCode.isBlank() ? branchCode : "ALL"));
            Paragraph sub = new Paragraph(periodeText, fontSub);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(15);
            document.add(sub);

            // Table setup (7 Kolom)
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 5f, 2f, 3f, 3f, 3f, 3f});

            // Table Header
            String[] headers = {"Kode Akun", "Nama Akun", "Posisi", "Saldo Awal", "Debet", "Kredit", "Saldo Akhir"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Number Formatter (Rupiah/Indonesia Format)
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));

            // Table Body
            for (NeracaSaldoDTO dto : dataList) {
                table.addCell(new Phrase(dto.getAccountCode(), fontBody));
                table.addCell(new Phrase(dto.getAccountName(), fontBody));
                table.addCell(new Phrase(dto.getNormalBalance(), fontBody));
                
                PdfPCell c1 = new PdfPCell(new Phrase(nf.format(dto.getSaldoAwal()), fontBody));
                c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c1);

                PdfPCell c2 = new PdfPCell(new Phrase(nf.format(dto.getTotalDebit()), fontBody));
                c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c2);

                PdfPCell c3 = new PdfPCell(new Phrase(nf.format(dto.getTotalKredit()), fontBody));
                c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c3);

                // Menentukan saldo akhir yang ditampilkan
                var saldoAkhir = dto.getSaldoAkhirDebit().compareTo(java.math.BigDecimal.ZERO) > 0 
                        ? dto.getSaldoAkhirDebit() 
                        : dto.getSaldoAkhirKredit();
                        
                PdfPCell c4 = new PdfPCell(new Phrase(nf.format(saldoAkhir), fontBody));
                c4.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(c4);
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}