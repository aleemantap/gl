$(document).ready(function () {
  const table = new DataTable("#table-cabang", {
    processing: true,
    serverSide: true,
    searching: false,
    paging: true,
    info: true,
    scrollX: true,
    pageLength: 10,
    lengthChange: false,
    ajax: {
      url: "/parameter/cabang/datatables",
      type: "GET",
      data: function (d) {
        return {
          draw: d.draw,
          start: d.start,
          length: 20,
          orderColumn: d.order[0] ? d.order[0].column : 1,
          orderDir: d.order[0] ? d.order[0].dir : "desc",
          namaCabang: $("#namaCabang").val(),
          kodeCabang: $("#kodeCabang").val(),
        };
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
                  <input type="checkbox" class="row-checkbox form-check-input m-0 align-middle" value="${row.id}">
              `;
        },
      },
      { data: "kodeCabang", orderable: false },
      { data: "namaCabang", orderable: false },

      { data: "alamatLengkap", orderable: false },
      {
        data: null,
        orderable: false,
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


  $("#btn-add").on("click", function (e) {
    e.preventDefault();
    $("#formCabang")[0].reset();
    $("#modalTitle").text("Tambah Data Cabang");
    $("#btnSubmitCabanag").text("Simpan");
    $("#modal-cabang").modal("show");
  });

   $("#btn-export-cabang").click(function () {
     const params = new URLSearchParams();
     // Ambil semua nilai input
     const kode = $("#kodeCabang").val();
     const cabang = $("#namaCabang").val();

    
     const selectedIds = getSelectedIds();
     if (selectedIds && selectedIds.length > 0) {
       params.append("ids", JSON.stringify(selectedIds));
     }
     // Append hanya jika value tidak kosong
     if (kode) params.append("kode", kode);
     if (cabang) params.append("cabang", cabang);
    
     const baseUrl = "/parameter/cabang/export-excel";
     const fullUrl = params.toString()
       ? `${baseUrl}?${params.toString()}`
       : baseUrl;
     // Redirect ke backend untuk download Excel
     window.location.href = fullUrl;
   });

  //  

   $("#btnSubmitCabang").on("click", function (e) {
     e.preventDefault();

     var editId = $("#cb_id").val();
     var url = "/parameter/cabang/add";
     if (editId) {
       url = "/parameter/cabang/update/" + editId;
     }
     // Hapus error sebelumnya
     $(".is-invalid").removeClass("is-invalid");
     $(".invalid-feedback").text("");

     // Validasi form
     var isValid = true;

     var kd_cabang = $("#kd_cabang").val().trim();
     if (!kd_cabang) {
       $("#kd_cabang").addClass("is-invalid");
       $("#kd_cabangError").text("Kode Cabang Wajib");
       isValid = false;
     }
   
     var nm_cabang = $("#nm_cabang").val().trim();
     if (!nm_cabang) {
       $("#nm_cabang").addClass("is-invalid");
       $("#nm_cabangError").text("Nama Cabang Wajib");
       isValid = false;
     }
     if (!isValid) {
       return;
     }
     // Disable button dan tampilkan loading
     $("#btnSubmitCabang").prop("disabled", true).html(`
            <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
            Menyimpan...
        `);

     // Siapkan data
     var formData = {
       kodeCabang: kd_cabang,
       namaCabang: nm_cabang,
       alamatLengkap: $("#al_cabang").val().trim(),
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
         $("#modal-cabang").modal("hide");

         // Tampilkan notifikasi sukses
         var te = editId
           ? "Data Cabang berhasil dirubah"
           : "Data Cabang berhasil ditambahkan";
         showToast("success", te);

         // Reload DataTable
         if ($.fn.DataTable.isDataTable("#table-cabang")) {
           $("#table-cabang").DataTable().ajax.reload();
         }

         // Reset form
         //   $("#formGlAccount")[0].reset();
         $("#btnSubmitCabang").prop("disabled", false).html(`
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
         $("#btnSubmitCabang").prop("disabled", false).html(`
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

    $("#btn-filter").click(function () {
      $("#table-cabang").DataTable().ajax.reload();
    });
    $("#btn-reset").click(function () {
      $("#namaCabang").val("");
      $("#kodeCabang").val("");
      $("#table-cabang").DataTable().ajax.reload();
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
        $("#btnSubmitCabang").text("Update");
      }

      function ubah(id) {
        resetModal();
        $("#modal-tc .modal-title").text("Ubah Data Cabang");
        $.ajax({
          url: "/parameter/cabang/get/" + id,
          type: "GET",
          success: function (res) {
            $("#cb_id").val(res.id);
            $("#kd_cabang").val(res.kodeCabang);
            $("#nm_cabang").val(res.namaCabang);
            $("#al_cabang").val(res.alamatLengkap);
            $("#modal-cabang").modal("show");
          },
        });
      }

       $(document).on("click", ".btn-delete", function (e) {
         e.preventDefault();
         const id = $(this).data("id");
         showAlert("warning", "Belum Bisa Delete");

       });
})