$(document).ready(function () {
  if ($("#group-id").val()) {
    updateForm($("#group-id").val());
  }

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
        namaGroup: $("#namaGroup").val(),
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
                $("#keterangan, #namaGroup").val("");
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

function updateForm(id) {
  $.ajax({
    url: "/management/groups/get/" + id,
    type: "GET",
    success: function (resp) {
      // console.log(resp.data)
      $("#group-id").val(resp.data.id);
      $("#namaGroup").val(resp.data.name);
      $("#keterangan").val(resp.data.description);
     
      if (resp.data.active === "Y") {
        $("#user-active-yes").prop("checked", true);
        $("#user-active-no").prop("checked", false);
      } else {
        $("#user-active-yes").prop("checked", false);
        $("#user-active-no").prop("checked", true);
      }

      // Hide loder
    },
    error: function (xhr, ajaxOptions, thrownError) {
      console.log(JSON.parse(xhr.responseText).responseMessage);
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "Error",
      });
    },
  });
}