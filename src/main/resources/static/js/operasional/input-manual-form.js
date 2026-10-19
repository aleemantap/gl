$(document).ready(function () {
  // Initialize Select2
  $(".select2").select2({
    theme: "bootstrap-5",
    placeholder: "Pilih...",
    allowClear: true,
  });

  $("#counterparty_div").show();

  $("#am_inf_pembayaran").hide();

  // if (v == "pembayaran") {
  //   $("#am_inf_pembayaran").show();
  //   $("#am_inf_pencairan").hide();
  //   $("#counterparty_div").hide();
  //   $("#tll").text("PEMBAYARAN");
  // } else if (v == "pencairan") {
  //   $("#am_inf_pencairan").show();
  //   $("#am_inf_pembayaran").hide();

  //   $("#counterparty_div").show();
  //   $("#tll").text("PENCAIRAN");
  // }
  // Show/hide fields based on jenisForm
  function toggleFormFields() {
    var jenisForm = $("#jenisForm").val();

    if (jenisForm === "pencairan") {
      $("#am_inf_pencairan").show();
      $("#collateral_inf_pencairan").show();
      
      $("#am_inf_pembayaran").hide();
      $("#tll").text("PENCAIRAN");
    } else {
      $("#am_inf_pencairan").hide();
      $("#collateral_inf_pencairan").hide();
      
      $("#am_inf_pembayaran").show();
      $("#tll").text("PEMBAYARAN");
    }
  }

  // Call on load
  toggleFormFields();

  // Call on change
  $("#jenisForm").on("change", function () {
    toggleFormFields();
  });

  // Reset Form
  $("#resetBtn").on("click", function () {
    $("#formTransaksiGL")[0].reset();
    $(".select2").val(null).trigger("change");
    toggleFormFields();
    // Reset validation states
    $(".is-invalid").removeClass("is-invalid");
    $(".invalid-feedback").remove();
    $("#submitBtn").prop("disabled", false);
  });

  // ==================== AJAX SUBMIT ====================
  $("#submitBtn").on("click", function (e) {
    e.preventDefault();

    // =====================================================
    // CLEAR PREVIOUS VALIDATION
    // =====================================================

    $(".is-invalid").removeClass("is-invalid");
    $(".invalid-feedback").remove();

    // =====================================================
    // GET FORM VALUE
    // =====================================================

    // Transaction Information
    var transactionNo = $("#transactionNo").val();
    // var transactionCode = $("#tranCode").val();
    var transactionCode = $("#tranCode option:selected").data("code");
    var occurredAt = $("#occurredAt").val();
    var idempotencyKey = $("#idempotencyKey").val();
    var productCode = $("#productCode option:selected").data("code"); //$("#productCode").val();
    var branchCode = $("#branchCode option:selected").data("code"); //$("#branchCode").val();

    // Jenis Form
    var jenisForm = $("#jenisForm").val();

    // Counterparty Information
    var customerId = $("#customerId").val();
    var customerName = $("#customerName").val();
    var customerType = $("#customerType").val();

    // Teller Information
    var teller = $("#teller").val();
    var kasPoint = $("#kasPoint").val();

    // Amount - Pembayaran
    var interest_amount = $("#interest_amount").val();
    var principal_payment = $("#principal_payment").val();
    var payment_amount = $("#payment_amount").val();
    //interest_amount payment_amount principal_payment

    // Amount - Pencairan
    var loanAmount = $("#loan_amount").val();
    var adminCost = $("#adminCost").val();
    var provision = $("#provision").val();
    var customerCash = $("#customer_cash").val();
    var customerBank = $("#customer_bank").val();
    var bankCode = $("#code_bank").val();
    var admin_in_advance = $("#admin_in_advance").val();
    var provision_in_advance = $("#provision_in_advance").val();

    // 
    var collateral_type = $("#collateral_type").val();
    // var appraised_value = $("#appraised_value").val();
    // var ltv = $("#ltv").val();

    // =====================================================
    // FUNCTION VALIDATION
    // =====================================================

    function showWarning(message) {
      Swal.fire({
        icon: "warning",
        title: "Data Belum Lengkap",
        text: message,
        confirmButtonText: "OK",
      });
    }

    // =====================================================
    // VALIDASI TRANSACTION
    // =====================================================

    if (!transactionNo || transactionNo.toString().trim() === "") {
      showWarning("Transaction No wajib diisi.");
      return;
    }

    if (!transactionCode || transactionCode.toString().trim() === "") {
      showWarning("Transaction Code wajib diisi.");
      return;
    }

    if (!productCode || productCode.toString().trim() === "") {
      showWarning("Product Code wajib diisi.");
      return;
    }

    if (!branchCode || branchCode.toString().trim() === "") {
      showWarning("Branch Code wajib diisi.");
      return;
    }

    if (!idempotencyKey || idempotencyKey.toString().trim() === "") {
      showWarning("Idempotency Key wajib diisi.");
      return;
    }

    if (!occurredAt || occurredAt.toString().trim() === "") {
      showWarning("Tanggal transaksi wajib diisi.");
      return;
    }

    // =====================================================
    // VALIDASI JENIS FORM
    // =====================================================

    if (!jenisForm || jenisForm.toString().trim() === "") {
      showWarning("Jenis Form wajib dipilih.");
      return;
    }

    // =====================================================
    // VALIDASI COUNTERPARTY
    // =====================================================

    if (!customerId || customerId.toString().trim() === "") {
      showWarning("Customer wajib dipilih.");
      return;
    }

    if (!customerName || customerName.toString().trim() === "") {
      showWarning("Customer Name wajib diisi.");
      return;
    }

    if (!customerType || customerType.toString().trim() === "") {
      showWarning("Customer Type wajib dipilih.");
      return;
    }

    // =====================================================
    // VALIDASI ACTOR
    // =====================================================

    if (!teller || teller.toString().trim() === "") {
      showWarning("Teller wajib dipilih.");
      return;
    }

    if (!kasPoint || kasPoint.toString().trim() === "") {
      showWarning("Kas Point wajib dipilih.");
      return;
    }

    // =====================================================
    // VALIDASI AMOUNT
    // =====================================================

    if (jenisForm === "pencairan") {
      if (!loanAmount || loanAmount.toString().trim() === "") {
        showWarning("Loan Amount wajib diisi.");
        return;
      }

      if (!adminCost || adminCost.toString().trim() === "") {
        showWarning("Admin Cost wajib diisi.");
        return;
      }

      if (!provision || provision.toString().trim() === "") {
        showWarning("Provision wajib diisi.");
        return;
      }

      if (!customerCash || customerCash.toString().trim() === "") {
        showWarning("Customer Cash wajib diisi.");
        return;
      }

      // validasi collateral
      if (!collateral_type || collateral_type.toString().trim() === "") {
        showWarning("Collateral Type dipilih.");
        return;
      }

      // if (!appraised_value || appraised_value.toString().trim() === "") {
      //   showWarning("Appraised value dipilih.");
      //   return;
      // }

      // if (!ltv || ltv.toString().trim() === "") {
      //   showWarning("Loan To Value value dipilih.");
      //   return;
      // }
    } else if (jenisForm === "pembayaran") {
      if (!payment_amount || payment_amount.toString().trim() === "") {
        showWarning("payment_amount Amount wajib diisi.");
        return;
      }

      if (!principal_payment || principal_payment.toString().trim() === "") {
        showWarning("principal_payment Amount wajib diisi.");
        return;
      }

      if (!interest_amount || interest_amount.toString().trim() === "") {
        showWarning("InterestAmount Cash wajib diisi.");
        return;
      }
      //interest_amount payment_amount principal_payment
    }

    if (occurredAt) {
      // occurredAt = occurredAt + "T00:00:00+07:00";
      occurredAt = occurredAt + ":00+07:00";
    }

    // =====================================================
    // BUILD JOURNAL REQUEST
    // =====================================================

    var formData = {
      // ==========================
      // Transaction Information
      // ==========================

      transactionNo: transactionNo,
      transactionCode: transactionCode,
      productCode: productCode,
      branchCode: branchCode,
      idempotencyKey: idempotencyKey,
      occurredAt: occurredAt,
      typeTransaction: jenisForm,

      // ==========================
      // Actor
      // ==========================

      actor: {
        tellerUserId: Number(teller),
        kasPointCode: kasPoint,
      },
      collateral: {
        collateral_type: collateral_type,
        // appraised_value: getNumericValue(appraised_value),
        // ltv: getNumericValue(ltv),
      },
      // ==========================
      // Counterparty
      // ==========================

      counterparty: {
        type: customerType,
        ref: customerId,
        name: customerName,
      },

      // ==========================
      // Amounts
      // ==========================

      amounts: {},
    };

    // =====================================================
    // AMOUNTS - PENCAIRAN
    // =====================================================

    if (jenisForm === "pencairan") {
      formData.amounts = {
        loan_amount: getNumericValue(loanAmount),
        admin_cost: getNumericValue(adminCost),
        provision: getNumericValue(provision),
        customer_cash: getNumericValue(customerCash),
        customer_bank: getNumericValue(customerBank),
       
      };
      formData.flags = {
        admin_in_advance: JSON.parse(admin_in_advance),
        provision_in_advance: JSON.parse(provision_in_advance),
      };

      formData.bankCode = bankCode
    }

    // =====================================================
    // AMOUNTS - PEMBAYARAN
    // =====================================================

    if (jenisForm === "pembayaran") {
      formData.amounts = {
        payment_amount: getNumericValue(payment_amount),
        principal_payment: getNumericValue(principal_payment),
        interest_amount: getNumericValue(interest_amount),
      }; //interest_amount payment_amount principal_payment

      // Kalau pembayaran memang membutuhkan interest,
      // uncomment bagian ini:

      // formData.amounts.interest_amount = Number(interestAmount);
    }
    // =====================================================
    // DEBUG
    // =====================================================
    console.log("Data yang akan dikirim:", JSON.stringify(formData, null, 4));
    // =====================================================
    // BUTTON LOADING
    // =====================================================
    var submitBtn = $("#submitBtn");
    var originalText = submitBtn.html();
    submitBtn.html(
      '<span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span> Loading...',
    );
    submitBtn.prop("disabled", true);
    // =====================================================
    // AJAX POST
    // =====================================================
    
    $.ajax({
      url: "/operasional/input-jurnal-manual/saveJurnal",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify(formData),
      dataType: "json",
      // =================================================
      // SUCCESS
      // =================================================
      success: function (response) {
        // console.log("Response sukses:", response);

        submitBtn.html(originalText);
        submitBtn.prop("disabled", false);

        Swal.fire({
          icon: "success",
          title: "Berhasil!",
          text: response.message || "Data transaksi berhasil disimpan",
          confirmButtonText: "OK",
        }).then(function (result) {
          if (result.isConfirmed) {
            $("#formTransaksiGL")[0].reset();

            // $(".select2").val(null).trigger("change");
            $(".select2").val(jenisForm).trigger("change");

            if (typeof toggleFormFields === "function") {
              toggleFormFields();
            }
          }
        });
      },

      // =================================================
      // ERROR
      // =================================================

      error: function (xhr, status, error) {
        // console.error("Error:", xhr);

        submitBtn.html(originalText);
        submitBtn.prop("disabled", false);

        if ((xhr.status === 400 || xhr.status === 422) && xhr.responseJSON) {
          var errors = xhr.responseJSON.errors || xhr.responseJSON;

          // -----------------------------------------
          // ARRAY ERROR
          // -----------------------------------------

          if (Array.isArray(errors)) {
            errors.forEach(function (err) {
              var fieldName = err.field || err.fieldName;

              var errorMsg = err.message || err.defaultMessage;

              if (fieldName) {
                var field = $('[name="' + fieldName + '"]');

                field.addClass("is-invalid");

                field.after(
                  '<div class="invalid-feedback">' + errorMsg + "</div>",
                );
              }
            });

            // -----------------------------------------
            // OBJECT ERROR
            // -----------------------------------------
          } else if (typeof errors === "object") {
            $.each(errors, function (key, value) {
              var field = $('[name="' + key + '"]');

              field.addClass("is-invalid");

              var errorMsg = Array.isArray(value) ? value[0] : value;

              field.after(
                '<div class="invalid-feedback">' + errorMsg + "</div>",
              );
            });
          }

          Swal.fire({
            icon: "error",
            title: "Validasi Gagal",
            text: xhr.responseJSON.message || "Mohon periksa kembali data Anda",
            confirmButtonText: "OK",
          });
        } else {
          Swal.fire({
            icon: "error",
            title: "Error!",
            text: xhr.responseJSON?.message || "Terjadi kesalahan pada server",
            confirmButtonText: "OK",
          });
        }
      },

      // =================================================
      // COMPLETE
      // =================================================

      complete: function () {
        console.log("Request completed");
      },
    }); 
  });
          // Optional: Add SweetAlert2 for better notifications
          // Uncomment if you want to use SweetAlert2
          /*
                    if (typeof Swal === 'undefined') {
                // Load SweetAlert2 if not already loaded
                var script = document.createElement('script');
                script.src = 'https://cdn.jsdelivr.net/npm/sweetalert2@11';
                document.head.appendChild(script);
            }
            */

    // ============ FORMAT RUPIAH OTOMATIS ============
    function formatRupiah(angka) {
        // Hanya ambil angka
        var numberString = angka.replace(/[^,\d]/g, '').toString();
        var split = numberString.split(',');
        var sisa = split[0].length % 3;
        var rupiah = split[0].substr(0, sisa);
        var ribuan = split[0].substr(sisa).match(/\d{3}/gi);
        
        if (ribuan) {
            var separator = sisa ? '.' : '';
            rupiah += separator + ribuan.join('.');
        }
        
        return rupiah;
    }

    // Apply ke semua input dengan class rupiah-input
    $('.rupiah-input').on('keyup', function(e) {
        // Hapus semua karakter kecuali angka
        var value = $(this).val().replace(/[^0-9]/g, '');
        
        if (value.length > 0) {
            // Format dengan titik
            var formatted = formatRupiah(value);
            $(this).val(formatted);
        } else {
            $(this).val('');
        }
    });

    // Saat input kehilangan fokus (blur), tambahkan ,00 jika perlu
    $('.rupiah-input').on('blur', function() {
        var value = $(this).val().replace(/[^0-9]/g, '');
        if (value.length > 0) {
            // Bisa tambahkan ,00 di belakang jika ingin
            // var formatted = formatRupiah(value) + ',00';
            // $(this).val(formatted);
        }
    });

    // Saat fokus, hilangkan format untuk memudahkan edit
    $('.rupiah-input').on('focus', function() {
        var value = $(this).val().replace(/[^0-9]/g, '');
        $(this).val(value);
        // Select all text for easy typing
        $(this).select();
    });

    // ============ FUNGSI UNTUK MENGAMBIL NILAI ANGKA ============
    function getNumericValue(rupiahString) {
        // Hilangkan titik dan koma, ambil hanya angka
        return parseInt(rupiahString.replace(/[^0-9]/g, '')) || 0;
        //  return rupiahString.replace(/[^0-9]/g, "") || 0;
    }
});
