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
  const checkUrl = "/operasional/period-closing/check";
  const confirmUrl = "/operasional/period-closing/confirm-pending-gadai";
  const submitUrl = "/operasional/period-closing/submit";

  let currentCheck = null;

  // CSRF Spring Security
  //   const csrfToken = $('meta[name="_csrf"]').attr("content");
  //   const csrfHeader = $('meta[name="_csrf_header"]').attr("content");

  //   $.ajaxSetup({
  //     beforeSend: function (xhr, settings) {
  //       if (
  //         csrfToken &&
  //         csrfHeader &&
  //         !/^(GET|HEAD|OPTIONS|TRACE)$/i.test(settings.type)
  //       ) {
  //         xhr.setRequestHeader(csrfHeader, csrfToken);
  //       }
  //     },
  //   });

  // Format angka
  function formatRupiah(value) {
    return new Intl.NumberFormat("id-ID", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(value || 0);
  }

  // Parameter periode
  function getPeriodParams() {
    return {
      tahun: $("#tahun").val(),
      bulan: $("#bulan").val(),
      cabangId: $("#cabangId").val(),
    };
  }

  // Pesan notifikasi
  function showMessage(message, type = "success") {
    $("#messagePanel").html(
      `<div class="alert alert-${type}">${message}</div>`,
    );
  }

  // Reset hasil pemeriksaan
  function resetCheck() {
    currentCheck = null;

    $("#checkPanel").hide();
    $("#gadaiPanel").hide();
    $("#submitPanel").hide();

    $("#btnSubmit").prop("disabled", true);
    $("#messagePanel").empty();
  }

  // Tampilkan hasil pemeriksaan
  function renderCheck(data) {
    currentCheck = data;

    $("#checkPanel").show();
    $("#gadaiPanel").show();
    $("#submitPanel").show();

    $("#namaCabang").text(`${data.kodeCabang} - ${data.namaCabang}`);

    $("#periode").text(`${data.bulan}/${data.tahun}`);

    $("#totalDebit").text(formatRupiah(data.totalDebit));
    $("#totalKredit").text(formatRupiah(data.totalKredit));

    $("#statusNeraca").text(
      data.neracaSeimbang ? "Seimbang" : "Tidak Seimbang",
    );

    $("#statusCkpnText").text(data.statusCkpn || "-");

    $("#statusCkpn").text(data.ckpnCreated ? "Selesai" : "Belum Selesai");

    if (data.pendingGadaiCount === null) {
      $("#pendingGadai").text("Belum dikonfirmasi");
      $("#statusGadai").text("Belum Dikonfirmasi");
    } else {
      $("#pendingGadai").text(`${data.pendingGadaiCount} transaksi`);

      $("#statusGadai").text(
        data.pendingGadaiCount === 0 ? "Selesai" : "Belum Selesai",
      );
    }

    // Daftar error pemeriksaan
    const $errorList = $("#errorList").empty();

    if (data.errors && data.errors.length > 0) {
      data.errors.forEach(function (error) {
        $("<li>").text(error).appendTo($errorList);
      });

      $("#errorPanel").show();
    } else {
      $("#errorPanel").hide();
    }

    $("#successPanel").toggle(!!data.canSubmit);

    $("#btnSubmit").prop("disabled", !data.canSubmit);
  }

  // Pemeriksaan periode
  $("#formCheck").on("submit", function (e) {
    e.preventDefault();

    if (!this.reportValidity()) {
      return;
    }

    resetCheck();

    $.ajax({
      url: checkUrl,
      type: "GET",
      data: getPeriodParams(),
      dataType: "json",

      success: function (response) {
        renderCheck(response);
      },

      error: function (xhr) {
        const message =
          xhr.responseJSON?.message || "Pemeriksaan periode gagal dilakukan.";

        showMessage(message, "danger");
      },
    });
  });

  // Konfirmasi pending Gadai
  $("#formGadai").on("submit", function (e) {
    e.preventDefault();

    if (!this.reportValidity()) {
      return;
    }

    const params = getPeriodParams();

    params.pendingCount = $("#pendingCount").val();
    params.note = $("#pendingNote").val();

    $.ajax({
      url: confirmUrl,
      type: "POST",
      data: params,
      dataType: "json",

      success: function () {
        showMessage("Konfirmasi transaksi Gadai berhasil disimpan.");

        // Periksa ulang setelah konfirmasi
        $("#formCheck").trigger("submit");
      },

      error: function (xhr) {
        const message =
          xhr.responseJSON?.message ||
          "Konfirmasi transaksi Gadai gagal disimpan.";

        showMessage(message, "danger");
      },
    });
  });

  // Ajukan tutup buku
  $("#btnSubmit").on("click", function () {
    if (!currentCheck || !currentCheck.canSubmit) {
      showMessage("Persyaratan tutup buku belum terpenuhi.", "warning");
      return;
    }

    if (
      !confirm("Apakah Anda yakin ingin mengajukan tutup buku periode ini?")
    ) {
      return;
    }

    const $button = $(this);

    $button.prop("disabled", true);

    $.ajax({
      url: submitUrl,
      type: "POST",
      data: getPeriodParams(),
      dataType: "json",

      success: function () {
        showMessage("Pengajuan tutup buku berhasil dikirim kepada checker.");

        // Setelah berhasil diajukan, tombol tetap nonaktif.
        $button.prop("disabled", true);
      },

      error: function (xhr) {
        const message =
          xhr.responseJSON?.message || "Pengajuan tutup buku gagal dilakukan.";

        showMessage(message, "danger");

        // Aktifkan kembali jika pengajuan gagal
        $button.prop("disabled", !currentCheck.canSubmit);
      },
    });
  });

  // Reset hasil jika periode atau cabang diganti
  $("#tahun, #bulan, #cabangId").on("change", function () {
    resetCheck();
  });
});
