$(document).ready(function () {
  // Initialize Select2
  if ($.fn.select2) {
    $("#cabangId").select2({
      theme: "bootstrap-5",
      width: "100%",
      placeholder: "-- Pilih Cabang --",
    });
  }

  // Format Rupiah
  function formatRupiah(value) {
    return new Intl.NumberFormat("id-ID", {
      style: "currency",
      currency: "IDR",
      minimumFractionDigits: 0,
      maximumFractionDigits: 2,
    }).format(value || 0);
  }

  // Format tanggal
  function formatTanggal(value) {
    if (!value) return "-";

    const parts = value.split("-");

    if (parts.length !== 3) return value;

    return parts[2] + "-" + parts[1] + "-" + parts[0];
  }

  // Reset tampilan laporan
  function resetReport() {
     $("#reportInfo").hide();
     $("#summarySection").hide();
     $("#balanceSection").hide();
     $("#detailSection").hide();
     $("#signatureSection").hide();

     $("#errorMessage").hide().text("");
     $("#emptyMessage").show();

     $("#btnExportPdf").prop("disabled", true);
  }

  // Tampilkan hasil laporan
  function renderReport(data) {
    $("#namaCabang").text(
      data.kodeCabang
        ? data.kodeCabang + " - " + (data.namaCabang || "")
        : "Cabang ID: " + data.cabangId,
    );

    $("#tanggalLaporan").text(formatTanggal(data.tanggal));

    // Ringkasan
    $("#saldoAwalKas").text(formatRupiah(data.saldoAwalKas));
    $("#kasMasuk").text(formatRupiah(data.kasMasuk));
    $("#kasKeluar").text(formatRupiah(data.kasKeluar));
    $("#totalSaldoAkhir").text(formatRupiah(data.totalSaldoAkhir));

    // Saldo per akun
    $("#saldoAwalKasDetail").text(formatRupiah(data.saldoAwalKas));
    $("#saldoAkhirKas").text(formatRupiah(data.saldoAkhirKas));

    $("#saldoAwalKasKecil").text(formatRupiah(data.saldoAwalKasKecil));

    $("#saldoAkhirKasKecil").text(formatRupiah(data.saldoAkhirKasKecil));

    // Detail transaksi
    const tbody = $("#detailTableBody");
    tbody.empty();

    let totalMasuk = 0;
    let totalKeluar = 0;

    if (data.details && data.details.length > 0) {
      $.each(data.details, function (index, item) {
        const kasMasuk = Number(item.kasMasuk || 0);
        const kasKeluar = Number(item.kasKeluar || 0);

        totalMasuk += kasMasuk;
        totalKeluar += kasKeluar;

        const row = $("<tr>");

        $("<td>")
          .text(index + 1)
          .appendTo(row);
        $("<td>")
          .text(item.transactionCode || "-")
          .appendTo(row);
        $("<td>")
          .text(item.transactionName || "-")
          .appendTo(row);

        $("<td>")
          .addClass("text-end")
          .text(formatRupiah(kasMasuk))
          .appendTo(row);

        $("<td>")
          .addClass("text-end")
          .text(formatRupiah(kasKeluar))
          .appendTo(row);

        tbody.append(row);
      });
    } else {
      tbody.append(
        $("<tr>").append(
          $("<td>")
            .attr("colspan", 5)
            .addClass("text-center text-muted")
            .text("Tidak ada transaksi pada tanggal ini."),
        ),
      );
    }

    $("#totalDetailMasuk").text(formatRupiah(totalMasuk));
    $("#totalDetailKeluar").text(formatRupiah(totalKeluar));

    // Tampilkan seluruh bagian
    $("#reportInfo").show();
    $("#summarySection").show();
    $("#balanceSection").show();
    $("#detailSection").show();

    $("#emptyMessage").hide();
    $("#errorMessage").hide();

     $("#signatureSection").show();
     $("#btnExportPdf").prop("disabled", false);

  }

 

  // Ambil data laporan
  function loadLaporan() {
    const cabangId = $("#cabangId").val();
    const tanggal = $("#tanggal").val();

    if (!cabangId) {
      alert("Silakan pilih cabang terlebih dahulu.");
      return;
    }

    if (!tanggal) {
      alert("Silakan pilih tanggal laporan.");
      return;
    }

    resetReport();

    $("#btnCari").prop("disabled", true);

    $.ajax({
      url: "/laporan/kas-harian/data",
      type: "GET",
      data: {
        cabangId: cabangId,
        tanggal: tanggal,
      },

      success: function (response) {
        renderReport(response);
      },

      error: function (xhr) {
        let message = "Gagal mengambil data Laporan Kas Harian.";

        if (xhr.responseJSON && xhr.responseJSON.message) {
          message = xhr.responseJSON.message;
        }

        $("#errorMessage").text(message).show();
        $("#emptyMessage").hide();
      },

      complete: function () {
        $("#btnCari").prop("disabled", false);
      },
    });
  }

  // Event tombol filter
  $("#btnCari").on("click", function () {
    loadLaporan();
  });

  $("#btnExportPdf").on("click", function () {
    const cabangId = $("#cabangId").val();
    const tanggal = $("#tanggal").val();

    if (!cabangId || !tanggal) {
      alert("Pilih cabang dan tanggal laporan terlebih dahulu.");
      return;
    }

    const url =
      "/laporan/kas-harian/export-pdf" +
      "?cabangId=" +
      encodeURIComponent(cabangId) +
      "&tanggal=" +
      encodeURIComponent(tanggal);

    window.open(url, "_blank");
  });
});
