package com.app.khaleeji.Response.searchLocationPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dcube on 12-10-2018.
 */

public class SearchedLocationMatchedSubstring {

    @SerializedName("length")
    @Expose
    private Integer length;
    @SerializedName("offset")
    @Expose
    private Integer offset;

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

}
