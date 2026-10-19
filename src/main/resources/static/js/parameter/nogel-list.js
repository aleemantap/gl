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


    //load kategori
    // function loadtype_gl(id) {
    //   $.ajax({
    //     url: "/parameter/gl-type-account/list",
    //     type: "GET",
    //     dataType: "json",
    //     success: function (response) {
    //       var select = $("#type_gl");
    //       select.empty();
    //       select.append('<option value="">Pilih Type</option>');
    //       if (response.content && response.content.length > 0) {
    //         $.each(response.content, function (index, item) {
    //           // select.append(
    //           //   '<option value="' + item.id + '">' + item.name + "</option>",
    //           // );
    //             var selected = id && item.id == id ? "selected" : "";
    //             select.append(
    //               '<option value="' +
    //                 item.id +
    //                 '" ' +
    //                 selected +
    //                 ">" +
    //                 item.name +
    //                 "</option>",
    //             );
    //         });
    //       }
    //     },
    //     error: function (xhr, status, error) {
    //       console.error("Error loading kategori:", error);
    //       showToast("error", "Gagal memuat data kategori");
    //     },
    //   });
    // }

    // function loadCabang() {
    //    $.ajax({
    //      url: "/parameter/cabang/list",
    //      type: "GET",
    //      dataType: "json",
    //      success: function (response) {
    //        var select = $("#cabangId");
    //        select.empty();
    //        select.append('<option value="">Pilih Cabang</option>');
    //        if (response.content && response.content.length > 0) {
    //          $.each(response.content, function (index, item) {
    //            select.append(
    //              '<option value="' +
    //                item.id +
    //                '">' +
    //                item.kodeCabang + "-"
    //                + item.namaCabang +
    //                "</option>",
    //            );
    //          });
    //        }
    //      },
    //      error: function (xhr, status, error) {
    //        console.error("Error loading cabang:", error);
    //        showToast("error", "Gagal memuat data cabang");
    //      },
    //    });
    //  }


    const table = new DataTable("#table-nogel", {
      processing: true,
      serverSide: true,
      searching: false,
      paging: true,
      info: true,
      scrollX: true,
      pageLength: 10,
      lengthChange: false,
      ajax: {
        url: "/parameter/nomor-gl/datatables",
        type: "GET",
        data: function (d) {
          return {
            draw: d.draw,
            start: d.start,
            length: 20,
            orderColumn: d.order[0] ? d.order[0].column : 1,
            orderDir: d.order[0] ? d.order[0].dir : "desc",
            // searchValue : "",
            nomorGl: $("#filterNomorGl").val() || null,
            namaGlAccount: $("#filterNamaGl").val() || null,
            typeId: $("#filterTypeGl").val() || null,
            //cabangId: $("#cabangGL").val() || null,
            saldoNormal: $("#filterSaldoNormal").val() || null,
          };
        },
      },

      columns: [
        // {
        //   data: null,
        //   defaultContent: "",
        //   render: function (data, type, row, meta) {
        //     return meta.row + 1 + (meta.settings._iDisplayStart || 0);
        //   },
        //   orderable: false, // Make row number not sortable
        // },
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
        //   { data: "id", visible: false },
        { data: "nomorGl" },
        { data: "namaGlAccount" },
        { data: "glTypeName" },
        { data: "saldoNormal" },
        // {
        //   data: null,
        //   render: function (data, type, row) {
        //     return `
        //           <div style="display: flex; gap: 15px;">
        //               ${row.kodeCabang} - ${row.cabangName}
        //           </div>
        //       `;
        //   },
        // },
        {
          data: null,
          render: function (data, type, row) {
            return `
                  <div style="display: flex; gap: 15px;">
                      <a class="btn-delete" href="#" data-id="${row.id}">
                          <img src="/img/delete.png" />
                      </a>
                      <a class="btn-ubah" href="#" data-id="${row.id}" >
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
     $("#table-nogel tbody").on("click", ".row-checkbox", function () {
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



    $("#btnTambah").on("click", function (e) {
        e.preventDefault();

        //mode = "add";
        //editId = null;

        $("#formGlAccount")[0].reset();

        // loadtype_gl();
        // loadCabang();

        $("#modalReportTitle").text("Tambah Account GL");
        $("#btnSubmit").text("Simpan");
        $("#modal-gl").modal("show");
    });


    // SUBMIT FORM
    $("#btnSubmit").on("click", function (e) {
      e.preventDefault();

       var editId = $("#gl_id").val();
       var url = "/parameter/nomor-gl/add";
       if(editId)
       {
         url = "/parameter/nomor-gl/update/" + editId;
         
       }
         // Hapus error sebelumnya
      $(".is-invalid").removeClass("is-invalid");
      $(".invalid-feedback").text("");

      // Validasi form
      var isValid = true;

      var nomorGl = $("#nomorGl").val().trim();
      if (!nomorGl) {
        $("#nomorGl").addClass("is-invalid");
        $("#nomorGlError").text("Nomor GL wajib diisi");
        isValid = false;
      }

      var namaGlAccount = $("#namaGlAccount").val().trim();
      if (!namaGlAccount) {
        $("#namaGlAccount").addClass("is-invalid");
        $("#namaGlAccountError").text("Nama GL Account wajib diisi");
        isValid = false;
      }
 
      var type_gl = $("#type_gl").val();
      if (!type_gl) {
        $("#type_gl").addClass("is-invalid");
        $("#type_glError").text("Type wajib dipilih");
        isValid = false;
      }

      var saldoNormal = $("#saldoNormal").val();
      if (!saldoNormal) {
        $("#saldoNormal").addClass("is-invalid");
        $("#saldoNormalError").text("D/K saldo Normal wajib dipilih");
        isValid = false;
      }

      // var cabangId = $("#cabangId").val();
      // if (!cabangId) {
      //   $("#cabangId").addClass("is-invalid");
      //   $("#cabangIdError").text("Cabang wajib dipilih");
      //   isValid = false;
      // }

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
        nomorGl: nomorGl,
        namaGlAccount: namaGlAccount,
        typeGlAccountId: parseInt(type_gl),
        saldoNormal: saldoNormal,
        // cabangId: parseInt(cabangId),
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
          var te = editId
          ? "Data GL Account berhasil dirubah"
          : "Data GL Account berhasil ditambahkan";
          if (response.success)
          {
            $("#modal-gl").modal("hide");
            showToast("success", te);
            if ($.fn.DataTable.isDataTable("#table-nogel")) {
              $("#table-nogel").DataTable().ajax.reload();
            }
          }
          else
          {
             showToast("error", response.message);

          }
          

          $("#btnSubmit").prop("disabled", false).html(`
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
                    Simpan
                `);
        },
      });

    });

    $(document).on("click", ".btn-ubah", function (e) {
        e.preventDefault();
        const id = $(this).data("id");
        ubah(id);
    });

    function resetModal() {
      // loadtype_gl();
      // loadCabang();
      $(".is-invalid").removeClass("is-invalid");
      $(".invalid-feedback").text("");
      $("#btnSubmit").text("Update");
    }

    function ubah(id) {
      resetModal();

      $("#modal-gl .modal-title").text("Ubah Account GL");

      $.ajax({
        url: "/parameter/nomor-gl/" + id,
        type: "GET",
        success: function (res) {
        
          $("#gl_id").val(res.id);
          $("#nomorGl").val(res.nomorGl);
          $("#namaGlAccount").val(res.namaGlAccount);
          $("#type_gl").val(res.glTypeId);
          $("#saldoNormal").val(res.saldoNormal);
          // $("#cabangId").val(res.cabangId);
          $("#modal-gl").modal("show");
        },
      });
    }

    $(document).on("click", ".btn-delete", function (e) {
      e.preventDefault();

       const id = $(this).data("id");
        hapus(id);
      
    });

    function hapus(id) {
      if (!confirm("Apakah Anda yakin ingin menghapus Account GL ini?")) {
        return;
      }

      $.ajax({
        url: "/parameter/nomor-gl/delete/" + id,
        type: "DELETE",
        dataType: "json",
        success: function (response) {
          // Cek response dari backend
          if (response.success) {
            $("#modal-gl").modal("hide");
            showToast("success", response.message || "Data berhasil dihapus.");
            if ($.fn.DataTable.isDataTable("#table-nogel")) {
              $("#table-nogel").DataTable().ajax.reload(null, false);
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
    
    $("#btnFilterGl").click(function(){
       $("#table-nogel").DataTable().ajax.reload();
    });
    $("#btnResetFilterGl").click(function () {
        // Reset input
      $("#filterContainer").find("input").val("");

      // Reset select
      $("#filterContainer").find("select").val("").trigger("change");

      // Reload DataTable
      $("#table-nogel").DataTable().ajax.reload();
    });

    $("#btn-export-exc").click(function () {
      const params = new URLSearchParams();
      // Ambil semua nilai input
      const nomorGl = $("#filterNomorGl").val();
      const namaGlAccount = $("#filterNamaGl").val();
      const typeId = $("#filterTypeGl").val();
      const saldoNormal = $("#filterSaldoNormal").val();
      const selectedIds = getSelectedIds();
      if (selectedIds && selectedIds.length > 0) {
        params.append("ids", JSON.stringify(selectedIds));
      }
      // Append hanya jika value tidak kosong
      if (nomorGl) params.append("nomorGl", nomorGl);
      if (namaGlAccount) params.append("namaGlAccount", namaGlAccount);
      if (typeId) params.append("typeId", typeId);
      if (saldoNormal) params.append("saldoNormal", saldoNormal);

      const baseUrl = "/parameter/nomor-gl/export-excel";
      const fullUrl = params.toString()
        ? `${baseUrl}?${params.toString()}`
        : baseUrl;

      //console.info("Generated URL:", fullUrl);

      // Redirect ke backend untuk download Excel
      window.location.href = fullUrl;
    });

});
