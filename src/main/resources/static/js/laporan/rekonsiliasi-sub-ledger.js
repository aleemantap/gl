$(document).ready(function () {
  $("#btnReconcile").on("click", function () {
    const glAccountId = $("#glAccountId").val();
    const startDate = $("#startDate").val();
    const endDate = $("#endDate").val();

    if (!glAccountId) {
      alert("Silakan pilih akun GL.");
      return;
    }

    if (!startDate || !endDate) {
      alert("Periode wajib diisi.");
      return;
    }

    if (endDate < startDate) {
      alert("Tanggal akhir tidak boleh lebih kecil dari tanggal mulai.");
      return;
    }

    const params = new URLSearchParams({
      glAccountId: glAccountId,
      startDate: startDate,
      endDate: endDate,
    });

    $.ajax({
      url: "/laporan/rekonsiliasi-sub-ledger/data?" + params.toString(),
      method: "GET",
      dataType: "json",
      success: function (data) {
        renderResult(data);
      },
      error: function (xhr, status, error) {
        console.error(error);
        alert("Gagal mengambil data rekonsiliasi.");
      },
    });
  });
});

function renderResult(data) {
  $("#resultSection").show();

  $("#accountName").text(data.nomorGl + " - " + data.namaGlAccount);

  $("#period").text(
    formatDate(data.startDate) + " s/d " + formatDate(data.endDate),
  );

  $("#totalDebit").text(formatRupiah(data.totalDebit));
  $("#totalCredit").text(formatRupiah(data.totalCredit));

  $("#saldoGl").text(formatRupiah(data.saldoGl));
  $("#saldoSubLedger").text(formatRupiah(data.saldoSubLedger));
  $("#difference").text(formatRupiah(data.difference));

  const statusElement = $("#status");
  statusElement.text(data.status);
  statusElement.attr(
    "class",
    data.status === "SESUAI" ? "status status-ok" : "status status-warning",
  );

  const tbody = $("#detailBody");
  tbody.empty();

  $.each(data.details, function (index, row) {
    const tr = $("<tr></tr>");

    tr.html(`
            <td>${formatDateTime(row.transactionDate)}</td>
            <td>${escapeHtml(row.journalNo || "")}</td>
            <td>${escapeHtml(row.transactionNo || "")}</td>
            <td class="text-right">${formatRupiah(row.debit)}</td>
            <td class="text-right">${formatRupiah(row.credit)}</td>
            <td class="text-right">${formatRupiah(row.balance)}</td>
            <td>${escapeHtml(row.description || "")}</td>
        `);

    tbody.append(tr);
  });
}

function formatRupiah(value) {
  return new Intl.NumberFormat("id-ID", {
    style: "currency",
    currency: "IDR",
    minimumFractionDigits: 0,
  }).format(value || 0);
}

function formatDate(value) {
  if (!value) return "-";
  const parts = value.split("-");
  return `${parts[2]}-${parts[1]}-${parts[0]}`;
}

function formatDateTime(value) {
  if (!value) return "-";
  const date = new Date(value);
  return new Intl.DateTimeFormat("id-ID", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(date);
}

function escapeHtml(value) {
  return $("<div>").text(value).html();
}

function exportToExcel() {
  // ============================================
  // 1. AMBIL DATA DARI DOM
  // ============================================
  var accountName =
    document.getElementById("accountName")?.innerText.trim() || "";
  var period = document.getElementById("period")?.innerText.trim() || "";
  var totalDebit =
    document.getElementById("totalDebit")?.innerText.trim() || "";
  var totalCredit =
    document.getElementById("totalCredit")?.innerText.trim() || "";
  var saldoGl = document.getElementById("saldoGl")?.innerText.trim() || "";
  var saldoSubLedger =
    document.getElementById("saldoSubLedger")?.innerText.trim() || "";
  var difference =
    document.getElementById("difference")?.innerText.trim() || "";
  var status = document.getElementById("status")?.innerText.trim() || "";

  // Ambil tabel detail
  var table = document.querySelector("#resultSection table");
  var rows = [];
  table.querySelectorAll("thead tr").forEach((tr) => {
    var row = [];
    tr.querySelectorAll("th").forEach((th) => row.push(th.innerText.trim()));
    rows.push(row);
  });
  table.querySelectorAll("tbody tr").forEach((tr) => {
    var row = [];
    tr.querySelectorAll("td").forEach((td) => row.push(td.innerText.trim()));
    rows.push(row);
  });

  // ============================================
  // 2. SUSUN DATA UNTUK WORKSHEET
  // ============================================
  var data = [];

  // --- Judul ---
  data.push(["LAPORAN DETAIL TRANSAKSI"]); // row 1
  data.push([]); // row 2 (kosong)

  // --- Info Akun & Periode ---
  data.push(["Akun", accountName]); // row 3
  data.push(["Periode", period]); // row 4
  data.push([]); // row 5 (kosong)

  // --- Summary ---
  data.push(["RINGKASAN"]); // row 6
  data.push(["Total Debit", totalDebit]); // row 7
  data.push(["Total Kredit", totalCredit]); // row 8
  data.push(["Saldo GL", saldoGl]); // row 9
  data.push(["Saldo Sub Ledger", saldoSubLedger]); // row 10
  data.push(["Selisih", difference]); // row 11
  data.push(["Status", status]); // row 12
  data.push([]); // row 13 (kosong)

  // --- Tabel Detail ---
  data.push(["DETAIL TRANSAKSI"]); // row 14
  rows.forEach((r) => data.push(r)); // mulai row 15

  // ============================================
  // 3. BUAT WORKSHEET
  // ============================================
  var ws = XLSX.utils.aoa_to_sheet(data);

  // ============================================
  // 4. MERGE CELL (judul & section title)
  // ============================================
  ws["!merges"] = [
    { s: { r: 0, c: 0 }, e: { r: 0, c: 6 } }, // Judul (A1:G1)
    { s: { r: 5, c: 0 }, e: { r: 5, c: 1 } }, // "RINGKASAN" (A6:B6)
    { s: { r: 13, c: 0 }, e: { r: 13, c: 6 } }, // "DETAIL TRANSAKSI" (A14:G14)
  ];

  // ============================================
  // 5. ATUR LEBAR KOLOM
  // ============================================
  ws["!cols"] = [
    { wch: 25 }, // A - label / Tanggal
    { wch: 30 }, // B - value / No Jurnal
    { wch: 25 }, // C - No Transaksi
    { wch: 15 }, // D - Debit
    { wch: 15 }, // E - Kredit
    { wch: 18 }, // F - Saldo
    { wch: 35 }, // G - Keterangan
  ];

  // ============================================
  // 6. BUAT WORKBOOK & EXPORT
  // ============================================
  var wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(
    wb,
    ws,
    "Rekonsiliasi sub ledger",
  );

  // Nama file dinamis
  // var fileName =
  //   "rekonsiliasi-sub-ledger-detail-transaksi-" +
  //   accountName.replace(/\s/g, "_") +
  //   ".xlsx";
  var fileName =
    "rekonsiliasi-sub-ledger-detail-transaksi.xlsx";
  XLSX.writeFile(wb, fileName);
}