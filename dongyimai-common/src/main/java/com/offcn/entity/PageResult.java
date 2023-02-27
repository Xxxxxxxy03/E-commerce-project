package com.offcn.entity;

import java.util.List;
/*
* 分页结果封装类*/
public class PageResult <T>{
    private long total;//总记录数
    private List<T> rows;//当前页数据集合

    public PageResult() {
    }

    public PageResult(long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
