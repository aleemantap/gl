$(document).ready(function () {
  const table = new DataTable("#table-groups", {
    processing: true,
    serverSide: true,
    searching: false,
    paging: true,
    info: true,
    // scrollX: false,
    pageLength: 10,
    lengthChange: false,
    ajax: {
      url: "/management/groups/datatables",
      type: "GET",
      data: function (d) {
        return {
          draw: d.draw,
          start: d.start,
          length: 20,
          orderColumn: d.order[0] ? d.order[0].column : 1,
          orderDir: d.order[0] ? d.order[0].dir : "desc",
          // email: $("#filterEmail").val(),
          // name: $("#filterName").val(),
          // cabangId: $("#customerId").val(),
          // userGroupId: $("#userGroupId").val()
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
      { data: "id", visible: false },
      { data: "name" },
      { data: "description" },
      {
        data: null,
        orderable: false,
        render: function (data, type, row) {
            if (row.active === "Y") {
            return '<span class="badge bg-success text-white">Active</span>';
            } else {
            return '<span class="badge bg-danger text-white">Inactive</span>';
            }
        },
      },
      {
        data: null,
        render: function (data, type, row) {
        //   var deleteA = `delete(${row.id})`;
        //   var editA = `edit(${row.id})`;
          return `
                <div style="display: flex; gap: 15px;">
                    <a  data-id="${row.id}" class="btn-delete">
                        <img src="/img/delete.png" />
                    </a>
                    <a  href="#" data-id="${row.id}" class="btn-ubah">
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

   $(document).on("click", ".btn-ubah", function (e) {
     e.preventDefault();
     const id = $(this).data("id");
    location.href="groups/edit/"+id;
   
   });

  $(document).on("click", ".btn-delete", function (e) {
    e.preventDefault();
    const id = $(this).data("id");

    Swal.fire({
      icon: "warning",
      title: "Konfirmasi",
      text: "Apakah anda mau menghapus data ini?",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#6c757d",
      confirmButtonText: "Ya",
      cancelButtonText: "Tidak",
      reverseButtons: true,
      showLoaderOnConfirm: true,
      allowOutsideClick: () => !Swal.isLoading(),
      preConfirm: () => {
        return $.ajax({
          url: `/management/groups/delete/${id}`,
          type: "DELETE",
          dataType: "json",
        })
          .then((resp) => {
            // ApiResponse: { success, message, data }
            if (!resp.success) {
              throw new Error(resp.message || "Gagal menghapus data");
            }
            return resp;
          })
          .catch((xhr) => {
            let msg = "Terjadi kesalahan pada server";
            try {
              const body =
                typeof xhr.responseJSON !== "undefined"
                  ? xhr.responseJSON
                  : JSON.parse(xhr.responseText);
              msg = body.message || msg;
            } catch (err) {
              msg = xhr.statusText || msg;
            }
            Swal.showValidationMessage(msg);
          });
      },
    }).then((result) => {
      if (result.isConfirmed && result.value) {
        Swal.fire({
          icon: "success",
          title: "Berhasil",
          text: result.value.message || "Data berhasil dihapus",
          timer: 1500,
          showConfirmButton: false,
        }).then(() => {
          // Reload DataTables tanpa reset paging
          if (typeof table !== "undefined" && table.ajax) {
            table.ajax.reload(null, false);
          } else {
            location.reload();
          }
        });
      }
    });
  });


   
  $(document).on("click", "#btn-submit-groups", function () {
    const $btn = $(this);
    const $btnHtml = $btn.html(); // simpan konten asli tombol

    // Update when user id has value
    var url = "/management/groups/save";
    var action = "update";
    if (!$("#group-id").val()) {
      action = "save";
    }

    // ===== VALIDASI =====
     if (!$("#namaGroup").val()) {
       Swal.fire({
         icon: "warning",
         title: "Warning",
         text: "Nama Group can't be empty",
       });
       $("#namaGroup").focus();
       return;
     }
    if (!$("#keterangan").val()) {
      Swal.fire({
        icon: "warning",
        title: "Warning",
        text: "Keterangan can't be empty",
      });
      $("#keterangan").focus();
      return;
    }
   

    // ===== LOADING STATE =====
    $btn
      .prop("disabled", true)
      .html(
        '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Menyimpan...',
      );

    // ===== AJAX =====
    $.ajax({
      url: url,
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify({
        id: $("#group-id").val(),
        nama: $("#namaGroup").val(),
        keterangan: $("#keterangan").val(),
        active: $('input[name="active"]:checked').val(),
      }),
      success: function (resp) {
        // ApiResponse: { success, message, data }
        if (resp && resp.success) {
          const isUpdate = !!$("#group-id").val();

          if (isUpdate) {
            Swal.fire({
              icon: "success",
              title: "Berhasil!",
              text: resp.message + ". Apakah mau kembali ke tabel user?",
              showCancelButton: true,
              confirmButtonColor: "#3085d6",
              cancelButtonColor: "#6c757d",
              confirmButtonText: "Ya",
              cancelButtonText: "Tetap Disini",
              reverseButtons: true,
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.href = "/management/groups";
              }
            });
          } else {
            Swal.fire({
              icon: "success",
              title: "Berhasil!",
              text: resp.message + ". Apakah Anda mau membuat lagi?",
              showCancelButton: true,
              confirmButtonColor: "#3085d6",
              cancelButtonColor: "#6c757d",
              confirmButtonText: "Ya, Buat User Baru",
              cancelButtonText: "Tidak, Kembali ke Daftar User",
              reverseButtons: true,
            }).then((result) => {
              if (result.isConfirmed) {
                // Reset form untuk membuat user baru
                $("#email, #name, #password, #confirm-password").val("");
                $("#group-id").val("");
                // $("#userGroupId, #cabangId").val("").trigger("change");
                $("#user-active-yes").prop("checked", true); // default Yes
                $("#email").focus();

                Swal.fire({
                  icon: "info",
                  title: "Form Reset",
                  text: "Silakan isi data user baru",
                  timer: 2000,
                  showConfirmButton: true,
                });
              } else {
                window.location.href = "/management/groups";
              }
            });
          }
        } else {
          // success=false dari ApiResponse
          Swal.fire({
            icon: "error",
            title: "Error",
            text: resp.message || "Terjadi kesalahan",
          });
        }
      },
      error: function (xhr) {
        let msg = "Terjadi kesalahan pada server";
        try {
          const body = JSON.parse(xhr.responseText);
          // ApiResponse: { success, message, data }
          msg = body.message || body.responseMessage || msg;
        } catch (e) {
          msg = xhr.responseText || xhr.statusText || msg;
        }
        Swal.fire({
          icon: "error",
          title: "Error",
          text: msg,
        });
      },
      complete: function () {
        // Selalu kembalikan tombol ke kondisi semula
        $btn.prop("disabled", false).html($btnHtml);
      },
    });
  });

});
