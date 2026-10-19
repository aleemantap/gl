document.addEventListener("DOMContentLoaded", function () {
  // Ambil semua element
  const uploadArea = document.getElementById("uploadArea");
  const fileInput = document.getElementById("fileInput");
  const btnPosting = document.getElementById("btnPosting");
  const btnBatal = document.getElementById("btnBatal");
  const previewArea = document.getElementById("previewArea");
  const loadingPreview = document.getElementById("loadingPreview");
  const fileInfo = document.getElementById("fileInfo");
  const fileName = document.getElementById("fileName");
  const fileSize = document.getElementById("fileSize");
  const totalRows = document.getElementById("totalRows");
  const totalCols = document.getElementById("totalCols");
  const rowCount = document.getElementById("rowCount");
  const colCount = document.getElementById("colCount");

  let uploadedData = null;
  let uploadedHeaders = [];
  let uploadedFileName = "";
  let uploadedFileType = ""; // ✅ TAMBAHAN: menyimpan tipe file (pembayaran/pencairan)

  /*
   * Update info dengan aman
   */
  function updateFileInfo(rows, cols) {
    if (rowCount) rowCount.textContent = rows + " Baris";
    if (colCount) colCount.textContent = cols + " Kolom";
    if (totalRows) totalRows.textContent = rows;
    if (totalCols) totalCols.textContent = cols;
  }

  /*
   * Tampilan awal upload
   */
  function resetUploadArea() {
    uploadArea.innerHTML = `
                        <input
                            type="file"
                            id="fileInput"
                            class="d-none"
                            accept=".xlsx,.xls,.csv">
                        <i class="ti ti-cloud-upload upload-icon"></i>
                        <div class="upload-title">
                            Klik atau tarik file ke sini untuk mengunggah
                        </div>
                        <div class="upload-description">
                            Mendukung format .xlsx, .xls, .csv (Maks. 10MB)
                        </div>
                    `;

    if (previewArea) {
      previewArea.innerHTML = `
                            <i class="ti ti-table preview-icon"></i>
                            <div class="preview-text">
                                Unggah file untuk melihat pratinjau jurnal sebelum posting.
                            </div>
                        `;
      previewArea.classList.remove("has-data");
    }

    if (fileInfo) {
      fileInfo.style.display = "none";
    }

    updateFileInfo(0, 0);

    if (btnPosting) {
      btnPosting.disabled = true;
      btnPosting.classList.remove("btn-primary");
      btnPosting.classList.add("btn-secondary");
    }

    uploadedData = null;
    uploadedHeaders = [];
    uploadedFileName = "";
    uploadedFileType = ""; // ✅ Reset tipe file

    const newFileInput = document.getElementById("fileInput");
    if (newFileInput) {
      newFileInput.addEventListener("change", handleFile);
    }
  }

  /*
   * Format ukuran file
   */
  function formatFileSize(bytes) {
    if (bytes === 0) return "0 Bytes";
    const k = 1024;
    const sizes = ["Bytes", "KB", "MB", "GB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
  }

  /*
   * ✅ TAMBAHAN: Deteksi tipe file berdasarkan nama
   * Mengembalikan "pembayaran", "pencairan", atau "" jika tidak ditemukan
   */
  function detectFileType(fileName) {
    if (!fileName) return "";
    const lowerName = fileName.toLowerCase();
    if (lowerName.includes("pembayaran")) return "pembayaran";
    if (lowerName.includes("pencairan")) return "pencairan";
    return "";
  }

  /*
   * =========================================================
   * PERBAIKAN UTAMA: Baca header dari baris pertama saja
   * =========================================================
   */
  function readExcelFile(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = function (e) {
        try {
          const data = new Uint8Array(e.target.result);
          const workbook = XLSX.read(data, {
            type: "array",
            cellDates: true,
            dateNF: "yyyy-mm-dd",
          });

          const firstSheet = workbook.Sheets[workbook.SheetNames[0]];

          // STEP 1: Baca semua data sebagai matrix
          const matrix = XLSX.utils.sheet_to_json(firstSheet, {
            header: 1,
            defval: "",
            raw: false,
          });

          if (!matrix || matrix.length === 0) {
            resolve({
              headers: [],
              data: [],
              totalRows: 0,
              totalCols: 0,
              sheetName: workbook.SheetNames[0],
            });
            return;
          }

          // STEP 2: Ambil HEADER dari baris pertama
          const headerRow = matrix[0] || [];

          // STEP 3: HITUNG jumlah kolom dari baris pertama (header)
          let headerLength = 0;
          for (let i = headerRow.length - 1; i >= 0; i--) {
            if (headerRow[i] && headerRow[i].trim() !== "") {
              headerLength = i + 1;
              break;
            }
          }

          if (headerLength === 0) {
            headerLength = headerRow.length;
          }

          console.log("Jumlah kolom dari header:", headerLength);
          console.log("Header row:", headerRow.slice(0, headerLength));

          // STEP 4: Ambil header sebanyak headerLength
          const headers = headerRow.slice(0, headerLength).map((col, index) => {
            if (!col || col.trim() === "") {
              return `Kolom ${index + 1}`;
            }
            return col.trim();
          });

          // STEP 5: Ambil data dari baris 2 sampai akhir
          const rawData = matrix.slice(1);
          const normalizedData = rawData.map((row) => {
            const slicedRow = row.slice(0, headerLength);
            while (slicedRow.length < headerLength) {
              slicedRow.push("");
            }
            return slicedRow;
          });

          // STEP 6: Konversi ke array of objects
          const jsonData = normalizedData.map((row) => {
            const obj = {};
            headers.forEach((header, index) => {
              obj[header] =
                row[index] !== undefined && row[index] !== null
                  ? row[index]
                  : "";
            });
            return obj;
          });

          console.log("Headers final:", headers);
          console.log("Jumlah data:", jsonData.length);
          console.log("Sample data:", jsonData[0]);

          resolve({
            headers: headers,
            data: jsonData,
            matrix: normalizedData,
            totalRows: jsonData.length,
            totalCols: headers.length,
            sheetName: workbook.SheetNames[0],
          });
        } catch (error) {
          reject(error);
        }
      };

      reader.onerror = function () {
        reject(new Error("Gagal membaca file"));
      };

      reader.readAsArrayBuffer(file);
    });
  }

  /*
   * Render preview table
   */
  function renderPreviewTable(headers, data, matrix) {
    if (!previewArea) return;

    if (!data || data.length === 0 || !headers || headers.length === 0) {
      previewArea.innerHTML = `
                            <i class="ti ti-table preview-icon"></i>
                            <div class="preview-text" style="color: #dc3545;">
                                ⚠️ File tidak memiliki data atau format tidak sesuai
                            </div>
                        `;
      previewArea.classList.remove("has-data");
      return;
    }

    const maxRows = 100;
    const displayData = data.slice(0, maxRows);
    const isTruncated = data.length > maxRows;

    let tableHtml = `
                        <div class="preview-table-wrapper">
                            <div class="table-scroll">
                                <table class="preview-table">
                                    <thead>
                                        <tr>
                                            <th class="row-number">#</th>
                                    `;

    headers.forEach((header, index) => {
      tableHtml += `<th class="header-cell">
                            <div class="header-with-number">
                                <span class="header-name">${escapeHtml(header)}</span>
                                <span class="header-number">#${index + 1}</span>
                            </div>
                        </th>`;
    });

    tableHtml += `
                                        </tr>
                                    </thead>
                                    <tbody>
                                    `;

    displayData.forEach((row, rowIndex) => {
      tableHtml += `<tr>
                            <td class="row-number">${rowIndex + 1}</td>`;

      headers.forEach((header) => {
        const value =
          row[header] !== undefined && row[header] !== null ? row[header] : "";
        const displayValue =
          value === ""
            ? '<span class="empty-cell">-</span>'
            : escapeHtml(String(value));
        tableHtml += `<td>${displayValue}</td>`;
      });

      tableHtml += `</tr>`;
    });

    tableHtml += `
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    `;

    let infoHtml = `
                        <div style="padding: 10px; background: #f8f9fa; border-top: 1px solid #dee2e6; display: flex; justify-content: space-between; flex-wrap: wrap; gap: 10px; border-radius: 0 0 6px 6px;">
                            <span><strong>Total Kolom:</strong> ${headers.length}</span>
                            <span><strong>Total Baris:</strong> ${data.length}</span>
                    `;

    if (isTruncated) {
      infoHtml += `
                            <span style="color: #856404; background: #fff3cd; padding: 2px 10px; border-radius: 4px;">
                                ⚠️ Menampilkan ${maxRows} dari ${data.length} baris
                            </span>
                        `;
    }

    infoHtml += `
                        </div>
                    `;

    previewArea.innerHTML = tableHtml + infoHtml;
    previewArea.classList.add("has-data");
  }

  /*
   * Escape HTML
   */
  function escapeHtml(text) {
    if (!text) return "";
    const div = document.createElement("div");
    div.textContent = text;
    return div.innerHTML;
  }

  /*
   * Handle file
   */
  async function handleFile() {
    try {
      if (!this || !this.files || this.files.length === 0) {
        return;
      }

      const file = this.files[0];

      if (file.size > 10 * 1024 * 1024) {
        alert("Ukuran file terlalu besar. Maksimal 10MB.");
        resetUploadArea();
        return;
      }

      // ✅ VALIDASI NAMA FILE: harus mengandung "pembayaran" atau "pencairan"
      const fileType = detectFileType(file.name);

      if (!fileType) {
        Swal.fire({
          icon: "error",
          title: "Nama File Tidak Sesuai",
          html: `
                        <p>Nama file harus mengandung kata <strong>"pembayaran"</strong> atau <strong>"pencairan"</strong>.</p>
                        <div style="text-align: left; margin-top: 10px; padding: 10px; background: #f8f9fa; border-radius: 4px; font-size: 13px;">
                            <strong>Contoh nama file yang benar:</strong><br>
                            • <code>pembayaran_januari_2024.xlsx</code><br>
                            • <code>pencairan_dana_2024.xlsx</code><br>
                            • <code>data_pembayaran.xls</code><br>
                            • <code>laporan_pencairan.csv</code>
                        </div>
                        <p style="margin-top: 10px;">Silakan ganti nama file Anda dan unggah kembali.</p>
                    `,
          confirmButtonText: "OK",
          confirmButtonColor: "#d33",
        }).then(() => {
          resetUploadArea();
        });
        return;
      }

      // ✅ Simpan tipe file
      uploadedFileType = fileType;
      console.log("Tipe file terdeteksi:", uploadedFileType);

      uploadedFileName = file.name;

      if (loadingPreview) {
        loadingPreview.style.display = "block";
      }

      if (previewArea) {
        previewArea.innerHTML = "";
      }

      const result = await readExcelFile(file);
      uploadedData = result.data;
      uploadedHeaders = result.headers;

      if (fileName) fileName.textContent = file.name;
      if (fileSize) fileSize.textContent = formatFileSize(file.size);

      updateFileInfo(result.totalRows, result.totalCols);

      if (fileInfo) {
        fileInfo.style.display = "block";
      }

      renderPreviewTable(result.headers, result.data, result.matrix);

      // Update upload area - tampilkan tipe file
      uploadArea.innerHTML = `
                            <input
                                type="file"
                                id="fileInput"
                                class="d-none"
                                accept=".xlsx,.xls,.csv">
                            <button
                                type="button"
                                class="file-remove-btn"
                                id="btnRemoveFile"
                                title="Hapus file">
                                <i class="ti ti-x"></i>
                            </button>
                            <i class="ti ti-file-spreadsheet upload-icon"></i>
                            <div class="upload-title">
                                ${file.name}
                            </div>
                            <div class="upload-description">
                                ${result.totalRows} baris × ${result.totalCols} kolom ditemukan
                                <br>
                                <span style="display: inline-block; margin-top: 5px; padding: 2px 8px; background: #d4edda; color: #155724; border-radius: 4px; font-size: 12px; font-weight: 600;">
                                    Tipe: ${uploadedFileType.toUpperCase()}
                                </span>
                            </div>
                        `;

      if (btnPosting) {
        btnPosting.disabled = false;
        btnPosting.classList.remove("btn-secondary");
        btnPosting.classList.add("btn-primary");
      }

      const removeBtn = document.getElementById("btnRemoveFile");
      if (removeBtn) {
        removeBtn.addEventListener("click", function (e) {
          e.stopPropagation();
          resetUploadArea();
        });
      }

      const newFileInput = document.getElementById("fileInput");
      if (newFileInput) {
        newFileInput.addEventListener("change", handleFile);
      }
    } catch (error) {
      console.error("Error:", error);
      if (previewArea) {
        previewArea.innerHTML = `
                                <i class="ti ti-alert-circle preview-icon" style="color: #dc3545;"></i>
                                <div class="preview-text" style="color: #dc3545;">
                                    ❌ Gagal membaca file: ${error.message}
                                    <br>
                                    <small>Pastikan format file sesuai dengan template.</small>
                                </div>
                            `;
        previewArea.classList.remove("has-data");
      }
    } finally {
      if (loadingPreview) {
        loadingPreview.style.display = "none";
      }
    }
  }

  /*
   * Event Listeners
   */
  if (uploadArea) {
    uploadArea.addEventListener("click", function (e) {
      if (e.target.closest(".file-remove-btn")) {
        return;
      }
      const input = document.getElementById("fileInput");
      if (input) {
        input.click();
      }
    });

    uploadArea.addEventListener("dragover", function (e) {
      e.preventDefault();
      uploadArea.classList.add("dragover");
    });

    uploadArea.addEventListener("dragleave", function () {
      uploadArea.classList.remove("dragover");
    });

    uploadArea.addEventListener("drop", function (e) {
      e.preventDefault();
      uploadArea.classList.remove("dragover");

      const files = e.dataTransfer.files;
      if (files.length > 0) {
        const input = document.getElementById("fileInput");
        if (input) {
          input.files = files;
          handleFile.call(input);
        }
      }
    });
  }

  if (fileInput) {
    fileInput.addEventListener("change", handleFile);
  }

  if (btnBatal) {
    btnBatal.addEventListener("click", function () {
      if (uploadedData && uploadedData.length > 0) {
        if (confirm("Data yang sudah diunggah akan hilang. Lanjutkan?")) {
          resetUploadArea();
        }
      } else {
        resetUploadArea();
      }
    });
  }

  if (btnPosting) {
    btnPosting.addEventListener("click", function () {
      if (!uploadedData || uploadedData.length === 0) {
        Swal.fire({
          icon: "warning",
          title: "Tidak Ada Data",
          text: "Silakan unggah file terlebih dahulu sebelum melakukan posting.",
          confirmButtonText: "OK",
          confirmButtonColor: "#3085d6",
        });
        return;
      }

      // ✅ Validasi ulang tipe file sebelum posting (safety check)
      if (!uploadedFileType) {
        Swal.fire({
          icon: "error",
          title: "Tipe File Tidak Dikenali",
          text: "Nama file harus mengandung kata 'pembayaran' atau 'pencairan'.",
          confirmButtonText: "OK",
          confirmButtonColor: "#d33",
        });
        return;
      }

      Swal.fire({
        title: "Konfirmasi Posting",
        html: `
                    <p>Apakah Anda yakin ingin memposting <strong>${uploadedData.length} baris</strong> data?</p>
                    <p style="margin-top: 10px;">
                        <span style="display: inline-block; padding: 4px 12px; background: #d4edda; color: #155724; border-radius: 4px; font-weight: 600;">
                            Tipe: ${uploadedFileType.toUpperCase()}
                        </span>
                    </p>
                `,
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Ya, Posting!",
        cancelButtonText: "Batal",
        reverseButtons: true,
      }).then((result) => {
        if (result.isConfirmed) {
          processPosting();
        }
      });
    });
  }

  // Inisialisasi
  resetUploadArea();

  function processPosting() {
    const originalText = btnPosting.innerHTML;

      let postingUrl = "";
      if (uploadedFileType === "pembayaran") {
        // postingUrl = "/import/posting-excel-pembayaran";
        postingUrl = "/import/posting-excel";
      } else if (uploadedFileType === "pencairan") {
        // postingUrl = "/import/posting-excel-pencairan";
        postingUrl = "/import/posting-excel";
      } else {
        // Safety check: seharusnya tidak pernah masuk ke sini
        Swal.fire({
          icon: "error",
          title: "Tipe File Tidak Valid",
          text: "Tipe file tidak dikenali. Silakan unggah ulang file dengan nama yang benar.",
          confirmButtonText: "OK",
          confirmButtonColor: "#d33",
        });
        return;
      }

    btnPosting.innerHTML = `
        <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
        Memproses...
    `;
    btnPosting.disabled = true;

    //  Kirim data ke server dengan tipe file
    

    $.ajax({
      url: postingUrl,
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify({
        fileName: uploadedFileName,
        headers: uploadedHeaders,
        data: uploadedData,
        typeExcel: uploadedFileType, // TAMBAHAN: kirim tipe file
      }),
      success: function (response) {
        btnPosting.innerHTML = originalText;
        btnPosting.disabled = false;

        let message = `sImport selesai!\n\n`;
        message += `Total Data: ${response.totalData}\n`;
        message += `Berhasil: ${response.successCount}\n`;
        message += `Gagal: ${response.errorCount}`;

        let iconType = response.errorCount > 0 ? "warning" : "success";
        let titleText =
          response.errorCount > 0
            ? "Import Selesai dengan Peringatan"
            : "Import Berhasil!";

        if (response.errors && response.errors.length > 0) {
          message += `\n\n📋 Detail Error:\n`;
          response.errors.slice(0, 5).forEach((err) => {
            message += `• Row ${err.row}: ${err.error}\n`;
          });
          if (response.errors.length > 5) {
            message += `\n... dan ${response.errors.length - 5} error lainnya`;
          }
        }

        Swal.fire({
          icon: iconType,
          title: titleText,
          text: message,
          confirmButtonText: "OK",
          confirmButtonColor: "#3085d6",
        }).then(() => {
          if (response.successCount > 0) {
            resetUploadArea();
          }
        });

        console.log("Response:", response);
      },
      error: function (xhr) {
        btnPosting.innerHTML = originalText;
        btnPosting.disabled = false;

        let errorMsg = "Gagal memposting data.";
        let errorDetail = "";

        if (xhr.responseJSON && xhr.responseJSON.message) {
          errorMsg = xhr.responseJSON.message;
        }

        if (xhr.responseJSON && xhr.responseJSON.errors) {
          errorDetail = xhr.responseJSON.errors.map((e) => `• ${e}`).join("\n");
        }

        Swal.fire({
          icon: "error",
          title: "Error!",
          html: `
                    <p>${errorMsg}</p>
                    ${
                      errorDetail
                        ? `<div style="text-align: left; margin-top: 10px; padding: 10px; background: #f8f9fa; border-radius: 4px; font-size: 13px;">${errorDetail}</div>`
                        : ""
                    }
                `,
          confirmButtonText: "OK",
          confirmButtonColor: "#d33",
        });

        console.error("Error:", xhr);
      },
    });
  }
});
