package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dcube on 21-11-2018.
 */

public class DeleteGroupResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("message_original")
    @Expose
    private String messageOriginal;

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
