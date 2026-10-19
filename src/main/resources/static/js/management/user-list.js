$(document).ready(function () {

    $("#filterContainer").hide();
    $("#toggleIcon")
      .removeClass("ti-chevron-down")
      .addClass("ti-chevron-right");
    $("#toggleText").text("Tampilkan Filter");
    let isFilterVisible = false;

    $("#toggleFilterBtn").on("click", function () {
      if (isFilterVisible) {
        $("#filterContainer").slideUp(300);
        $("#toggleIcon")
          .removeClass("ti-chevron-down")
          .addClass("ti-chevron-right");
        $("#toggleText").text("Tampilkan Filter");
        isFilterVisible = false;
      } else {
        $("#filterContainer").slideDown(300);
        $("#toggleIcon")
          .removeClass("ti-chevron-right")
          .addClass("ti-chevron-down");
        $("#toggleText").text("Sembunyikan Filter");
        isFilterVisible = true;
      }
    });
  
    const table = new DataTable("#table-users", {
      processing: true,
      serverSide: true,
      searching: false,
      paging: true,
      info: true,
      // scrollX: false,
      pageLength: 10,
      lengthChange: false,
      ajax: {
        url: "/management/users/datatables",
        type: "GET",
        data: function (d) {
          return {
            draw: d.draw,
            start: d.start,
            length: 20,
            orderColumn: d.order[0] ? d.order[0].column : 1,
            orderDir: d.order[0] ? d.order[0].dir : "desc",
            name: $("#filterNama").val(),
            userGroupId: $("#filterRole").val(),
            status: $("#filterStatus").val(),
            cabang: $("#filterCabang").val(),
          };
        },
      },
      columns: [
        // {
        //   data: null,
        //   defaultContent: "",
        //   render: function (data, type, row, meta) {
        //     return meta.row + 1 + (meta.settings._iDisplayStart || 0);
        //   },
        //   orderable: false, // Make row number not sortable
        // },
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
        { data: "id", visible: false },
        { data: "name" },
        { data: "email" },
        { data: "userGroup" },
        { data: "cabangName" },
        {
          data: null,
          orderable: false,
          render: function (data, type, row) {
            if (row.active === "Y") {
              return '<span class="badge bg-success text-white">Active</span>';
            } else {
              return '<span class="badge bg-danger text-white">Inactive</span>';
            }
          },
        },
        {
          data: null,
          render: function (data, type, row) {
            var deleteA = `delete(${row.id})`;
            var editA = `delete(${row.id})`;
            return `
                <div style="display: flex; gap: 15px;">
                    <a class="" href="#" onclick="${deleteA}">
                        <img src="/img/delete.png" />
                    </a>
                    <a class="" href="#" onclick="${editA}">
                        <img src="/img/pencil.png" />
                    </a>
                </div>
            `;
          },
          orderable: false,
        },
      ],
      drawCallback: function () {
        $("#check-all").prop("checked", false);
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
     $("#check-all").on("click", function () {
       const isChecked = $(this).is(":checked");
       $(".row-checkbox").prop("checked", isChecked);
     });

     // 2. Event Uncheck "Select All" jika salah satu checkbox baris di-uncheck
     $("#table-users tbody").on("click", ".row-checkbox", function () {
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

      $("#btn-filter").click(function () {
        $("#table-users").DataTable().ajax.reload();
      });
      $("#btn-reset").click(function () {
        $("#filterNama").val("");
        $("#filterRole").val("");
        $("#filterStatus").val("");
        $("#filterCabang").val("");
        $("#table-users").DataTable().ajax.reload();
      });

      $("#btn-export-user").click(function () {
        const params = new URLSearchParams();

        // Ambil semua nilai input
        const name = $("#filterNama").val();
        const userGroupId = $("#filterRole").val();
        const status = $("#filterStatus").val();
        const cabang = $("#filterCabang").val();
        // const pembalikan = $("#pembalikan").val();
        const selectedIds = getSelectedIds();
        if (selectedIds && selectedIds.length > 0) {
          params.append("ids", JSON.stringify(selectedIds));
        }
        // Append hanya jika value tidak kosong
        if (name) params.append("name", name);
        if (userGroupId) params.append("userGroupId", userGroupId);
        if (status) params.append("status", status);
        if (cabang) params.append("kodeTc", cabang);
       
       
        const baseUrl = "/management/users/export-excel";
        const fullUrl = params.toString()
          ? `${baseUrl}?${params.toString()}`
          : baseUrl;

        window.location.href = fullUrl;
      });

});
