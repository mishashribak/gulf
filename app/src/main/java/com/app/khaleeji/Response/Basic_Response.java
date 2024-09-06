package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Basic_Response {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private forgotOtpResponse data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public forgotOtpResponse getData() {
        return data;
    }

    public void setData(forgotOtpResponse data) {
        this.data = data;
    }
}