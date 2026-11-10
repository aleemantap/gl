package com.project.gl.service;

import com.project.gl.model.response.LaporanKasHarianDto;
import com.project.gl.model.response.GlCashMovementDetailDto;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class LaporanKasHarianPdfGenerator {

    public ByteArrayInputStream generatePdf(LaporanKasHarianDto data) {

        Document document = new Document(PageSize.A4, 40, 40, 35, 35);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 14
            );

            Font fontSub = FontFactory.getFont(
                    FontFactory.HELVETICA, 10
            );

            Font fontHeader = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 9
            );

            Font fontBody = FontFactory.getFont(
                    FontFactory.HELVETICA, 9
            );

            Font fontBold = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 9
            );

            NumberFormat nf = NumberFormat.getNumberInstance(
                    new Locale("id", "ID")
            );

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd-MM-yyyy");

            // JUDUL
            Paragraph title = new Paragraph(
                    "LAPORAN KAS HARIAN",
                    fontTitle
            );

            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(12);
            document.add(title);

            // INFORMASI CABANG
            PdfPTable info = new PdfPTable(2);
            info.setWidthPercentage(100);
            info.setWidths(new float[]{1f, 2f});

            addInfoRow(info, "Cabang",
                    data.getKodeCabang() + " - " + data.getNamaCabang(), fontBody);

            addInfoRow(info, "Tanggal Laporan",
                    data.getTanggal().format(formatter), fontBody);

            document.add(info);

            document.add(new Paragraph(" "));

            // POSISI SALDO KAS
            Paragraph saldoTitle = new Paragraph(
                    "POSISI SALDO KAS",
                    fontBold
            );

            saldoTitle.setSpacingAfter(6);
            document.add(saldoTitle);

            PdfPTable saldoTable = new PdfPTable(3);
            saldoTable.setWidthPercentage(100);
            saldoTable.setWidths(new float[]{3f, 3f, 3f});

            addHeader(saldoTable, "Akun", fontHeader);
            addHeader(saldoTable, "Saldo Awal", fontHeader);
            addHeader(saldoTable, "Saldo Akhir", fontHeader);

            addSaldoRow(
                    saldoTable,
                    "110101 - Kas",
                    data.getSaldoAwalKas(),
                    data.getSaldoAkhirKas(),
                    fontBody,
                    nf
            );

            addSaldoRow(
                    saldoTable,
                    "110102 - Kas Kecil",
                    data.getSaldoAwalKasKecil(),
                    data.getSaldoAkhirKasKecil(),
                    fontBody,
                    nf
            );

            document.add(saldoTable);

            document.add(new Paragraph(" "));

            // RINGKASAN
            PdfPTable summary = new PdfPTable(2);
            summary.setWidthPercentage(100);
            summary.setWidths(new float[]{3f, 2f});

            addInfoRow(summary, "Kas Masuk",
                    nf.format(data.getKasMasuk()), fontBody);

            addInfoRow(summary, "Kas Keluar",
                    nf.format(data.getKasKeluar()), fontBody);

            addInfoRow(summary, "Total Saldo Akhir",
                    nf.format(data.getTotalSaldoAkhir()), fontBold);

            document.add(summary);

            document.add(new Paragraph(" "));

            // RINCIAN TRANSAKSI
            Paragraph detailTitle = new Paragraph(
                    "RINCIAN TRANSAKSI KAS",
                    fontBold
            );

            detailTitle.setSpacingAfter(6);
            document.add(detailTitle);

            PdfPTable detail = new PdfPTable(5);
            detail.setWidthPercentage(100);
            detail.setWidths(new float[]{0.7f, 2f, 3f, 2f, 2f});

            addHeader(detail, "No", fontHeader);
            addHeader(detail, "Kode", fontHeader);
            addHeader(detail, "Jenis Transaksi", fontHeader);
            addHeader(detail, "Kas Masuk", fontHeader);
            addHeader(detail, "Kas Keluar", fontHeader);

            int no = 1;

            for (GlCashMovementDetailDto item : data.getDetails()) {

                addCell(detail, String.valueOf(no++), fontBody,
                        Element.ALIGN_CENTER);

                addCell(detail, item.getTransactionCode(), fontBody,
                        Element.ALIGN_LEFT);

                addCell(detail, item.getTransactionName(), fontBody,
                        Element.ALIGN_LEFT);

                addCell(detail, nf.format(item.getKasMasuk()), fontBody,
                        Element.ALIGN_RIGHT);

                addCell(detail, nf.format(item.getKasKeluar()), fontBody,
                        Element.ALIGN_RIGHT);
            }

            // TOTAL DETAIL
            addTotalCell(detail, "TOTAL", fontBold, 3);
            addTotalCell(detail, nf.format(data.getKasMasuk()), fontBold, 1);
            addTotalCell(detail, nf.format(data.getKasKeluar()), fontBold, 1);

            document.add(detail);

            document.add(new Paragraph(" "));

            // TANDA TANGAN
            PdfPTable signature = new PdfPTable(2);
            signature.setWidthPercentage(100);

            signature.addCell(signatureCell(
                    "Kasir",
                    "Nama Kasir",
                    fontBody
            ));

            signature.addCell(signatureCell(
                    "Kepala Cabang",
                    "Kepala Cabang",
                    fontBody
            ));

            document.add(signature);

            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException(
                    "Gagal membuat PDF Laporan Kas Harian", e
            );
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addHeader(
            PdfPTable table,
            String value,
            Font font
    ) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private void addCell(
            PdfPTable table,
            String value,
            Font font,
            int alignment
    ) {
        PdfPCell cell = new PdfPCell(new Phrase(
                value == null ? "-" : value, font
        ));

        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addInfoRow(
            PdfPTable table,
            String label,
            String value,
            Font font
    ) {
        table.addCell(new Phrase(label, font));
        table.addCell(new Phrase(": " + value, font));
    }

    private void addSaldoRow(
            PdfPTable table,
            String akun,
            java.math.BigDecimal awal,
            java.math.BigDecimal akhir,
            Font font,
            NumberFormat nf
    ) {
        addCell(table, akun, font, Element.ALIGN_LEFT);
        addCell(table, nf.format(awal), font, Element.ALIGN_RIGHT);
        addCell(table, nf.format(akhir), font, Element.ALIGN_RIGHT);
    }

    private PdfPCell signatureCell(
            String title,
            String name,
            Font font
    ) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingTop(10);

        Paragraph paragraph = new Paragraph(title, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(paragraph);

        Paragraph space = new Paragraph("\n\n\n\n", font);
        cell.addElement(space);

        Paragraph line = new Paragraph("________________________", font);
        line.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(line);

        Paragraph signer = new Paragraph(name, font);
        signer.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(signer);

        return cell;
    }

    private void addTotalCell(
            PdfPTable table,
            String value,
            Font font,
            int colspan
    ) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(6);
        table.addCell(cell);
    }
}