package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HotSpotUserResponse {
    @SerializedName("data")
    @Expose
    private List<HotSpotUser> data = null;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("message_original")
    @Expose
    private String messageOriginal;

    public List<HotSpotUser> getData() {
        return data;
    }

    public void setData(List<HotSpotUser> data) {
        this.data = data;
    }

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

    public String getMessageOriginal() {
        return messageOriginal;
    }

    public void setMessageOriginal(String messageOriginal) {
        this.messageOriginal = messageOriginal;
    }
}
