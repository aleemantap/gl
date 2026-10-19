$(document).ready(function () {
 
 
 

  let table = new DataTable("#table-component-rule", {
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
      url: "/parameter/aturan-produk-detail/rule-component/datatables",
      type: "GET",
      data: function (d) {
        return {
          draw: d.draw,
          start: d.start,
          length: 5,
          orderColumn: d.order[0] ? d.order[0].column : 1,
          orderDir: d.order[0] ? d.order[0].dir : "desc",
          //product_rule_id: 1,
          search: $("#searchInput").val(),
          active: $("#statusFilter").val(),
          productRuleId: $("#productRuleId").val()
            ? $("#productRuleId").val()
            : 0,
        };
      },
    },
    columns: [
      { data: "componentCode" },
      { data: "componentName" },
      { data: "calculationType" },
      { data: "amountSource" },
      { data: "fixedAmount", visible : false },
      // { data: "cashMovementType" },
      { data: "isActive" },
 
      {
        data: null,
        render: function (data, type, row) {
          return `
                    <div style="display: flex; gap: 15px;">
                        <a class="btn-ubah-component" href="#" data-id="${row.id}">
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
  
  

  // function loadTableData() {
  //   if (table) {
  //     table.ajax.reload();
  //   } else {
  //     initDataTable();
  //   }
  // }

  // // Atau jika tombolnya adalah filter/search
  // $("#searchButton").on("click", function() {
  //   if (table) {
  //     table.ajax.reload();
  //   } else {
  //     initDataTable();
  //   }
  // });


  
  // $(document).on("click", "#btn-add-produk-component", function (e) {
  //    $("#calculation_type").change
  // })

  $("#calculation_type").change(function () {
    var selectedValue = $(this).val();
    //console.log("Selected Calculation Type: " + selectedValue);

    // Lakukan sesuatu berdasarkan nilai yang dipilih
    if (selectedValue === "INPUT") {
       $("#amount_source_div").show();
       $("#fixed_amount_div").hide(); 
    
    } else if (selectedValue === "CALCULATED") {
      alert()
    } else if (selectedValue === "FIXED") {
       $("#amount_source_div").hide() 
       $("#fixed_amount_div").show(); 
       
    }
  });
 

  $(document).on("click", "#btn-add-produk-component", function (e) {
    e.preventDefault();
    //const id = $(this).data("id");
    //$("#viewModalLabel").text("Edit Product Rule");
    $("#product_rule_component_id").val(""); 
    $("#modal-detail-rule-component").modal("show");
  });

  $(document).on("click", ".btn-ubah-component", function (e) {
    e.preventDefault();
    const id = $(this).data("id");
    $("#modal-detail-rule-component").modal("show");
    $("#btnSaveRuleComponent").text("Update");
    ubahComponent(id);
  });

  $(document).on("click", "#btnSaveRuleComponent", function (e) {
    e.preventDefault();
    // 1. Ambil Form dan lakukan validasi HTML5 bawaan (misal: atribut required)
    const $form = $("#componentForm");
    if (!$form[0].checkValidity()) {
      $form[0].reportValidity();
      return;
    }

    var editId = $("#product_rule_component_id").val();
    var url =  "/parameter/aturan-produk-detail/rule-component";
    if (editId) {
        url = "/parameter/aturan-produk-detail/rule-component/update/" + editId;
    }

    //  $("#product_rule_component_id").val(rule.id || "");

    // 2. Kumpulkan data form ke dalam objek JavaScript
    const formData = {
      //   id: $("#product_rule_component_id").val(),
      productRuleId: $("#productRuleId").val(),
      componentId: $("#componenCode").val(),
      // componentName: $("#componentName").val(),
      calculationType: $("#calculation_type").val(),
      amountSource: $("#amount_source").val(),
      //cashMovementType: $("#cash_movement_type").val(),
      cashMovementType : "",
      isActive: $("#isActive").val() == 1 ? true : false,
    };

    // Alternatif jika Controller menerima form-urlencoded:
    // const serializedData = $form.serialize();

    // 3. Kirim data via Ajax
    $.ajax({
    //   url: "/parameter/aturan-produk-detail/rule-component",
      url : url,
      type: "POST",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      data: JSON.stringify(formData), // Jika menggunakan JSON

      beforeSend: function () {
        // Disable tombol agar tidak terjadi double submit
        $("#btnSaveRuleComponent")
          .prop("disabled", true)
          .html('<i class="fas fa-spinner fa-spin"></i> Menyimpan...');
      },
      success: function (response) {
        // Aksi jika berhasil (misal: tutup modal, refresh datatable, tampilkan alert)
        $("#modal-detail-rule-component").modal("hide");    
        // $("#table-component-rule").ajax.reload();   
         $("#table-component-rule").DataTable().ajax.reload(null, false);
        showToast("success", "Data berhasil disimpan");
       
        // Reset form jika diperlukan
        $form[0].reset();
      },
      error: function (xhr, status, error) {
        // Handling jika terjadi error
        console.error("Error:", error);
        alert(
          "Gagal menyimpan data: " +
            (xhr.responseJSON?.message || "Terjadi kesalahan sistem."),
        );
      },
      complete: function () {
        // Kembalikan tombol ke keadaan semula
        $("#btnSaveRuleComponent")
          .prop("disabled", false)
          .html('<i class="fas fa-save"></i> Simpan');
      },
    });
  });
});

function ubahComponent(id) {
  $.ajax({
    url: "/parameter/aturan-produk-detail/rule-component/get/"+id,
    type: "GET",
    success: function (response) {
      if (response.success) {
        var rule = response.data;
        // console.error(rule);
        $("#product_rule_component_id").val(rule.id || "");
        // Informasi dasar
        $("#productRuleComponentId").val(rule.productRuleId || "");
        // $("#produk").val(rule.productName || rule.productCode || "");
        $("#componenCode").val(rule.componentId || "");
        // Masa berlaku
        // $("#componentName").val(rule.componentName || "");
        $("#calculation_type").val(rule.calculationType || "");
        $("#amount_source").val(rule.amountSource || "");
        $("#cash_movement_type").val(rule.cashMovementType || "");
        let isa = rule.active ? 1 : 0;
        $("#isActive").val(isa);
    
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