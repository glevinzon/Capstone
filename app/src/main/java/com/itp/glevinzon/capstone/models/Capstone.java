package com.itp.glevinzon.capstone.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Glevinzon on 5/8/2017.
 */

public class Capstone {

    @SerializedName("equations")
    @Expose
    private Equations equations;
    @SerializedName("tags")
    @Expose
    private List<Tag> tags = null;
    @SerializedName("records")
    @Expose
    private List<Record> records = null;

    public Equations getEquations() {
        return equations;
    }

    public void setEquations(Equations equations) {
        this.equations = equations;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

}