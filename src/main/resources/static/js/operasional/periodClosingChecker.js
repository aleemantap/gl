$(document).ready(function () {

  $("#cabangId").select2({
    theme: "bootstrap-5",
    width: "100%",
    allowClear: false,
    // dropdownParent: $("#modal1"),
  });
   const $page = $("#periodClosingCheckerPage");

   let selectedCheckerPeriod = null;

   const urls = {
     list: $page.data("list-url"),
     check: $page.data("check-url"),
     approve: $page.data("approve-url"),
     reject: $page.data("reject-url"),
   };
   //console.log(urls);
   const $cabangId = $("#cabangId");
   const $checkerBody = $("#checkerBody");
   function formatRupiah(value) {
     return new Intl.NumberFormat("id-ID", {
       style: "currency",
       currency: "IDR",
       minimumFractionDigits: 2,
     }).format(value || 0);
   }

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

   function showMessage(message, type = "info") {
     $("#messagePanel")
       .removeClass("alert-info alert-success alert-danger alert-warning")
       .addClass("alert-" + type)
       .text(message)
       .show();
   }

   function renderTable(data) {
     $checkerBody.empty();

     if (!data || data.length === 0) {
       $checkerBody.html(`
                <tr>
                    <td colspan="9" class="text-center text-muted">
                        Tidak ada pengajuan yang menunggu persetujuan.
                    </td>
                </tr>
            `);
       return;
     }

     data.forEach(function (item, index) {
       const periode = new Date(
         item.tahun,
         item.bulan - 1,
         1,
       ).toLocaleDateString("id-ID", {
         month: "long",
         year: "numeric",
       });

       const neraca =
         item.neracaSeimbang === true
           ? '<span class="badge bg-success text-white">Seimbang</span>'
           : '<span class="badge bg-danger text-white">Tidak Seimbang</span>';

       const pendingGadai = item.pendingGadaiCount ?? 0;

       $checkerBody.append(`
                <tr>
                    <td>${index + 1}</td>
                    <td>${periode}</td>
                    <td>${item.submittedBy || "-"}</td>
                    <td>${formatDateTime(item.submittedAt)}</td>
                    <td class="text-end">${formatRupiah(item.totalDebit)}</td>
                    <td class="text-end">${formatRupiah(item.totalKredit)}</td>
                    <td>${neraca}</td>
                    <td class="text-center">${pendingGadai}</td>
                    <td>
                        <button type="button"
                                class="btn btn-sm btn-primary btn-detail"
                                data-tahun="${item.tahun}"
                                data-bulan="${item.bulan}"
                                data-cabang-id="${item.cabangId}">
                            Detail
                        </button>
                    </td>
                </tr>
            `);
     });
   }

   function loadPeriods() {
     const cabangId = $cabangId.val();

     if (!cabangId) {
       showMessage("Silakan pilih cabang terlebih dahulu.", "warning");
       return;
     }

     $checkerBody.html(`
            <tr>
                <td colspan="9" class="text-center">
                    Memuat data...
                </td>
            </tr>
        `);

     $("#messagePanel").hide();

     $.ajax({
       url: urls.list,
       type: "GET",
       data: {
         cabangId: cabangId,
       },
       success: function (response) {
         renderTable(response);
       },
       error: function (xhr) {
         console.error(xhr.responseText);

         $checkerBody.html(`
                    <tr>
                        <td colspan="9" class="text-center text-danger">
                            Gagal mengambil data pengajuan.
                        </td>
                    </tr>
                `);

         showMessage(
           xhr.responseJSON?.message ||
             "Terjadi kesalahan saat mengambil data.",
           "danger",
         );
       },
     });
   }

   $("#btnLoadPeriods").on("click", function () {
     loadPeriods();
   });

   
function renderCheckerDetail(data) {
  const monthNames = [
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

  const statusClass = {
    TERBUKA: "bg-secondary",
    MENUNGGU_PERSETUJUAN: "bg-warning",
    DITUTUP: "bg-success",
    DITOLAK: "bg-danger",
  };

  const status = data.status || "-";

  $("#detailPeriod").text(monthNames[data.bulan - 1] + " " + data.tahun);

  $("#detailStatus").html(
    `<span class="text-white badge ${statusClass[status] || "bg-secondary"}">
            ${status}
        </span>`,
  );

  $("#detailSubmittedBy").text(data.submittedBy || "-");
  $("#detailSubmittedAt").text(formatDateTime(data.submittedAt));
  $("#detailCabang").text(
    data.namaCabang || data.kodeCabang || data.cabangId || "-",
  );

  $("#detailDebit").text(formatRupiah(data.totalDebit));
  $("#detailKredit").text(formatRupiah(data.totalKredit));

  $("#detailNeraca").html(
    data.neracaSeimbang === true
      ? '<span class="badge bg-success text-white">Seimbang</span>'
      : '<span class="badge bg-danger text-white">Tidak Seimbang</span>',
  );

  $("#detailCkpn").html(
    data.ckpnCreated === true
      ? '<span class="badge bg-success text-white">Sudah Dibuat</span>'
      : '<span class="badge bg-danger text-white">Belum Dibuat</span>',
  );

  $("#detailStatusCkpn").text(data.statusCkpn || "-");
  $("#detailCadanganGadai").text(formatRupiah(data.totalCadanganGadai));

  $("#detailPendingGadai").text(data.pendingGadaiCount ?? "Belum dikonfirmasi");

  $("#detailPendingNote").text(data.pendingGadaiNote || "-");

  const errors = data.errors || [];
  $("#detailErrorList").empty();

  if (errors.length > 0) {
    errors.forEach(function (error) {
      $("#detailErrorList").append($("<li>").text(error));
    });

    $("#detailErrorPanel").show();
  } else {
    $("#detailErrorPanel").hide();
  }

  $("#checkerDetailPanel").show();

  $("html, body").animate(
    {
      scrollTop: $("#checkerDetailPanel").offset().top - 80,
    },
    300,
  );
  const isWaiting = data.status === "MENUNGGU_PERSETUJUAN";

  $("#checkerActionPanel").toggle(isWaiting);
}

function loadCheckerDetail(tahun, bulan, cabangId) {
  $("#checkerDetailPanel").hide();

  $.ajax({
    url: urls.check,
    type: "GET",
    data: {
      tahun: tahun,
      bulan: bulan,
      cabangId: cabangId,
    },
    success: function (response) {
      renderCheckerDetail(response);
    },
    error: function (xhr) {
      showMessage(
        xhr.responseJSON?.message || "Gagal mengambil detail pengajuan.",
        "danger",
      );
    },
  });
}

// Klik tombol Detail
// $(document).on("click", ".btn-detail", function () {
//   const tahun = $(this).data("tahun");
//   const bulan = $(this).data("bulan");
//   const cabangId = $(this).data("cabang-id");

//   loadCheckerDetail(tahun, bulan, cabangId);
// });
$(document).on("click", ".btn-detail", function () {
  const tahun = $(this).data("tahun");
  const bulan = $(this).data("bulan");
  const cabangId = $(this).data("cabang-id");

  selectedCheckerPeriod = {
    tahun: tahun,
    bulan: bulan,
    cabangId: cabangId,
  };

  loadCheckerDetail(tahun, bulan, cabangId);
});

$("#btnApprove").on("click", function () {
  if (!selectedCheckerPeriod) {
    showMessage("Silakan pilih pengajuan terlebih dahulu.", "warning");
    return;
  }

//   if (!confirm("Apakah Anda yakin menyetujui tutup buku periode ini?")) {
//     return;
//   }

//   $.ajax({
//     url: urls.approve,
//     type: "POST",
//     data: selectedCheckerPeriod,

//     success: function (response) {
//       showMessage(
//         response.message || "Tutup buku berhasil disetujui.",
//         "success",
//       );

//       $("#checkerDetailPanel").hide();

//       loadPeriods();
//     },

//     error: function (xhr) {
//       showMessage(
//         xhr.responseJSON?.message || "Gagal menyetujui tutup buku.",
//         "danger",
//       );
//     },
//   });
Swal.fire({
  title: "Konfirmasi",
  text: "Apakah Anda yakin menyetujui tutup buku periode ini?",
  icon: "question",
  showCancelButton: true,
  confirmButtonText: "Ya, Setujui",
  cancelButtonText: "Batal",
  confirmButtonColor: "#198754", // hijau (Bootstrap success)
  cancelButtonColor: "#6c757d", // abu-abu (Bootstrap secondary)
  reverseButtons: true,
}).then((result) => {
  if (!result.isConfirmed) {
    return;
  }

  $.ajax({
    url: urls.approve,
    type: "POST",
    data: selectedCheckerPeriod,

    success: function (response) {
      showMessage(
        response.message || "Tutup buku berhasil disetujui.",
        "success",
      );

      $("#checkerDetailPanel").hide();

      loadPeriods();
    },

    error: function (xhr) {
      showMessage(
        xhr.responseJSON?.message || "Gagal menyetujui tutup buku.",
        "danger",
      );
    },
  });
});
});

$("#btnReject").on("click", function () {
  $("#rejectionReason").val("");

  const modal = new bootstrap.Modal(document.getElementById("modalReject"));

  modal.show();
});

$("#btnConfirmReject").on("click", function () {
  if (!selectedCheckerPeriod) {
    showMessage("Silakan pilih pengajuan terlebih dahulu.", "warning");
    return;
  }

  const reason = $("#rejectionReason").val().trim();

  if (!reason) {
    alert("Alasan penolakan wajib diisi.");
    return;
  }

  $.ajax({
    url: urls.reject,
    type: "POST",
    data: {
      ...selectedCheckerPeriod,
      rejectionReason: reason,
    },

    success: function (response) {
      bootstrap.Modal.getInstance(
        document.getElementById("modalReject"),
      ).hide();

      showMessage(response.message || "Pengajuan berhasil ditolak.", "success");

      $("#checkerDetailPanel").hide();

      loadPeriods();
    },

    error: function (xhr) {
      showMessage(
        xhr.responseJSON?.message || "Gagal menolak pengajuan.",
        "danger",
      );
    },
  });
});

});