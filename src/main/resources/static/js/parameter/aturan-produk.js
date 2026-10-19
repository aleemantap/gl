$(document).ready(function () {
  $("#btn-add-produk").click(function (e) {
    $("#form-fieldset1").find("input").prop({ disabled: false });
    $("#kodeProduk").focus();
    $("#btn-add-produk").prop({disabled:true});
    $("#batal-1").prop({ disabled: false });
    $("#btnSubmitProduk").prop({ disabled: false });
   
  });
  $("#batal-1").click(function (e) {
    $("#btn-add-produk").prop({ disabled: false });
    $("#btnSubmitProduk").prop({ disabled: true });
    $("#form-fieldset1").find("input").val("");
    $("#form-fieldset1").find("input").prop({ disabled: true });
    $("#batal-1").prop({ disabled: true });
  });

   $("#btnSubmitProduk").on("click", function (e) {
     e.preventDefault();

     var editId = $("#produk-id").val();
     var url = "/parameter/aturan-produk/add";
     if (editId) {
       url = "/parameter/aturan-produk/update/" + editId;
     }
     // Hapus error sebelumnya
     $(".is-invalid").removeClass("is-invalid");
     $(".invalid-feedback").text("");

     // Validasi form
     var isValid = true;

     var kd_produk = $("#kodeProduk").val().trim();
     if (!kd_produk) {
       $("#kodeProduk").addClass("is-invalid");
       $("#kodeProdukError").text("Kode Produk Wajib diisi");
       isValid = false;
     }

     var namaProduk = $("#namaProduk").val().trim();
     if (!namaProduk) {
       $("#namaProduk").addClass("is-invalid");
       $("#namaProdukError").text("Nama Produk Wajib diisi");
       isValid = false;
     }
     if (!isValid) {
       return;
     }
     // Disable button dan tampilkan loading
     $("#btnSubmitProduk").prop("disabled", true).html(`
            <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
            Menyimpan...
        `);
     // Siapkan data
     var formData = {
       productCode: kd_produk,
       productName: namaProduk,
       isActive: 1 //$("#isActive").is(":checked") ? 1 : 0
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
         $("#form-fieldset1").find("input").val("");
         // Tampilkan notifikasi sukses
         var te = editId
           ? "Data Produk berhasil dirubah"
           : "Data Produk berhasil ditambahkan";
         showToast("success", te);

         // Reload DataTable
         if ($.fn.DataTable.isDataTable("#table-master-produk")) {
           $("#table-master-produk").DataTable().ajax.reload();
         }

          // Reset form
          $("#btnSubmitProduk")
            .prop("disabled", false)
            .html(
              `<i class="ti ti-device-floppy" style="font-size: 22px;"></i>Simpan`,
            );
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
         $("#btnSubmitProduk").prop("disabled", false).html(`
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


  const table = new DataTable("#table-master-produk", {
    processing: true,
    serverSide: true,
    searching: false,
    paging: true,
    info: true,
    scrollX: true,
    pageLength: 5,
    lengthChange: false,
    order: [[0, "desc"]],
    ajax: {
      url: "/parameter/aturan-produk/datatables",
      type: "GET",
      data: function (d) {
        return {
          draw: d.draw,
          start: d.start,
          length: 5,
          orderColumn: d.order[0] ? d.order[0].column : 1,
          orderDir: d.order[0] ? d.order[0].dir : "desc",
          search: $("#searchInput").val(),
          active: $("#statusFilter").val(),
        };
      },
    },
    columns: [
     
      { data: "productCode" },
      { data: "productName" },
      // {
      //   data: "status",
      //   render: function (data) {
      //     return data
      //       ? '<span class="badge bg-green-lt">Active</span>'
      //       : '<span class="badge bg-secondary-lt">Inactive</span>';
      //   },
      // },
      { data: "totalRules" },
      {
        data: null,
        render: function (data, type, row) {
          return ` <div style="display: flex; gap: 15px;">
                      <a class="btn-detail" href="#" data-nama="${row.productName}" data-id="${row.id}" data-kode="${row.productCode}">
                         Config
                      </a>
                     
                  </div>`;
        },
      },
      {
        data: null,
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

//  $(".btn-detail").click(function(){

//  });

  $(document).on("click", ".btn-detail", function (e) {
    //  e.preventDefault();
    const id = $(this).data("id");
    const kode = $(this).data("kode");
    // const nama = $(this).data("nama");
    //  var t = "FORM DETAIL PRODUK. KODE "+kode;
    //  $("#modalTitleDetail").text(t)
    //  $("#modal-detail").modal("show");
     window.location.href =
       "/parameter/aturan-produk-detail/" + id + "/" + kode;
  });

// parameter / aturan - produk - detail / id / kode;
  
}); //


// Filter function
function applyFilters() {
    $('#productTable').DataTable().ajax.reload();
}

// Reset filters
function resetFilters() {
    $('#searchInput').val('');
    $('#statusFilter').val('');
    $('#productTable').DataTable().ajax.reload();
}

