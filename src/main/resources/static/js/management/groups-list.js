$(document).ready(function () {
  const table = new DataTable("#table-groups", {
    processing: true,
    serverSide: true,
    searching: false,
    paging: true,
    info: true,
    // scrollX: false,
    pageLength: 10,
    lengthChange: false,
    ajax: {
      url: "/management/groups/datatables",
      type: "GET",
      data: function (d) {
        return {
          draw: d.draw,
          start: d.start,
          length: 20,
          orderColumn: d.order[0] ? d.order[0].column : 1,
          orderDir: d.order[0] ? d.order[0].dir : "desc",
          // email: $("#filterEmail").val(),
          // name: $("#filterName").val(),
          // cabangId: $("#customerId").val(),
          // userGroupId: $("#userGroupId").val()
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
      { data: "id", visible: false },
      { data: "name" },
      { data: "description" },
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
        //   var deleteA = `delete(${row.id})`;
        //   var editA = `edit(${row.id})`;
          return `
                <div style="display: flex; gap: 15px;">
                    <a href="#" data-id="${row.id}" class="btn-delete">
                        <img src="/img/delete.png" />
                    </a>
                    <a  href="#" data-id="${row.id}" class="btn-ubah">
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

   $(document).on("click", ".btn-ubah", function (e) {
     e.preventDefault();
     const id = $(this).data("id");
     alert("Belum Berfungsi");
   
   });

   $(document).on("click", ".btn-delete", function (e) {
     e.preventDefault();
     const id = $(this).data("id");
     alert("Belum Berfungsi");
   });

// function edit(id)
// {
// alert("belum berfungsi");
// }

});
