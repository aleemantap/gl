$(document).ready(function () {
  // ==================== FUNGSI LOAD DATA ====================
   $("#cabangTab1").select2({
     theme: "bootstrap-5",
     width: "24%",
     allowClear: false,
    
   });
   kodeTransaksi;
    $("#cabangId,#kodeTransaksi").select2({
      theme: "bootstrap-5",
      width: "100%",
      allowClear: false,
      dropdownParent: $("#modal1"),
    });
  function jurnalCode(id) {
    $.ajax({
      url: "/parameter/pemetaan-akun/journal-code/list",
      type: "GET",
      dataType: "json",
      success: function (response) {
        console.log("Journal Code Response:", response);
        var select = $(id);
        select.empty();
        select.append('<option value="">Pilih Journal Code</option>');

        if (response && response.length > 0) {
          $.each(response, function (index, item) {
            select.append(
              '<option value="' +
                item.id +
                '">' +
                item.journalCode +
                " - " +
                (item.description ?? "") +
                "</option>",
            );
          });
        }
      },
      error: function (xhr, status, error) {
        console.error("Error loading journal code:", error);
        showToast("error", "Gagal memuat data journal code");
      },
    });
  }

  // function loadCabang(id) {
  //   $.ajax({
  //     url: "/cabang/list",
  //     type: "GET",
  //     dataType: "json",
  //     success: function (response) {
  //       var select = $(id);
  //       select.empty();
  //       select.append('<option value="">Pilih Cabang</option>');
  //       if (response.content && response.content.length > 0) {
  //         $.each(response.content, function (index, item) {
  //           select.append(
  //             '<option value="' +
  //               item.id +
  //               '">' +
  //               item.namaCabang +
  //               "</option>",
  //           );
  //         });
  //       }
  //     },
  //     error: function (xhr, status, error) {
  //       console.error("Error loading cabang:", error);
  //       showToast("error", "Gagal memuat data cabang");
  //     },
  //   });
  // }

  // function loadKodeTC() {
  //   $.ajax({
  //     url: "/parameter/kode-transaksi/list",
  //     type: "GET",
  //     dataType: "json",
  //     success: function (response) {
  //       var select = $("#kodeTransaksi");
  //       select.empty();
  //       select.append('<option value="">Pilih Kode Transaksi</option>');

  //       var dataList = response.content || response.data || response;

  //       if (Array.isArray(dataList) && dataList.length > 0) {
  //         $.each(dataList, function (index, item) {
  //           select.append(
  //             '<option value="' +
  //               item.id +
  //               '">' +
  //               item.kode +
  //               " - " +
  //               item.namaTransaksi +
  //               "</option>",
  //           );
  //         });
  //       } else {
  //         console.warn("Data Kode Transaksi kosong");
  //       }
  //     },
  //     error: function (xhr, status, error) {
  //       console.error("Error loading Kode Transaksi:", error);
  //       showToast("error", "Gagal memuat data Kode Transaksi");
  //     },
  //   });
  // }

  // ==================== LOAD INITIAL DATA ====================
  // loadCabang("#cabangTab1");

  // ==================== EVENT TAMBAH DATA ====================
  $("#btn-tambah-tab1").on("click", function (e) {
    e.preventDefault();
    $("#form1")[0].reset();
    $("#jr_tk_id").val(""); // Reset hidden ID
    // loadCabang("#cabangId");
    jurnalCode("#journalCode");
    // loadKodeTC();

    $("#modalTitle1").text("Tambah Pemetaan TC");
    $("#btn-submit-tc1").text("Simpan");
    $("#modal1").modal("show");
  });

  // ==================== DATATABLE ====================
  const table2 = new DataTable("#table1", {
    processing: true,
    serverSide: true,
    searching: false,
    paging: true,
    info: true,
    scrollX: false,
    pageLength: 10,
    lengthChange: false,
    ajax: {
      url: "/parameter/pemetaan-akun/datatable-transaction-journal",
      type: "GET",
      data: function (d) {
        var cabangId = $("#cabangTab1").val();
        // Cek dan validasi cabangId
        if (
          cabangId === "NaN" ||
          cabangId === undefined ||
          cabangId === null ||
          cabangId === ""
        ) {
          cabangId = null;
        } else {
          cabangId = parseInt(cabangId);
          if (isNaN(cabangId)) {
            cabangId = null;
          }
        }

        return {
          draw: d.draw,
          start: d.start,
          length: 20,
          orderColumn: d.order[0] ? d.order[0].column : 1,
          orderDir: d.order[0] ? d.order[0].dir : "desc",
          cabangId: cabangId,
        };
      },
    },
    columns: [
      {
        data: null,
        defaultContent: "",
        render: function (data, type, row, meta) {
          return meta.row + 1 + (meta.settings._iDisplayStart || 0);
        },
        orderable: false,
      },
      { data: "kode", orderable: false, className: "text-start" },
      { data: "namaTransaksi", orderable: false, className: "text-start" },
      //{ data: "namaCabang", orderable: false, className: "text-start" },
      {
        data: "namaCabang",
        orderable: false,
        className: "text-start",

        render: function (data, type, row) {
          return `<span class="badge bg-secondary text-white">${row.kodeCabang} - ${row.namaCabang}</span>`;
        },
      },
      {
        data: "journalCode",
        orderable: false,
        className: "text-start",
        render: function (data, type, row) {
          return `<span class="badge bg-success text-white">${row.journalCode}</span>`;
        },
      },
      {
        data: null,
        orderable: false,
        className: "text-start",
        render: function (data, type, row) {
          return `
                        <div style="display: flex; gap: 15px;">
                            <a class="btn-delete1" href="#" data-id="${row.id}">
                                <img src="/img/delete.png" />
                            </a>
                            <a class="btn-ubah1" href="#" data-id="${row.id}">
                                <img src="/img/pencil.png" alt="Edit" />
                            </a>
                        </div>
                    `;
        },
      },
    ],
    initComplete: function () {
      $(".dataTables_paginate").css({
        float: "right",
        "text-align": "right",
      });
      $(".dataTables_info").css({
        float: "left",
        "padding-top": "0.755em",
      });
    },
  });

  // ==================== FUNGSI SUBMIT ====================
  $("#btnSubmit1").on("click", function (e) {
    e.preventDefault();

    var editId = $("#jr_tk_id").val();
    var url = "/parameter/pemetaan-akun/journal-trk/add";
    if (editId) {
      url = "/parameter/pemetaan-akun/journal-trk/update/" + editId;
    }

    // Hapus error sebelumnya
    $(".is-invalid").removeClass("is-invalid");
    $(".invalid-feedback").text("");

    // Validasi form
    var isValid = true;
    var kodeTransaksi = $("#kodeTransaksi").val();
    if (!kodeTransaksi) {
      $("#kodeTransaksi").addClass("is-invalid");
      $("#kodeTransaksiError").text("Kode Transaksi wajib diisi");
      isValid = false;
    }

    var cabangId = $("#cabangId").val();
    if (!cabangId) {
      $("#cabangId").addClass("is-invalid");
      $("#cabangError").text("Cabang wajib diisi");
      isValid = false;
    }

    var journalCode = $("#journalCode").val();
    if (!journalCode) {
      $("#journalCode").addClass("is-invalid");
      $("#journalIdError").text("Journal ID wajib diisi");
      isValid = false;
    }

    if (!isValid) {
      return;
    }

    // Disable button dan tampilkan loading
    $("#btnSubmit2").prop("disabled", true).html(`
            <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
            Menyimpan...
        `);

    // Siapkan data
    var formData = {
      kodeTransaksi: parseInt(kodeTransaksi),
      cabangId: parseInt(cabangId),
      journalId: parseInt(journalCode),
    };

    // Kirim data ke server
    $.ajax({
      url: url,
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify(formData),
      dataType: "json",
      success: function (response) {
        $("#modal2").modal("hide");
        $("#btnSubmit2").prop("disabled", false).html(`
                    <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                        <path d="M12 5l0 14" />
                        <path d="M5 12l14 0" />
                    </svg>
                    Simpan
                `);

        if (response.success) {
          showToast("success", response.message);
          if ($.fn.DataTable.isDataTable("#table1")) {
            $("#table1").DataTable().ajax.reload();
          }
          $("#modal1").modal("hide");
        } else {
          showToast("error", response.message);
        }
      },
      error: function (xhr, status, error) {
        console.error("Error saving data:", xhr);
        var errorMessage = "Gagal memproses data";
        if (xhr.responseJSON && xhr.responseJSON.message) {
          errorMessage = xhr.responseJSON.message;
        } else if (xhr.responseText) {
          try {
            var response = JSON.parse(xhr.responseText);
            if (response.message) {
              errorMessage = response.message;
            }
          } catch (e) {}
        }

        showToast("error", errorMessage);

        $("#btnSubmit2").prop("disabled", false).html(`
                    <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                        <path d="M12 5l0 14" />
                        <path d="M5 12l14 0" />
                    </svg>
                    Simpan
                `);
      },
    });
  });

  // ==================== FUNGSI UBAH ====================
  $(document).on("click", ".btn-ubah1", function (e) {
    e.preventDefault();
    const id = $(this).data("id");
    ubah(id);
  });

  function resetModal() {
    $(".is-invalid").removeClass("is-invalid");
    $(".invalid-feedback").text("");
    $("#btnSubmit1").text("Update");
    $("#jr_tk_id").val(""); // Reset hidden ID
  }

  function ubah(id) {
    //console.info("Ubah ID:", id);

    $("#modal1 .modal-title").text("Ubah Pemetaan TC");
    resetModal();

    // Load data ke select
    //loadCabang("#cabangId");
    jurnalCode("#journalCode");
    //loadKodeTC();

    // Ambil data dari server
    $.ajax({
      url: "/parameter/pemetaan-akun/journal-trk-get/" + id, 
      type: "GET",
      dataType: "json",
      success: function (res) {
        console.info("Response:", res);

        if (res.success === true && res.data) {
          var data = res.data;
          console.info("Data:", data);

          // Set ID
          $("#jr_tk_id").val(data.id);

          // Set values ke select - gunakan setTimeout agar select sudah siap
          setTimeout(function () {
            $("#kodeTransaksi").val(data.kodeTransaksiId).trigger("change");
            $("#cabangId").val(data.cabangId).trigger("change");
            $("#journalCode").val(data.journalId).trigger("change");

            // console.info(
            //   "Values set - kodeTransaksi:",
            //   $("#kodeTransaksi").val(),
            // );
            // console.info("Values set - cabangId:", $("#cabangId").val());
            // console.info("Values set - journalCode:", $("#journalCode").val());
          }, 300);

          // Tampilkan modal
          $("#modal1").modal("show");
        } else {
          showToast("error", res.message || "Gagal mengambil data");
        }
      },
      error: function (xhr, status, error) {
        console.error("AJAX Error:", status, error);
        console.error("Response Text:", xhr.responseText);
        showToast("error", "Gagal mengambil data: " + error);
      },
    });
  }

  $(document).on("click", ".btn-delete1", function (e) {
    e.preventDefault();

    const id = $(this).data("id");
    // hapus(id);
    alert("Belum bisa digunakan");
  });
   function hapus(id) {
     showConfirm(
	    "Hapus Kode Transaksi",
       "Apakah Anda yakin ingin menghapus  Kode Transaksi ini?",
       function () {
         // User klik Ya
         $.ajax({
           url: "/parameter/pemetaan-akun/journal-trk/delete/" + id,
           type: "DELETE",
           dataType: "json",
           success: function () {
             showToast("success", "Data berhasil dihapus.");
             if ($.fn.DataTable.isDataTable("#table1")) {
               $("#table1").DataTable().ajax.reload(null, false);
             }
           },
           error: function (xhr) {
             let message = "Gagal menghapus data.";
             if (xhr.responseJSON && xhr.responseJSON.message) {
               message = xhr.responseJSON.message;
             }
             showToast("error", message);
             console.log(xhr);
           },
         });
       },
       function () {
         // User klik Batal (opsional)
         console.log("Penghapusan dibatalkan");
         // showToast("info", "Penghapusan dibatalkan");
       },
     );
   }



    // Tombol Filter
    $("#btnFilterCabang").on("click", function (e) {
        e.preventDefault();
        applyFilter();
    });

    // Tombol Reset Filter
    $("#btnResetFilterCabang").on("click", function (e) {
        e.preventDefault();
        resetFilter();
    });

     function applyFilter() {
       var cabangId = $("#cabangTab1").val();
       console.info("Filter by cabang:", cabangId);

       // Reload DataTable dengan filter
       if ($.fn.DataTable.isDataTable("#table1")) {
         $("#table1").DataTable().ajax.reload();
       }
     }

     function resetFilter() {
       // Reset select ke option pertama
       $("#cabangTab1").val("");
       console.info("Filter reset");

       // Reload DataTable tanpa filter
       if ($.fn.DataTable.isDataTable("#table1")) {
         $("#table1").DataTable().ajax.reload();
       }
     }


});
