let selectedRejectId = null;

// =========================================
// APPROVE
// =========================================

$(document).on("click", ".btn-approve", function () {
  const id = $(this).data("id");

  if (
    !confirm("Apakah Anda yakin ingin menyetujui permintaan pembalikan ini?")
  ) {
    return;
  }

  $.ajax({
    url: "/operasional/persetujuan/approve/" + id,
    type: "POST",

    success: function (response) {
      if (response.success) {
        alert(response.message);

        location.reload();
      } else {
        alert(response.message);
      }
    },

    error: function (xhr) {
      let message = "Gagal menyetujui permintaan.";

      if (xhr.responseJSON && xhr.responseJSON.message) {
        message = xhr.responseJSON.message;
      }

      alert(message);
    },
  });
});

// =========================================
// OPEN REJECT MODAL
// =========================================

$(document).on("click", ".btn-reject", function () {
  selectedRejectId = $(this).data("id");

  $("#rejectionReason").val("");

  $("#rejectError").addClass("d-none");

  const modal = new bootstrap.Modal(document.getElementById("rejectModal"));

  modal.show();
});

// =========================================
// CONFIRM REJECT
// =========================================

$("#confirmReject").on("click", function () {
   const reason = $("#rejectionReason").val().trim();

   if (!reason) {
     $("#rejectError")
       .removeClass("d-none")
       .text("Alasan penolakan wajib diisi.");

     return;
   }


  $.ajax({
    url: `/operasional/persetujuan/reject/${selectedRejectId}`,

    type: "POST",

    data: {
      rejectionReason: reason,
    },

    success: function (response) {
      if (response.success) {
        location.reload();
      } else {
        $("#rejectError").removeClass("d-none").text(response.message);
      }
    },

    error: function (xhr) {
      let message = "Gagal menolak permintaan.";

      if (xhr.responseJSON && xhr.responseJSON.message) {
        message = xhr.responseJSON.message;
      }

      $("#rejectError").removeClass("d-none").text(message);
    },
  });

 
});

let currentReversalRequestId = null;
 function formatRupiah(value) {
   return new Intl.NumberFormat("id-ID", {
     style: "currency",
     currency: "IDR",
     minimumFractionDigits: 2,
   }).format(value || 0);
 }
function showReversalDetail(id) {
  currentReversalRequestId = id;

  const modalElement = document.getElementById("reversalDetailModal");

  const modal = bootstrap.Modal.getOrCreateInstance(modalElement);

  document.getElementById("reversalDetailLoading").style.display = "block";

  document.getElementById("reversalDetailContent").style.display = "none";

  modal.show();

  fetch(`/operasional/persetujuan/detail/${id}`)
    .then((response) => {
      if (!response.ok) {
        throw new Error("Gagal mengambil detail reversal.");
      }

      return response.json();
    })
    .then((data) => {
      renderReversalDetail(data);

      document.getElementById("reversalDetailLoading").style.display = "none";

      document.getElementById("reversalDetailContent").style.display = "block";
    })
    .catch((error) => {
      document.getElementById("reversalDetailLoading").innerHTML = `
            <div class="alert alert-danger">
                ${error.message}
            </div>
        `;
    });
}

function renderReversalDetail(data) {
  document.getElementById("detailJournalNo").textContent =
    data.journalNo || "-";

  document.getElementById("detailTransactionNo").textContent =
    data.transactionNo || "-";

  document.getElementById("detailCabang").textContent =
    `${data.cabangCode || "-"} - ${data.cabangName || "-"}`;

  document.getElementById("detailRequestedBy").textContent =
    data.requestedBy || "-";

  document.getElementById("detailRequestedAt").textContent =
    data.requestedAt || "-";

  document.getElementById("detailReason").textContent = data.reason || "-";

  document.getElementById("detailDescription").textContent =
    data.description || "-";

  document.getElementById("detailStatus").textContent = data.status || "-";

  // Journal Detail
  const journalBody = document.getElementById("detailJournalBody");

  journalBody.innerHTML = "";

  (data.journalDetails || []).forEach((detail) => {
    const row = document.createElement("tr");

    row.innerHTML = `
            <td>
                <div class="fw-semibold">
                    ${detail.nomorGl || "-"}
                </div>

                <small class="text-muted">
                    ${detail.namaGlAccount || "-"}
                </small>
            </td>

            <td class="text-center">
                <span class="badge ${
                  detail.dk === "D"
                    ? "bg-primary text-white"
                    : "bg-secondary text-white"
                }">
                    ${detail.dk}
                </span>
            </td>

            <td class="text-end">
                ${formatRupiah(detail.amount)}
            </td>

            <td>
                ${detail.description || "-"}
            </td>
        `;

    journalBody.appendChild(row);
  });

  document.getElementById("detailTotalDebit").textContent = formatRupiah(
    data.totalDebit,
  );

  document.getElementById("detailTotalCredit").textContent = formatRupiah(
    data.totalCredit,
  );

  // Balance
  const balance = document.getElementById("detailBalance");

  const debit = Number(data.totalDebit || 0);

  const credit = Number(data.totalCredit || 0);

  if (debit === credit) {
    balance.className = "alert alert-success mt-3 mb-0";

    balance.innerHTML = `
            <i class="bi bi-check-circle me-2"></i>
            <strong>Jurnal Balance</strong>
            — Total debit dan kredit sama.
        `;
  } else {
    balance.className = "alert alert-danger mt-3 mb-0";

    balance.innerHTML = `
            <i class="bi bi-exclamation-triangle me-2"></i>
            <strong>Jurnal Tidak Balance</strong>
            — Debit dan kredit berbeda.
        `;
  }

  // Cash Movement
  const cashBody = document.getElementById("detailCashBody");

  cashBody.innerHTML = "";

  const movements = data.cashMovements || [];

  if (movements.length === 0) {
    cashBody.innerHTML = `
            <tr>
                <td colspan="5"
                    class="text-center text-muted py-3">
                    Tidak ada pergerakan kas.
                </td>
            </tr>
        `;
  } else {
    movements.forEach((movement) => {
      const row = document.createElement("tr");

      const directionClass =
        movement.direction === "IN" ? "bg-success text-white" : "bg-danger text-white";

      row.innerHTML = `
                <td>
                    <span class="badge ${directionClass}">
                        ${movement.direction}
                    </span>
                </td>

                <td class="text-end">
                    ${formatRupiah(movement.amount)}
                </td>

                <td>
                    ${movement.kasPointCode || "-"}
                </td>

                <td>
                    ${movement.counterpartyName || "-"}
                </td>

                <td>
                    ${movement.description || "-"}
                </td>
            `;

      cashBody.appendChild(row);
    });
  }

  /*
   * Tombol approval hanya aktif jika
   * request masih MENUNGGU.
   */
  const isWaiting = data.status === "MENUNGGU";

  document.getElementById("detailApproveButton").style.display = isWaiting
    ? "inline-block"
    : "none";

  document.getElementById("detailRejectButton").style.display = isWaiting
    ? "inline-block"
    : "none";
}

document
  .getElementById("detailApproveButton")
  .addEventListener("click", function () {
    if (!currentReversalRequestId) {
      return;
    }

    if (!confirm("Yakin ingin menyetujui pembalikan jurnal ini?")) {
      return;
    }

    approveReversal(currentReversalRequestId);
  });

document
  .getElementById("detailRejectButton")
  .addEventListener("click", function () {
    if (!currentReversalRequestId) {
      return;
    }

    showRejectModal(currentReversalRequestId);
  });