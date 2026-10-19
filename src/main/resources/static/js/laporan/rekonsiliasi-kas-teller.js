$(document).ready(function () {
  // Apply ke semua input dengan class rupiah-input
  $(".rupiah-input").on("keyup", function (e) {
    // Hapus semua karakter kecuali angka
    var value = $(this)
      .val()
      .replace(/[^0-9]/g, "");

    if (value.length > 0) {
      // Format dengan titik
      var formatted = formatRupiah(value);
      $(this).val(formatted);
    } else {
      $(this).val("");
    }
  });

  // Saat input kehilangan fokus (blur), tambahkan ,00 jika perlu
  $(".rupiah-input").on("blur", function () {
    var value = $(this)
      .val()
      .replace(/[^0-9]/g, "");
    if (value.length > 0) {
      // Bisa tambahkan ,00 di belakang jika ingin
      // var formatted = formatRupiah(value) + ',00';
      // $(this).val(formatted);
    }
  });

  // Saat fokus, hilangkan format untuk memudahkan edit
  $(".rupiah-input").on("focus", function () {
    var value = $(this)
      .val()
      .replace(/[^0-9]/g, "");
    $(this).val(value);
    // Select all text for easy typing
    $(this).select();
  });

  function formatRupiah(angka) {
    // Hanya ambil angka
    var numberString = angka.replace(/[^,\d]/g, "").toString();
    var split = numberString.split(",");
    var sisa = split[0].length % 3;
    var rupiah = split[0].substr(0, sisa);
    var ribuan = split[0].substr(sisa).match(/\d{3}/gi);

    if (ribuan) {
      var separator = sisa ? "." : "";
      rupiah += separator + ribuan.join(".");
    }

    return rupiah;
  }
});

function exportToExcelPergerakanKas() {
  // ============================================
  // 1. AMBIL DATA DARI DOM
  // ============================================

  // --- Info filter dari form ---
  var tellerUserId =
    document.querySelector('input[name="tellerUserId"]')?.value.trim() || "";
  var kasPointCode =
    document.querySelector('input[name="kasPointCode"]')?.value.trim() || "";
  var date = document.querySelector('input[name="date"]')?.value.trim() || "";
  var openingBalance =
    document.querySelector('input[name="openingBalance"]')?.value.trim() || "";
  var physicalBalance =
    document.querySelector('input[name="physicalBalance"]')?.value.trim() || "";

  // --- Ringkasan Kas (dari summary-box) ---
  function getSummaryValue(title) {
    var boxes = document.querySelectorAll(".summary-box");
    for (var i = 0; i < boxes.length; i++) {
      var t = boxes[i].querySelector(".summary-title")?.innerText.trim();
      if (t === title) {
        return (
          boxes[i].querySelector(".summary-value")?.innerText.trim() ||
          boxes[i].querySelector(".status")?.innerText.trim() ||
          ""
        );
      }
    }
    return "";
  }

  var saldoAwal = getSummaryValue("Saldo Awal");
  var kasMasuk = getSummaryValue("Kas Masuk");
  var kasKeluar = getSummaryValue("Kas Keluar");
  var saldoSistem = getSummaryValue("Saldo Sistem");
  var status = getSummaryValue("Status");

  // --- Hasil Rekonsiliasi (card ke-3) ---
  var hasilCard = document.querySelectorAll("#card-data .card")[2];
  var hasilRows = [];
  if (hasilCard) {
    hasilCard.querySelectorAll("table tbody tr").forEach(function (tr) {
      var label = tr.querySelector("td:first-child")?.innerText.trim() || "";
      var value = tr.querySelector("td:last-child")?.innerText.trim() || "";
      hasilRows.push([label, value]);
    });
  }

  // --- Tabel Detail Pergerakan Kas (card ke-4) ---
  var detailCard = document.querySelectorAll("#card-data .card")[3];
  var detailRows = [];
  if (detailCard) {
    var table = detailCard.querySelector("table");

    // Header
    table.querySelectorAll("thead tr").forEach(function (tr) {
      var row = [];
      tr.querySelectorAll("th").forEach(function (th) {
        row.push(th.innerText.trim());
      });
      detailRows.push(row);
    });

    // Body
    table.querySelectorAll("tbody tr").forEach(function (tr) {
      var row = [];
      tr.querySelectorAll("td").forEach(function (td) {
        row.push(td.innerText.trim());
      });
      detailRows.push(row);
    });
  }

  // ============================================
  // 2. SUSUN DATA UNTUK WORKSHEET
  // ============================================
  var data = [];

  // --- Judul ---
  data.push(["LAPORAN REKONSILIASI KAS TELLER"]); // row 1
  data.push([]); // row 2

  // --- Info Filter ---
  data.push(["Teller User ID", tellerUserId]); // row 3
  data.push(["Kas Point", kasPointCode]); // row 4
  data.push(["Tanggal", date]); // row 5
  data.push(["Saldo Awal (Input)", openingBalance]); // row 6
  data.push(["Saldo Kas Fisik (Input)", physicalBalance]); // row 7
  data.push([]); // row 8

  // --- Ringkasan Kas ---
  data.push(["RINGKASAN KAS"]); // row 9
  data.push(["Saldo Awal", saldoAwal]); // row 10
  data.push(["Kas Masuk", kasMasuk]); // row 11
  data.push(["Kas Keluar", kasKeluar]); // row 12
  data.push(["Saldo Sistem", saldoSistem]); // row 13
  data.push(["Status", status]); // row 14
  data.push([]); // row 15

  // --- Hasil Rekonsiliasi ---
  data.push(["HASIL REKONSILIASI"]); // row 16
  hasilRows.forEach(function (r) {
    data.push(r);
  });
  data.push([]); // row kosong

  // --- Detail Pergerakan Kas ---
  data.push(["DETAIL PERGERAKAN KAS"]); // row judul detail
  detailRows.forEach(function (r) {
    data.push(r);
  });

  // ============================================
  // 3. BUAT WORKSHEET
  // ============================================
  var ws = XLSX.utils.aoa_to_sheet(data);

  // ============================================
  // 4. MERGE CELL (judul & section title)
  // ============================================
  // Cari index row untuk merge secara dinamis
  var idxJudul = 0;
  var idxRingkasan = data.findIndex(
    (r) => r.length === 1 && r[0] === "RINGKASAN KAS",
  );
  var idxHasil = data.findIndex(
    (r) => r.length === 1 && r[0] === "HASIL REKONSILIASI",
  );
  var idxDetail = data.findIndex(
    (r) => r.length === 1 && r[0] === "DETAIL PERGERAKAN KAS",
  );

  ws["!merges"] = [
    { s: { r: idxJudul, c: 0 }, e: { r: idxJudul, c: 4 } }, // Judul A1:E1
    { s: { r: idxRingkasan, c: 0 }, e: { r: idxRingkasan, c: 1 } }, // RINGKASAN
    { s: { r: idxHasil, c: 0 }, e: { r: idxHasil, c: 1 } }, // HASIL
    { s: { r: idxDetail, c: 0 }, e: { r: idxDetail, c: 4 } }, // DETAIL
  ];

  // ============================================
  // 5. ATUR LEBAR KOLOM
  // ============================================
  ws["!cols"] = [
    { wch: 22 }, // A - Waktu
    { wch: 12 }, // B - Journal
    { wch: 14 }, // C - Direction
    { wch: 45 }, // D - Keterangan
    { wch: 20 }, // E - Nominal
  ];

  // ============================================
  // 6. BUAT WORKBOOK & EXPORT
  // ============================================
  var wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, "Rekonsiliasi Kas Teller");

  var fileName = "rekonsiliasi-kas-teller.xlsx";
  XLSX.writeFile(wb, fileName);
}