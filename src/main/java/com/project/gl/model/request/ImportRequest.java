package com.project.gl.model.request;
import java.util.List;
import java.util.Map;

import java.util.List;
import java.util.Map;

public class ImportRequest {
    private String fileName;
    private List<Map<String, Object>> data;
    private List<String> headers;
    private String typeExcel;

    public String getTypeExcel() {
        return typeExcel;
    }

    public void setTypeExcel(String typeExcel) {
        this.typeExcel = typeExcel;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }
}