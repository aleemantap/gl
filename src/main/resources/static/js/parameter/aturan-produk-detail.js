var table2;

$(document).ready(function () {
  table2 = new DataTable("#table-detail-produk-rule", {
    processing: true,
    serverSide: true,
    searching: false,
    paging: true,
    info: true,
    scrollX: true,
    pageLength: 5,
    lengthChange: false,
    order: [[0, "desc"]],
    ajax: {
      url: "/parameter/aturan-produk-detail/datatables",
      type: "GET",
      data: function (d) {
        return {
          draw: d.draw,
          start: d.start,
          length: 5,
          orderColumn: d.order[0] ? d.order[0].column : 1,
          orderDir: d.order[0] ? d.order[0].dir : "desc",
          search: $("#searchInput").val(),
          active: $("#statusFilter").val(),
          productId: $("#produk-id").val(),
        };
      },
    },
    columns: [
      {
        className: "dt-control",
        orderable: false,
        data: null,
        defaultContent: "",
      },
      { data: "ruleVersion" },
      { data: "effectiveFrom" },
      { data: "effectiveTo" },
      {
        data: "isActive",
        render: function (data, type, row) {
          if (data === true) {
            return '<span class="badge bg-success text-white">Active</span>';
          } else {
            return '<span class="badge bg-danger text-white">Inactive</span>';
          }
        },
      },
      // {
      //   data: null,
      //   visible : false,
      //   render: function (data, type, row) {
      //     return `
      //             <div style="display: flex; gap: 15px;">
      //                 <a class="btn btn-delete" href="#" data-id="${row.id}">
      //                    Add Component
      //                 </a>
      //                 <a class="btn btn-edit" href="#" data-id="${row.id}">
      //                     Add Journal
      //                 </a>
      //             </div>
      //         `;
      //   },
      //   orderable: false,
      // },
      {
        data: null,
        render: function (data, type, row) {
          return `
                  <div style="display: flex; gap: 15px;">
                      <a class="btn btn-delete" href="#" data-id="${row.id}">
                          <img src="/img/delete.png" />
                      </a>
                      <a class="btn btn-edit" href="#" data-id="${row.id}">
                          <img src="/img/pencil.png" />
                      </a>
                      <a class="btn btn-component" href="#" data-id="${row.id}">
                          Component
                      </a>
                     
                  </div>
              `;
        },
        orderable: false,
      },
    ],
    //  <a class="btn btn-journal" href="#" data-id="${row.id}">
    //                       Journal
    //                   </a>
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
    table2.on('click', 'tbody td.dt-control', function (e) {
        let tr = e.target.closest('tr');
        let row = table2.row(tr);

        if (row.child.isShown()) {
            // This row is already open - close it
            row.child.hide();
        }
        else {
            // Open this row
            row.child(format(row.data())).show();
        }
    });

      // Event click untuk row
    // $("#table-detail-produk-rule tbody").on("click", "tr", function () {
    //   var data = $(this).data(); // atau ambil data dari row
    //   console.log("Row clicked:", data);

    //   // Contoh: ambil isi kolom pertama
    //   var firstColumn = $(this).find("td:eq(0)").text();
    //   alert("Anda mengklik: " + firstColumn);
    // });

    function format(d) {
      // Helper untuk menangani nilai null / undefined
      const showVal = (val) =>
        val !== null && val !== undefined && val !== "" ? val : "-";

      return (
        '<div class="p-3 bg-light rounded border">' +
        '<div class="row g-3">' +
        // SECTION 1: Informasi Produk & Rule
        '<div class="col-md-4">' +
        '<h6 class="fw-bold text-primary border-bottom pb-1 mb-2">1. Info Produk & Rule</h6>' +
        '<dl class="row mb-0 small">' +
        // '<dt class="col-5">Product ID:</dt><dd class="col-7">' +
        // showVal(d.productId) +
        // "</dd>" +
        // '<dt class="col-5">Product Code:</dt><dd class="col-7">' +
        // showVal(d.productCode) +
        // "</dd>" +
        // '<dt class="col-5">Product Name:</dt><dd class="col-7">' +
        // showVal(d.productName) +
        // "</dd>" +
        '<dt class="col-5">Rule Version:</dt><dd class="col-7">' +
        showVal(d.ruleVersion) +
        "</dd>" +
        '<dt class="col-5">Source System:</dt><dd class="col-7">' +
        showVal(d.sourceSystem) +
        "</dd>" +
        "</dl>" +
        "</div>" +
        // SECTION 2: Masa Berlaku & Bunga
        '<div class="col-md-4">' +
        '<h6 class="fw-bold text-primary border-bottom pb-1 mb-2">2. Masa Berlaku & Bunga</h6>' +
        '<dl class="row mb-0 small">' +
        '<dt class="col-5">Effective From:</dt><dd class="col-7">' +
        showVal(d.effectiveFrom) +
        "</dd>" +
        '<dt class="col-5">Effective To:</dt><dd class="col-7">' +
        showVal(d.effectiveTo) +
        "</dd>" +
        "</dl>" +
        "</div>" +
        // SECTION 3: Biaya & Tenor
        '<div class="col-md-4">' +
        '<h6 class="fw-bold text-primary border-bottom pb-1 mb-2">3.  Tenor</h6>' +
        '<dl class="row mb-0 small">' +
        '<dt class="col-5">Tenor Min / Max:</dt><dd class="col-7">' +
        showVal(d.tenorMinDays) +
        " s/d " +
        showVal(d.tenorMaxDays) +
        " Hari</dd>" +
        '<dt class="col-5">Auction Trigger:</dt><dd class="col-7">' +
        (d.auctionTriggerDays !== null ? d.auctionTriggerDays + " Hari" : "-") +
        "</dd>" +
        "</dl>" +
        "</div>" +
        // SECTION 4: Status & Audit Trail (Full Width)
        '<div class="col-12">' +
        '<h6 class="fw-bold text-primary border-bottom pb-1 mb-2">4. Status & Audit Trail</h6>' +
        '<div class="row small">' +
        '<div class="col-md-3"><strong>Is Active:</strong> ' +
        (d.isActive
          ? '<span class="badge bg-success text-white">Active</span>'
          : '<span class="badge bg-danger text-white">Inactive</span>') +
        "</div>" +
        '<div class="col-md-3"><strong>Approval:</strong> ' +
        showVal(d.approvalStatusBadge).replace(
          "bg-warning text-white",
          "bg-warning text-white",
        ) +
        "</div>" +
        '<div class="col-md-3"><strong>Approved By:</strong> ' +
        showVal(d.approvedBy) +
        " (" +
        showVal(d.approvedAt) +
        ")</div>" +
        '<div class="col-md-3"><strong>Created By:</strong> ' +
        showVal(d.createdBy) +
        " (" +
        showVal(d.createdAt) +
        ")</div>" +
        "</div>" +
        "</div>" +
        "</div>" +
        "</div>"
      );
    }

    $(document).on("click", ".btn-edit", function (e) {
       e.preventDefault();
      const id = $(this).data("id");
      $("#viewModalLabel").text("Edit Product Rule");
      $("#modal-detail-rule").modal("show");
      ubah(id);
     
    });

     $(document).on("click", ".btn-delete", function (e) {
       e.preventDefault();
       const id = $(this).data("id");
        alert("Blum Bisa");
       //ubah(id);
     });

      $(document).on("click", ".btn-component", function (e) {
        e.preventDefault();
        const id = $(this).data("id");
        $("#component").show();
        $("#productRuleId").val(id);   
        // loadTableData();
        // $("#table-component-rule").ajax.reload();
         $("#table-component-rule").DataTable().ajax.reload();
      });

    // $(document).on("click", ".btn-journal", function (e) {
    //     e.preventDefault();
    //     const id = $(this).data("id");
    //     $("#journal").show();
    //     $("#productRuleJournalId").val(id);  
    // });

    $(document).on("click", "#hide-component", function (e) {
      $("#component").hide();
    });
    // $(document).on("click", "#hide-journal", function (e) {
    //    $("#journal").hide();
    // });



   $("#btn-add-produk-rule").click(function (e) {
    $("#modal-detail-rule").modal("show");
   });

    $("#btnSaveProductRule").click(function (e) {
      saveProductRule();        
    });


  
});

// // View Detail
function ubah(id) {
  $.ajax({
    url: "/parameter/aturan-produk-detail/product-rules/get/" + id,
    type: "GET",
    success: function (response) {
      if (response.success) {
        var rule = response.data;
        // console.info(rule);

        // $("#produk").val($("#nama-produk-temp").val() || "");

        $("#ruleId").val(rule.id || "");

        // Informasi dasar
        $("#productId").val(rule.productId || "");
        // $("#produk").val(rule.productName || rule.productCode || "");
        $("#ruleVersion").val(rule.ruleVersion || 1);

        // Masa berlaku
        $("#effectiveFrom").val(rule.effectiveFrom || "");
        $("#effectiveTo").val(rule.effectiveTo || "");

        // Pengaturan bunga
        $("#interestRate").val(
          rule.interestRate != null ? rule.interestRate : "",
        );

        $("#interestPeriodDays").val(
          rule.interestPeriodDays != null ? rule.interestPeriodDays : "",
        );

        $("#interestMethod").val(rule.interestMethod || "");

        // Biaya administrasi
        $("#adminType").val(rule.adminType || "");

        $("#adminValue").val(rule.adminValue != null ? rule.adminValue : "");

        // Biaya provisi
        $("#provisionType").val(rule.provisionType || "");

        $("#provisionValue").val(
          rule.provisionValue != null ? rule.provisionValue : "",
        );

        // Tenor & lelang
        $("#tenorMinDays").val(
          rule.tenorMinDays != null ? rule.tenorMinDays : "",
        );

        $("#tenorMaxDays").val(
          rule.tenorMaxDays != null ? rule.tenorMaxDays : "",
        );

        $("#auctionTriggerDays").val(
          rule.auctionTriggerDays != null ? rule.auctionTriggerDays : "",
        );

        
        ///var html = buildDetailHTML(rule);
        //$(".modal-body").html(html);
        // viewModal.show();
        // showToast("success", te);
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
// // Build Detail HTML
function buildDetailHTML(rule) {
    return `
        <div class="table-responsive">
            <table class="table table-bordered table-striped">
                <tr>
                    <th width="30%">ID</th>
                    <td>${rule.id}</td>
                </tr>
                <tr>
                    <th>Product Information</th>
                    <td>
                        <strong>ID:</strong> ${rule.productId || '-'}<br>
                        <strong>Code:</strong> ${rule.productCode || '-'}<br>
                        <strong>Name:</strong> ${rule.productName || '-'}
                    </td>
                </tr>
                <tr>
                    <th>Rule Version</th>
                    <td>${rule.ruleVersion}</td>
                </tr>
                <tr>
                    <th>Effective From</th>
                    <td>${rule.effectiveFrom ? new Date(rule.effectiveFrom).toLocaleDateString('id-ID') : '-'}</td>
                </tr>
                <tr>
                    <th>Effective To</th>
                    <td>${rule.effectiveTo ? new Date(rule.effectiveTo).toLocaleDateString('id-ID') : '-'}</td>
                </tr>
                <tr>
                    <th>Interest Rate</th>
                    <td>${rule.interestRate ? parseFloat(rule.interestRate).toFixed(4) + '%' : '-'}</td>
                </tr>
                <tr>
                    <th>Interest Period Days</th>
                    <td>${rule.interestPeriodDays || '-'}</td>
                </tr>
                <tr>
                    <th>Interest Method</th>
                    <td>${rule.interestMethod || '-'}</td>
                </tr>
                <tr>
                    <th>Admin Type</th>
                    <td>${rule.adminType || '-'}</td>
                </tr>
                <tr>
                    <th>Admin Value</th>
                    <td>${rule.adminValue ? parseFloat(rule.adminValue).toFixed(2) : '-'}</td>
                </tr>
                <tr>
                    <th>Provision Type</th>
                    <td>${rule.provisionType || '-'}</td>
                </tr>
                <tr>
                    <th>Provision Value</th>
                    <td>${rule.provisionValue ? parseFloat(rule.provisionValue).toFixed(4) : '-'}</td>
                </tr>
                <tr>
                    <th>Tenor Min Days</th>
                    <td>${rule.tenorMinDays || '-'}</td>
                </tr>
                <tr>
                    <th>Tenor Max Days</th>
                    <td>${rule.tenorMaxDays || '-'}</td>
                </tr>
                <tr>
                    <th>Auction Trigger Days</th>
                    <td>${rule.auctionTriggerDays || '-'}</td>
                </tr>
                <tr>
                    <th>Status</th>
                    <td>${rule.isActive ? '<span class="badge bg-success">Active</span>' : '<span class="badge bg-danger">Inactive</span>'}</td>
                </tr>
                <tr>
                    <th>Approval Status</th>
                    <td>${getApprovalBadge(rule.approvalStatus)}</td>
                </tr>
                ${rule.approvedBy ? `
                <tr>
                    <th>Approved By</th>
                    <td>${rule.approvedBy}</td>
                </tr>
                ` : ''}
                ${rule.approvedAt ? `
                <tr>
                    <th>Approved At</th>
                    <td>${new Date(rule.approvedAt).toLocaleString('id-ID')}</td>
                </tr>
                ` : ''}
                <tr>
                    <th>Source System</th>
                    <td>${rule.sourceSystem || '-'}</td>
                </tr>
                <tr>
                    <th>Created By</th>
                    <td>${rule.createdBy || '-'}</td>
                </tr>
                <tr>
                    <th>Created At</th>
                    <td>${rule.createdAt ? new Date(rule.createdAt).toLocaleString('id-ID') : '-'}</td>
                </tr>
                ${rule.updatedBy ? `
                <tr>
                    <th>Updated By</th>
                    <td>${rule.updatedBy}</td>
                </tr>
                ` : ''}
                ${rule.updatedAt ? `
                <tr>
                    <th>Updated At</th>
                    <td>${new Date(rule.updatedAt).toLocaleString('id-ID')}</td>
                </tr>
                ` : ''}
            </table>
        </div>
    `;
}

function getApprovalBadge(status) {
    var colors = {
        'APPROVED': 'success',
        'REJECTED': 'danger',
        'PENDING': 'warning'
    };
    return `<span class="badge bg-${colors[status] || 'secondary'}">${status || '-'}</span>`;
}

// Save Product Rule
function saveProductRule() {
  //clearErrors();

  // Validate
  var isValid = true;

  

  if (!$("#effectiveFrom").val()) {
    $("#effectiveFrom").addClass("is-invalid");
    $("#effectiveFromError").text("Effective From harus diisi");
    isValid = false;
  }

  // Collect data
  // Helper untuk mengambil String (mengubah "" menjadi null)
  const getStr = (selector) => {
    const val = $(selector).val();
    return val !== null && val !== undefined && val.trim() !== ""
      ? val.trim()
      : null;
  };

  // Helper untuk mengambil Integer (aman untuk nilai 0)
  const getInt = (selector) => {
    const val = getStr(selector);
    return val !== null && !isNaN(val) ? parseInt(val, 10) : null;
  };

  // Helper untuk mengambil Float/Desimal (aman untuk nilai 0 dan desimal)
  const getFloat = (selector) => {
    const val = getStr(selector);
    return val !== null && !isNaN(val) ? parseFloat(val) : null;
  };

  var formData = {
    //id: getStr("#ruleId"),
    productId: $("#productId").val(),
    ruleVersion: getInt("#ruleVersion") ?? 1,

    effectiveFrom: getStr("#effectiveFrom"),
    effectiveTo: getStr("#effectiveTo"),

    // Handling Bunga
    // interestRate: getFloat("#interestRate"),
    // interestPeriodDays: getInt("#interestPeriodDays"),
    // interestMethod: getStr("#interestMethod"),

    // Handling Admin Fee
    // adminType: getStr("#adminType"),
    // adminValue: getFloat("#adminValue"),

    // Handling Provision Fee
    // provisionType: getStr("#provisionType"),
    // provisionValue: getFloat("#provisionValue"),

    // Handling Tenor & Lelang
    tenorMinDays: getInt("#tenorMinDays"),
    tenorMaxDays: getInt("#tenorMaxDays"),
    auctionTriggerDays: getInt("#auctionTriggerDays"),
  };

  if (!isValid) return;

  // Show loading
  $("#btnSaveProductRule").prop("disabled", true).html(`
            <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
            Menyimpan...
        `);

  $.ajax({
    url: "/parameter/aturan-produk-detail/product-rules/save",
    type: "POST",
    contentType: "application/json",
    data: JSON.stringify(formData),
    success: function (response) {
      if (response.success) {
        $("#modal-detail-rule").modal("hide");
        showToast("success", response.message);
        table2.ajax.reload();
      } else {
        showToast("error", response.message);
      }
    },
    error: function (xhr) {
      var response = xhr.responseJSON;
      showToast(
        "error",
        response?.message || "Terjadi kesalahan saat menyimpan data",
      );
    },
  });
}
