$(document).ready(function () {
  // Inisialisasi Select2
  $("#cabangId").select2({
    theme: "bootstrap-5",
    width: "100%",
    allowClear: false,
  });

  $("#kasirId").select2({
    theme: "bootstrap-5",
    width: "100%",
    allowClear: false,
  });

  $("#kasPoint").select2({
    theme: "bootstrap-5",
    width: "100%",
    allowClear: false,
  });

  $("#jenisTransaksi").select2({
    theme: "bootstrap-5",
    width: "100%",
    allowClear: false,
    placeholder: "-- Pilih Jenis Transaksi --",
  });
 const urlJenisTransaksi =
   "input-transaksi-manual/search-jenis-transaksi";
  
 
  loadJenisTransaksi();
  // URL endpoint jenis transaksi
  // Ambil nilai tipe transaksi
  function getTipeTransaksi() {
    return $('input[name="tipe"]:checked').val();
  }

  // Ambil nilai arah kas
  function getArahKas() {
    return $('input[name="arahKas"]:checked').val();
  }

  // Load jenis transaksi berdasarkan filter
  function loadJenisTransaksi() {
    const tipeTransaksi = getTipeTransaksi();
    const arahKas = getArahKas();

    console.log("Parameter AJAX:", {
      tipeTransaksi: tipeTransaksi,
      arahKas: arahKas,
    });

    const combo = $("#jenisTransaksi");

    // Reset combo
    combo.empty();
    combo.append(new Option("-- Pilih Jenis Transaksi --", ""));

    combo.val(null).trigger("change.select2");

    // Filter belum lengkap
    if (!tipeTransaksi || !arahKas) {
      console.log("Filter belum lengkap.");
      return;
    }

    // Request AJAX
    $.ajax({
      url: urlJenisTransaksi,
      type: "GET",
      dataType: "json",
      data: {
        tipeTransaksi: tipeTransaksi,
        arahKas: arahKas,
      },

      beforeSend: function () {
        console.log("Mengirim request AJAX...");
      },

      success: function (response) {
        console.log("AJAX BERHASIL:", response);
        console.log("Jumlah data:", response.length);

        if (response && response.length > 0) {
          $.each(response, function (index, item) {
            combo.append(
              new Option(item.kode + " - " + item.namaTransaksi, item.id),
            );
          });
        } else {
          console.log("Data jenis transaksi kosong.");
        }

        combo.val(null).trigger("change.select2");
      },

      error: function (xhr, status, error) {
        console.error("AJAX ERROR:", status, error);
        console.error("HTTP Status:", xhr.status);
        console.error("Response:", xhr.responseText);
      },
    });
  }

  // Ketika tipe transaksi berubah
  $('input[name="tipe"]').on("change", function () {
    console.log("Tipe transaksi berubah:", getTipeTransaksi());
    loadJenisTransaksi();
  });

  // Ketika arah kas berubah
  $('input[name="arahKas"]').on("change", function () {
    console.log("Arah kas berubah:", getArahKas());
    loadJenisTransaksi();
  });

  // Ketika cabang berubah
  $("#cabangId").on("change", function () {
    console.log("Cabang berubah:", $(this).val());
  });
});
