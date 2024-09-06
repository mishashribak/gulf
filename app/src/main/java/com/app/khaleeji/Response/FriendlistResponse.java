package com.app.khaleeji.Response;

import com.google.gson.annotations.Expose;

 import java.util.List;
 import com.google.gson.annotations.Expose;
 import com.google.gson.annotations.SerializedName;

public class FriendlistResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private friendDataModel data = new friendDataModel();
    @SerializedName("message_original")
    @Expose
    private String messageOriginal;
    @SerializedName("type")
    @Expose
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public friendDataModel getData() {
        return data;
    }

    public void setData(friendDataModel data) {
        this.data = data;
    }

    public String getMessageOriginal() {
        return messageOriginal;
    }

    public void setMessageOriginal(String messageOriginal) {
        this.messageOriginal = messageOriginal;
    }

}
