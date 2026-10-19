// global.js
window.showToast = function (type, message) {
  // Gunakan Bootstrap Toast jika ada
  var toastHtml = `
        <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 9999;">
            <div class="toast align-items-center text-white bg-${type === "success" ? "success" : "danger"} border-0 show" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="d-flex">
                    <div class="toast-body">
                        ${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
            </div>
        </div>`;

  // Hapus toast sebelumnya
  $(".toast-container").remove();

  // Tambahkan toast baru
  $("body").append(toastHtml);

  // Auto hide setelah 3 detik
  setTimeout(function () {
    $(".toast-container").fadeOut(function () {
      $(this).remove();
    });
  }, 3000);
};

function showConfirm(title,message, callback) {
  // Hapus modal lama jika ada
  $("#confirmModal").remove();

  // Buat modal
  const modalHtml =
    `
        <div class="modal fade" id="confirmModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title display-1">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>
                            ` +
                            title +
                            `
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body py-4">
                        <p class="mb-0 fs-2">${message}</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Batal</button>
                        <button type="button" class="btn btn-danger" id="confirmYes">Ya!</button>
                    </div>
                </div>
            </div>
        </div>
    `;

  $("body").append(modalHtml);

  // Tampilkan modal
  const modal = new bootstrap.Modal(document.getElementById("confirmModal"));
  modal.show();

  // Event klik tombol Ya
  $("#confirmYes").on("click", function () {
    modal.hide();
    if (typeof callback === "function") {
      callback();
    }
  });

  // Hapus modal dari DOM setelah ditutup
  $("#confirmModal").on("hidden.bs.modal", function () {
    $(this).remove();
  });
}

window.showConfirm = showConfirm;

// alert pop up

function showAlert(type, message) {
  // Tentukan warna berdasarkan type
  var styles = {
    success: {
      bg: "#d4edda",
      color: "#155724",
      border: "#c3e6cb",
      icon: "✅",
    },
    danger: {
      bg: "#f8d7da",
      color: "#721c24",
      border: "#f5c6cb",
      icon: "❌",
    },
    warning: {
      bg: "#fff3cd",
      color: "#856404",
      border: "#ffeeba",
      icon: "⚠️",
    },
    info: {
      bg: "#d1ecf1",
      color: "#0c5460",
      border: "#bee5eb",
      icon: "ℹ️",
    },
  };

  var style = styles[type] || styles.success;

  // Hapus alert sebelumnya
  $(".custom-alert-floating").remove();

  // Buat alert baru - POP UP PALING ATAS
  var alertHtml = `
        <div class="custom-alert-floating" style="
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            background: ${style.bg};
            color: ${style.color};
            padding: 18px 50px 18px 25px;
            border: 2px solid ${style.border};
            border-radius: 10px;
            font-size: 16px;
            font-weight: 500;
            z-index: 999999;
            min-width: 350px;
            max-width: 80%;
            box-shadow: 0 8px 30px rgba(0,0,0,0.2);
            animation: slideDown 0.5s ease;
            text-align: left;
        ">
            <span style="font-size: 20px; margin-right: 10px;">${style.icon}</span>
            ${message}
            <button onclick="$(this).parent().fadeOut('fast', function(){$(this).remove()})" style="
                position: absolute;
                right: 12px;
                top: 50%;
                transform: translateY(-50%);
                background: none;
                border: none;
                font-size: 26px;
                cursor: pointer;
                color: ${style.color};
                opacity: 0.6;
                padding: 0 5px;
                line-height: 1;
            ">×</button>
        </div>
    `;

  // Tambahkan CSS animation jika belum ada
  if ($("#alert-animation-style").length === 0) {
    $("head").append(`
            <style id="alert-animation-style">
                @keyframes slideDown {
                    from {
                        opacity: 0;
                        transform: translateX(-50%) translateY(-30px);
                    }
                    to {
                        opacity: 1;
                        transform: translateX(-50%) translateY(0);
                    }
                }
                @keyframes slideUp {
                    from {
                        opacity: 1;
                        transform: translateX(-50%) translateY(0);
                    }
                    to {
                        opacity: 0;
                        transform: translateX(-50%) translateY(-30px);
                    }
                }
            </style>
        `);
  }

  // Tambahkan ke body
  $("body").append(alertHtml);

  // Auto hilang setelah 5 detik
  clearTimeout(window.alertTimer);
  window.alertTimer = setTimeout(function () {
    $(".custom-alert-floating").fadeOut("slow", function () {
      $(this).remove();
    });
  }, 5000);
}

window.showAlert = showAlert;