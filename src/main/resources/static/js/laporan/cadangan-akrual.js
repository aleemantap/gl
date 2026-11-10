$(document).ready(function () {
  $("#kodeCabang").select2({
    theme: "bootstrap-5",
    width: "100%",
    allowClear: false,
    // dropdownParent: $("#modal1"),
  });

  $("#formProsesCkpn").on("submit", function (e) {
    e.preventDefault();

    const periode = $("#periode").val();
    const cabangId = $("#kodeCabang").val();

    const kodeCabang = $("#kodeCabang option:selected").data("kode");

    if (!periode || !cabangId) {
      alert("Periode dan cabang wajib dipilih.");
      return;
    }

    const $btn = $("#btnProses");

    $.ajax({
      url: "/laporan/cadangan-akrual/proses-ajax",
      type: "POST",
      data: {
        periode: periode,
        cabangId: cabangId,
        kodeCabang: kodeCabang,
      },
      beforeSend: function () {
        $btn.prop("disabled", true);
        $btn.html(
          '<span class="spinner-border spinner-border-sm me-1"></span> Memproses...',
        );
      },
      success: function (response) {
        console.log("Preview CKPN:", response);
        tampilkanPreviewCkpn(response);
      },
      error: function (xhr) {
        let message = "Terjadi kesalahan saat memproses pencadangan.";

        if (xhr.responseJSON && xhr.responseJSON.message) {
          message = xhr.responseJSON.message;
        } else if (xhr.responseText) {
          try {
            const response = JSON.parse(xhr.responseText);
            message = response.message || message;
          } catch (e) {
            // Gunakan pesan default
          }
        }

        Swal.fire({
          icon: "warning",
          title: "Data Tidak Ditemukan",
          text: message,
          confirmButtonText: "Mengerti",
        });
      },
      complete: function () {
        $btn.prop("disabled", false);
        $btn.html('<i class="ti ti-refresh me-1"></i> Proses');
      },
    });
  });

  $("#formPostingCkpn").on("submit", function () {
    const $btn = $(this).find('button[type="submit"]');

    $btn.prop("disabled", true);

    $btn.html(`
        <span class="spinner-border spinner-border-sm me-1"></span>
        Posting...
    `);
  });
});

function tampilkanPreviewCkpn(response) {
  // Isi informasi preview
  $("#previewPeriode").text(response.periode);
  $("#previewCabang").text(response.namaCabang);
  $("#previewReferensi").text(response.referensiLaporan);

  $("#previewNominal").text(
    new Intl.NumberFormat("id-ID", {
      style: "currency",
      currency: "IDR",
      maximumFractionDigits: 0,
    }).format(response.nominalJurnal),
  );

  $("#previewArahJurnal").text(response.arahJurnal);

  // Isi hidden input untuk proses posting
  $("#postingPeriode").val(response.periode);
  $("#postingCabangId").val(response.cabangId);
  $("#postingKodeCabang").val(response.kodeCabang);

  // Tampilkan modal
  const modal = new bootstrap.Modal(document.getElementById("modalPosting"));

  modal.show();
}