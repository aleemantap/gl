document.addEventListener("DOMContentLoaded", function () {
  const modalElement = document.getElementById("modalDetailLabaRugi");
  const modal = new bootstrap.Modal(modalElement);

  const loading = document.getElementById("detailLoading");
  const empty = document.getElementById("detailEmpty");
  const tableWrapper = document.getElementById("detailTableWrapper");
  const tableBody = document.getElementById("detailTableBody");
  const subtitle = document.getElementById("detailSubtitle");

  const periodeInput = document.querySelector('[name="periode"]');
  const cabangInput = document.querySelector('[name="cabangId"]');

  const rupiah = new Intl.NumberFormat("id-ID", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });

  function formatRupiah(value) {
    return "Rp " + rupiah.format(Number(value || 0));
  }

  function formatTanggal(value) {
    if (!value) return "-";

    const date = new Date(value);

    return new Intl.DateTimeFormat("id-ID", {
      day: "2-digit",
      month: "short",
      year: "numeric",
    }).format(date);
  }

  document.addEventListener("click", async function (event) {
    const button = event.target.closest(".js-detail-laba-rugi");

    if (!button) return;

    const kodePos = button.dataset.kodePos;
    const namaPos = button.dataset.namaPos;

    const periode = periodeInput.value;
    const cabangId = cabangInput.value;

    subtitle.textContent = `${kodePos} - ${namaPos} | ${periode}`;

    tableBody.replaceChildren();
    loading.classList.remove("d-none");
    empty.classList.add("d-none");
    tableWrapper.classList.add("d-none");

    modal.show();

    const params = new URLSearchParams({
      periode: periode,
    });

    if (cabangId) {
      params.append("cabangId", cabangId);
    }

    try {
      const response = await fetch(
        `/laporan/laba-rugi/detail/${encodeURIComponent(kodePos)}?${params}`,
      );

      if (!response.ok) {
        throw new Error("Gagal mengambil detail jurnal");
      }

      const data = await response.json();

      loading.classList.add("d-none");

      if (!data || data.length === 0) {
        empty.classList.remove("d-none");
        return;
      }

      data.forEach((item) => {
        const row = document.createElement("tr");

        const values = [
          formatTanggal(item.tanggal),
          item.nomorJurnal || "-",
          item.nomorTransaksi || "-",
          item.nomorGl || "-",
          item.namaGlAccount || "-",
          formatRupiah(item.debit),
          formatRupiah(item.kredit),
          formatRupiah(item.saldo),
        ];

        values.forEach((value, index) => {
          const cell = document.createElement("td");
          cell.textContent = value;

          if (index >= 5) {
            cell.classList.add("text-end", "text-nowrap");
          }

          row.appendChild(cell);
        });

        tableBody.appendChild(row);
      });

      tableWrapper.classList.remove("d-none");
    } catch (error) {
      loading.classList.add("d-none");

      empty.textContent = error.message;
      empty.classList.remove("d-none");
    }
  });
});
