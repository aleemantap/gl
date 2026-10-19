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

    const table = new DataTable("#table-kategori-gl-type", {
      processing: true,
      serverSide: true,
      searching: false,
      paging: true,
      info: true,
      scrollX: false,
      pageLength: 10,
      lengthChange: false,
      ajax: {
        url: "/parameter/gl-type-account/datatables",
        type: "GET",
        data: function (d) {
          return {
            draw: d.draw,
            start: d.start,
            length: 20,
            orderColumn: d.order[0] ? d.order[0].column : 1,
            orderDir: d.order[0] ? d.order[0].dir : "desc",
            // searchValue : "",
            name: $("#filterKategori").val() || null,
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
        { data: "name" },
        { data: "note" },
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

    $("#btnTambah").on("click", function (e) {
      e.preventDefault();
      $("#formKategori")[0].reset();
      $("#modalReportTitle").text("Tambah Type GL");
      $("#btnSubmit").text("Simpan");
      $("#modal-kategori").modal("show");
    });

     $("#btnSubmit").on("click", function (e) {
       e.preventDefault();

       var editId = $("#id").val();
       var url = "/parameter/gl-type-account/add";
       if (editId) {
         url = "/parameter/gl-type-account/update/" + editId;
       }
       // Hapus error sebelumnya
       $(".is-invalid").removeClass("is-invalid");
       $(".invalid-feedback").text("");

       // Validasi form
       var isValid = true;

       var name = $("#name").val().trim();
       if (!name) {
         $("#name").addClass("is-invalid");
         $("#nameError").text("Nama Wajib diisi");
         isValid = false;
       }

       var note = $("#note").val().trim();
       if (!note) {
         $("#note").addClass("is-invalid");
         $("#noteError").text("Note Wajib");
         isValid = false;
       }
       if (!isValid) {
         return;
       }
       // Disable button dan tampilkan loading
       $("#btnSubmit").prop("disabled", true).html(`
            <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
            Menyimpan...
        `);

       // Siapkan data
       var formData = {
         name: name,
         note: note
         
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
           if ($.fn.DataTable.isDataTable("#table-kategori-gl-type")) {
             $("#table-kategori-gl-type").DataTable().ajax.reload();
           }

           // Reset form
           //   $("#formGlAccount")[0].reset();
           $("#btnSubmit").prop("disabled", false).html(`
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
           $("#btnSubmit").prop("disabled", false).html(`
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
        $("#table-kategori-gl-type").DataTable().ajax.reload();
      });
      $("#btnResetFilter").click(function () {
        $("#filterKategori").val("");
        //$("#note").val("");
        $("#table-kategori-gl-type").DataTable().ajax.reload();
      });

      $("#btn-export-type").click(function () {
        const params = new URLSearchParams();
        // Ambil semua nilai input
        const kategori = $("#filterKategori").val();
        const selectedIds = getSelectedIds();
        if (selectedIds && selectedIds.length > 0) {
          params.append("ids", JSON.stringify(selectedIds));
        }
        // Append hanya jika value tidak kosong
        if (kategori) params.append("kategori", kategori);
        const baseUrl = "/parameter/gl-type-account/export-excel";
        const fullUrl = params.toString()
          ? `${baseUrl}?${params.toString()}`
          : baseUrl;

        window.location.href = fullUrl;
      });
   
      $(document).on("click", ".btn-ubah", function (e) {
        e.preventDefault();
        const id = $(this).data("id");
         $("#modal-kategori .modal-title").text("Ubah Data Kategori");
         $.ajax({
           url: "/parameter/gl-type-account/get/" + id,
           type: "GET",
           success: function (res) {
             $("#id").val(res.id);
             $("#name").val(res.name);
             $("#note").val(res.note);
             $("#modal-kategori").modal("show");
           },
         });
      });
     $(document).on("click", ".btn-delete", function (e) {
       e.preventDefault();
       const id = $(this).data("id");
       const name = $(this).data("name");

       // Konfirmasi dengan confirm biasa
       if (
         confirm('Apakah Anda yakin ingin menghapus kategori "' + name + '"?')
       ) {
         // Tampilkan loading (opsional)
         const btn = $(this);
         const originalHtml = btn.html();
         btn.html('<i class="fas fa-spinner fa-spin"></i> Menghapus...');
         btn.prop("disabled", true);

         $.ajax({
           url: "/parameter/gl-type-account/delete/" + id,
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

      $("#check-all").on("click", function () {
        const isChecked = $(this).is(":checked");
        $(".row-checkbox").prop("checked", isChecked);
      });

      // 2. Event Uncheck "Select All" jika salah satu checkbox baris di-uncheck
      $("#table-kategori-gl-type tbody").on(
        "click",
        ".row-checkbox",
        function () {
          if (!$(this).is(":checked")) {
            $("#check-all").prop("checked", false);
          }
        },
      );

      // 3. Fungsi Helper untuk Mengambil Nilai ID Baris yang Dicentang
      function getSelectedIds() {
        const selectedIds = [];
        $(".row-checkbox:checked").each(function () {
          selectedIds.push($(this).val());
        });
        return selectedIds; // Mengembalikan array ID yang dipilih, misal: ["12", "15"]
      }
   
})
