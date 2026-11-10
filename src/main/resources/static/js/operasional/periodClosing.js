$(document).ready(function () {

  $("#cabangId").select2({
    theme: "bootstrap-5",
    width: "100%",
    allowClear: false,
    // dropdownParent: $("#modal1"),
  });
  $("#bulan").select2({
    theme: "bootstrap-5",
    width: "100%",
    allowClear: false,
    // dropdownParent: $("#modal1"),
  });  

  const $page = $("#periodClosingPage");

  const urls = {
    list: $page.data("list-url"),
    check: $page.data("check-url"),
    confirm: $page.data("confirm-url"),
    submit: $page.data("submit-url"),
  };

  let selectedPeriod = null;
  let currentCheck = null;


  const monthNames = [
    "",
    "Januari",
    "Februari",
    "Maret",
    "April",
    "Mei",
    "Juni",
    "Juli",
    "Agustus",
    "September",
    "Oktober",
    "November",
    "Desember",
  ];

  function formatPeriod(year, month) {
    return `${monthNames[month]} ${year}`;
  }

  function formatRupiah(value) {
    return new Intl.NumberFormat("id-ID", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(value || 0);
  }

  function showMessage(message, type = "success") {
    $("#messagePanel").html(
      $("<div>").addClass(`alert alert-${type}`).text(message),
    );
  }

  function resetDetail() {
    selectedPeriod = null;
    currentCheck = null;

    $("#checkPanel").hide();
    $("#gadaiPanel").hide();
    $("#submitPanel").hide();

    $("#btnSubmit").prop("disabled", true);
  }

  function statusBadge(status) {
    const styles = {
      TERBUKA: "bg-success-lt text-success",
      MENUNGGU_PERSETUJUAN: "bg-warning-lt text-warning",
      DITUTUP: "bg-secondary-lt text-secondary",
      DITOLAK: "bg-danger-lt text-danger",
    };

    const labels = {
      TERBUKA: "TERBUKA",
      MENUNGGU_PERSETUJUAN: "MENUNGGU PERSETUJUAN",
      DITUTUP: "DITUTUP",
      DITOLAK: "DITOLAK",
    };

    return $("<span>")
      .addClass("badge " + (styles[status] || "bg-secondary-lt"))
      .text(labels[status] || status || "-");
  }

  function renderPeriods(data) {
    const $tbody = $("#periodClosingBody").empty();

    if (!data || data.length === 0) {
      $tbody.append(
        $("<tr>").append(
          $("<td>")
            .attr("colspan", 6)
            .addClass("text-center text-muted")
            .text("Belum ada data periode tutup buku."),
        ),
      );
      return;
    }

   
    data.forEach(function (item) {

        //console.log("DATA PERIOD CLOSING:", data);
        //console.log("DATA PERTAMA:", data[0]);


      const period = formatPeriod(item.tahun, item.bulan);

      const $row = $("<tr>");

      $row.append($("<td>").text(period));

      // Status
      $row.append($("<td>").append(statusBadge(item.status)));

      // Neraca Saldo
      const neracaSeimbang =
        item.neracaSeimbang ??
        item.isNeracaSeimbang ??
        item.is_neraca_seimbang ??
        false;

      $row.append(
        $("<td>").text(neracaSeimbang ? "Seimbang" : "Tidak Seimbang"),
      );

      // Antrean Gadai
      const pending = item.pendingGadaiCount;

      const pendingText =
        pending === null || pending === undefined
          ? "Belum dikonfirmasi"
          : `${pending} tertunda`;

      $row.append($("<td>").text(pendingText));

      // Ditutup oleh
      const closedBy = item.approvedBy || "-";

      $row.append($("<td>").text(closedBy));

      // Aksi
      const $action = $("<td>");

      const $button = $("<button>")
        .attr("type", "button")
        .addClass("btn btn-sm btn-outline-primary btn-check-period")
        .text("Periksa")
        .data("tahun", item.tahun)
        .data("bulan", item.bulan)
        .data("cabang-id", item.cabangId);

      $action.append($button);
      $row.append($action);

      $tbody.append($row);
    });
  }

  // Ambil daftar periode
  function loadPeriods(keepDetail = false) {
    const cabangId = $("#cabangId").val();

    if (!cabangId) {
      showMessage("Silakan pilih cabang terlebih dahulu.", "warning");
      return;
    }

    if (!keepDetail) {
      resetDetail();
    }

    $.ajax({
      url: urls.list,
      type: "GET",
      data: { cabangId: cabangId },
      dataType: "json",

      success: function (response) {
        renderPeriods(response);
      },

      error: function (xhr) {
        showMessage(
          xhr.responseJSON?.message || "Gagal mengambil daftar periode.",
          "danger",
        );
      },
    });
  }

  $("#btnLoadPeriods").on("click", loadPeriods);

  // Periksa periode ketika tombol di tabel diklik
  $(document).on("click", ".btn-check-period", function () {
    const params = {
      tahun: $(this).data("tahun"),
      bulan: $(this).data("bulan"),
      cabangId: $(this).data("cabang-id"),
    };

    selectedPeriod = params;

    checkPeriod(params);
  });

  
  function renderCheck(data) {
    currentCheck = data;

    const status = data.status || "TERBUKA";

    // Format tanggal
    function formatDateTime(value) {
      if (!value) return "-";

      const date = new Date(value);

      if (isNaN(date.getTime())) return value;

      return date.toLocaleString("id-ID", {
        day: "2-digit",
        month: "long",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      });
    }

    // Informasi periode
    $("#selectedPeriod").text(formatPeriod(data.tahun, data.bulan));

    // Informasi Maker dan Checker
    $("#submittedBy").text(data.submittedBy || "-");
    $("#submittedAt").text(formatDateTime(data.submittedAt));
    $("#approvedBy").text(data.approvedBy || "-");
    $("#approvedAt").text(formatDateTime(data.approvedAt));

    // Informasi status
    const statusLabel = {
      TERBUKA: "TERBUKA",
      MENUNGGU_PERSETUJUAN: "MENUNGGU PERSETUJUAN",
      DITUTUP: "SUDAH DITUTUP",
      DITOLAK: "DITOLAK",
    };

    $("#closingStatus")
      .text(statusLabel[status] || status)
      .removeClass("bg-success bg-warning bg-danger bg-secondary")
      .addClass(
        status === "DITUTUP"
          ? "bg-success"
          : status === "MENUNGGU_PERSETUJUAN"
            ? "bg-warning"
            : status === "DITOLAK"
              ? "bg-danger"
              : "bg-secondary",
      );

    // Neraca Saldo
    const neracaOk = data.neracaSeimbang === true;

    const neracaText = neracaOk
      ? `✓ Neraca saldo seimbang. Debit: Rp ${formatRupiah(data.totalDebit)}, Kredit: Rp ${formatRupiah(data.totalKredit)}`
      : `✕ Neraca saldo belum seimbang. Debit: Rp ${formatRupiah(data.totalDebit)}, Kredit: Rp ${formatRupiah(data.totalKredit)}`;

    $("#checkNeraca")
      .removeClass("alert-success alert-danger")
      .addClass(neracaOk ? "alert-success" : "alert-danger")
      .text(neracaText);

    // CKPN
    const ckpnOk = data.ckpnCreated === true;

    const ckpnText = ckpnOk
      ? `✓ Jurnal CKPN sudah dibuat. Status: ${data.statusCkpn || "-"}`
      : "✕ Jurnal CKPN belum selesai.";

    $("#checkCkpn")
      .removeClass("alert-success alert-danger")
      .addClass(ckpnOk ? "alert-success" : "alert-danger")
      .text(ckpnText);

    // Saldo Cadangan
    $("#totalCadanganGadai").text(
      "Rp " + formatRupiah(data.totalCadanganGadai),
    );

    $("#saldoCadanganGl").text("Rp " + formatRupiah(data.saldoCadanganGl));

    // Antrean Gadai
    let gadaiOk = false;
    let gadaiText = "";

    if (data.pendingGadaiCount == null) {
      gadaiText = "✕ Antrean Gadai belum dikonfirmasi oleh Finance.";
    } else if (data.pendingGadaiCount === 0) {
      gadaiOk = true;
      gadaiText = "✓ Tidak ada transaksi Gadai yang tertunda.";
    } else {
      gadaiText = `✕ Masih ada ${data.pendingGadaiCount} transaksi Gadai yang belum dibukukan.`;
    }

    $("#checkGadai")
      .removeClass("alert-success alert-danger")
      .addClass(gadaiOk ? "alert-success" : "alert-danger")
      .text(gadaiText);

    $("#pendingCount").val(
      data.pendingGadaiCount == null ? "" : data.pendingGadaiCount,
    );

    $("#pendingNote").val(data.pendingGadaiNote || "");

    // Daftar error validasi
    const $errorList = $("#errorList").empty();

    (data.errors || []).forEach(function (error) {
      $("<li>").text(error).appendTo($errorList);
    });

    $("#errorPanel").toggle(
      Array.isArray(data.errors) && data.errors.length > 0,
    );

    // Status yang masih boleh diproses Finance
    const canFinanceAction = status === "TERBUKA" || status === "DITOLAK";

    // Tampilkan panel sesuai status
    $("#gadaiPanel").toggle(canFinanceAction);
    $("#submitPanel").toggle(canFinanceAction);

    // Tombol pengajuan hanya aktif jika seluruh validasi lolos
    const canSubmit = canFinanceAction && data.canSubmit === true;

    $("#btnSubmit").prop("disabled", !canSubmit);

    // Pesan status proses
    $("#statusMessage")
      .hide()
      .removeClass("alert-info alert-success alert-danger alert-warning");

    if (status === "MENUNGGU_PERSETUJUAN") {
      $("#statusMessage")
        .addClass("alert-warning")
        .text("Periode sudah diajukan dan sedang menunggu persetujuan Checker.")
        .show();
    } else if (status === "DITUTUP") {
      $("#statusMessage")
        .addClass("alert-success")
        .text("Periode sudah ditutup dan tidak dapat diproses kembali.")
        .show();
    } else if (status === "DITOLAK") {
      $("#statusMessage")
        .addClass("alert-danger")
        .text(
          "Pengajuan tutup buku ditolak. Silakan periksa dan ajukan kembali.",
        )
        .show();
    }

    // Panel hasil validasi
    $("#successPanel").toggle(canSubmit);

    // Tampilkan detail pemeriksaan
    $("#checkPanel").show();
  }

  // function checkPeriod2(params) {
  //   resetDetail();

  //   $.ajax({
  //     url: urls.check,
  //     type: "GET",
  //     data: params,
  //     dataType: "json",

  //     success: function (response) {
  //       renderCheck(response);
  //     },

  //     error: function (xhr) {
  //       showMessage(
  //         xhr.responseJSON?.message || "Pemeriksaan periode gagal.",
  //         "danger",
  //       );
  //     },
  //   });
  // }

  function checkPeriod(params) {
    resetDetail();

    // Pertahankan periode yang sedang dipilih
    selectedPeriod = params;

    $.ajax({
      url: urls.check,
      type: "GET",
      data: params,
      success: renderCheck,
      error: function (xhr) {
        showMessage(
          xhr.responseJSON?.message || "Pemeriksaan periode gagal.",
          "danger",
        );
      },
    });
  }

  // Konfirmasi pending Gadai
  $("#formGadai").on("submit", function (e) {
    e.preventDefault();

    if (!selectedPeriod) {
      showMessage("Pilih periode terlebih dahulu.", "warning");
      return;
    }

    if (!this.reportValidity()) {
      return;
    }

    const params = {
      ...selectedPeriod,
      pendingCount: $("#pendingCount").val(),
      note: $("#pendingNote").val(),
    };

    $.ajax({
      url: urls.confirm,
      type: "POST",
      data: params,
      dataType: "json",

      success: function () {
        showMessage("Konfirmasi transaksi Gadai berhasil disimpan.");

         checkPeriod(selectedPeriod);
         loadPeriods(true);
      },

      error: function (xhr) {
        showMessage(
          xhr.responseJSON?.message || "Konfirmasi transaksi Gadai gagal.",
          "danger",
        );
      },
    });
  });

  // Ajukan tutup buku
  $("#btnSubmit").on("click", function () {
    if (!selectedPeriod || !currentCheck || !currentCheck.canSubmit) {
      showMessage("Persyaratan tutup buku belum terpenuhi.", "warning");
      return;
    }

    if (!confirm("Ajukan tutup buku periode ini kepada checker?")) {
      return;
    }

    const $button = $(this);

    $button.prop("disabled", true);

    $.ajax({
      url: urls.submit,
      type: "POST",
      data: selectedPeriod,
      dataType: "json",

      success: function () {
        showMessage("Pengajuan tutup buku berhasil dikirim kepada checker.");

        resetDetail();
        loadPeriods();
      },

      error: function (xhr) {
        showMessage(
          xhr.responseJSON?.message || "Pengajuan tutup buku gagal.",
          "danger",
        );

        // $button.prop("disabled", !currentCheck.canSubmit);
        $button.prop(
          "disabled",
          !currentCheck || currentCheck.canSubmit !== true,
        );
      },
    });
  });
});
