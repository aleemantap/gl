$(document).ready(function () {
  // const table = $("#neracaSaldoTable").DataTable({
  //   // processing: true,
  //   // serverSide: true,
  //   processing: true,
  //   serverSide: true,
  //   searching: false,
  //   deferLoading: 0,
  //   paging: true,
  //   info: true,
  //   scrollX: true,
  //   pageLength: 10,
  //   lengthChange: false,
  //   language: {
  //     emptyTable: "Silakan gunakan filter terlebih dahulu",
  //   },
  //   ajax: {
  //     url: "/laporan/neraca-saldo/datatables",
  //     type: "GET",
  //     data: function (d) {
  //       d.tanggalDari = $("#tanggalDari").val();
  //       d.tanggalSampai = $("#tanggalSampai").val();
  //       d.branchCode = $("#branchCode").val();
  //     },
  //   },
  //   columns: [
  //     {
  //       data: "accountCode",
  //     },

  //     {
  //       data: "accountName",
  //     },
  //     {
  //       data: "normalBalance",
  //       className: "text-center",
  //     },

  //     {
  //       data: "saldoAwal",
  //       className: "text-end",
  //       render: formatNumber,
  //     },

  //     {
  //       data: "totalDebit",
  //       className: "text-end",
  //       render: formatNumber,
  //     },

  //     {
  //       data: "totalKredit",
  //       className: "text-end",
  //       render: formatNumber,
  //     },

  //     {
  //       data: "saldoAkhirDebit",
  //       className: "text-end",
  //       render: formatNumber,
  //     },

  //     {
  //       data: "saldoAkhirKredit",
  //       className: "text-end",
  //       render: formatNumber,
  //     },
  //   ],

  //   order: [[0, "asc"]],
  //   pageLength: 10,
  //   lengthMenu: [
  //     [10, 25, 50, 100],
  //     [10, 25, 50, 100],
  //   ],

  //   footerCallback: function (row, data, start, end, display) {
  //     let totalSaldoAwal = 0;
  //     let totalDebit = 0;
  //     let totalKredit = 0;
  //     let totalSaldoDebit = 0;
  //     let totalSaldoKredit = 0;

  //     data.forEach(function (item) {
  //       totalSaldoAwal += parseFloat(item.saldoAwal || 0);

  //       totalDebit += parseFloat(item.totalDebit || 0);

  //       totalKredit += parseFloat(item.totalKredit || 0);

  //       totalSaldoDebit += parseFloat(item.saldoAkhirDebit || 0);

  //       totalSaldoKredit += parseFloat(item.saldoAkhirKredit || 0);
  //     });

  //     // Kolom 3 = Saldo Awal
  //     //$('th', this.api().table().footer()) .eq(3).html(formatNumber(totalSaldoAwal));
  //     $(table.column(3).footer()).html(formatNumber(totalSaldoAwal));

  //     $(table.column(4).footer()).html(formatNumber(totalDebit));

  //     $(table.column(5).footer()).html(formatNumber(totalKredit));

  //     $(table.column(6).footer()).html(formatNumber(totalSaldoDebit));

  //     $(table.column(7).footer()).html(formatNumber(totalSaldoKredit));
  //   },
  // });

  // // =====================================
  // // FILTER
  // // =====================================

  // $("#btnFilter").on("click", function () {
  //   table.ajax.reload();
  // });

   $("#branchCode").select2({
     theme: "bootstrap-5",
     width: "100%",
     allowClear: false,
     // dropdownParent: $("#modal1"),
   });

     let table = null;

     $("#btnFilter").on("click", function () {
       // Kalau DataTable belum dibuat
       if (table === null) {
         table = $("#neracaSaldoTable").DataTable({
           processing: true,
           serverSide: true,
           searching: false,
           paging: true,
           info: true,
           scrollX: true,
           pageLength: 10,
           lengthChange: false,

           ajax: {
             url: "/laporan/neraca-saldo/datatables",
             type: "GET",
             data: function (d) {
               d.tanggalDari = $("#tanggalDari").val();
               d.tanggalSampai = $("#tanggalSampai").val();
               d.branchCode = $("#branchCode").val();
             },
           },

           columns: [
             {
               data: null,
               orderable: false,
               searchable: false,
               className: "text-center align-middle",
               render: function (data, type, row) {
                 return `
                  <input type="checkbox" class="row-checkbox form-check-input m-0 align-middle" value="${row.glAccountId}">
              `;
               },
             },
             {
               data: "accountCode",
             },
             {
               data: "accountName",
             },
             {
               data: "normalBalance",
               className: "text-center",
             },
             {
               data: "saldoAwal",
               className: "text-end",
               render: formatNumber,
             },
             {
               data: "totalDebit",
               className: "text-end",
               render: formatNumber,
             },
             {
               data: "totalKredit",
               className: "text-end",
               render: formatNumber,
             },
             {
               data: "saldoAkhirDebit",
               className: "text-end",
               render: formatNumber,
             },
             {
               data: "saldoAkhirKredit",
               className: "text-end",
               render: formatNumber,
             },
           ],

           order: [[0, "asc"]],

           language: {
             emptyTable: "Tidak ada data",
           },

           drawCallback: function () {
             // calculateAmount();
             // Uncheck "Select All" setiap kali halaman berganti / redraw
             $("#check-all").prop("checked", false);
           },

           footerCallback: function (row, data, start, end, display) {
             let totalSaldoAwal = 0;
             let totalDebit = 0;
             let totalKredit = 0;
             let totalSaldoDebit = 0;
             let totalSaldoKredit = 0;

             data.forEach(function (item) {
               totalSaldoAwal += parseFloat(item.saldoAwal || 0);
               totalDebit += parseFloat(item.totalDebit || 0);
               totalKredit += parseFloat(item.totalKredit || 0);
               totalSaldoDebit += parseFloat(item.saldoAkhirDebit || 0);
               totalSaldoKredit += parseFloat(item.saldoAkhirKredit || 0);
             });

             $(table.column(3).footer()).html(formatNumber(totalSaldoAwal));

             $(table.column(4).footer()).html(formatNumber(totalDebit));

             $(table.column(5).footer()).html(formatNumber(totalKredit));

             $(table.column(6).footer()).html(formatNumber(totalSaldoDebit));

             $(table.column(7).footer()).html(formatNumber(totalSaldoKredit));
           },
         });
       } else {
         // Kalau DataTable sudah ada,
         // cukup reload dengan filter terbaru
         table.ajax.reload();
       }
     });


  


  // =====================================
  // FORMAT ANGKA
  // =====================================

  function formatNumber(data) {
    if (data === null || data === undefined || data === "") {
      return "0,00";
    }

    return new Intl.NumberFormat("id-ID", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(data);
  }


  // =====================================
  // SUBMIT VALIDASI
  // =====================================

  $("#btnSubmitValidasi").on("click", function () {
    const tanggalSampai = $("#tanggalSampai").val();
    const branchCode = $("#branchCode").val();
    // =================================
    // VALIDASI INPUT
    // =================================
    if (!tanggalSampai) {
      // alert(
      //     "Tanggal sampai harus diisi."
      // );
      showToast("error", "Tanggal sampai harus diisi.");
      return;
    }
    if (!branchCode) {
      // alert(
      //     "Silakan pilih cabang terlebih dahulu."
      // );
      showToast("error", "Silakan pilih cabang terlebih dahulu");
      return;
    }

    // =================================
    // KONFIRMASI
    // =================================

    const confirmSubmit = confirm(
      "Kirim Neraca Saldo periode " + tanggalSampai + " untuk validasi?",
    );

    if (!confirmSubmit) {
      return;
    }

    // =================================
    // DISABLE BUTTON
    // =================================

    const button = $(this);
    button.prop("disabled", true);
    const periode = tanggalSampai.substring(0, 7) + "-01";

    // =================================
    // AJAX
    // =================================

    $.ajax({
      url: "/laporan/neraca-saldo/submit",
      type: "POST",
      data: {
        periode: periode,
        branchCode: branchCode,
      },
      success: function (response) {
        if (response.success) {
          // alert(
          //     response.message
          // );
          showToast("success", response.message);
          console.info("Submission ID:", response.submissionId);
        } else {
          alert(response.message);
        }
      },

      error: function (xhr) {
        let message = "Terjadi kesalahan saat mengirim Neraca Saldo.";

        if (xhr.responseJSON && xhr.responseJSON.message) {
          message = xhr.responseJSON.message;
        }

        //alert(message);
        showToast("error", message);
      },

      complete: function () {
        button.prop("disabled", false);
      },
    });

    //
  });

 $("#btn-export-pdf").on("click", function () {
   var tanggalDari = $("#tanggalDari").val() || "";
   var tanggalSampai = $("#tanggalSampai").val() || "";
   var branchCode = $("#branchCode").val() || "";

   // Susun URL Query
   var params = $.param({
     tanggalDari: tanggalDari,
     tanggalSampai: tanggalSampai,
     branchCode: branchCode,
   });

   // Trigger download PDF
   window.location.href = "/laporan/neraca-saldo/export-pdf?" + params;
 });

  $("#btn-export-exc").click(function () {
      const params = new URLSearchParams();
      // Ambil semua nilai input
      var tanggalDari = $("#tanggalDari").val() || "";
      var tanggalSampai = $("#tanggalSampai").val() || "";
      var branchCode = $("#branchCode").val() || "";
      const selectedIds = getSelectedIds();
      if (selectedIds && selectedIds.length > 0) {
        params.append("ids", JSON.stringify(selectedIds));
      }
      // Append hanya jika value tidak kosong
      if (tanggalDari) params.append("tanggalDari", tanggalDari);
      if (tanggalSampai) params.append("tanggalSampai", tanggalSampai);
      if (branchCode) params.append("branchCode", branchCode);       
      const baseUrl = "/laporan/neraca-saldo/export-excel";
      const fullUrl = params.toString()
        ? `${baseUrl}?${params.toString()}`
        : baseUrl;
      window.location.href = fullUrl;
  });

    // 1. Event Centang Semua (Select All)
  $("#check-all").on("click", function () {
    const isChecked = $(this).is(":checked");
    $(".row-checkbox").prop("checked", isChecked);
  });

  // 2. Event Uncheck "Select All" jika salah satu checkbox baris di-uncheck
  $("#neracaSaldoTable tbody").on("click", ".row-checkbox", function () {
    if (!$(this).is(":checked")) {
      $("#check-all").prop("checked", false);
    }
  });

  // 3. Fungsi Helper untuk Mengambil Nilai ID Baris yang Dicentang
  function getSelectedIds() {
    const selectedIds = [];
    $(".row-checkbox:checked").each(function () {
      selectedIds.push($(this).val());
    });
    return selectedIds; // Mengembalikan array ID yang dipilih, misal: ["12", "15"]
  }

});



