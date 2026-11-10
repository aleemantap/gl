$(document).ready(function () {
  const container = $("#tutupHarianContainer");

  const tanggal = container.data("tanggal");
  const cabangId = container.data("cabang-id");

  const baseUrl = "/operasional/tutup-harian";

  loadPemeriksaan();

  // =========================
  // LOAD PEMERIKSAAN
  // =========================
  function loadPemeriksaan() {
    $.ajax({
      url: baseUrl + "/data",
      type: "GET",
      data: {
        tanggal: tanggal,
        cabangId: cabangId,
      },
      success: function (response) {
        $("#totalAntrean").text(response.totalAntrean + " transaksi");

        $("#totalRejected").text(response.totalRejected + " transaksi");

        $("#hasilPerbandinganKas").text(response.hasilPerbandinganKas);

        setStatus("#statusAntrean", response.statusAntrean);
        setStatus("#statusRejected", response.statusRejected);
        setStatus("#statusKas", response.statusKas);

        updateButton(response.statusClosing);
      },
      error: function (xhr) {
        console.error(xhr.responseText);

        Swal.fire("Error", "Gagal mengambil data pemeriksaan.", "error");
      },
    });
  }

  // =========================
  // STATUS BADGE
  // =========================
  function setStatus(element, status) {
    let badge = "";

    if (status === "OK") {
      badge = '<span class="badge bg-success text-white">OK</span>';
    } else if (status === "TIDAK_OK") {
      badge = '<span class="badge bg-danger text-white">TIDAK OK</span>';
    } else {
      badge = '<span class="badge bg-secondary text-white">-</span>';
    }

    $(element).html(badge);
  }

  // =========================
  // BUTTON STATE
  // =========================
//   function updateButton(status) {
//     if (status === "DITUTUP") {
//       $("#btnTutupHarian").prop("disabled", true).text("Hari Sudah Ditutup");
//     } else {
//       $("#btnTutupHarian").prop("disabled", false).text("Tutup Hari Ini");
//     }
//   }

  
function updateButton(status) {
  const badge = $("#statusClosing");

  if (status === "DITUTUP") {
    badge.removeClass("bg-success").addClass("bg-danger text-white").text("DITUTUP");

    $("#btnTutupHarian").prop("disabled", true).text("Hari Sudah Ditutup");
  } else {
    badge
      .removeClass("bg-danger")
      .addClass("bg-success text-white")
      .text("TERBUKA");

    $("#btnTutupHarian").prop("disabled", false).text("Tutup Hari Ini");
  }

  if (status === "DITUTUP") {
    $("#btnReopenHarian").show();
  } else {
    $("#btnReopenHarian").hide();
  }
}


  // =========================
  // TUTUP HARIAN
  // =========================
  $("#btnTutupHarian").on("click", function () {
    Swal.fire({
      title: "Tutup Hari Ini?",
      text: "Tanggal ini akan dikunci untuk transaksi cabang.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Ya, Tutup Hari Ini",
      cancelButtonText: "Batal",
    }).then((result) => {
      if (!result.isConfirmed) return;

      $.ajax({
        url: baseUrl + "/close",
        type: "POST",
        data: {
          tanggal: tanggal,
          cabangId: cabangId,
        },
        success: function (response) {

            // if (!response.success) {
            //     Swal.fire("Informasi", response.message, "warning");
            //     return;
            // }
            // Swal.fire("Berhasil", response.message, "success");
            // loadPemeriksaan();

            if (!response.success) {
              Swal.fire("Informasi", response.message, "warning");
              return;
            }

            Swal.fire("Berhasil", response.message, "success").then(() => {
              loadPemeriksaan();
            });

        },
        error: function (xhr) {
          const message =
            xhr.responseJSON?.message || "Gagal melakukan tutup harian.";

          Swal.fire("Gagal", message, "error");
        },
      });
    });
  });

  $("#btnReopenHarian").on("click", function () {
    $("#reopenReason").val("");
    $("#modalReopen").modal("show");
  });

  $("#btnSubmitReopen").on("click", function () {
    const reason = $("#reopenReason").val().trim();

    if (!reason) {
      Swal.fire("Perhatian", "Alasan wajib diisi.", "warning");
      return;
    }

    $.ajax({
      url: "/operasional/tutup-harian/reopen",
      type: "POST",
      data: {
        tanggal: $("#tutupHarianContainer").data("tanggal"),
        cabangId: $("#tutupHarianContainer").data("cabang-id"),
        reason: reason,
      },
      success: function (response) {
        if (!response.success) {
          Swal.fire("Gagal", response.message, "warning");
          return;
        }

        $("#modalReopen").modal("hide");

        Swal.fire("Berhasil", response.message, "success").then(() =>
          loadPemeriksaan(),
        );
      },
      error: function (xhr) {
        Swal.fire("Error", "Gagal membuka kembali tanggal.", "error");
      },
    });
  });

});
