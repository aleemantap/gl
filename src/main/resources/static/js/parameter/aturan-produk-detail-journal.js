$(document).ready(function () {
  jurnalCode("#journalId_j");
  const table = new DataTable("#table-component-rule-journal", {
    processing: true,
    serverSide: true,
    searching: false,
    paging: true,
    info: true,
    scrollX: false,
    pageLength: 5,
    lengthChange: false,
    order: [[0, "desc"]],
    ajax: {
      url: "/parameter/aturan-produk-detail/rule-journal/datatables",
      type: "GET",
      data: function (d) {
        return {
          draw: d.draw,
          start: d.start,
          length: 5,
          orderColumn: d.order[0] ? d.order[0].column : 1,
          orderDir: d.order[0] ? d.order[0].dir : "desc",
          product_rule_id: 1,
          search: $("#searchInput").val(),
          active: $("#statusFilter").val(),
        };
      },
    },
    columns: [
      { data: "journalId" },
      { data: "sequenceNo" },
      { data: "isActive" },
      {
        data: null,
        render: function (data, type, row) {
          return `
                  <div style="display: flex; gap: 15px;">
                      <a class="btn-ubah-journal" href="#" data-id="${row.id}">
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

   $(document).on("click", ".btn-ubah-journal", function (e) {
     e.preventDefault();
     const id = $(this).data("id");
     $("#modal-detail-rule-journal").modal("show");
     ubahJournal(id);
   });


  $(document).on("click", "#btn-add-produk-rule-journal", function (e) {
    e.preventDefault();
    //const id = $(this).data("id");
    //$("#viewModalLabel").text("Edit Product Rule");
    $("#modal-detail-rule-journal").modal("show");
    //  alert("TES");
  });
  $(document).on("click", "#btnSaveRuleJournal", function (e) {
    e.preventDefault();
    // 1. Validasi bawaan HTML5 (misal: input required)
    const $form = $("#journalForm");
    if (!$form[0].checkValidity()) {
      $form[0].reportValidity();
      return;
    }

    var editId = $("#id_tabel_prj").val();
    var url =  "/parameter/aturan-produk-detail/rule-journal";
    if (editId) {
    url = "/parameter/aturan-produk-detail/rule-journal/update/" + editId;
    }

    // 2. Kumpulkan data dari form
    const formData = {
      id: $("#id_tabel_prj").val(),
      productRuleId: $("#productRuleJournalId").val(),
      journalId: $("#journalId_j").val(),
      sequenceNo: $("#sequenceNo").val(),
      isActive: $("#isActiveJournal").val() === 1 ? true : false,
    };

    // 3. Kirim data via AJAX
    $.ajax({
      url:url,
      type: "POST",
      contentType: "application/json; charset=utf-8",
      // Hapus jika backend menggunakan form-data standar
      dataType: "json",
      data: JSON.stringify(formData), // Gunakan $('#journalForm').serialize() jika pakai form-urlencoded

      beforeSend: function () {
        // Disable tombol simpan dan tampilkan spinner
        $("#btnSaveRuleJournal")
          .prop("disabled", true)
          .html('<i class="fas fa-spinner fa-spin"></i> Menyimpan...');
      },
      success: function (response) {
        // Tutup modal dan beri notifikasi
        $("#modal-detail-rule-journal").modal("hide");
        // alert("Data Rule Journal berhasil disimpan!");
        $("#able-component-rule-journal").DataTable().ajax.reload(null, false);
        showToast("success", "Data berhasil disimpan");

        // Reset form
        $form[0].reset();
      },
      error: function (xhr, status, error) {
        console.error("Error:", error);
        // alert(
        //   "Gagal menyimpan data: " +
        //     (xhr.responseJSON?.message || "Terjadi kesalahan pada server."),
        // );
        showToast(
          "error",
          xhr.responseJSON?.message || "Terjadi kesalahan pada server.",
        );
      },
      complete: function () {
        // Kembalikan status tombol simpan
        $("#btnSaveRuleJournal")
          .prop("disabled", false)
          .html('<i class="fas fa-save"></i> Simpan');
      },
    });
  });
});
function jurnalCode(id) {
  $.ajax({
    url: "/parameter/pemetaan-akun/journal-code/list",
    type: "GET",
    dataType: "json",
    success: function (response) {
      //console.log("Journal Code Response:", response);
      var select = $(id);
      select.empty();
      select.append('<option value="">Pilih Journal Code</option>');

      if (response && response.length > 0) {
        $.each(response, function (index, item) {
          select.append(
            '<option value="' +
              item.id +
              '">' +
              item.journalCode +
              " - " +
              (item.description ?? "") +
              "</option>",
          );
        });
      }
    },
    error: function (xhr, status, error) {
      console.error("Error loading journal code:", error);
      showToast("error", "Gagal memuat data journal code");
    },
  });
}

function ubahJournal(id) {
  $.ajax({
    url: "/parameter/aturan-produk-detail/rule-journal/get/" + id,
    type: "GET",
    success: function (response) {
      if (response.success) {
        var rule = response.data;
        console.info(rule);
        $("#id_tabel_prj").val(rule.id || "");  
        $("#productRuleJournalId").val(rule.productRuleId || "");         
        let isa = rule.isActive ? 1 : 0;
        $("#isActiveJournal").val(isa);
        $("#sequenceNo").val(rule.sequenceNo || "");
        $("#journalId_j").val(rule.journalId || "");
      
      } else {
        showToast("error", response.message || "Failed to load data");
      }
    },
    error: function (xhr) {
      showToast(
        "error",
        xhr.responseJSON?.message || "Failed to load product rule data",
      );
    },
  });
}