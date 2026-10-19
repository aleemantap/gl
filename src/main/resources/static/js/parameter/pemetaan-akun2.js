let globalComponentOptions = [];
//let productRuleId;
$(document).ready(function () {
  let selectedGL = [];
  $("#filterContainer").hide();
  $("#toggleIcon").removeClass("ti-chevron-down").addClass("ti-chevron-right");
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

  $("#btn-tambah-tab2").on("click", function (e) {
    e.preventDefault();
    $("#form2")[0].reset();
    //$("#modalTitle2").text("Tambah Pemetaan TC");
    //$("#btn-submit-tc2").text("Simpan");
    $("#form2")[0].reset();
    $("#jr_id").val("");
    $("#glAccountDebit").empty().val(null).trigger("change");
    $("#glAccounKredit").empty().val(null).trigger("change");
    $("#generate-id").prop("disabled", false);
    $("#modal2 .modal-title").text("Tambah Journal");
    $("#btnSubmit2").text("Simpan");
    $("#modal2").modal("show");
  });
  $("#generate-id").on("click", function (e) {
    e.preventDefault();
    // Tampilkan loading
    $(this).prop("disabled", true);
    $(this).html('<i class="fa fa-spinner fa-spin"></i> Generating...');
    $.ajax({
      ///parameter/pemetaan-akun
      url: "/parameter/pemetaan-akun/generate-random-id",
      type: "GET",
      dataType: "json",
      success: function (response) {
        if (response.success) {
          $("#journalKode").val(response.randomId);
          $("#journalIdDisplay").val(response.randomId);
          showAlert("success", "ID berhasil digenerate: " + response.randomId);
        } else {
          showAlert("danger", "Gagal generate ID: " + response.message);
        }
      },
      error: function (xhr, status, error) {
        showAlert("danger", "Terjadi kesalahan: " + error);
        console.error("Error:", error);
      },
      complete: function () {
        // Reset button
        $("#generate-id").prop("disabled", false);
        $("#generate-id").html("Generate ID");
      },
    });
  });

  $("#btnSubmit2").on("click", function (e) {
    e.preventDefault();
    // alert("EE")
    var editId = $("#jr_id").val();
    var url = "/parameter/pemetaan-akun/add";
    if (editId) {
      url = "/parameter/pemetaan-akun/update/" + editId;
    }
    // Hapus error sebelumnya
    //$(".is-invalid").removeClass("is-invalid");
    //$(".invalid-feedback").text("");

    // Validasi form
    //var isValid = true;

    var journalId = $("#journalIdDisplay").val();
    if (!journalId) {
      //$("#journalCode").addClass("is-invalid");
      //$("#journalIdError").text("JournalId  wajib diisi");
      //isValid = false;
      showToast("error", "JournalId wajib ada");
      return;
    }

    var catatan = $("#catatan").val().trim();
     if (!catatan) {
       showToast("error", "Catatan wajib ada");
       return;
     }

      var productRuleId = $("#productRuleId").val().trim();
      if (!productRuleId) {
        showToast("error", "ProductRuleId wajib ada");
        return;
      }

     
    // Validasi minimal harus ada GL yang dipilih
    if (selectedGL.length === 0) {
      // alert('Silakan pilih minimal satu GL.');
      showToast("error", "Silakan pilih minimal satu GL.");
      return;
    }

    // if (!isValid) {
    //   return;
    // }

    // Disable button dan tampilkan loading
    $("#btnSubmit2").prop("disabled", true).html(`
            <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
            Menyimpan...
        `);

   
    var formData = {
      journalCode: journalId,
      catatan: catatan,
      glItems: [],
    };

    $("#table-gl-selected tbody tr").each(function () {
      const row = $(this);

      // Ambil ID dari hidden td atau data attribute
      const id = row.find("td:first").text().trim(); // atau row.data('gl-id')

      // Ambil nilai select DK
      const dk = row.find(".gl-dk-select").val();

      // Ambil nilai select Component Rule
      // const componentRuleId = row.find(".gl-option-select").val();
       // Ambil select element
      const selectElement = row.find(".gl-option-select");
      
      // Ambil option yang dipilih
      const selectedOption = selectElement.find("option:selected");
      
      // Ambil data-id dari option yang dipilih (bukan value)
      const componentRuleId = selectedOption.data("id");

      const cashMovementType = row.find(".tipe_pergerakan_kas").val();

      // Hanya tambahkan jika componentRuleId tidak kosong
      if (componentRuleId && componentRuleId !== "") {
        formData.glItems.push({
          id: id,
          dk: dk,
          componentRuleId: componentRuleId,
          cashMovementType: cashMovementType,
        });
      }
    });

   // console.error("SS", formData);

    // Kirim data ke server
    $.ajax({
      url: url,
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify(formData),
      dataType: "json",
      success: function (response) {
        // Tutup modal
        $("#modal2").modal("hide");

        // Tampilkan notifikasi sukses
        var te = editId
          ? "Data Journal ID berhasil dirubah"
          : "Data Journal ID berhasil ditambahkan";
        showToast("success", te);

        // Reload DataTable
        if ($.fn.DataTable.isDataTable("#table2")) {
          $("#table2").DataTable().ajax.reload();
        }

        // Reset form
        $("#btnSubmit2").prop("disabled", false).html(`
                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                    <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                    <path d="M12 5l0 14" />
                    <path d="M5 12l14 0" />
                </svg>
                Simpan
            `);
      },
      error: function (xhr, status, error) {
        console.error("Error saving data:", xhr);
        // Tampilkan error dari server
        var errorMessage = "Gagal memproses data";
        if (xhr.responseJSON && xhr.responseJSON.message) {
          errorMessage = xhr.responseJSON.message;
        } else if (xhr.responseText) {
          try {
            var response = JSON.parse(xhr.responseText);
            if (response.message) {
              errorMessage = response.message;
            }
          } catch (e) {}
        }

        showToast("error", errorMessage);

        // Enable button kembali
        $("#btnSubmit2").prop("disabled", false).html(`
                    <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                        <path d="M12 5l0 14" />
                        <path d="M5 12l14 0" />
                    </svg>
                    Simpan
                `);
      },
    });
  });

  $("#btnSubmit3").on("click", function (e) {
    e.preventDefault();
    // alert("EE")
    //var editId = $("#jlt_id").val();
    var url = "/parameter/pemetaan-akun/add-one";
    //if (editId) {
    //  url = "/parameter/pemetaan-akun/update-one/" + editId;
    //}
    //   
     var nomorglcmp = $("#nomor-gl-cmp").val();
     if (!nomorglcmp) {
       showToast("error", "GlAccount wajib ada");
     }
     //alay
      var formData = {
        jurnalId: $("#jurnal_id_cmp").val(),
        nomorglcmpId: nomorglcmp,
        dkcmp: $("#dk-cmp").val(),
        componentRuleId: $("#component-rule-cmp").val(),
        typePergerakanCash: $("#type-pergerakan-cash").val(),
        status: $("#sts-cmp").val() == 1 ? true : false,
      };
       $.ajax({
         url: url,
         type: "POST",
         contentType: "application/json",
         data: JSON.stringify(formData),
         dataType: "json",
         success: function (response) {
           // Tutup modal
           $("#modal3").modal("hide");

           // Tampilkan notifikasi sukses
          //  var te = editId
          //    ? "Data Journal ID berhasil dirubah"
          //    : "Data Journal ID berhasil ditambahkan";
           showToast("success", "Data Component berhasil ditambahkan");

           // Reload DataTable
           if ($.fn.DataTable.isDataTable("#table2")) {
             $("#table2").DataTable().ajax.reload();
           }

           // Reset form
           $("#btnSubmit3").prop("disabled", false).html(`
                <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                    <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                    <path d="M12 5l0 14" />
                    <path d="M5 12l14 0" />
                </svg>
                Simpan
            `);
         },
         error: function (xhr, status, error) {
           console.error("Error saving data:", xhr);
           // Tampilkan error dari server
           var errorMessage = "Gagal memproses data";
           if (xhr.responseJSON && xhr.responseJSON.message) {
             errorMessage = xhr.responseJSON.message;
           } else if (xhr.responseText) {
             try {
               var response = JSON.parse(xhr.responseText);
               if (response.message) {
                 errorMessage = response.message;
               }
             } catch (e) {}
           }

           showToast("error", errorMessage);

           // Enable button kembali
           $("#btnSubmit3").prop("disabled", false).html(`
                    <svg xmlns="http://www.w3.org/2000/svg" class="icon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                        <path d="M12 5l0 14" />
                        <path d="M5 12l14 0" />
                    </svg>
                    Simpan
                `);
         },
       });

  });

  const table = new DataTable("#table2", {
    processing: true,
    serverSide: true,
    searching: false,
    paging: true,
    info: true,
    scrollX: false,
    pageLength: 10,
    lengthChange: false,
    order: [[1, "desc"]],
    ajax: {
      url: "/parameter/pemetaan-akun/datatables",
      type: "GET",

      data: function (d) {
        let orderColumn = 1; // default ke kolom id (index 1)
        let orderDir = "desc";

        if (d.order && d.order.length > 0) {
          // DataTables mengirim order dengan column index
          // Tapi kita perlu mapping ke column data
          orderColumn = d.order[0].column;
          orderDir = d.order[0].dir;
        }

        return {
          draw: d.draw,
          start: d.start,
          length: 20,
          orderColumn: orderColumn,
          orderDir: orderDir,
          // orderColumn: d.order[0] ? d.order[0].column : 1,
          // orderDir: d.order[0] ? d.order[0].dir : "desc",
          // email: $("#filterEmail").val(),
          // name: $("#filterName").val(),
          // cabangId: $("#customerId").val(),
          // userGroupId: $("#userGroupId").val()
        };
      },
    },

    columns: [
      {
        data: null,
        className: "details-control text-center",
        orderable: false,
        searchable: false,
        width: "30px",
        defaultContent: `
                <i class="ti ti-chevron-right"></i>
            `,
      },
      { data: "id", visible: false },
      { data: "journalCode", orderable: false, className: "text-start" },
      {
        data: "description",
        orderable: false,
        className: "text-start",
        render: function (data, type, row) {
          // Render sebagai input text
          return `<input type="text" class="form-control form-control-sm description-input" value="${data || ""}" data-id="${row.id || row.DT_RowId}" />`;
        },
      },
      {
        data: "totalAkun",
        orderable: false,
        className: "text-start",
      },
      {
        data: "isActive",
        orderable: false,
        className: "text-start",
        render: function (data) {
          let isActive = data === true || data === "true";
          return `
            <select class="form-select form-select-sm select-sts">
              <option value="1" ${isActive ? "selected" : ""}>Active</option>
              <option value="0" ${!isActive ? "selected" : ""}>Inactive</option>
            </select>
          `;
        },
      },
      {
        data: null,
        orderable: false,
        className: "text-start",
        render: function (data, type, row) {
          return `
                <div style="display: flex; gap: 15px;">
                    <a class="btn-ubah-atas" href="#" data-id="${row.id}"  data-desc="${row.description}" data-sts="${row.isActive}">
                       Save
                    </a>
                    <a class="btn-delete-atas" href="#" data-id="${row.id}">
                        Hapus
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
  $("#table2 tbody").on("click", "td.details-control", function () {
    const tr = $(this).closest("tr");
    const row = table.row(tr);

    const journalId = row.data().id;

    if (row.child.isShown()) {
      // Tutup detail
      row.child.hide();

      tr.removeClass("shown");

      $(this)
        .find("i")
        .removeClass("ti-chevron-down")
        .addClass("ti-chevron-right");
    } else {
      // Tampilkan loading
      row
        .child(
          `
            <div class="p-3 text-center">
                <div class="spinner-border spinner-border-sm"></div>
                Memuat detail...
            </div>
        `,
        )
        .show();

      tr.addClass("shown");

      $(this)
        .find("i")
        .removeClass("ti-chevron-right")
        .addClass("ti-chevron-down");

      // Ambil detail dari controller
      $.ajax({
        url: `/parameter/pemetaan-akun/journal-line-template/${journalId}`,
        type: "GET",

        // success: function (data) {
        //    dataComponent(1);
        //    row.child(renderJournalDetail(data)).show();
         
        // },
        success: function (data) {
            // console.error("data", data[0].productRuleId);
            //console.error("data", data); journalId;
            //$("#temp_product_rule_id").val(data[0].productRuleId);
            //productRuleId = data[0].productRuleId;
            dataComponent(data[0].productRuleId)
              .then(function (response) {
                // Setelah dataComponent selesai, baru render child
                row.child(renderJournalDetail(data)).show();
              })
              .catch(function (error) {
                console.error("Error loading component options:", error);
                // Tetap tampilkan detail meskipun ada error
                row.child(renderJournalDetail(data)).show();
              });
        },

        error: function (xhr) {
          row
            .child(
              `
                    <div class="alert alert-danger m-3">
                        Gagal mengambil detail Journal ID.
                    </div>
                `,
            )
            .show();
        },
      });
    }
  });

  $(document).on("click", ".btn-ubah-atas", function (e) {
    e.preventDefault();

    const id = $(this).data("id");
    var $row = $(this).closest("tr");
    var description = $row.find(".description-input").val();
    var isActive = $row.find(".select-sts").val();

    // Validasi
    if (!description || description.trim() === "") {
      alert("Description tidak boleh kosong!");
      return;
    }

    // Confirm bawaan JavaScript
    if (
      confirm(
        `Apakah anda yakin ingin meng-update data ini?\n\nDescription: ${description}\nStatus: ${isActive == 1 ? "Active" : "Inactive"}`,
      )
    ) {
      // Kirim ke controller via AJAX
      $.ajax({
        url: "/parameter/pemetaan-akun/jurnal-update/" + id,
        method: "POST",
        data: {
          id: id,
          description: description,
          isActive: isActive,
          // _token: $('meta[name="csrf-token"]').attr("content"),
        },
        success: function (response) {
          if (response.success) {
            alert("Data berhasil diupdate!");
            $("#table2").DataTable().ajax.reload();
          } else {
            alert("Gagal: " + (response.message || "Terjadi kesalahan"));
          }
        },
        error: function (xhr) {
          let errorMessage = "Terjadi kesalahan!";

          if (xhr.responseJSON && xhr.responseJSON.message) {
            errorMessage = xhr.responseJSON.message;
          } else if (xhr.status === 404) {
            errorMessage = "Data tidak ditemukan!";
          } else if (xhr.status === 500) {
            errorMessage = "Terjadi kesalahan pada server!";
          }

          alert("Error: " + errorMessage);
        },
      });
    }
  });

   $(document).on("click", ".btn-delete-atas", function (e) {
     e.preventDefault();
     const id = $(this).data("id");
    //  alert("Belum Bisa digunakan"+id);
     if (confirm(`Apakah anda yakin ingin meng-hapus data ini?`)) {
       // Kirim ke controller via AJAX
       // var formData = {
       //   id: id,

       //   // _token: $('meta[name="csrf-token"]').attr("content"),
       // };
       $.ajax({
         url: "/parameter/pemetaan-akun/journal-id-hapus/" + id,
         method: "POST",
         //data: JSON.stringify(formData),
         // data: formData,
         success: function (response) {
           alert("Data berhasil dihapus");
           $("#table2").DataTable().ajax.reload();
         },
         error: function (xhr) {
           alert("Terjadi kesalahan!");
         },
       });
     }
   });

  $(document).on("click", ".btn-save2", function (e) {
        e.preventDefault();
        const id = $(this).data("id");
        var $row = $(this).closest("tr");
        // var selectCom = $row.find(".gl-option-select-child").val();
        var selectedOption = $row.find(
            ".gl-option-select-child option:selected",
          );
        var dataId = selectedOption.data("id");
        var selectDk = $row.find(".dk-child").val();
        var isActive = $row.find(".status-child").val();
        var selectKasMov = $row.find(".kas-child").val();
        // Validasi
        // if (!description || description.trim() === "") {
        //   alert("Description tidak boleh kosong!");
        //   return;
        // }
        //  alert(id)
        // Confirm bawaan JavaScript
        if (
          confirm(
            `Apakah anda yakin ingin meng-update data ini?`,
          )
        ) {
          // Kirim ke controller via AJAX
          var formData = {
            id: id,
            selectComponent: dataId,
            dk: selectDk,
            isActive: isActive == 1 ? true : false,
            cashMovementType: selectKasMov,
            // _token: $('meta[name="csrf-token"]').attr("content"),
          };
          $.ajax({
            url: "/parameter/pemetaan-akun/jlt-update/" + id,
            method: "POST",
            //data: JSON.stringify(formData),
            data: formData,
            success: function (response) {
              alert("Data berhasil diupdate!");
              $("#table2").DataTable().ajax.reload();
            },
            error: function (xhr) {
              alert("Terjadi kesalahan!");
            },
          });
        }
  });

   $(document).on("click", ".btn-hapus2", function (e) {
     e.preventDefault();
     const id = $(this).data("id");
     //  alert("Belum Bisa digunakan-"+id);
     //Detail Line Template
      if (confirm(`Apakah anda yakin ingin meng-hapus data ini?`)) {
        // Kirim ke controller via AJAX
        // var formData = {
        //   id: id,
       
        //   // _token: $('meta[name="csrf-token"]').attr("content"),
        // };
        $.ajax({
          url: "/parameter/pemetaan-akun/jlt-template-hapus/" + id,
          method: "POST",
          //data: JSON.stringify(formData),
          // data: formData,
          success: function (response) {
            alert("Data berhasil dihapus");
            $("#table2").DataTable().ajax.reload();
          },
          error: function (xhr) {
            alert("Terjadi kesalahan!");
          },
        });
      }
   });

   

   $("#table2 tbody").on("click", ".add-child", function (e) {
     e.preventDefault();
    var ruleId = $(this).data("rule");
    console.info(ruleId)
    //  alert("s", ruleId);
    // dataComponent(rule);

    const parentTr = $(this).closest("tr").prev("tr");
   // Ambil data dari DataTables
    const row = table.row(parentTr);
    const journalId = row.data().id;
    
    //console.log('Journal ID from parent:', journalId);
    
     dataComponent(ruleId)
       .then(function (response) {
          var $select = $("#component-rule-cmp");
          $select.empty();

          // Tambahkan option default
          var options = '<option value="">-- Pilih Component --</option>';

          // Generate option dari response
          response.forEach(function (item) {
            options += `
                <option value="${item.productRuleComponentId}" data-cash-movement="${item.cashMovementType}">
                      ${item.componentCode} - ${item.componentName}
                </option>
            `;
          });

          // Masukkan ke select
          $select.html(options);
       })
       .catch(function (error) {
           console.error("Error:", error);
       }); 
    $("#jurnal_id_cmp").val(journalId);
    $("#modal3").modal("show");

   });


  function resetModal() {
    $(".is-invalid").removeClass("is-invalid");
    $(".invalid-feedback").text("");
    $("#btnSubmit2").text("Update");
  }

  function ubah(id) {
    resetModal();
    $("#modal2 .modal-title").text("Ubah Journal");
    $.ajax({
      url: "/parameter/pemetaan-akun/get/" + id,
      type: "GET",
      success: function (res) {
        console.log("RES", res);
        $("#generate-id").prop({ disabled: true });
        $("#jr_id").val(res.id);
        $("#journalCode").val(res.journalCode);
        $("#journalIdDisplay").val(res.journalCode);
        // Kosongkan option lama
        $("#glAccountDebit").empty();
        const debitOption = new Option(
          res.debetNomorGl,
          res.debetGlAccountId,
          true,
          true,
        );
        // Tambahkan data lengkap ke Select2
        $(debitOption).data("data", {
          id: res.debetGlAccountId,
          text: res.debetNomorGl,
          nomorGl: res.debetNomorGl,
          nama: res.debetGlName,
        });
        $("#glAccountDebit").append(debitOption).trigger("change");

        // Kosongkan option lama
        $("#glAccounKredit").empty();
        // Buat option GL Kredit
        const kreditOption = new Option(
          res.kreditNomorGl,
          res.kreditGlAccountId,
          true,
          true,
        );
        // Tambahkan data lengkap ke Select2
        $(kreditOption).data("data", {
          id: res.kreditGlAccountId,
          text: res.kreditNomorGl,
          nomorGl: res.kreditNomorGl,
          nama: res.kreditGlName,
        });
        $("#glAccounKredit").append(kreditOption).trigger("change");
        $("#btnSubmit2").text("Update");
        $("#catatan").val(res.description);
        $("#modal2").modal("show");
      },
    });
  }

  // GL map ----
 const table2Mapgl = new DataTable("#table2-map-gl-account", {
   processing: true,
   serverSide: true,
   searching: false,
   paging: true,
   info: true,
   // scrollY: true,
   scrollX: true,
   pageLength: 10,
   lengthChange: false,
   ajax: {
     url: "/parameter/nomor-gl/datatables",
     type: "GET",
     data: function (d) {
       return {
         draw: d.draw,
         start: d.start,
         length: 20,
         orderColumn: d.order[0] ? d.order[0].column : 1,
         orderDir: d.order[0] ? d.order[0].dir : "desc",
         nomorGl: $("#filterNomorGl").val() || null,
         namaGlAccount: $("#filterNamaGl").val() || null,
         kategoriId: $("#filterKategori").val() || null,
         cabangId: $("#cabangIdModal").val() || null,
         debitKredit: $("#filterDk").val() || null,
       };
     },
   },
   columns: [
     {
       data: "id",
       orderable: false,
       searchable: false,
       className: "text-center",
       render: function (data, type, row) {
         return `
                  <input type="checkbox"
                        class="form-check-input check-gl"
                        value="${row.id}"
                        data-nomor-gl="${row.nomorGl}"
                        data-nama-gl="${row.namaGlAccount}"
                        data-kategori="${row.kategoriName}"
                        data-dk="${row.debitKredit}"
                        data-cabang="${row.cabangName}">
              `;
       },
     },
     { data: "nomorGl" },
     { data: "namaGlAccount" },
     { data: "glTypeName" },
     // { data: "debitKredit" },
     // { data: "cabangName" },
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
   drawCallback: function () {
     $("#table2-map-gl-account .check-gl").each(function () {
       const id = $(this).val();

       const checked = selectedGL.some((x) => x.id == id);

       $(this).prop("checked", checked);
     });
   },
 });
  $("#btnFilterGl").click(function () {
    $("#table2-map-gl-account").DataTable().ajax.reload();
  });
  $("#btnResetFilterGl").click(function () {
    // Reset input
    $("#filterContainer").find("input").val("");
    // Reset select[]
    $("#filterContainer").find("select").val("").trigger("change");
    // Reload DataTable
    $("#table2-map-gl-account").DataTable().ajax.reload();
  });

  // Event handler
  $("#productRuleId").change(function () {
    const id = $(this).val();

    if (id) {
      $("#rw-tbl").show();

      // Panggil AJAX untuk mendapatkan data options
      dataComponent(id);
    } else {
      $("#rw-tbl").hide();
      const tbody = $("#table-gl-selected tbody");
      tbody.empty();
      $(".check-gl").prop("checked", false);

      // Reset ke options default
      // generateSelectOptions = function (selectedValue = "") {
      //   const options = [{ value: "", label: "Pilih Opsi" }];

      //   return options
      //     .map(
      //       (opt) =>
      //         `<option value="${opt.value}" ${selectedValue === opt.value ? "selected" : ""}>${opt.label}</option>`,
      //     )
      //     .join("");
      // };
      globalComponentOptions = [];
    }
  });

  // $("#table2-map-gl-account tbody").on("change", ".check-gl", function () {
  //   const checkbox = $(this);
  //   const id = checkbox.val();
  //   const data = {
  //     id: id,
  //     nomorGl: checkbox.data("nomor-gl"),
  //     namaGl: checkbox.data("nama-gl"),
  //     kategori: checkbox.data("kategori"),
  //     dk: checkbox.data("dk"),
  //     cabang: checkbox.data("cabang"),
  //   };

  //   if (checkbox.is(":checked")) {
  //     // Tambahkan jika belum ada
  //     if (!selectedGL.some((x) => x.id == id)) {
  //       selectedGL.push(data);
  //     }
  //   } else {
  //     // Hapus jika checkbox dibatalkan
  //     selectedGL = selectedGL.filter((x) => x.id != id);
  //   }

  //   renderSelectedGL();
  // });

  $("#table2-map-gl-account tbody").on("change", ".check-gl", function () {
    const checkbox = $(this);
    const id = checkbox.val();
    const data = {
      id: id,
      nomorGl: checkbox.data("nomor-gl"),
      namaGl: checkbox.data("nama-gl"),
      kategori: checkbox.data("kategori"),
      dk: checkbox.data("dk"),
      cabang: checkbox.data("cabang"),
      selectedOption: "", // Tambahkan field untuk menyimpan pilihan option
    };

    if (checkbox.is(":checked")) {
      // Tambahkan jika belum ada
      if (!selectedGL.some((x) => x.id == id)) {
        selectedGL.push(data);
      }
    } else {
      // Hapus jika checkbox dibatalkan
      selectedGL = selectedGL.filter((x) => x.id != id);
    }

    renderSelectedGL();
  });

  $("#table-gl-selected tbody").on("click", ".btn-remove-gl", function () {
    const id = $(this).data("id");
    // Hapus dari array
    selectedGL = selectedGL.filter((x) => x.id != id);
    // Uncheck checkbox di tabel kiri
    $(`#table2-map-gl-account .check-gl[value="${id}"]`).prop("checked", false);

    // Render ulang
    renderSelectedGL();
  });

  // Event handler untuk perubahan select option di tabel kanan
  $("#table-gl-selected tbody").on("change", ".gl-option-select", function () {
    const id = $(this).data("id");
    const selectedValue = $(this).val();

    // Cari data GL di array selectedGL dan update nilai selectedOption
    const gl = selectedGL.find((item) => item.id == id);
    if (gl) {
      gl.selectedOption = selectedValue;
    }
  });

  // Event handler untuk perubahan select D/K di tabel kanan
  $("#table-gl-selected tbody").on("change", ".gl-dk-select", function () {
    const id = $(this).data("id");
    const selectedValue = $(this).val();

    // Cari data GL di array selectedGL dan update nilai dk
    const gl = selectedGL.find((item) => item.id == id);
    if (gl) {
      gl.dk = selectedValue;
    }
  });

  function renderSelectedGLXX() {
    const tbody = $("#table-gl-selected tbody");

    tbody.empty();

    selectedGL.forEach(function (gl) {
      const selectedD = gl.dk === "D" ? "selected" : "";
      const selectedK = gl.dk === "K" ? "selected" : "";
      tbody.append(`
            <tr>
                <td style="display: none;">${gl.id}</td>
                <td>${gl.nomorGl}</td>
                <td>${gl.namaGl}</td>
                <td>
                   <select class="form-select form-select-sm gl-dk-select" data-id="${gl.id}">
                        <option value="D" ${selectedD}>D (Debit)</option>
                        <option value="K" ${selectedK}>K (Kredit)</option>
                    </select>
                </td>
                <td><select class="form-select form-select-sm gl-option-select" data-id="${gl.id}">
                        ${generateSelectOptions(gl.selectedOption || "")}
                    </select>
                </td>
                <td class="text-center">

                    <button type="button"
                            class="btn btn-sm btn-outline-danger btn-remove-gl"
                            data-id="${gl.id}">
                        <i class="ti ti-trash"></i>
                    </button>

                </td>
            </tr>
        `);
    });
  }

  function renderSelectedGL() {
    const tbody = $("#table-gl-selected tbody");
    tbody.empty();

    selectedGL.forEach(function (gl) {
      const selectedD = gl.dk === "D" ? "selected" : "";
      const selectedK = gl.dk === "K" ? "selected" : "";

      tbody.append(`
            <tr>
                <td style="display: none;">${gl.id}</td>
                <td>${gl.nomorGl}</td>
                <td>${gl.namaGl}</td>
                <td>
                    <select class="form-select form-select-sm gl-dk-select" data-id="${gl.id}">
                        <option value="D" ${selectedD}>D (Debit)</option>
                        <option value="K" ${selectedK}>K (Kredit)</option>
                    </select>
                </td>
                <td>
                    <select class="form-select form-select-sm gl-option-select" data-id="${gl.id}">
                        ${generateSelectOptions(gl.selectedOption || "")}
                    </select>
                </td>
                <td>
                  <select class="form-select form-select-sm tipe_pergerakan_kas">
                      <option value="IN">IN</option>
                      <option value="OUT">OUT</option>
                      <option value="NONE">NONE</option>                     
                  </select>
                                    
                </td>
                <td class="text-center">
                    <button type="button"
                            class="btn btn-sm btn-outline-danger btn-remove-gl"
                            data-id="${gl.id}">
                        <i class="ti ti-trash"></i>
                    </button>
                </td>
            </tr>
        `);
    });
  }
});
function renderJournalDetail(data) {
  if (!data || data.length === 0) {
    return `
            <div class="p-3">
                <div class="alert alert-info mb-0">
                    <i class="ti ti-info-circle me-1"></i>
                    Belum ada GL Account yang dipetakan.
                </div>
            </div>
        `;
  }

  let html =
    `
        <div class="p-3 bg-light">
            <div class="d-flex align-items-center mb-2">
              <input type="hidden"  class="temp_product_rule_id" value="` +
    data[0].productRuleId +
    `" />
              <i class="ti ti-list me-2"></i>
              <strong>Detail Line Template</strong>
              <a class="btn btn-success btn-sm add-child"  data-rule="` +
    data[0].productRuleId +
    `" style="margin-left: auto;">Add Cmp</a>
            </div>
            <div class="table-responsive">
                <table class="table table-sm table-striped table-bordered table-striped mb-0">
                    <thead>
                        <tr>
                            <th style="width: 50px;"
                                class="text-center">
                                No
                            </th>
                            <th>
                                Nomor GL
                            </th>
                            <th>
                                Nama GL Account
                            </th>
                            <th style="width: 80px;"
                                class="text-center">
                                D/K
                            </th>
                            <th>
                                Component
                            </th>
                            <th>
                                Pergerakan Kas
                            </th>
                            <th>
                                Status
                            </th>
                            <th>
                                Aksi
                            </th>
                        </tr>
                    </thead>
                    <tbody>
    `;

  data.forEach(function (item, index) {
    //let dkBadge = "";
    // console.error(item) 
    //componentId
    var dkBadge = `<select class="dk-child">
              <option value="D" ${item.dk === "D" ? "selected" : ""}>D</option>
              <option value="K" ${item.dk === "K" ? "selected" : ""}>K</option>
              </select>`;
    var status = `<select class="status-child">
              <option value="1" ${item.isActive === true ? "selected" : ""}>Active</option>
              <option value="0" ${item.isActive === false ? "selected" : ""}>InActiv</option>
              </select>`;
    
    var kasMov = `<select class="kas-child">
              <option value="IN" ${item.cashMovementType === "IN" ? "selected" : ""}>IN</option>
              <option value="OUT" ${item.cashMovementType === "OUT" ? "selected" : ""}>OUT</option>
              <option value="NONE" ${item.cashMovementType === "NONE" ? "selected" : ""}>NONE</option>
              </select>`;

    // if (item.dk === "D" || item.dk === "DEBIT") {
    //   dkBadge = `
    //             <span class="badge bg-success text-white">
    //                 D
    //             </span>
    //         `;
    // } else if (item.dk === "K" || item.dk === "KREDIT") {
    //   dkBadge = `
    //             <span class="badge bg-warning text-white">
    //                 K
    //             </span>
    //         `;
    // } else {
    //   dkBadge = `
    //             <span class="badge bg-secondary text-white">
    //                 ${item.dk ?? "-"}
    //             </span>
    //         `;
    // }

    html += `
            <tr>
                <td class="text-center">
                    ${index + 1}
                </td>
                <td>
                    <strong>
                        ${item.nomorGl ?? "-"}
                    </strong>
                </td>
                <td>
                    ${item.namaGlAccount ?? "-"}
                </td>   
                <td class="text-center">
                    ${dkBadge}
                </td>
                <td>
                     <select class="form-select form-select-sm gl-option-select-child" data-id="${item.id}">
                        ${generateSelectOptions(item.componentId || "")}
                    </select>                    
                </td>
                <td>
                    ${kasMov}
                </td>
                <td>
                    ${status}
                </td>
                <td>
                    <div style="display: flex; gap: 15px;">
                      
                        <a class="btn-save2" href="#" data-id="${item.id}">
                           Save
                        </a>
                        <a class="btn-hapus2" href="#" data-id="${item.id}">
                           Hapus
                        </a>
                    </div>
                </td>

            </tr>

        `;
  });

  html += `

                    </tbody>

                </table>

            </div>

        </div>

    `;

  return html;
}

// Fungsi generateSelectOptions yang menggunakan data global
function generateSelectOptions(selectedValue = "") {
    // Jika ada data dari AJAX, gunakan data tersebut
    if (globalComponentOptions && globalComponentOptions.length > 0) {
        let optionsHtml = `<option value="">Pilih Komponen</option>`;
        
        globalComponentOptions.forEach(function (item) {
           const valueId = item.productRuleComponentId;
             //console.log("KAPROK",item) 
             const value = item.componentId;
            const label = `${item.componentCode} - ${item.componentName} - ${item.amountSource}`;
            const selected = selectedValue == value ? "selected" : "";
            optionsHtml += `<option data-id="${valueId}" value="${value}" ${selected}>${label}</option>`;
        });
        
        return optionsHtml;
    }
    
    // Fallback: jika belum ada data, return option default
    const options = [
        { value: "", label: "Pilih Opsi" },
    ];
    
    return options
        .map(opt => 
            `<option value="${opt.value}" ${selectedValue === opt.value ? "selected" : ""}>${opt.label}</option>`
        )
        .join("");
}


function dataComponent(productRuleid = 1) {
  return new Promise((resolve, reject) => {
    $.ajax({
      url: `/parameter/pemetaan-akun/options-rule-component/${productRuleid}`,
      type: "GET",
      dataType: "json",
      success: function (response) {
        //console.error("res",response);
        globalComponentOptions = response;

        // Re-render tabel jika ada data yang sudah terpilih
        // if (typeof selectedGL !== "undefined" && selectedGL.length > 0) {
        //   renderSelectedGL();
        // }

        resolve(response); // Resolve Promise setelah selesai
      },
      error: function (xhr, status, error) {
        console.error("Error fetching options:", error);
        globalComponentOptions = [];
        reject(error); // Reject Promise jika error
      },
    });
  });
}

function dataComponentXX(productRuleid=1){

  $.ajax({
    url: `/parameter/pemetaan-akun/options-rule-component/${productRuleid}`,
    type: "GET",
    dataType: "json",
    success: function (response) {
      // Update fungsi generateSelectOptions dengan data dari AJAX
      // generateSelectOptions = function (selectedValue = "") {
      //   let optionsHtml = `<option value="">Pilih Komponen</option>`;

      //   // Loop data dari response
      //   response.forEach(function (item) {
      //     const value = item.productRuleComponentId;
      //     // Gabungkan field-field menjadi label
      //     const label = `${item.componentCode} - ${item.componentName} - ${item.amountSource} - ${item.cashMovementType}`;
      //     // const label = `${item.componentCode} - ${item.componentName}`;
      //     const selected = selectedValue == value ? "selected" : "";
      //     optionsHtml += `<option value="${value}" ${selected}>${label}</option>`;
      //   });

      //   return optionsHtml;
      // };
      // Simpan data ke variable global
      globalComponentOptions = response;

      // Re-render tabel jika ada data yang sudah terpilih
      if (typeof selectedGL !== "undefined" && selectedGL.length > 0) {
        renderSelectedGL();
      }
    },
    error: function (xhr, status, error) {
      console.error("Error fetching options:", error);
      // Fallback ke options default jika error
      // generateSelectOptions = function (selectedValue = "") {
      //   const options = [{ value: "", label: "Pilih Opsi" }];

      //   return options
      //     .map(
      //       (opt) =>
      //         `<option value="${opt.value}" ${selectedValue === opt.value ? "selected" : ""}>${opt.label}</option>`,
      //     )
      //     .join("");
      // };
      globalComponentOptions = [];
    },
  });

}