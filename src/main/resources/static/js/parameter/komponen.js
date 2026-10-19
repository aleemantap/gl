$(document).ready(function() {
//   alert('ss')
  let table;
  let modalInstance;
  let isEdit = false;

  // Initialize DataTable
  function initTable() {
    table = $("#componentTable").DataTable({
      processing: true,
      serverSide: true,
      searching: false,
      paging: true,
      info: true,
      scrollX: true,
      pageLength: 10,
      lengthChange: false,
      ajax: {
        url: "/parameter/komponen/datatables",
        type: "GET",
        data: function (d) {
          return {
            draw: d.draw,
            start: d.start,
            length: 20,
            orderColumn: d.order[0] ? d.order[0].column : 1,
            orderDir: d.order[0] ? d.order[0].dir : "desc",
            componentCode: $("#filterKode").val(),
            componentName: $("#filterNama").val(),
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
        { data: "componentCode" },
        { data: "componentName" },
        { data: "description" },
        {
          data: "isActive",
          render: function (data) {
            return data
              ? '<span class="badge bg-success text-white">Active</span>'
              : '<span class="badge bg-danger text-white">Inactive</span>';
          },
        },

        {
          data: "id",
          render: function (data) {
            return `
                                    <div class="btn-group btn-group-sm">
                                        <button class="btn  btn-edit" data-id="${data}">
                                             <img src="/img/pencil.png" />
                                        </button>
                                        <button class="btn btn-delete ms-1" data-id="${data}">
                                             <img src="/img/delete.png" />
                                        </button>
                                    </div>
                                `;
          },
          orderable: false,
          width: "120px",
        },
      ],
      drawCallback: function () {
        $("#check-all").prop("checked", false);
      },
      columnDefs: [{ targets: [0], visible: true }],
      order: [[1, "asc"]],
      pageLength: 10,
      language: {
        processing:
          '<div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div>',
      },
    });
  }

  // Initialize modal
  function initModal() {
    modalInstance = new bootstrap.Modal(
      document.getElementById("componentModal"),
      {
        backdrop: "static",
        keyboard: false,
      },
    );
  }

 

   // Reset form
    function resetForm() {
        $('#componentForm')[0].reset();
        $('#componentId').val('');
        $('#componentForm .is-invalid').removeClass('is-invalid');
        isEdit = false;
        $('#modalTitle').text('Add New Component');
        $('#btnSubmit').text('Save');
        $("#kd_komponen").prop("readonly", false);
    }
            
    // Load data for edit
    function loadEditData(id) {
        $.ajax({
            url: '/parameter/komponen/' + id,
            type: 'GET',
            success: function(response) {
                if (response.success) {
                    const data = response.data;
                    $('#componentId').val(data.id);
                    $("#kd_komponen").val(data.componentCode);
                    $("#nm_komponen").val(data.componentName);
                    $('#description').val(data.description);
                    // $('#isActive').prop('checked', data.isActive);
                     $("#isActiveC").val(data.isActive ? 1 : 0);
                    
                    isEdit = true;
                    $('#modalTitle').text('Edit Component');
                    $('#btnSubmit').text('Update');
                    $('#componentCode').prop('readonly', true);
                    
                    modalInstance.show();
                } else {
                    showToast( 'error',response.message);
                }
            },
            error: function(xhr) {
                showToast( 'error','Error loading data: ' + xhr.responseText);
            }
        });
    }
            
    // Save data (create or update)
    function saveData() {
        // Validate form
        let isValid = true;
        const code = $("#kd_komponen").val().trim();
        const name = $("#nm_komponen").val().trim();
        const description = $("#description").val().trim();
        
        if (!code) {
            $("#kd_komponen").addClass("is-invalid");
            $('#codeError').text('Component code is required');
            isValid = false;
        } else {
            $("#kd_komponen").removeClass("is-invalid");
        }
        
        if (!name) {
            $("#nm_komponen").addClass("is-invalid");
            $('#nameError').text('Component name is required');
            isValid = false;
        } else {
            $("#nm_komponen").removeClass("is-invalid");
        }

        if (!description) {
          $("#description").addClass("is-invalid");
          $("#descriptionError").text("Description name is required");
          isValid = false;
        } else {
          $("#description").removeClass("is-invalid");
        }
        
        if (!isValid) return;
        
        const id = $('#componentId').val();
        const data = {
            componentCode: code,
            componentName: name,
            description: $('#description').val().trim(),
            isActive: $('#isActiveC').val() == 1 ? true : false//is(':checked')
        };
        
        const url = id
          ? "/parameter/komponen/" + id
          : "/parameter/komponen/create";
        const method = id ? 'PUT' : 'POST';
        
        $.ajax({
            url: url,
            type: method,
            contentType: 'application/json',
            data: JSON.stringify(data),
            beforeSend: function() {
                $('#btnSubmit').prop('disabled', true).html('<span class="spinner-border spinner-border-sm"></span> Saving...');
            },
            success: function(response) {
               
                if (response.success) {
                    showToast('success',response.message);
                    modalInstance.hide();
                    table.ajax.reload();
                     //console.error(response.success);
                    $("#btnSubmit").prop("disabled", false);
                    // alert('ss')
                } else {
                   
                      showToast('error',response.message);
                    //   alert(response.message);
                      $("#btnSubmit").prop("disabled", false);
                   
                }
                //  alert("sss");
            },
            error: function(xhr) {
                let message = 'Error saving data';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    message = xhr.responseJSON.message;
                }
                showToast('error',message);
            },
            complete: function() {
                $("#btnSubmit")
                  .prop("disabled", false)
                  .html(
                    '<i class="fas fa-save"></i> <span id="btnSaveText">' +
                      (isEdit ? "Update" : "Save") +
                      "</span>",
                  );
            }
        });
    }
    
    // Delete data
    function deleteData(id) {
        if (!confirm('Are you sure you want to delete this component?')) return;
        
        $.ajax({
            url: '/parameter/komponen/' + id,
            type: 'DELETE',
            success: function(response) {
                if (response.success) {
                    showToast('success',response.message);
                    table.ajax.reload();
                } else {
                    showToast('error',response.message);
                }
            },
            error: function(xhr) {
                let message = 'Error deleting data';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    message = xhr.responseJSON.message;
                }
                //   showToast(message, 'error');
                 showToast("error", message);
            }
        });
    }
    
    // Event Handlers
    $("#btnTambah").click(function () {
      resetForm();
      modalInstance.show();
    });
            
    $(document).on('click', '.btn-edit', function() {
        const id = $(this).data('id');
        loadEditData(id);
    });
            
    $(document).on('click', '.btn-delete', function() {
        const id = $(this).data('id');
        deleteData(id);
    });
    
    $('#btnSubmit').click(function() {
        saveData();
    });
            
    // Enter key on form inputs
    $('#componentForm input, #componentForm textarea').keypress(function(e) {
        if (e.which === 13) {
            e.preventDefault();
            saveData();
        }
    });
            
    // Filter handlers
    $('#searchCode, #searchName').on('keyup', function() {
        table.ajax.reload();
    });
    
    $('#btnReset').click(function() {
        $('#searchCode').val('');
        $('#searchName').val('');
        table.ajax.reload();
    });
            
    // Modal hidden event
    $('#componentModal').on('hidden.bs.modal', function() {
        resetForm();
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
    
    // Initialize
    initModal();
    initTable();


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

      $("#btnFilterGl").click(function () {
        $("#componentTable").DataTable().ajax.reload();
      });
      $("#btnResetFilterGl").click(function () {
        // Reset input
        $("#filterContainer").find("input").val("");

        // Reset select
        $("#filterContainer").find("select").val("").trigger("change");

        // Reload DataTable
        $("#componentTable").DataTable().ajax.reload();
      });

       $("#btn-export-komponen").click(function () {
         const params = new URLSearchParams();
         // Ambil semua nilai input
         const nama = $("#filterNama").val();
         const kode = $("#filterKode").val();
        
         const selectedIds = getSelectedIds();
         if (selectedIds && selectedIds.length > 0) {
           params.append("ids", JSON.stringify(selectedIds));
         }
         // Append hanya jika value tidak kosong
         if (nama) params.append("nama", nama);
         if (kode) params.append("kode", kode);
         
         const baseUrl = "/parameter/komponen/export-excel";
         const fullUrl = params.toString()
           ? `${baseUrl}?${params.toString()}`
           : baseUrl;
         window.location.href = fullUrl;
       });

            
})
