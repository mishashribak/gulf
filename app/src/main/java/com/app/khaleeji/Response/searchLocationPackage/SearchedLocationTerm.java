package com.app.khaleeji.Response.searchLocationPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dcube on 12-10-2018.
 */

public class SearchedLocationTerm {

    @SerializedName("offset")
    @Expose
    private Integer offset;
    @SerializedName("value")
    @Expose
    private String value;

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
