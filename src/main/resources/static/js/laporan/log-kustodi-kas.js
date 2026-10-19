$(document).ready(function () {


   $("#branchCode").select2({
     theme: "bootstrap-5",
     width: "100%",
     allowClear: false,
     // dropdownParent: $("#modal1"),
   });

  const table = $("#logKustodiKasTable").DataTable({
    processing: true,
    serverSide: true,
    searching: false,
    paging: true,
    info: true,
    scrollX: false,
    pageLength: 10,
    lengthChange: false,

    ajax: {
      url: "/laporan/log-kustodi-kas/datatables",
      type: "GET",
      data: function (d) {
        d.tanggalDari = $("#tanggalDari").val();
        d.tanggalSampai = $("#tanggalSampai").val();
        d.branchCode = $("#branchCode").val();
        d.direction = $("#arahKas").val();
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
        data: "occurredAt",
        // title: "Occurred At",
        render: function (data) {
          if (!data) return "-";
          const date = new Date(data);
          return formatDateTime(date);
        },
      },
      {
        data: "transactionDate",
        render: function (data) {
          if (!data) return "-";
          const date = new Date(data);
          return formatDateTime(date);
        },
      },
      {
        data: "direction",
        // title: "Direction",
      },
      {
        data: "amount",
        // title: "Amount",
        className: "text-center",
        render: formatNumber,
      },
      {
        data: "tellerUserId",
        title: "Teller",
      },
      {
        data: "description",
        // title: "Description",
        className: "text-start",
      },
      {
        data: null,
        title: "Detail",
        className: "text-center",
        orderable: false,
        defaultContent:
          '<button class="btn btn-sm btn-info btn-detail">Detail</button>',
      },
      // Kolom untuk tombol expand (opsional)

      //   {
      //     data: "occurredAt",
      //   },
      //   {
      //     data: "transactionDate",
      //   },

      //   {
      //     data: "direction",
      //   },
      //   {
      //     data: "amount",
      //     className: "text-center",
      //     render: formatNumber,
      //   },

      //   {
      //     data: "tellerUserId",
      //     className: "text-end",
      //   },

      //   {
      //     data: "kasPointCode",
      //     className: "text-end",
      //   },

      //   {
      //     data: "counterpartyName",
      //     className: "text-end",
      //   },

      //   {
      //     data: "counterpartyType",
      //     className: "text-end",
      //   },

      //   {
      //     data: "counterpartyRef",
      //     className: "text-end",
      //   },

      //   {
      //     data: "transactionNo",
      //     className: "text-end",
      //   },

      //   {
      //     data: "journalNo",
      //     className: "text-end",
      //   },
      //   {
      //     data: "branchCode",
      //     className: "text-end",
      //   },
      //   {
      //     data: "description",
      //     className: "text-start",
      //   },
    ],

    columnDefs: [
      {
        // targets: [6, 7, 8, 9, 10, 11, 12,13], // indeks kolom yang disembunyikan
        visible: false,
      },
    ],
    drawCallback: function () {
      $("#check-all").prop("checked", false);
    },
    order: [[0, "asc"]],
    pageLength: 10,
    lengthMenu: [
      [10, 25, 50, 100],
      [10, 25, 50, 100],
    ],
  });

  $("#logKustodiKasTable tbody").on("click", ".btn-detail", function () {
    const tr = $(this).closest("tr");
    const row = table.row(tr);

    if (row.child.isShown()) {
      // Tutup child
      row.child.hide();
      $(this).text("Detail");
      tr.removeClass("shown");
    } else {
      // Buka child
      const data = row.data();
      row.child(formatChildRow(data)).show();
      $(this).text("Sembunyikan");
      tr.addClass("shown");
    }
  });



   $("#check-all").on("click", function () {
     const isChecked = $(this).is(":checked");
     $(".row-checkbox").prop("checked", isChecked);
   });

   // 2. Event Uncheck "Select All" jika salah satu checkbox baris di-uncheck
   $("#logKustodiKasTable tbody").on("click", ".row-checkbox", function () {
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
  // Fungsi untuk memformat child row
  function formatChildRow(data) {
    return `
        <table class="table table-sm table-bordered" style="margin: 0; width: 100%;">
            <tr>
                <td style="width: 20%; font-weight: bold;">Kas Point Code</td>
                <td>${data.kasPointCode || "-"}</td>
            </tr>
            <tr>
                <td style="width: 20%; font-weight: bold;">Counterparty Name</td>
                <td>${data.counterpartyName || "-"}</td>
            </tr>
            <tr>
                <td style="width: 20%; font-weight: bold;">Counterparty Type</td>
                <td>${data.counterpartyType || "-"}</td>
            </tr>
            <tr>
                <td style="width: 20%; font-weight: bold;">Counterparty Ref</td>
                <td>${data.counterpartyRef || "-"}</td>
            </tr>
            <tr>
                <td style="width: 20%; font-weight: bold;">Transaction No</td>
                <td>${data.transactionNo || "-"}</td>
            </tr>
            <tr>
                <td style="width: 20%; font-weight: bold;">Journal No</td>
                <td>${data.journalNo || "-"}</td>
            </tr>
            <tr>
                <td style="width: 20%; font-weight: bold;">Branch Code</td>
                <td>${data.branchCode || "-"}</td>
            </tr>
        </table>
    `;
  }

  // =====================================
  // FILTER
  // =====================================

  $("#btnFilter").on("click", function () {
    table.ajax.reload();
  });

  // =====================================
  // FORMAT ANGKA
  // =====================================

  function formatNumber(data) {
    if (data === null || data === undefined || data === "") {
      return "0,00";
    }

    return new Intl.NumberFormat("id-ID", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(data);
  }

  function formatDateTime(date) {
    const options = {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
    };

    const dateStr = date.toLocaleDateString("id-ID", options);
    const timeStr = date.toLocaleTimeString("id-ID", {
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
      hour12: false,
    });

    return `
        <div style="text-align: center;">
            <div style="font-weight: 500;">${dateStr}</div>
            <div style="font-size: 11px; color: #f4043c;">${timeStr}</div>
        </div>
    `;
  }

    $("#btnReset").click(function () {
      $("#tanggalDari").val("");
      $("#tanggalSampai").val("");
      $("#branchCode").val("");
       $("#direction").val("");
      $("#logKustodiKasTable").DataTable().ajax.reload();
    });
 

   $("#btn-export-exc").click(function () {
     const params = new URLSearchParams();
     // Ambil semua nilai input
     const tanggalDari = $("#tanggalDari").val();
     const tanggalSampai = $("#tanggalSampai").val();
     const branchCode = $("#branchCode").val();
     const direction = $("#direction").val();
    
     const selectedIds = getSelectedIds();
     if (selectedIds && selectedIds.length > 0) {
       params.append("ids", JSON.stringify(selectedIds));
     }
     // Append hanya jika value tidak kosong
     if (tanggalDari) params.append("tanggalDari", tanggalDari);
     if (tanggalSampai) params.append("tanggalSampai", tanggalSampai);
     if (branchCode) params.append("branchCode", branchCode);
     if (direction) params.append("direction", direction);
  
     const baseUrl = "/laporan/log-kustodi-kas/export-excel";
     const fullUrl = params.toString()
       ? `${baseUrl}?${params.toString()}`
       : baseUrl;

     //  console.info("Generated URL:", fullUrl);

     // Redirect ke backend untuk download Excel
     window.location.href = fullUrl;

   
   });
});
