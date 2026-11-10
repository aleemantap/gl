
$(document).ready(function () {
//     $("#amount_source").select2({
//       theme: "bootstrap-5",
//       width: "100%",
//       allowClear: false,
//       // dropdownParent: $("#modal1"),
//     });
    
//   $("#componenCode").select2({
//     theme: "bootstrap-5",
//     width: "100%",
//     allowClear: false,
//     // dropdownParent: $("#modal1"),
//   }); 
 
  $(document).on("click", ".btn-component", function (e) {
    e.preventDefault();
    const id = $(this).data("id");
    $("#component").show();
    $("#productRuleId").val(id);
    // loadTableData();
    // $("#table-component-rule").ajax.reload();
    $("#table-component-rule").DataTable().ajax.reload();
  });



  $(document).on("click", "#hide-component", function (e) {
    $("#component").hide();
  });


  $("#btn-add-produk-rule").click(function (e) {
    $("#modal-detail-rule").modal("show");
  });

 
});

function getApprovalBadge(status) {
  var colors = {
    APPROVED: "success",
    REJECTED: "danger",
    PENDING: "warning",
  };
  return `<span class="badge bg-${colors[status] || "secondary"}">${status || "-"}</span>`;
}
