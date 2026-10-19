$(document).ready(function () {

  $("#branchCode").select2({
    theme: "bootstrap-5",
    width: "100%",
    allowClear: false,
    // dropdownParent: $("#modal1"),
  });
  $("#glAccountId").select2({
    theme: "bootstrap-5",
    width: "100%",
    allowClear: false,
    // dropdownParent: $("#modal1"),
  });


  let bukuBesarTable;
  function loadBukuBesar() {
    // const glAccountId = $("#glAccountId").val();
    // const tanggalDari = $("#tanggalDari").val();
    // const tanggalSampai = $("#tanggalSampai").val();
    // const branchCode = $("#branchCode").val();

     const glAccountId = $("#glAccountId").val();
     const tanggalDari = $("#tanggalDari").val();
     const tanggalSampai = $("#tanggalSampai").val();
     const branchCode = $("#branchCode").val();

     // Validate all required fields
     if (!glAccountId || glAccountId === "" || glAccountId === "0") {
       alert("Silakan pilih akun terlebih dahulu");
       return;
     }

     if (!tanggalDari) {
       alert("Silakan pilih tanggal dari");
       return;
     }

     if (!tanggalSampai) {
       alert("Silakan pilih tanggal sampai");
       return;
     }

    $.ajax({
      url: "/laporan/buku-besar/datatables",
      type: "GET",

      data: {
        glAccountId: glAccountId,
        tanggalDari: tanggalDari,
        tanggalSampai: tanggalSampai,
        branchCode: branchCode,
      },

      success: function (response) {
        const saldoAwal = Number(response.saldoAwal || 0);

        const normalBalance = response.normalBalance;

        const data = response.data || [];

        let saldoBerjalan = saldoAwal;

        let totalDebit = 0;
        let totalKredit = 0;

        // ==========================================
        // HITUNG SALDO BERJALAN
        // ==========================================

        data.forEach(function (row) {
          const amount = Number(row.amount || 0);

          if (row.dk === "D") {
            totalDebit += amount;
          } else if (row.dk === "K") {
            totalKredit += amount;
          }

          if (normalBalance === "D") {
            if (row.dk === "D") {
              saldoBerjalan += amount;
            } else {
              saldoBerjalan -= amount;
            }
          } else {
            if (row.dk === "K") {
              saldoBerjalan += amount;
            } else {
              saldoBerjalan -= amount;
            }
          }

          // simpan saldo pada row
          row.saldoBerjalan = saldoBerjalan;
        });

        // ==========================================
        // SALDO AKHIR
        // ==========================================

        const saldoAkhir = saldoBerjalan;

        // ==========================================
        // SUMMARY
        // ==========================================

        $("#saldoAwal").text(formatRupiah(saldoAwal));

        $("#totalDebit").text(formatRupiah(totalDebit));

        $("#totalKredit").text(formatRupiah(totalKredit));

        $("#saldoAkhir").text(formatRupiah(saldoAkhir));

        // ==========================================
        // DATATABLE
        // ==========================================

        if ($.fn.DataTable.isDataTable("#tableBukuBesar")) {
          bukuBesarTable.clear().rows.add(data).draw();
        } else {
          bukuBesarTable = $("#tableBukuBesar").DataTable({
            data: data,
            processing: true,
            // serverSide: true,
            searching: false,
            paging: true,
            info: true,
            scrollX: true,
            pageLength: 25,
            lengthChange: false,

            columns: [
              {
                data: null,
                orderable: false,
                searchable: false,
                className: "text-center align-middle",
                render: function (data, type, row) {
                  return `
                      <input type="checkbox" class="row-checkbox form-check-input m-0 align-middle" value="${row.detailId}">
                  `;
                },
              },
              {
                data: "accountCode",
                render: function (data, type, row) {
                  return `
                      <div>
                          <div>${data}</div>
                          <div class="text-muted small">${row.accountName}</div>
                      </div>`;
                },
              },
              {
                data: "branchCode",
                render: function (data, type, row) {
                  return `
                      <div>
                          <div>${data}</div>
                          <div class="text-muted small">${row.branchName}</div>
                      </div>`;
                },
              },
              {
                data: "transactionDate",
              },

              {
                data: "journalNo",
              },

              {
                data: "transactionNo",
              },

              {
                data: "transactionCode",
              },
              {
                data: "dk",
              },
              {
                data: "amount",
                className: "text-end",
                render: function (data) {
                  return formatRupiah(data);
                },
              },

              {
                data: "saldoBerjalan",
                className: "text-end",
                render: function (data) {
                  return formatRupiah(data);
                },
              },

              // {
              //   data: "description",
              // },
            ],
            drawCallback: function () {
              $("#check-all").prop("checked", false);
            },
          });
        }
      },
    });
  }

//  document.getElementById("glAccountId").selectedIndex = 0;

//   loadBukuBesar();
    $("#btnCari").on("click", function () {
      loadBukuBesar();
     });
    $("#btnReset").on("click", function () {
     //loadBukuBesar();
    });

    $("#btn-export").on("click", function () {
       const params = new URLSearchParams();
       // Ambil semua nilai input
       const glAccountId = $("#glAccountId").val();
       const tanggalDari = $("#tanggalDari").val();
       const tanggalSampai = $("#tanggalSampai").val();
       const branchCode = $("#branchCode").val();
      const selectedIds = getSelectedIds();
     
      // Validate all required fields
      if (!glAccountId || glAccountId === "" || glAccountId === "0") {
        alert("Silakan pilih akun terlebih dahulu");
        return;
      }

      if (!tanggalDari) {
        alert("Silakan pilih tanggal dari");
        return;
      }

      if (!tanggalSampai) {
        alert("Silakan pilih tanggal sampai");
        return;
      }
   
      // Append hanya jika value tidak kosong
       if (selectedIds && selectedIds.length > 0) {
         params.append("ids", JSON.stringify(selectedIds));
       }
       if (glAccountId) params.append("glAccountId", glAccountId);
       if (tanggalDari) params.append("tanggalDari", tanggalDari);
       if (tanggalSampai) params.append("tanggalSampai", tanggalSampai);
       if (branchCode) params.append("branchCode", branchCode);
      //  window.location.href =  "/laporan/buku-besar/export/excel?" + params.toString();
       const baseUrl = "/laporan/buku-besar/export/excel";
       const fullUrl = params.toString()
         ? `${baseUrl}?${params.toString()}`
         : baseUrl;

       console.info("Generated URL:", fullUrl);

       // Redirect ke backend untuk download Excel
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


function formatRupiah(value) {
  return Number(value || 0).toLocaleString("id-ID", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}