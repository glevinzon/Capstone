package com.itp.glevinzon.capstone.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Glevinzon on 5/1/2017.
 */

public class Keyword {
    @SerializedName("tags")
    @Expose
    private List<Tag> tags = null;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
