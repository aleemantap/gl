package com.project.gl.model.request;

public class DataTablesApiClientFinalRequest {
    private int draw;
    private int start;
    private int length;
    private String searchValue;
    private String orderColumn;
    private String orderDir;

    // Getters & Setters
    public int getDraw() { return draw; }
    public void setDraw(int draw) { this.draw = draw; }

    public int getStart() { return start; }
    public void setStart(int start) { this.start = start; }

    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }

    public String getSearchValue() { return searchValue; }
    public void setSearchValue(String searchValue) { this.searchValue = searchValue; }

    public String getOrderColumn() { return orderColumn; }
    public void setOrderColumn(String orderColumn) { this.orderColumn = orderColumn; }

    public String getOrderDir() { return orderDir; }
    public void setOrderDir(String orderDir) { this.orderDir = orderDir; }
}
