package com.itp.glevinzon.capstone.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Glevinzon on 5/8/2017.
 */

public class Search {

    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("currentPage")
    @Expose
    private Integer currentPage;
    @SerializedName("perPage")
    @Expose
    private Integer perPage;
    @SerializedName("lastPage")
    @Expose
    private Integer lastPage;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public Integer getLastPage() {
        return lastPage;
    }

    public void setLastPage(Integer lastPage) {
        this.lastPage = lastPage;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

}