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
      scrollX: false,
      pageLength: 10,
      lengthChange: false,
      ajax: {
        url: "/management/users/datatables",
        type: "GET",
        data: function (d) {
          return {
            draw: d.draw,
            start: d.start,
            length: 10,
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
            // Cek status active dari data row
            const isActive = row.active === "Y";

            // Tentukan teks, icon, dan class berdasarkan status
            let buttonText, iconClass, btnClass, actionFunction;

            if (isActive) {
              buttonText = "Non-Active-kan";
              iconClass = `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                              <path stroke-linecap="round" stroke-linejoin="round" d="M18.364 18.364A9 9 0 0 0 5.636 5.636m12.728 12.728A9 9 0 0 1 5.636 5.636m12.728 12.728L5.636 5.636" />
                          </svg>`; // Icon untuk non-aktif
              btnClass = "color: #dc3545;"; // Warna merah (menggantikan text-danger inline style)
              actionFunction = `nonActiveUser(${row.id})`;
            } else {
              buttonText = "Active-kan";
              iconClass = `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                              <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z" />
                              <path stroke-linecap="round" stroke-linejoin="round" d="m9 12.75 3 3 3-3" />
                          </svg>`; // Icon untuk aktif
              btnClass = "color: #28a745;"; // Warna hijau (menggantikan text-success inline style)
              actionFunction = `activeUser(${row.id})`;
            }

            // HTML untuk kustom dropdown (tanpa bergantung pada Bootstrap JS)
            return `
            <div class="custom-dropdown-container" style="position: relative; display: inline-block;">
                <button type="button" class="btn btn-sm btn-outline-secondary custom-dropdown-btn" onclick="toggleCustomDropdown(event, '${row.id}')">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-three-dots-vertical" viewBox="0 0 16 16">
                      <path d="M9.5 13a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m0-5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0"/>
                    </svg>
                </button>
                <div id="customMenu-${row.id}" class="custom-dropdown-menu" style="display: none; position: absolute; right: 0; background: #fff; border: 1px solid #ddd; box-shadow: 0 4px 6px rgba(0,0,0,0.1); border-radius: 4px; z-index: 9999; min-width: 160px; text-align: left;">
                    <a href="javascript:void(0);" class="dropdown-item-custom" onclick="editUs(${row.id}); closeAllDropdowns();" style="display: block; padding: 8px 12px; color: #333; text-decoration: none; font-size: 13px;">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10" />
                        </svg> Edit
                    </a>
                    <a href="javascript:void(0);" class="dropdown-item-custom" onclick="${actionFunction}; closeAllDropdowns();" style="display: block; padding: 8px 12px; ${btnClass} text-decoration: none; font-size: 13px;">
                       ${iconClass} ${buttonText}
                    </a>
                    <a href="javascript:void(0);" class="dropdown-item-custom" onclick="gantPasswordUs(${row.id}); closeAllDropdowns();" style="display: block; padding: 8px 12px; color: #333; text-decoration: none; font-size: 13px;">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M16.5 10.5V6.75a4.5 4.5 0 1 0-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 0 0 2.25-2.25v-6.75a2.25 2.25 0 0 0-2.25-2.25H6.75a2.25 2.25 0 0 0-2.25 2.25v6.75a2.25 2.25 0 0 0 2.25 2.25Z" />
                        </svg> Ganti Password
                    </a>
                </div>
            </div>
        `;
          },
          orderable: false,
          searchable: false,
        },
      ],
      // dom: "rtip",
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

      $("#btnTambahUser").on("click", function (e) {
        location.href = "users/create";
       });

});



function editUs(id) {
  window.location.href = "/management/users/edit/" + id;
}
function gantPasswordUs(id) {
  window.location.href = "/management/users/reset-password/" + id;
}

function nonActiveUser(userId) {
  Swal.fire({
    title: "Konfirmasi",
    text: "Apakah Anda yakin ingin menonaktifkan user ini?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#d33",
    cancelButtonColor: "#3085d6",
    confirmButtonText: "Ya, Nonaktifkan!",
    cancelButtonText: "Batal",
  }).then((result) => {
    if (result.isConfirmed) {
      $.ajax({
        url: "/management/users/non-active/" + userId,
        type: "PUT",
        success: function (response) {
          Swal.fire({
            icon: "success",
            title: "Berhasil!",
            text: response.responseMessage,
          });
          // Reload data table
          $("#table-users").DataTable().ajax.reload();
        },
        error: function (xhr) {
          Swal.fire({
            icon: "error",
            title: "Error",
            text: xhr.responseJSON?.responseMessage || "Terjadi kesalahan",
          });
        },
      });
    }
  });
}

// Mengaktifkan user
function activeUser(userId) {
  Swal.fire({
    title: "Konfirmasi",
    text: "Apakah Anda yakin ingin mengaktifkan user ini?",
    icon: "question",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Ya, Aktifkan!",
    cancelButtonText: "Batal",
  }).then((result) => {
    if (result.isConfirmed) {
      $.ajax({
        url: "/management/users/active/" + userId,
        type: "PUT",
        success: function (response) {
          Swal.fire({
            icon: "success",
            title: "Berhasil!",
            text: response.responseMessage,
          });
          $("#table-users").DataTable().ajax.reload();
        },
        error: function (xhr) {
          Swal.fire({
            icon: "error",
            title: "Error",
            text: xhr.responseJSON?.responseMessage || "Terjadi kesalahan",
          });
        },
      });
    }
  });
}

// Toggle status user
function toggleUser(userId) {
  Swal.fire({
    title: "Konfirmasi",
    text: "Apakah Anda yakin ingin mengubah status user ini?",
    icon: "question",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "Ya, Ubah!",
    cancelButtonText: "Batal",
  }).then((result) => {
    if (result.isConfirmed) {
      $.ajax({
        url: "/management/users/toggle/" + userId,
        type: "PUT",
        success: function (response) {
          Swal.fire({
            icon: "success",
            title: "Berhasil!",
            text: response.responseMessage,
          });
          $("#table-users").DataTable().ajax.reload();
        },
        error: function (xhr) {
          Swal.fire({
            icon: "error",
            title: "Error",
            text: xhr.responseJSON?.responseMessage || "Terjadi kesalahan",
          });
        },
      });
    }
  });
}

// // Fungsi untuk membuka/menutup dropdown yang diklik
// function toggleCustomDropdown(event, id) {
//     event.stopPropagation();
    
//     // Tutup semua dropdown lain yang sedang terbuka
//     var allMenus = document.querySelectorAll('.custom-dropdown-menu');
//     allMenus.forEach(function(menu) {
//         if (menu.id !== 'customMenu-' + id) {
//             menu.style.display = 'none';
//         }
//     });

//     // Toggle menu yang dipilih
//     var menu = document.getElementById('customMenu-' + id);
//     if (menu.style.display === 'block') {
//         menu.style.display = 'none';
//     } else {
//         menu.style.display = 'block';
//     }
// }

// // Menutup dropdown jika pengguna mengklik di luar area dropdown
// window.addEventListener('click', function() {
//     var allMenus = document.querySelectorAll('.custom-dropdown-menu');
//     allMenus.forEach(function(menu) {
//         menu.style.display = 'none';
//     });
// });

// Fungsi untuk membuka/menutup dropdown kustom per baris
function toggleCustomDropdown(event, id) {
    event.stopPropagation();
    
    // Tutup semua dropdown lain yang sedang terbuka
    var allMenus = document.querySelectorAll('.custom-dropdown-menu');
    allMenus.forEach(function(menu) {
        if (menu.id !== 'customMenu-' + id) {
            menu.style.div = 'none'; // keamanan
            menu.style.display = 'none';
        }
    });

    // Toggle menu yang sedang dipilih
    var menu = document.getElementById('customMenu-' + id);
    if (menu.style.display === 'block') {
        menu.style.display = 'none';
    } else {
        menu.style.display = 'block';
    }
}

// Fungsi bantu untuk menutup semua menu
function closeAllDropdowns() {
    var allMenus = document.querySelectorAll('.custom-dropdown-menu');
    allMenus.forEach(function(menu) {
        menu.style.display = 'none';
    });
}

// Menutup dropdown jika pengguna mengklik di luar area dropdown
window.addEventListener('click', function() {
    closeAllDropdowns();
});