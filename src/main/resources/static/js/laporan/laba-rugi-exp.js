
document.getElementById('btnExportExcel').addEventListener('click', function () {
    exportTableToExcel('card-laba-rugi', 'Rincian_Laba_Rugi');
});

function exportTableToExcel(tableWrapperId, filename) {
    // 1. Ambil elemen card berdasarkan id
    const wrapper = document.getElementById(tableWrapperId);
    if (!wrapper) {
        alert('Tabel tidak ditemukan!');
        return;
    }

    // 2. Ambil judul & subtitle untuk header Excel
    const titleEl    = wrapper.querySelector('.card-title');
    const subtitleEl = wrapper.querySelector('.card-subtitle');
    const title      = titleEl ? titleEl.innerText.trim() : 'Laporan';
    const subtitle   = subtitleEl ? subtitleEl.innerText.trim() : '';

    // 3. Ambil tabel
    const table = wrapper.querySelector('table');
    if (!table) {
        alert('Tabel tidak ditemukan!');
        return;
    }

    // 4. Clone tabel supaya HTML asli tidak berubah
    const clone = table.cloneNode(true);

    // 5. Bersihkan tombol/link di dalam clone → ganti jadi teks saja
    //    (supaya Excel tidak menampilkan tombol)
    clone.querySelectorAll('button').forEach(function (btn) {
        const td = btn.closest('td');
        if (td) {
            td.textContent = btn.innerText.trim();
        }
    });

    // 6. Hapus komentar? (tidak perlu, cloneNode tidak membawa komentar)

    // 7. Build HTML untuk Excel
    const html = `
        <html xmlns:o="urn:schemas-microsoft-com:office:office"
              xmlns:x="urn:schemas-microsoft-com:office:excel"
              xmlns="http://www.w3.org/TR/REC-html40">
        <head>
            <meta charset="UTF-8">
            <!--[if gte mso 9]>
            <xml>
                <x:ExcelWorkbook>
                    <x:ExcelWorksheets>
                        <x:ExcelWorksheet>
                            <x:Name>Laba Rugi</x:Name>
                            <x:WorksheetOptions>
                                <x:DisplayGridlines/>
                            </x:WorksheetOptions>
                        </x:ExcelWorksheet>
                    </x:ExcelWorksheets>
                </x:ExcelWorkbook>
            </xml>
            <![endif]-->
            <style>
                table { border-collapse: collapse; font-family: Arial, sans-serif; font-size: 12px; }
                th, td { border: 1px solid #ccc; padding: 4px 8px; }
                th { background: #f0f0f0; font-weight: bold; }
                .text-end { text-align: right; }
                .text-nowrap { white-space: nowrap; }
                .fw-bold { font-weight: bold; }
                .table-light { background: #f8f9fa; }
                .table-secondary { background: #e2e3e5; }
                .table-success { background: #d1e7dd; }
                .ps-4 { padding-left: 1.5rem !important; }
                .title { font-size: 16px; font-weight: bold; margin-bottom: 4px; }
                .subtitle { font-size: 12px; color: #666; margin-bottom: 8px; }
            </style>
        </head>
        <body>
            <div class="title">${escapeHtml(title)}</div>
            <div class="subtitle">${escapeHtml(subtitle)}</div>
            ${clone.outerHTML}
        </body>
        </html>
    `;

    // 8. Buat Blob & trigger download
    const blob = new Blob([html], { type: 'application/vnd.ms-excel;charset=utf-8;' });
    const url  = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename + '_' + formatDate(new Date()) + '.xls';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
}

// Helper: escape HTML agar aman
function escapeHtml(str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

// Helper: format tanggal yyyyMMdd
function formatDate(d) {
    const y  = d.getFullYear();
    const m  = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    return `${y}${m}${dd}`;
}