$(document).ready(function () {
  // ========== HANDLE UPDATE PASSWORD ==========
  $("#update-password").on("click", function () {
    const userId = $("#user-id").val(); // Ambil dari hidden input (dari profile)
    // const oldPassword = $("#oldPassword").val();
    const newPassword = $("#newPassword").val();
    const confirmPassword = $("#confirmPassword").val();

    // Validasi
    if (!userId) {
      Swal.fire({
        icon: "warning",
        title: "Peringatan",
        text: "ID User tidak ditemukan",
      });
      return;
    }

    if (!newPassword) {
      Swal.fire({
        icon: "warning",
        title: "Peringatan",
        text: "Password baru wajib diisi",
      });
      $("#newPassword").focus();
      return;
    }

    if (newPassword.length < 8) {
      Swal.fire({
        icon: "warning",
        title: "Peringatan",
        text: "Password minimal 8 karakter",
      });
      $("#newPassword").focus();
      return;
    }

    if (newPassword !== confirmPassword) {
      Swal.fire({
        icon: "warning",
        title: "Peringatan",
        text: "Password baru dan konfirmasi password tidak sama",
      });
      $("#confirmPassword").focus();
      return;
    }

    // Tampilkan loading
    Swal.fire({
      title: "Menyimpan...",
      text: "Mohon tunggu sebentar",
      allowOutsideClick: false,
      didOpen: function () {
        Swal.showLoading();
      },
    });

    // Siapkan data
    const formData = new FormData();
    formData.append("id", userId);
    formData.append("newPassword", newPassword);

    // Kirim ke server
    $.ajax({
      url: "/setting/user/reset-password",
      type: "POST",
      data: formData,
      processData: false,
      contentType: false,
      success: function (response) {
        Swal.close();
        // console.log("Response:", response);

        // Reset form

        $("#newPassword").val("");
        $("#confirmPassword").val("");

        Swal.fire({
          icon: "success",
          title: "Berhasil!",
          text: response || "Password berhasil diubah",
          showCancelButton: true,
          confirmButtonText: "OK",
        });
      },
      error: function (xhr) {
        Swal.close();
        console.error("Error:", xhr);

        let errorMessage = xhr.responseText || "Terjadi kesalahan";
        try {
          const resp = JSON.parse(xhr.responseText);
          errorMessage = resp.responseMessage || resp.message || errorMessage;
        } catch (e) {
          // Jika response bukan JSON
        }

        Swal.fire({
          icon: "error",
          title: "Error",
          text: errorMessage,
        });
      },
    });
  });

  // ========== VALIDASI REAL-TIME ==========
  // Cek kekuatan password
  $("#newPassword").on("keyup", function () {
    const password = $(this).val();
    const strength = checkPasswordStrength(password);

    // Tampilkan indikator kekuatan password
    if (password.length > 0) {
      let color = "";
      let text = "";

      if (strength === "weak") {
        color = "#dc3545";
        text = "Weak";
      } else if (strength === "medium") {
        color = "#ffc107";
        text = "Medium";
      } else if (strength === "strong") {
        color = "#28a745";
        text = "Strong";
      }

      // Update indikator (tambahkan element jika belum ada)
      let indicator = $("#passwordStrength");
      if (indicator.length === 0) {
        $(this).after('<div id="passwordStrength" class="mt-1"></div>');
        indicator = $("#passwordStrength");
      }

      indicator.html(
        '<span style="color: ' +
          color +
          '; font-weight: bold;margin-left:10px;margin-right:10px;">Strength: ' +
          text +
          "</span>",
      );
    } else {
      $("#passwordStrength").remove();
    }
  });

  // Cek kesamaan password
  $("#confirmPassword").on("keyup", function () {
    const newPassword = $("#newPassword").val();
    const confirmPassword = $(this).val();

    let matchIndicator = $("#passwordMatch");
    if (matchIndicator.length === 0) {
      $(this).after('<div id="passwordMatch" class="mt-1"></div>');
      matchIndicator = $("#passwordMatch");
    }

    if (confirmPassword.length > 0) {
      if (newPassword === confirmPassword) {
        matchIndicator.html(
          '<span style="color: #28a745;margin-left:10px;margin-right:10px">✓ Password match</span>',
        );
      } else {
        matchIndicator.html(
          '<span style="color: #dc3545;margin-left:10px;margin-right:10px">✗ Password not match</span>',
        );
      }
    } else {
      matchIndicator.html("");
    }
  });

  // ========== FUNGSI CEK KEKUATAN PASSWORD ==========
  function checkPasswordStrength(password) {
    let strength = 0;

    // Cek panjang
    if (password.length >= 8) strength++;
    if (password.length >= 12) strength++;

    // Cek huruf kecil
    if (password.match(/[a-z]/)) strength++;

    // Cek huruf besar
    if (password.match(/[A-Z]/)) strength++;

    // Cek angka
    if (password.match(/[0-9]/)) strength++;

    // Cek karakter spesial
    if (password.match(/[^a-zA-Z0-9]/)) strength++;

    // Kategorikan
    if (strength <= 2) return "weak";
    if (strength <= 4) return "medium";
    return "strong";
  }


 $(document).on("click", ".toggle-password", function () {
   const $btn = $(this);
   const targetSelector = $btn.data("target");
   const $input = $("#" + targetSelector);

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
});



 