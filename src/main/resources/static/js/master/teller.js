/**
 * Teller - CRUD + DataTables
 */
(function () {
  "use strict";

  const ctx = document.body.dataset.ctx || "/";
  const API_URL = ctx + "master/teller";

  let table = null;
  let modalForm = null;
  let modalDelete = null;

  function checkLibraries() {
    const missing = [];
    if (typeof window.jQuery === "undefined") missing.push("jQuery");
    if (typeof window.jQuery?.fn?.DataTable === "undefined")
      missing.push("DataTables");
    if (typeof window.bootstrap === "undefined") missing.push("Bootstrap");
    if (typeof window.Swal === "undefined") missing.push("SweetAlert2");
    if (missing.length) {
      console.error("[teller] Library belum di-load:", missing.join(", "));
      return false;
    }
    return true;
  }

  function checkElements() {
    const required = [
      "#tblTeller",
      "#modalForm",
      "#modalDelete",
      "#deleteId",
      "#formTeller",
    ];
    const missing = required.filter(
      (sel) => document.querySelector(sel) === null,
    );
    if (missing.length) {
      console.error("[teller] Elemen tidak ditemukan:", missing.join(", "));
      return false;
    }
    return true;
  }

  function initModals() {
    modalForm = new bootstrap.Modal(document.getElementById("modalForm"));
    modalDelete = new bootstrap.Modal(document.getElementById("modalDelete"));
  }

  function initDataTable() {
    table = $("#tblTeller").DataTable({
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
          d.nama = $("#filterNama").val() || "";
          d.userGadai = $("#filterUserGadai").val() || "";
        },
        error: function (xhr) {
          console.error(
            "[teller] Gagal load data:",
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
        { data: "nama", defaultContent: "-" },
        { data: "userGadai", defaultContent: "-" },
        { data: "createdAt", defaultContent: "-", visible: false },
        { data: "updatedAt", defaultContent: "-", visible: false },
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

  function bindFilter() {
    $("#btn-filter").on("click", function () {
      if (table) table.ajax.reload();
    });
    $("#btn-reset").on("click", function () {
      $("#filterNama").val("");
      $("#filterUserGadai").val("");
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

  function bindAdd() {
    $("#btnAdd").on("click", function (e) {
      e.preventDefault();
      $("#modalTitle").text("Tambah Teller");
      $("#formTeller")[0].reset();
      $("#id").val("");
      modalForm.show();
    });
  }

  function bindEdit() {
    $(document).on("click", ".btn-edit", function () {
      const id = $(this).data("id");
      $.ajax({
        url: API_URL + "/" + id,
        type: "GET",
        success: function (data) {
          $("#modalTitle").text("Edit Teller");
          $("#id").val(data.id);
          $("#nama").val(data.nama);
          $("#userGadai").val(data.userGadai);
          modalForm.show();
        },
        error: function (xhr) {
          console.error("[teller] Gagal ambil detail:", xhr.status);
          Swal.fire({
            icon: "error",
            title: "Gagal",
            text: "Gagal mengambil data",
          });
        },
      });
    });
  }

  function bindSubmit() {
    $("#formTeller").on("submit", function (e) {
      e.preventDefault();

      const id = $("#id").val();
      const payload = {
        nama: $("#nama").val(),
        userGadai: $("#userGadai").val(),
      };

      if (!payload.nama || !payload.userGadai) {
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
          console.error("[teller] Gagal simpan:", xhr.status, xhr.responseText);
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
          console.error("[teller] Gagal hapus:", xhr.status);
          Swal.fire({
            icon: "error",
            title: "Gagal",
            text: "Gagal menghapus data",
          });
        },
      });
    });
  }

  $(function () {
    console.group("[teller] init");
    try {
      if (!checkLibraries()) return;
      if (!checkElements()) return;

      initModals();
      initDataTable();
      bindFilter();
      bindAdd();
      bindEdit();
      bindSubmit();
      bindDelete();

      console.log("[teller]  semua init sukses");
    } catch (err) {
      console.error("[teller]  init gagal:", err);
    } finally {
      console.groupEnd();
    }
  });
})();
