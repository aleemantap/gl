$(document).ready(function () {
  const note = $("#note");
  const validateNote = $("#validateNote");
  const rejectNote = $("#rejectNote");

  $(document).on(
    "submit",
    'form[action*="/governance/validasi-neraca-saldo/validate/"], form[action*="/governance/validasi-neraca-saldo/reject/"]',
    function () {
      validateNote.val(note.val());
      rejectNote.val(note.val());
    },
  );
});

function submitReject(form) {
  const note = document.getElementById("note").value.trim();

  if (note === "") {
    alert("Catatan wajib diisi untuk melakukan penolakan.");

    return false;
  }

  form.querySelector('input[name="note"]').value = note;

  return confirm("Apakah Anda yakin ingin MENOLAK Neraca Saldo ini?");
}

function submitValidate(form) {
  const note = document.getElementById("note").value.trim();

  form.querySelector('input[name="note"]').value = note;

  return confirm("Apakah Anda yakin Neraca Saldo ini sudah sesuai?");
}
