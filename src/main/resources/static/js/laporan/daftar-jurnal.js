$(document).ready(function () {
  $("#cabang").select2({
    theme: "bootstrap-5",
    width: "100%",
    allowClear: false,
    // dropdownParent: $("#modal1"),
  });
  $("#kodeTc").select2({
    theme: "bootstrap-5",
    width: "100%",
    allowClear: false,
    // dropdownParent: $("#modal1"),
  });

  $("#reversal").on("click", function () {
    //  alert("Fitur ini belum tersedia");//
    showToast("warning", "Maaf fitur ini belum tersedia");

    // Kembalikan checkbox ke keadaan semula (uncheck)
    $(this).prop("checked", false);
  });

  const table = new DataTable("#table-daftar-jurnal", {
    processing: true,
    serverSide: true,
    searching: false,
    paging: true,
    info: true,
    scrollX: true,
    pageLength: 10,
    lengthChange: false,
    ajax: {
      url: "/laporan/daftar-jurnal/datatables",
      type: "GET",
      data: function (d) {
        return {
          draw: d.draw,
          start: d.start,
          length: 20,
          orderColumn: d.order[0] ? d.order[0].column : 1,
          orderDir: d.order[0] ? d.order[0].dir : "desc",
          fromDate: $("#tanggalDari").val(),
          toDate: $("#tanggalSampai").val(),
          branchCode: $("#cabang").val(),
          transactionCode: $("#kodeTc").val(),
          statusReversal: $("#pembalikan").val(),
        };
      },
    },

  
    columns: [
      {
        data: null,
        orderable: false,
        searchable: false,
        className: "text-center align-middle",
        render: function (data, type, row) {
          return `
                  <input type="checkbox" class="row-checkbox form-check-input m-0 align-middle" value="${row.id}">
              `;
        },
      },
      {
        className: "dt-control",
        orderable: false,
        data: null,
        defaultContent: "",
      },
      { data: "journalNo" },
      { data: "transactionNo", visible: false },
      { data: "transactionCode", visible: false },
      {
        data: "branchCode",
        render: function (data, type, row) {
          return `
                      <div>
                          <div>${data}</div>
                          <div class="text-muted small">${row.namaCabang}</div>
                      </div>`;
        },
      },
      {
        data: "transactionDate",
        render: function (data, type, row) {
          if (!data) {
            return "";
          }

          const date = new Date(data);

          const bulan = [
            "Januari",
            "Februari",
            "Maret",
            "April",
            "Mei",
            "Juni",
            "Juli",
            "Agustus",
            "September",
            "Oktober",
            "November",
            "Desember",
          ];

          const hari = String(date.getDate()).padStart(2, "0");
          const bulanNama = bulan[date.getMonth()];
          const tahun = date.getFullYear();

          const jam = String(date.getHours()).padStart(2, "0");
          const menit = String(date.getMinutes()).padStart(2, "0");
          const detik = String(date.getSeconds()).padStart(2, "0");

          const tanggal = `${hari} ${bulanNama} ${tahun}`;
          const waktu = `${jam}:${menit}:${detik}`;

          return `
            <div>
                <div>${tanggal}</div>
                <div class="text-muted small">${waktu}</div>
            </div>
        `;
        },
      },
      {
        data: "totalDebit",
        className: "text-end",
        render: function (data, type, row) {
          if (data == null || data === "") {
            return "0.00";
          }
          return Number(data).toLocaleString("id-ID", {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
          });
        },
      },
      {
        data: "totalCredit",
        className: "text-end",
        render: function (data, type, row) {
          if (data == null || data === "") {
            return "0.00";
          }
          return Number(data).toLocaleString("id-ID", {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
          });
        },
      },
      { data: "reversalStatus" },
      {
        data: null,
        render: function (data, type, row) {
          //console.info(row);
          let reversalAction = "";
          if (row.reversalStatus === "NORMAL") {
            reversalAction = `
                <a class="btn btn-ajukan-pembalikan bg-success text-white"
                   href="#"
                   data-id="${row.id}"
                   data-noj="${row.journalNo}">
                    Ajukan Pembalikan
                </a>
            `;
          } else if (row.reversalStatus === "SUDAH DIREVERSAL") {
            reversalAction = `
                <span class="badge bg-warning text-white">
                    Sudah Direversal
                </span>
            `;
          } else if (row.reversalStatus === "REVERSAL") {
            reversalAction = `
                <span class="badge bg-secondary text-white"">
                    Reversal
                </span>
            `;
          }

          return `
            <div style="display: flex; gap: 15px; align-items: center;">

                <a class="btn btn-detail"
                   href="#"
                   data-noj="${row.journalNo}">
                    Detail
                </a>

                ${reversalAction}

            </div>
        `;
        },

        orderable: false,
      },
    ],
    //order: [[2, "desc"]],
    drawCallback: function () {
      // calculateAmount();
      // Uncheck "Select All" setiap kali halaman berganti / redraw
      $("#check-all").prop("checked", false);
    },
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

  // Add event listener for opening and closing details
  table.on("click", "tbody td.dt-control", function (e) {
    let tr = e.target.closest("tr");
    let row = table.row(tr);

    if (row.child.isShown()) {
      // This row is already open - close it
      row.child.hide();
    } else {
      // Open this row
      row.child(format(row.data())).show();
    }
  });

  // function calculateAmount() {
  //   let total = 0;

  //   $("#table-daftar-jurnal tbody tr").each(function () {
  //     const valueText = $(this).find("td").eq(6).text().trim();

  //     const value = parseFloat(valueText.replace(/\./g, "").replace(",", "."));

  //     if (!isNaN(value)) {
  //       total += value;
  //     }
  //   });

  //   $("#total-amount").text(
  //     total.toLocaleString("id-ID", {
  //       minimumFractionDigits: 2,
  //       maximumFractionDigits: 2,
  //     }),
  //   );
  // }

  $("#btn-filter").click(function () {
    $("#table-daftar-jurnal").DataTable().ajax.reload();
  });
  $("#btn-reset").click(function () {
    $("#periode").val("");
    $("#cabang").val("");
    $("#kodeTc").val("");
    $("#table-daftar-jurnal").DataTable().ajax.reload();
  });
  $("#btn-export-exc").click(function () {
    const params = new URLSearchParams();
    // Ambil semua nilai input
    const fromDate = $("#tanggalDari").val();
    const toDate = $("#tanggalSampai").val();
    const cabang = $("#cabang").val();
    const kodeTc = $("#kodeTc").val();
    const pembalikan = $("#pembalikan").val();
    const selectedIds = getSelectedIds();
   if (selectedIds && selectedIds.length > 0) {
     params.append("ids", JSON.stringify(selectedIds));
   }
    // Append hanya jika value tidak kosong
    if (fromDate) params.append("fromDate", fromDate);
    if (toDate) params.append("toDate", toDate);
    if (cabang) params.append("cabang", cabang); 
    if (kodeTc) params.append("kodeTc", kodeTc);
    if (pembalikan) params.append("pembalikan", pembalikan);
   const baseUrl = "/laporan/daftar-jurnal/export/excel";
   const fullUrl = params.toString()
     ? `${baseUrl}?${params.toString()}`
     : baseUrl;

  //  console.info("Generated URL:", fullUrl);

   // Redirect ke backend untuk download Excel
   window.location.href = fullUrl;

    // $.get(
    //   "/laporan/daftar-jurnal/export/count?" + params.toString(),
    //   function (resp) {
    //     if (resp.total > 6000) {
    //       var te =
    //         "Jumlah data yang akan diexport adalah " +
    //         resp.total +
    //         " baris. Maksimal export hanya 6000 baris. Silakan perkecil filter pencarian.";
    //       showToast("error", te);
    //     } else {
    //       window.location.href =
    //         "/laporan/daftar-jurnal/export/excel?" + params.toString();
    //     }
    //   },
    // );
  });

  $(document).on("click", ".btn-detail", function (e) {
    e.preventDefault();
    const noj = $(this).data("noj");
    window.open("/laporan/daftar-jurnal/detail/" + noj, "_blank");
  });

  $(document).on("click", ".btn-ajukan-pembalikan", function (e) {
    e.preventDefault();

    const journalId = $(this).data("id");
    const journalNo = $(this).data("noj");

    $("#reversalJournalId").val(journalId);
    $("#reversalJournalNo").val(journalNo);

    $("#reversalReason").val("");
    $("#reversalDescription").val("");
    $("#reversalError").addClass("d-none").text("");

    const modal = new bootstrap.Modal(
      document.getElementById("reversalRequestModal"),
    );

    modal.show();
  });

  $("#submitReversalRequest").on("click", function () {
    const journalId = $("#reversalJournalId").val();
    const reason = $("#reversalReason").val().trim();
    const description = $("#reversalDescription").val().trim();

    if (!reason) {
      $("#reversalRequestError")
        .removeClass("d-none")
        .text("Alasan pembalikan wajib diisi.");

      return;
    }

    const button = $(this);

    button.prop("disabled", true);

    $.ajax({
      url: "/operasional/persetujuan/request",
      type: "POST",
      data: {
        journalId: journalId,
        reason: reason,
        description: description,
      },

      success: function (response) {
        if (response.success) {
          bootstrap.Modal.getInstance(
            document.getElementById("reversalRequestModal"),
          ).hide();

          // alert(response.message);
          showToast("success", response.message);
          // reload DataTable
          $("#table-daftar-jurnal").DataTable().ajax.reload(null, false);
        } else {
          // $("#reversalRequestError")
          //   .removeClass("d-none")
          //   .text(response.message);
          showToast("warning", response.message);
        }
      },

      error: function (xhr) {
        let message = "Terjadi kesalahan.";
        //console.info(xhr.responseJSON.message);
        if (xhr.responseJSON && xhr.responseJSON.message) {
          message = xhr.responseJSON.message;
        }

        // $("#reversalRequestError").removeClass("d-none").text(message);
        showToast("warning", message);
      },

      complete: function () {
        button.prop("disabled", false);
      },
    });
  });

  //
  //  $("#btn-export-exc").on("click", function () {
  //   showToast("warning","TES")
  //  });
  // 1. Event Centang Semua (Select All)
  $("#check-all").on("click", function () {
    const isChecked = $(this).is(":checked");
    $(".row-checkbox").prop("checked", isChecked);
  });

  // 2. Event Uncheck "Select All" jika salah satu checkbox baris di-uncheck
  $("#table-daftar-jurnal tbody").on("click", ".row-checkbox", function () {
    if (!$(this).is(":checked")) {
      $("#check-all").prop("checked", false);
    }
  });

  // 3. Fungsi Helper untuk Mengambil Nilai ID Baris yang Dicentang
  function getSelectedIds() {
    const selectedIds = [];
    $(".row-checkbox:checked").each(function () {
      selectedIds.push($(this).val());
    });
    return selectedIds; // Mengembalikan array ID yang dipilih, misal: ["12", "15"]
  }
});

function format(d) {
  // `d` is the original data object for the row
  return (
    "<dl>" +
    "<dt>Transaction No:</dt>" +
    "<dd>" +
    d.transactionNo +
    "</dd>" +
    "<dt>Transaction Code:</dt>" +
    "<dd>" +
    d.transactionCode +
    "</dd>" +
    "<dt></dt>" +
    "<dd></dd>" +
    "</dl>"
  );
}

