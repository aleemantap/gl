$(document).ready(function () {

  $("#filterContainer").hide();
  $("#toggleIcon").removeClass("ti-chevron-down").addClass("ti-chevron-right");
  $("#toggleText").text("Tampilkan Filter");
  let isFilterVisible = false;

  $("#toggleFilterBtn").on("click", function () {
    if (isFilterVisible) {
      $("#filterContainer").slideUp(300);
      $("#toggleIcon")
        .removeClass("ti-chevron-down")
        .addClass("ti-chevron-right");
      $("#toggleText").text("Tampilkan Filter");
      isFilterVisible = false;
    } else {
      $("#filterContainer").slideDown(300);
      $("#toggleIcon")
        .removeClass("ti-chevron-right")
        .addClass("ti-chevron-down");
      $("#toggleText").text("Sembunyikan Filter");
      isFilterVisible = true;
    }
  });

  const table = new DataTable("#table-kode-transaksi", {
    processing: true,
    serverSide: true,
    searching: false,
    paging: true,
    info: true,
    scrollX: true,
    pageLength: 10,
    lengthChange: false,
    ajax: {
      url: "/parameter/kode-transaksi/datatables",
      type: "GET",
      data: function (d) {
        return {
          draw: d.draw,
          start: d.start,
          length: 20,
          orderColumn: d.order[0] ? d.order[0].column : 1,
          orderDir: d.order[0] ? d.order[0].dir : "desc",
          kode: $("#kode").val(),
          nama: $("#nama").val(),
         
        };
      },
    },
    columns: [
      //  {
      //    data: null,
      //    defaultContent: "",
      //    render: function (data, type, row, meta) {
      //      return meta.row + 1 + (meta.settings._iDisplayStart || 0);
      //    },
      //    orderable: false, // Make row number not sortable
      //  },
      {
        data: null,
        orderable: false,
        searchable: false,
        className: "text-center align-middle",
        render: function (data, type, row) {
          return `
                  <input type="checkbox" class="row-checkbox form-check-input m-0 align-middle" value="${row.id}">
              `;
        },
      },
      { data: "kode", orderable: false, className: "text-start" },
      { data: "namaTransaksi", orderable: false, className: "text-start" },
      {
        data: null,
        orderable: false,
        className: "text-start",
        render: function (data, type, row) {
          return `
                <div style="display: flex; gap: 15px;">
                    <a class="btn-delete" href="#" data-id="${row.id}">
                        <img src="/img/delete.png" />
                    </a>
                    <a class="btn-ubah" href="#" data-id="${row.id}">
                        <img src="/img/pencil.png" />
                    </a>
                </div>
            `;
        },
        orderable: false,
      },
    ],
    drawCallback: function () {
      $("#check-all").prop("checked", false);
    },
    //   dom: 'rtip',
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

  $("#btn-add-kode").on("click", function (e) {
    e.preventDefault();
    //mode = "add";
    //editId = null;
    $("#formTC")[0].reset();
    $("#modalTitle").text("Tambah Kode Transaksi");
    $("#btn-submit-tc").text("Simpan");
    $("#modal-tc").modal("show");
  });

  $("#btn-submit-tc").on("click", function (e) {
    e.preventDefault();

    var editId = $("#tc_id").val();
    var url = "/parameter/kode-transaksi/add";
    if (editId) {
      url = "/parameter/kode-transaksi/update/" + editId;
    }
    // Hapus error sebelumnya
    $(".is-invalid").removeClass("is-invalid");
    $(".invalid-feedback").text("");

    // Validasi form
    var isValid = true;

    var kode = $("#kode").val().trim();
    if (!kode) {
      $("#kode").addClass("is-invalid");
      $("#kodeError").text("Kode  wajib diisi");
      isValid = false;
    }

    var namaTransaksi = $("#namaTransaksi").val().trim();
    if (!namaTransaksi) {
      $("#namaTransaksi").addClass("is-invalid");
      $("#namaTransaksiError").text("Nama Transaksi wajib diisi");
      isValid = false;
    }
    if (!isValid) {
      return;
    }

    // Disable button dan tampilkan loading
    $("#btn-submit-tc").prop("disabled", true).html(`
            <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
            Menyimpan...
        `);

    // Siapkan data
    var formData = {
      kode: kode,
      namaTransaksi: namaTransaksi,
    };

    // Kirim data ke server
    $.ajax({
      url: url,
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify(formData),
      dataType: "json",
      success: function (response) {
        // Tutup modal
        $("#modal-tc").modal("hide");

        // Tampilkan notifikasi sukses
        var te = editId
          ? "Data Kode Transaksi berhasil dirubah"
          : "Data Kode Transkasi berhasil ditambahkan";
        showToast("success", te);

        // Reload DataTable
        if ($.fn.DataTable.isDataTable("#table-kode-transaksi")) {
          $("#table-kode-transaksi").DataTable().ajax.reload();
        }

        // Reset form
        $("#btn-submit-tc").prop("disabled", false).html(`
                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                    <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                    <path d="M12 5l0 14" />
                    <path d="M5 12l14 0" />
                </svg>
                Simpan
            `);
      },
      error: function (xhr, status, error) {
        console.error("Error saving data:", xhr);
        // Tampilkan error dari server
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

        // Enable button kembali
        $("#btn-submit-tc").prop("disabled", false).html(`
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

  $(document).on("click", ".btn-ubah", function (e) {
    e.preventDefault();
    const id = $(this).data("id");
    //alert(id);
    ubah(id);
  });

  function resetModal() {
    $(".is-invalid").removeClass("is-invalid");
    $(".invalid-feedback").text("");
    $("#btn-submit-tc").text("Update");
  }

  function ubah(id) {
    resetModal();
    $("#modal-tc .modal-title").text("Ubah Kode Transaksi");
    $.ajax({
      url: "/parameter/kode-transaksi/" + id,
      type: "GET",
      success: function (res) {
        $("#tc_id").val(res.id);
        $("#kode").val(res.kode);
        $("#namaTransaksi").val(res.namaTransaksi);
        $("#modal-tc").modal("show");
      },
    });
  }

  $(document).on("click", ".btn-delete", function (e) {
    e.preventDefault();

    const id = $(this).data("id");
    hapus(id);
  });

  function hapus2(id) {
    showConfirm(
	  "Hapus Kode Transaksi", 
      "Apakah Anda yakin ingin menghapus  Kode Transaksi ini?",
      function () {
        // User klik Ya
        $.ajax({
          url: "/parameter/kode-transaksi/" + id,
          type: "DELETE",
          dataType: "json",
          success: function () {
            showToast("success", "Data berhasil dihapus.");
            if ($.fn.DataTable.isDataTable("#table-kode-transaksi")) {
              $("#table-kode-transaksi").DataTable().ajax.reload(null, false);
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

  function hapus(id) {
    if (!confirm("Apakah Anda yakin ingin menghapus  Kode Transaksi in?")) {
      return;
    }

    $.ajax({
      url: "/parameter/kode-transaksi/" + id,
      type: "DELETE",
      dataType: "json",
      success: function (response) {
        if (response.success) {
          $("#modal-tc").modal("hide");
          showToast("success", response.message || "Data berhasil dihapus.");
          if ($.fn.DataTable.isDataTable("#table-kode-transaksi")) {
            $("#table-kode-transaksi").DataTable().ajax.reload(null, false);
          }
        } else {
          // Backend return success: false
          showToast("error", response.message || "Gagal menghapus data.");
        }
      },
      error: function (xhr) {
        let message = "Gagal menghapus data.";

        // Cek response dari server
        if (xhr.responseJSON) {
          if (xhr.responseJSON.message) {
            message = xhr.responseJSON.message;
          } else if (xhr.responseJSON.data) {
            message = xhr.responseJSON.data;
          }
        } else if (xhr.status === 404) {
          message = "Data tidak ditemukan.";
        } else if (xhr.status === 500) {
          message = "Terjadi kesalahan pada server.";
        }

        showToast("error", message);
        console.error("Error detail:", xhr);
      },
    });
  }

  $("#check-all").on("click", function () {
    const isChecked = $(this).is(":checked");
    $(".row-checkbox").prop("checked", isChecked);
  });

  // 2. Event Uncheck "Select All" jika salah satu checkbox baris di-uncheck
  $("#tableBukuBesar tbody").on("click", ".row-checkbox", function () {
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

    $("#btn-export-kode").click(function () {
      const params = new URLSearchParams();
      // Ambil semua nilai input
      // const fromDate = $("#tanggalDari").val();
      // const toDate = $("#tanggalSampai").val();
      // const cabang = $("#cabang").val();
      // const kodeTc = $("#kodeTc").val();
      // const pembalikan = $("#pembalikan").val();
      const selectedIds = getSelectedIds();
      if (selectedIds && selectedIds.length > 0) {
        params.append("ids", JSON.stringify(selectedIds));
      }
      // Append hanya jika value tidak kosong
      // if (fromDate) params.append("fromDate", fromDate);
      // if (toDate) params.append("toDate", toDate);
      // if (cabang) params.append("cabang", cabang);
      // if (kodeTc) params.append("kodeTc", kodeTc);
      // if (pembalikan) params.append("pembalikan", pembalikan);
      const baseUrl = "/parameter/kode-transaksi/export-excel";
      const fullUrl = params.toString()
        ? `${baseUrl}?${params.toString()}`
        : baseUrl;

      //console.info("Generated URL:", fullUrl);

      // Redirect ke backend untuk download Excel
      window.location.href = fullUrl;
    });

     $("#btnFilter").click(function () {
       $("#table-kode-transaksi").DataTable().ajax.reload();
     });
     $("#btnResetFilter").click(function () {
       // Reset input
       $("#filterContainer").find("input").val("");

       // Reset select
       $("#filterContainer").find("select").val("").trigger("change");

       // Reload DataTable
       $("#table-kode-transaksi").DataTable().ajax.reload();
     });
   
});