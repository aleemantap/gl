$(document).ready(function () {
  if ($("#user-id").val()) {
    updateForm($("#user-id").val());
    $("#pass").hide();
    $("#pass2").hide();
  }

  $("#userGroupId").select2({
    placeholder: "Pilih Group User",
    allowClear: true,
  });
  $("#cabangId").select2({
    placeholder: "Pilih Cabang",
    allowClear: true,
  });

  $(document).on("click", ".toggle-password", function () {
    const $btn = $(this);
    const targetSelector = $btn.data("target");
    const $input = $(targetSelector);

    if ($input.length === 0) return;

    const $eye = $btn.find(".icon-eye");
    const $eyeSlash = $btn.find(".icon-eye-slash");

    if ($input.attr("type") === "password") {
      $input.attr("type", "text");
      $eye.addClass("d-none");
      $eyeSlash.removeClass("d-none");
    } else {
      $input.attr("type", "password");
      $eye.removeClass("d-none");
      $eyeSlash.addClass("d-none");
    }
  });


  $(document).on("click", "#btn-submit-user", function () {
    const $btn = $(this);
    const $btnHtml = $btn.html(); // simpan konten asli tombol

    // Update when user id has value
    var url = "/management/users/save";
    var action = "update";
    if (!$("#user-id").val()) {
      action = "save";
    }

    // ===== VALIDASI =====
    if (!$("#email").val()) {
      Swal.fire({
        icon: "warning",
        title: "Warning",
        text: "Email can't be empty",
      });
      $("#email").focus();
      return;
    }
    if (!$("#name").val()) {
      Swal.fire({
        icon: "warning",
        title: "Warning",
        text: "Name can't be empty",
      });
      $("#name").focus();
      return;
    }
    if (!$("#password").val() && action === "save") {
      Swal.fire({
        icon: "warning",
        title: "Warning",
        text: "Password can't be empty",
      });
      $("#password").focus();
      return;
    }
    if (!$("#confirm-password").val() && action === "save") {
      Swal.fire({
        icon: "warning",
        title: "Warning",
        text: "Please confirm password",
      });
      $("#confirm-password").focus();
      return;
    }
    if (action === "save") {
      if ($("#password").val() !== $("#confirm-password").val()) {
        Swal.fire({
          icon: "warning",
          title: "Warning",
          text: "Password doesn't match",
        });
        $("#confirm-password").focus();
        return;
      }
    }
    if (!$("#userGroupId").val()) {
      Swal.fire({
        icon: "warning",
        title: "Warning",
        text: "User group can't be empty",
      });
      $("#userGroupId").focus();
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
        id: $("#user-id").val(),
        email: $("#email").val(),
        name: $("#name").val(),
        password: $("#password").val(),
        group: $("#userGroupId").val(),
        cabang: $("#cabangId").val(),
        active: $('input[name="active"]:checked').val(),
      }),
      success: function (resp) {
        // ApiResponse: { success, message, data }
        if (resp && resp.success) {
          const isUpdate = !!$("#user-id").val();

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
                window.location.href = "/management/users";
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
                $("#user-id").val("");
                $("#userGroupId, #cabangId").val("").trigger("change");
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
                window.location.href = "/management/users";
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
    url: "/management/users/get/" + id,
    type: "GET",
    success: function (resp) {
      // console.log(resp.data)
      $("#user-id").val(resp.data.id);
      $("#name").val(resp.data.name);
      $("#email").val(resp.data.email);
      $("#userGroupId").val(resp.data.userGroupId).trigger("change");
      $("#cabangId").val(resp.data.cabangId).trigger("change");
    
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