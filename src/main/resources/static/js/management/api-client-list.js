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
  const table = new DataTable("#table-api-client", {
    processing: true,
    serverSide: true,
    searching: false,
    paging: true,
    info: true,
    // scrollX: false,
    pageLength: 10,
    lengthChange: false,
    ajax: {
      url: "/management/api-client/datatables",
      type: "GET",
      data: function (d) {
        return {
          draw: d.draw,
          start: d.start,
          length: 20,
          orderColumn: d.order[0] ? d.order[0].column : 1,
          orderDir: d.order[0] ? d.order[0].dir : "desc",
          client_name: $("#client_name").val(),
          client_id: $("#client_id").val(),
          active: $("#status").val(),
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
      //   {
      //     data: null,
      //     orderable: false,
      //     searchable: false,
      //     className: "text-center align-middle",
      //     render: function (data, type, row) {
      //       return `
      //               <input type="checkbox" class="row-checkbox form-check-input m-0 align-middle" value="${row.id}">
      //           `;
      //     },
      //   },
      { data: "clientName" },
      { data: "clientId" },
      {
        data: null,
        orderable: false,
        render: function (data, type, row) {
          if (row.active == 1) {
            return '<span class="badge bg-success text-white">Active</span>';
          } else {
            return '<span class="badge bg-danger text-white">Inactive</span>';
          }
        },
      },
      {
        data: "expiredAt",
        render: (d) => (d ? formatDateTime(d) : "-"),
      },
      { data: "lastUsedAt" },
      {
        data: null,
        render: function (data, type, row) {
          var editA = `edit(${row.id})`;
          var regen = `regen(${row.id})`;
          var tmb = row.active == 1 ? "Non Aktifkan" : "Aktifkan";

          return (
            `
                <div style="display: flex; gap: 15px;">                  
                    <a class="btn btn-primary" href="#" onclick="${editA}">
                        Edit
                    </a>
                    <a class="btn btn-success" href="#" onclick="${regen}">
                       Regenerate Secret
                    </a>
                    <button class="btn btn-primary-gray btn-tmb" data-id="` +
            row.id +
            `" data-tmb="` +
            tmb +
            `">
                       ` +
            tmb +
            `
                    </button>
                </div>
            `
          );
        },
        orderable: false,
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

  $("#btn-add-client").on("click", function (e) {
    e.preventDefault();
     resetFormApiClient();

    $("#modalTitle").text("Tambah Data Key API Client");
    // $("#btnSubmitCabanag").text("Simpan");
    $("#modal-api").modal("show");
  });

  $("#btnFilter").click(function () {
    $("#table-api-client").DataTable().ajax.reload();
  });
  $("#btnResetFilter").click(function () {
    $("#client_name").val("");
    $("#client_id").val("");
    $("#status").val("");
    if ($.fn.DataTable.isDataTable("#table-api-client")) {
      $("#table-api-client").DataTable().ajax.reload();
    }
  });

  $("#simpan-client-api").on("click", function (e) {
    e.preventDefault();

    var editId = $("#api_id").val();
    var url = "/management/api-client/add";
    if (editId) {
      url = "/management/api-client/update/" + editId;
    }

    var client_name = $("#clientName").val().trim();
    if (!client_name) {
      showToast("warning", "Client Name Harus Diisi");
    }

    var client_id = $("#clientId").val().trim();
    if (!client_id) {
      showToast("warning", "Client ID Harus Diisi");
    }

    var description = $("#description").val().trim();
    if (!description) {
      showToast("warning", "Description  Harus Diisi");
    }

    var expiredAt = $("#expiredAt").val();
    if (!expiredAt) {
      showToast("warning", "ExpiredAt Harus Diisi");
    }

     var active = $("#modal-api #active").val();
     if (!active) {
       showToast("warning", "Status Harus Dipilih");
     }

    // Disable button dan tampilkan loading
    $("#simpan-client-api").prop("disabled", true).html(`
            <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
            Menyimpan...
        `);

    // Siapkan data
    var formData = {
      clientName: client_name,
      clientId: client_id,
      description: description,
      expiredAt: expiredAt,
      status: active,
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
         $("#modal-api").modal("hide");

         // Tampilkan notifikasi sukses
         var te = editId
           ? "Data API berhasil dirubah"
           : "Data API berhasil ditambahkan";
         showToast("success", te);

         // Reload DataTable
         if ($.fn.DataTable.isDataTable("#table-api-client")) {
           $("#table-api-client").DataTable().ajax.reload();
         }

         // Reset form
         $("#simpan-client-api").prop("disabled", false).html(`
                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                    <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                    <path d="M12 5l0 14" />
                    <path d="M5 12l14 0" />
                </svg>
                Simpan
            `);

         if (!editId) {
           $("#secretWrapper").show();
           $("#clientSecret").val(response.clientSecret);
           $("#modal-api-2").modal("show");
         }
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

         showAlert("error", errorMessage);

         // Enable button kembali
         $("#simpan-client-api").prop("disabled", false).html(`
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
  //

  $("#btnCopySecret").on("click", function () {
    const text = $("#clientSecret").val();

    if (!text) {
      // toastr.warning("Tidak ada client secret untuk disalin");
      showAlert("warning", "Tidak ada client secret untuk disalin");
      return;
    }

    copyToClipboard(text);
  });

  $(document).on("click", ".btn-tmb", function (e) {
    const id = $(this).data("id");
    const tmb = $(this).data("tmb");
    showConfirm(
      "Konfirmasi " + tmb,
      "Apakah Anda yakin ingin " + tmb + "?",
      function () {
        // User klik Ya
        $.ajax({
          url: "/management/api-client/aktif-non-aktif/" + id,
          type: "POST",
          contentType: "application/json",
          data: JSON.stringify({ type: tmb }),
          dataType: "json",
          success: function (response) {
            showAlert("success", response.message);
            if ($.fn.DataTable.isDataTable("#table-api-client")) {
              $("#table-api-client").DataTable().ajax.reload();
            }
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
            showAlert("error", errorMessage);
          },
        });
      },
      function () {
        // User klik Batal (opsional)
        //console.log("Penghapusan dibatalkan");
        // showToast("info", "Penghapusan dibatalkan");
      },
    );
  });
});

function edit(id) {
  $("#modal-api .modal-title").text("Ubah Data key Api");
  resetFormApiClient();
  $.ajax({
    url: "/management/api-client/get/" + id,
    type: "GET",
    success: function (res) {
      $("#api_id").val(res.id);
      $("#clientName").val(res.clientName);
      $("#clientId").val(res.clientId);
      $("#clientId").prop("readonly", true); 
      $("#description").val(res.description);
      $("#expiredAt").val(res.expiredAt);
     
      $("#modal-api #active").val(String(res.active));
      $("#modal-api").modal("show");
    },
  });
}

function regen(id) {
  showConfirm(
    "Regenerate Secret",
    "Apakah Anda yakin ingin regenerate-secret ?",
    function () {
      // User klik Ya
      $.ajax({
        url: "/management/api-client/regenerate-secret/" + id,
        type: "POST",
        contentType: "application/json",
        //data: JSON.stringify(formData),
        dataType: "json",
        success: function (response) {
          showAlert("success", "Generate Secret Berhasil.");
          if ($.fn.DataTable.isDataTable("#table-api-client")) {
            $("#table-api-client").DataTable().ajax.reload();
          }
          $("#secretWrapper").show();
          $("#clientSecret").val(response.clientSecret);
          $("#modal-api-2").modal("show");
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
          showAlert("error", errorMessage);
        },
      });
    },
    function () {
      // User klik Batal (opsional)
      //console.log("Penghapusan dibatalkan");
      // showToast("info", "Penghapusan dibatalkan");
    },
  );
}

// function regen(id){

//    $.ajax({
//      url: "/management/api-client/regenerate-secret" + id,
//      type: "POST",
//      contentType: "application/json",
//      //data: JSON.stringify(formData),
//      dataType: "json",
//      success: function (response) {
//        showAlert("success", "Secret Key Berhasil di-regenerate");
//        // Reload DataTable
//        if ($.fn.DataTable.isDataTable("#table-api-client")) {
//          $("#table-api-client").DataTable().ajax.reload();
//        }
//        // console.info("response+" + response);
//        // console.info(JSON.stringify(response, null, 2));
//        //clientSecret expiredAt
//        if (!editId) {
//          $("#secretWrapper").show();
//          $("#clientSecret").val(response.clientSecret);
//          $("#modal-api-2").modal("show");
//        }
//      },
//      error: function (xhr, status, error) {
//        console.error("Error saving data:", xhr);
//        // Tampilkan error dari server
//        var errorMessage = "Gagal memproses data";
//        if (xhr.responseJSON && xhr.responseJSON.message) {
//          errorMessage = xhr.responseJSON.message;
//        } else if (xhr.responseText) {
//          try {
//            var response = JSON.parse(xhr.responseText);
//            if (response.message) {
//              errorMessage = response.message;
//            }
//          } catch (e) {}
//        }

//        showAlert("error", errorMessage);

//      },
//    });

// }
function formatDateTime(str) {
  if (!str) return "-";
  const d = new Date(str);
  if (isNaN(d.getTime())) return str;
  const pad = (n) => String(n).padStart(2, "0");
  return `${pad(d.getDate())}-${pad(d.getMonth() + 1)}-${d.getFullYear()} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}
//  showToast("warning", "Tidak ada client secret untuk disalin");
function copyToClipboard(text) {
  // Prioritas: Clipboard API modern
  if (navigator.clipboard && window.isSecureContext) {
    navigator.clipboard
      .writeText(text)
      .then(() => showToast("success", "Client secret disalin"))
      .catch(() => fallbackCopy(text));
  } else {
    fallbackCopy(text);
  }
}

function fallbackCopy(text) {
  const ta = document.createElement("textarea");
  ta.value = text;
  ta.style.position = "fixed";
  ta.style.top = "-9999px";
  ta.style.left = "-9999px";
  document.body.appendChild(ta);
  ta.focus();
  ta.select();

  try {
    const ok = document.execCommand("copy");
    if (ok) showToast("success", "Client secret disalin");
    else showToast("error", "Gagal disalin");
  } catch (err) {
    console.error("Copy failed:", err);
    //toastr.error("Gagal menyalin: " + err.message);
    showToast("error", err.message);
  } finally {
    document.body.removeChild(ta);
  }
}
function resetFormApiClient() {
  $("#formApiClient")
    .find("input, select, textarea")
    .each(function () {
      var $el = $(this);

      if ($el.is("select")) {
        $el.val(""); // reset select
      } else if (
        $el.attr("type") === "checkbox" ||
        $el.attr("type") === "radio"
      ) {
        $el.prop("checked", false); // reset checkbox/radio
      } else if ($el.attr("type") !== "hidden") {
        $el.val(""); // reset input biasa
      }
    });
  $("#clientId").prop("readonly", false); 
  // hidden field id dikosongkan manual
  $("#api_id").val("");
}