package com.app.khaleeji.Response.searchLocationPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Dcube on 12-10-2018.
 */

public class SearchLocationResponse {

    @SerializedName("predictions")
    @Expose
    private List<SearchLocationPrediction> predictions = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<SearchLocationPrediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<SearchLocationPrediction> predictions) {
        this.predictions = predictions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
