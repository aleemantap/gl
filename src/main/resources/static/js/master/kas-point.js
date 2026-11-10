/**
 * Kas Point - CRUD + DataTables
 * Dipisah dari template agar mudah di-debug & di-cache browser.
 */
(function () {
  "use strict";

  // ===== Konfigurasi =====
  // Ambil base URL dari atribut data di <body> atau <meta>
  // Fallback: '/'
  const ctx = document.body.dataset.ctx || "/";
  const API_URL = ctx + "master/kas-point";

  let table = null;
  let modalForm = null;
  let modalDelete = null;

  // ===== Helper: cek library =====
  function checkLibraries() {
    const missing = [];
    if (typeof window.jQuery === "undefined") missing.push("jQuery");
    if (typeof window.jQuery?.fn?.DataTable === "undefined")
      missing.push("DataTables");
    if (typeof window.jQuery?.fn?.select2 === "undefined")
      missing.push("Select2");
    if (typeof window.bootstrap === "undefined") missing.push("Bootstrap");
    if (typeof window.Swal === "undefined") missing.push("SweetAlert2");
    if (missing.length) {
      console.error("[kas-point] Library belum di-load:", missing.join(", "));
      return false;
    }
    return true;
  }

  // ===== Helper: cek elemen wajib =====
  function checkElements() {
    const required = [
      "#tblKasPoint",
      "#modalForm",
      "#modalDelete",
      "#deleteId",
      "#formKasPoint",
      "#jenis",
      "#cabangId",
    ];
    const missing = required.filter(
      (sel) => document.querySelector(sel) === null,
    );
    if (missing.length) {
      console.error("[kas-point] Elemen tidak ditemukan:", missing.join(", "));
      return false;
    }
    return true;
  }

  // ===== Init Select2 =====
  function initSelect2() {
    $("#cabangId").select2({
      placeholder: "Pilih Cabang",
      allowClear: true,
      dropdownParent: $("#modalForm"),
    });
    $("#jenis").select2({
      placeholder: "Pilih Jenis",
      allowClear: true,
      dropdownParent: $("#modalForm"),
    });
    $("#filterJenis").select2({
      placeholder: "Pilih Jenis",
      allowClear: true,
    });
    $("#filterCabang").select2({
      placeholder: "Pilih Cabang",
      allowClear: true,
    });
  }

  // ===== Init Modal =====
  function initModals() {
    modalForm = new bootstrap.Modal(document.getElementById("modalForm"));
    modalDelete = new bootstrap.Modal(document.getElementById("modalDelete"));
  }

  // ===== Init DataTable =====
  function initDataTable() {
    table = $("#tblKasPoint").DataTable({
      processing: true,
      lengthChange: false,
      searching: false,
      info: true,
      scrollX: true,
      pageLength: 10,
      order: [[0, "desc"]],
      ajax: {
        url: API_URL + "/data",
        dataSrc: "",
        data: function (d) { 
          d.code = $("#filterCode").val() || "";
          d.nama = $("#filterNama").val() || "";
          d.jenis = $("#filterJenis").val() || "";
          d.cabangId = $("#filterCabang").val() || "";
        },
        error: function (xhr) {
          console.error(
            "[kas-point] Gagal load data:",
            xhr.status,
            xhr.responseText,
          );
          Swal.fire({
            icon: "error",
            title: "Gagal Memuat Data",
            text: "Status " + xhr.status + ": " + (xhr.statusText || "Unknown"),
          });
        },
      },
      columns: [
        { data: "id", defaultContent: "-", visible: false },
        { data: "kasPointCode", defaultContent: "-" },
        { data: "nama", defaultContent: "-" },
        { data: "jenis", defaultContent: "-" },
        {
          data: "namaCabang",
          defaultContent: "-",
          render: function (data, type, row) {
            return row.kodeCabang ? row.kodeCabang + " - " + data : data || "-";
          },
        },
        {
          data: null,
          orderable: false,
          render: function (row) {
            return `
                            <button class="btn btn-sm btn-warning btn-edit" data-id="${row.id}" title="Edit">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24"
                                     fill="none" stroke="currentColor" stroke-width="2"
                                     stroke-linecap="round" stroke-linejoin="round">
                                    <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                                    <path d="M4 20h4l10.5 -10.5a2.828 2.828 0 1 0 -4 -4l-10.5 10.5v4"/>
                                    <path d="M13.5 6.5l4 4"/>
                                </svg>
                            </button>
                            <button class="btn btn-sm btn-danger btn-delete" data-id="${row.id}" title="Hapus">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24"
                                     fill="none" stroke="currentColor" stroke-width="2"
                                     stroke-linecap="round" stroke-linejoin="round">
                                    <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                                    <path d="M4 7h16"/>
                                    <path d="M10 11v6"/>
                                    <path d="M14 11v6"/>
                                    <path d="M5 7l1 12a2 2 0 0 0 2 2h8a2 2 0 0 0 2 -2l1 -12"/>
                                    <path d="M9 7v-3a1 1 0 0 1 1 -1h4a1 1 0 0 1 1 1v3"/>
                                </svg>
                            </button>`;
          },
        },
      ],
    });
  }

  // ===== Handler: filter =====
  function bindFilter() {
    $("#btn-filter").on("click", function () {
      if (table) table.ajax.reload();
    });
    $("#btn-reset").on("click", function () {
      $("#filterNama").val("");
      $("#filterJenis").val(null).trigger("change");
      $("#filterCabang").val(null).trigger("change");
      if (table) table.ajax.reload();
    });
    $("#toggleFilterBtn").on("click", function () {
      $("#filterContainer").slideToggle();
      const hidden = $("#filterContainer").is(":hidden");
      $("#toggleText").text(hidden ? "Tampilkan Filter" : "Sembunyikan Filter");
      $("#toggleIcon")
        .removeClass("ti-chevron-down ti-chevron-up")
        .addClass(hidden ? "ti-chevron-down" : "ti-chevron-up");
    });
  }

  // ===== Handler: tambah =====
  function bindAdd() {
    $("#btnAdd").on("click", function (e) {
      e.preventDefault();
      $("#modalTitle").text("Tambah Kas Point");
      $("#formKasPoint")[0].reset();
      $("#id").val("");
      $("#kasPointCode").val("");
      $("#kasPointCode").attr({disabled : false});
      $("#nama").val("");
      $("#jenis").val(null).trigger("change");
      $("#cabangId").val(null).trigger("change");
      modalForm.show();
    });
  }

  // ===== Handler: edit =====
  function bindEdit() {
    $(document).on("click", ".btn-edit", function () {
      const id = $(this).data("id");
      $.ajax({
        url: API_URL + "/" + id,
        type: "GET",
        success: function (data) {
          $("#modalTitle").text("Edit Kas Point");
          $("#id").val(data.id);
          $("#kasPointCode").val(data.kasPointCode);
          $("#kasPointCode").attr({disabled:true})
          $("#nama").val(data.nama);
          $("#jenis").val(data.jenis).trigger("change");
          $("#cabangId").val(data.cabangId).trigger("change");
          modalForm.show();
        },
        error: function (xhr) {
          console.error("[kas-point] Gagal ambil detail:", xhr.status);
          Swal.fire({
            icon: "error",
            title: "Gagal",
            text: "Gagal mengambil data",
          });
        },
      });
    });
  }

  // ===== Handler: submit (insert/update) =====
  function bindSubmit() {
    $("#formKasPoint").on("submit", function (e) {
      e.preventDefault();

      const id = $("#id").val();
      const cabangVal = $("#cabangId").val();
      const payload = {
        kasPointCode: $("#kasPointCode").val().replace(/\s+/g, "-"),
        nama: $("#nama").val(),
        jenis: $("#jenis").val(),
        cabangId: cabangVal ? parseInt(cabangVal) : null,
      };

      if (!payload.nama || !payload.jenis || !payload.cabangId) {
        Swal.fire({
          icon: "warning",
          title: "Lengkapi Data",
          text: "Semua field wajib diisi",
        });
        return;
      }

      const method = id ? "PUT" : "POST";
      const url = id ? API_URL + "/" + id : API_URL;

      $.ajax({
        url: url,
        type: method,
        contentType: "application/json",
        data: JSON.stringify(payload),
        success: function () {
          modalForm.hide();
          if (table) table.ajax.reload(null, false);
          Swal.fire({
            icon: "success",
            title: "Berhasil",
            text: id ? "Data berhasil diperbarui" : "Data berhasil disimpan",
            timer: 1500,
            showConfirmButton: false,
          });
        },
        error: function (xhr) {
          console.error(
            "[kas-point] Gagal simpan:",
            xhr.status,
            xhr.responseText,
          );
          Swal.fire({
            icon: "error",
            title: "Gagal Menyimpan",
            text:
              xhr.responseJSON?.message ||
              xhr.statusText ||
              "Terjadi kesalahan",
          });
        },
      });
    });
  }

  // ===== Handler: delete =====
  function bindDelete() {
    $(document).on("click", ".btn-delete", function () {
      $("#deleteId").val($(this).data("id"));
      modalDelete.show();
    });

    $("#btnConfirmDelete").on("click", function () {
      const id = $("#deleteId").val();
      $.ajax({
        url: API_URL + "/" + id,
        type: "DELETE",
        success: function () {
          modalDelete.hide();
          if (table) table.ajax.reload(null, false);
          Swal.fire({
            icon: "success",
            title: "Berhasil",
            text: "Data berhasil dihapus",
            timer: 1500,
            showConfirmButton: false,
          });
        },
        error: function (xhr) {
          console.error("[kas-point] Gagal hapus:", xhr.status);
          Swal.fire({
            icon: "error",
            title: "Gagal",
            text: "Gagal menghapus data",
          });
        },
      });
    });
  }

  // ===== Bootstrap =====
  $(function () {
    console.group("[kas-point] init");
    try {
      if (!checkLibraries()) return;
      if (!checkElements()) return;

      initSelect2();
      initModals();
      initDataTable();
      bindFilter();
      bindAdd();
      bindEdit();
      bindSubmit();
      bindDelete();

      console.log("[kas-point]  semua init sukses");
    } catch (err) {
      console.error("[kas-point] ❌ init gagal:", err);
    } finally {
      console.groupEnd();
    }
  });
})();
