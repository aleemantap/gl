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

  const table = new DataTable("#table-kategori-transaksi", {
    processing: true,
    serverSide: true,
    searching: false,
    paging: true,
    info: true,
    scrollX: false,
    pageLength: 10,
    lengthChange: false,
    ajax: {
      url: "/parameter/kategori-transaksi/datatables",
      type: "GET",
      data: function (d) {
        return {
          draw: d.draw,
          start: d.start,
          length: 20,
          orderColumn: d.order[0] ? d.order[0].column : 1,
          orderDir: d.order[0] ? d.order[0].dir : "desc",
          // searchValue : "",
          kode: $("#filterKategoriKode").val() || null,
          name: $("#filterKategoriName").val() || null,
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
        orderable: false, // Make row number not sortable
      },
      { data: "code" },
      { data: "name" },
      { data: "description" },
      {
        data: null,
        render: function (data, type, row) {
          return `
                  <div style="display: flex; gap: 15px;">
                      <a class="btn-delete" href="#" data-id="${row.id}" data-name="${row.name}">
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

  $("#btnTambah").on("click", function (e) {
    e.preventDefault();
    $("#formKategoriTransaksi")[0].reset();
    $("#modalReportTitle").text("Tambah Kategori Transksi");
    $("#btnSubmitKT").text("Simpan");
    $("#modal-kategori-transaksi").modal("show");
  });

  $("#btnSubmitKT").on("click", function (e) {
    e.preventDefault();

    var editId = $("#id").val();
    var url = "/parameter/kategori-transaksi/add";
    if (editId) {
      url = "/parameter/kategori-transaksi/update/" + editId;
    }
    // Hapus error sebelumnya
    $(".is-invalid").removeClass("is-invalid");
    $(".invalid-feedback").text("");

    // Validasi form
    var isValid = true;


    
    var kode = $("#kode-transaksi").val().trim();
    if (!kode) {
      $("#kode-transaksi").addClass("is-invalid");
      $("#kode-transaksiError").text("Kode Wajib");
      isValid = false;
    }

    var name = $("#name").val().trim();
    if (!name) {
      $("#name").addClass("is-invalid");
      $("#nameError").text("Nama Wajib diisi");
      isValid = false;
    }

    if (!isValid) {
      return;
    }
    // Disable button dan tampilkan loading
    $("#btnSubmitKT").prop("disabled", true).html(`
            <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
            Menyimpan...
        `);

    // Siapkan data
    var formData = {
      code: kode,
      name: name,
      description: $("#deskripsi").val().trim(),
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
        $("#modal-kategori").modal("hide");

        // Tampilkan notifikasi sukses
        var te = editId
          ? "Data Kategori berhasil dirubah"
          : "Data Kategori berhasil ditambahkan";
        showToast("success", te);

        // Reload DataTable
        if ($.fn.DataTable.isDataTable("#table-kategori-transaksi")) {
          $("#table-kategori-transaksi").DataTable().ajax.reload();
        }

        // Reset form
        //   $("#formGlAccount")[0].reset();
        $("#btnSubmitKT").prop("disabled", false).html(`
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
        $("#btnSubmitKT").prop("disabled", false).html(`
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

  $("#btnFilter").click(function () {
    $("#table-kategori-transaksi").DataTable().ajax.reload();
  });
  $("#btnResetFilter").click(function () {
    $("#filterKategori").val("");
    //$("#note").val("");
    $("#table-kategori-transaksi").DataTable().ajax.reload();
  });

  $(document).on("click", ".btn-ubah", function (e) {
    e.preventDefault();
    const id = $(this).data("id");
    $("#modal-kategori-transaksi .modal-title").text("Ubah Data Kategori");
    $.ajax({
      url: "/parameter/kategori-transaksi/get/" + id,
      type: "GET",
      success: function (res) {
        $("#id").val(res.id);
        $("#kode-transaksi").val(res.code);
        $("#name").val(res.name);
        $("#deskripsi").val(res.description);
        
        $("#modal-kategori-transaksi").modal("show");
      },
    });
  });
  $(document).on("click", ".btn-delete", function (e) {
    e.preventDefault();
    const id = $(this).data("id");
    const name = $(this).data("name");

    // Konfirmasi dengan confirm biasa
    if (confirm('Apakah Anda yakin ingin menghapus kategori "' + name + '"?')) {
      // Tampilkan loading (opsional)
      const btn = $(this);
      const originalHtml = btn.html();
      btn.html('<i class="fas fa-spinner fa-spin"></i> Menghapus...');
      btn.prop("disabled", true);

      $.ajax({
        url: "/parameter/kategori-transaksi/delete/" + id,
        method: "DELETE",
        success: function (response) {
          // Reset tombol
          btn.html(originalHtml);
          btn.prop("disabled", false);

          // Reload DataTable
          table.ajax.reload();

          // Tampilkan pesan sukses
          alert(" " + response);
        },
        error: function (xhr, status, error) {
          // Reset tombol
          btn.html(originalHtml);
          btn.prop("disabled", false);

          // Ambil pesan error dari response
          let errorMessage = "Gagal menghapus data!";
          if (xhr.responseText) {
            try {
              const response = JSON.parse(xhr.responseText);
              errorMessage = response;
            } catch (e) {
              errorMessage = xhr.responseText;
            }
          }

          // Tampilkan pesan error
          alert("✗ " + errorMessage);
        },
      });
    }
  });
});
