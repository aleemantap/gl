$(document).ready(function () {
  const table = new DataTable("#transactionTable", {
    processing: true,
    serverSide: true,
    searching: false,
    paging: true,
    scrollX: true,
    info: true,
    pageLength: 10,
    lengthChange: false,
    ajax: {
      url: "/operasional/log-transaksi-ditolak/datatable",
      type: "GET",
      data: function (d) {
        return {
          draw: d.draw,
          start: d.start,
          length: d.length || 10,
          orderColumn: d.order[0] ? d.order[0].column : 0,
          orderDir: d.order[0] ? d.order[0].dir : "desc",
          status: $("#statusFilter").val(),
          fromDate: $("#fromDateFilter").val(),
          toDate: $("#toDateFilter").val(),
        };
      },
      error: function (xhr, error, thrown) {
        console.error("DataTable error:", error);
        Swal.fire({
          icon: "error",
          title: "Error!",
          text: "Gagal memuat data: " + error,
          timer: 3000,
        });
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
        data: "transactionTime",
        title: "Transaction Time",
        render: function (data) {
          if (data) {
            let date = new Date(data);
            return date.toLocaleString("id-ID", {
              year: "numeric",
              month: "2-digit",
              day: "2-digit",
              hour: "2-digit",
              minute: "2-digit",
              second: "2-digit",
            });
          }
          return "-";
        },
      },
      {
        data: "branchCode",
        name: "branchCode",
        title: "Branch Code",
      },
      {
        data: "transactionNo",
        name: "transactionNo",
        title: "Transaction No",
      },
      {
        data: "transactionCode",
        name: "transactionCode",
        title: "Transaction Code",
      },
      {
        data: "rejectionReason",
        name: "rejectionReason",
        title: "Rejection Reason",
      },
      {
        data: "payload",
        name: "payload",
        title: "Payload",
        render: function (data, type, row) {
          if (!data) return "-";

          // PERBAIKAN SYNTAX HTML
          return `<button type="button" class="btn btn-sm btn-info btn-view-payload"><i class="fas fa-eye"></i> View Payload</button>`;
        },
        orderable: false,
      },
      {
        data: "status",
        name: "status",
        title: "Status",
        render: function (data) {
          let badgeClass = data === "REJECTED" ? "bg-info" : "bg-info";
          return '<span class="badge">' + data + "</span>";
        },
      },
    ],
    drawCallback: function () {
      $("#check-all").prop("checked", false);
    },
    order: [[0, "desc"]],
    pageLength: 10,
    lengthMenu: [10, 25, 50, 100],
    responsive: true,
    language: {
      processing: '<i class="fas fa-spinner fa-spin fa-2x"></i>',
      search: '<i class="fas fa-search"></i>',
      lengthMenu: "Show _MENU_ entries",
      info: "Showing _START_ to _END_ of _TOTAL_ entries",
      infoEmpty: "No entries found",
      infoFiltered: "(filtered from _MAX_ total entries)",
      zeroRecords: "No matching records found",
    },
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

  // Event handler untuk tombol View Payload (Membaca data langsung dari DataTable Row)
  $("#transactionTable").on("click", ".btn-view-payload", function () {
    // Ambil data dari baris/row DataTable
    let rowData = table.row($(this).closest("tr")).data();

    if (!rowData) return;

    let payloadData = rowData.payload;
    let transactionNo = rowData.transactionNo;

    // Set transaction number
    $("#modalTransactionNo").text(transactionNo || "-");

    // Display payload dengan formatting
    try {
      // Jika payloadData sudah dalam bentuk objek, langsung stringify
      let jsonObj =
        typeof payloadData === "object" ? payloadData : JSON.parse(payloadData);
      let formattedJson = JSON.stringify(jsonObj, null, 2);
      $("#payloadJson").text(formattedJson);
      $("#payloadJson").addClass("json-formatted");
    } catch (e) {
      // Jika bukan JSON, tampilkan sebagai string
      $("#payloadJson").text(payloadData);
      $("#payloadJson").removeClass("json-formatted");
    }

    // Show modal
    $("#payloadModal").modal("show");
  });

  // Copy to Clipboard
  $("#copyPayloadBtn").on("click", function () {
    let payloadText = $("#payloadJson").text();
    navigator.clipboard
      .writeText(payloadText)
      .then(function () {
        Swal.fire({
          icon: "success",
          title: "Copied!",
          text: "Payload copied to clipboard",
          timer: 2000,
          showConfirmButton: false,
        });
      })
      .catch(function (err) {
        // Fallback untuk browser yang tidak support clipboard API
        let textarea = document.createElement("textarea");
        textarea.value = payloadText;
        document.body.appendChild(textarea);
        textarea.select();
        document.execCommand("copy");
        document.body.removeChild(textarea);
        Swal.fire({
          icon: "success",
          title: "Copied!",
          text: "Payload copied to clipboard",
          timer: 2000,
          showConfirmButton: false,
        });
      });
  });

  // Format JSON
  $("#formatPayloadBtn").on("click", function () {
    let currentText = $("#payloadJson").text();
    try {
      let jsonObj = JSON.parse(currentText);
      let formattedJson = JSON.stringify(jsonObj, null, 2);
      $("#payloadJson").text(formattedJson);
      Swal.fire({
        icon: "success",
        title: "Formatted!",
        text: "JSON has been formatted",
        timer: 1500,
        showConfirmButton: false,
      });
    } catch (e) {
      Swal.fire({
        icon: "warning",
        title: "Not JSON",
        text: "This is not a valid JSON format",
        timer: 2000,
      });
    }
  });

  // Apply filters
  $("#applyFilters").on("click", function () {
    table.draw();
  });

  // Clear filters
  $("#clearFilters").on("click", function () {
    $("#statusFilter").val("");
    $("#fromDateFilter").val("");
    $("#toDateFilter").val("");
    table.draw();
  });

  // Auto-submit on Enter key in filter inputs
  $("#statusFilter, #fromDateFilter, #toDateFilter").on(
    "keypress",
    function (e) {
      if (e.which === 13) {
        table.draw();
      }
    },
  );

  $("#btn-export").click(function () {
    const params = new URLSearchParams();
    // Ambil semua nilai input
    const status = $("#statusFilter").val();
    const fromDate = $("#fromDateFilter").val();
    const toDate = $("#toDateFilter").val();

    // const kodeTc = $("#kodeTc").val();
    // const pembalikan = $("#pembalikan").val();

     const selectedIds = getSelectedIds();
     if (selectedIds && selectedIds.length > 0) {
       params.append("ids", JSON.stringify(selectedIds));
     }

    // Append hanya jika value tidak kosong
    if (status) params.append("status", status);
    if (fromDate) params.append("fromDate", fromDate);
    if (toDate) params.append("toDate", toDate);
    const baseUrl = "/operasional/log-transaksi-ditolak/export-excel";
    const fullUrl = params.toString()
      ? `${baseUrl}?${params.toString()}`
      : baseUrl;

    window.location.href = fullUrl;
  });
  
	 $("#check-all").on("click", function () {
     const isChecked = $(this).is(":checked");
     $(".row-checkbox").prop("checked", isChecked);
   });

   // 2. Event Uncheck "Select All" jika salah satu checkbox baris di-uncheck
   $("#tableBukuBesar tbody").on("click", ".row-checkbox", function () {
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
