$(document).ready(function () {});

function exportToExcelKeputusan() {
  // ============================================
  // 1. AMBIL DATA DARI TABEL #table-keputusan
  // ============================================
  var table = document.querySelector("#table-keputusan");
  if (!table) {
    alert("Tabel keputusan tidak ditemukan.");
    return;
  }

  // --- Header (dari thead) ---
  var headerRow = [];
  table.querySelectorAll("thead tr th").forEach(function (th) {
    // Skip kolom "Aksi" (kolom terakhir) karena tidak perlu diexport
    var text = th.innerText.trim();
    if (text.toLowerCase() !== "aksi") {
      headerRow.push(text);
    }
  });

  // --- Body (dari tbody) ---
  var bodyRows = [];
  table.querySelectorAll("tbody tr").forEach(function (tr) {
    // Skip empty state (row dengan colspan)
    if (tr.querySelector("td[colspan]")) return;

    var tds = tr.querySelectorAll("td");
    if (tds.length === 0) return;

    // Kolom 0: Judul + Deskripsi → gabung jadi satu string
    var judul = tds[0].querySelector(".fw-bold")?.innerText.trim() || "";
    var deskripsi = tds[0].querySelector(".text-muted")?.innerText.trim() || "";
    var keputusanIsu = deskripsi ? judul + " - " + deskripsi : judul;

    // Kolom 1: Kategori
    var kategori = tds[1]?.innerText.trim() || "";

    // Kolom 2: Status
    var status = tds[2]?.innerText.trim() || "";

    // Kolom 3: Pemilik
    var pemilik = tds[3]?.innerText.trim() || "";

    // Kolom 4: Tanggal
    var tanggal = tds[4]?.innerText.trim() || "";

    // Kolom 5: Tautan Aturan
    var tautan = tds[5]?.innerText.trim() || "";

    bodyRows.push([keputusanIsu, kategori, status, pemilik, tanggal, tautan]);
  });

  // ============================================
  // 2. SUSUN DATA UNTUK WORKSHEET
  // ============================================
  var data = [];

  // --- Judul ---
  data.push(["LAPORAN KEPUTUSAN / ISU GOVERNANCE"]); // row 1
  data.push([]); // row 2

  // --- Info ringkas ---
  data.push(["Total Keputusan / Isu", bodyRows.length]); // row 3
  data.push(["Tanggal Export", new Date().toLocaleString("id-ID")]); // row 4
  data.push([]); // row 5

  // --- Header tabel ---
  var idxHeader = data.length; // simpan index header
  data.push(headerRow);

  // --- Isi tabel ---
  bodyRows.forEach(function (r) {
    data.push(r);
  });

  // ============================================
  // 3. BUAT WORKSHEET
  // ============================================
  var ws = XLSX.utils.aoa_to_sheet(data);

  // ============================================
  // 4. MERGE CELL (judul)
  // ============================================
  var totalCols = headerRow.length; // 6 kolom
  ws["!merges"] = [
    { s: { r: 0, c: 0 }, e: { r: 0, c: totalCols - 1 } }, // Judul
  ];

  // ============================================
  // 5. ATUR LEBAR KOLOM
  // ============================================
  ws["!cols"] = [
    { wch: 50 }, // Keputusan / Isu
    { wch: 16 }, // Kategori
    { wch: 14 }, // Status
    { wch: 20 }, // Pemilik
    { wch: 14 }, // Tanggal
    { wch: 25 }, // Tautan Aturan
  ];

  // ============================================
  // 6. BUAT WORKBOOK & EXPORT
  // ============================================
  var wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, "Keputusan Governance");

  var fileName = "keputusan-governance.xlsx";
  XLSX.writeFile(wb, fileName);
}